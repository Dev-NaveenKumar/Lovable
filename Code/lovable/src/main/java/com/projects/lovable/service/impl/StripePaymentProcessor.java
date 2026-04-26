package com.projects.lovable.service.impl;

import com.projects.lovable.dto.subscription.CheckoutRequest;
import com.projects.lovable.dto.subscription.CheckoutResponse;
import com.projects.lovable.dto.subscription.PortalResponse;
import com.projects.lovable.entity.Plan;
import com.projects.lovable.entity.User;
import com.projects.lovable.enums.SubscriptionStatus;
import com.projects.lovable.error.ResourceNotFoundException;
import com.projects.lovable.repository.PlanRepository;
import com.projects.lovable.repository.UserRepository;
import com.projects.lovable.security.AuthUtil;
import com.projects.lovable.service.PaymentProcessor;
import com.projects.lovable.service.SubscriptionService;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentProcessor implements PaymentProcessor {

    private final AuthUtil authUtil;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final SubscriptionService  subscriptionService;

    @Value("${client.url}")
    private String frontEndUrl;

    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request) {

        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(()-> new ResourceNotFoundException("Plan", request.planId()));
        Long userId = authUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User", userId));

        var params = SessionCreateParams.builder()
                .addLineItem(
                        SessionCreateParams.LineItem.builder().setPrice(plan.getStripePriceId()).setQuantity(1L).build())
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(frontEndUrl + "/success.html?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontEndUrl + "/cancel.html")
                .putMetadata("user_id", userId.toString())
                .putMetadata("plan_id", plan.getId().toString());
        try {
            String stripeCustomerId = user.getStripeCustomerId();
            if(stripeCustomerId==null || stripeCustomerId.isEmpty()){
                params.setCustomerEmail(user.getUsername());
            }else{
                params.setCustomer(stripeCustomerId);
            }
            Session session = Session.create(params.build());
            return new  CheckoutResponse(session.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PortalResponse openCustomerPortal() {
        return null;
    }

    @Override
    public void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata) {
        log.debug("Type: {}", type);

        switch (type) {
            case "checkout.session.completed":
                handleCheckoutSessionCompleted((Session) stripeObject, metadata);
                break;
            case "customer.subscription.updated":
                handleCustomerSubscriptionUpdated((Subscription) stripeObject);
                break;
            case "customer.subscription.deleted":
                handleCustomerSubscriptionDeleted((Subscription) stripeObject);
                break;
            case "invoice.paid":
                handleInvoicePaid((Invoice) stripeObject);
                break;
            case "invoice.payment_failed":
                handleInvoicePaymentFailed((Invoice) stripeObject);
                break;
            default:
                log.debug("Ignoring the event: {}", type);
        }
    }

    private void handleCheckoutSessionCompleted(Session session, Map<String, String> metadata) {

        if(session == null){log.error("Session is null inside handleCheckoutSessionCompleted"); return;}

        Long userId = Long.parseLong(metadata.get("user_id"));
        Long planId = Long.parseLong(metadata.get("plan_id"));

        String subscription = session.getSubscription();
        String customerId = session.getCustomer();

        User user = getUser(userId);

        if(user.getStripeCustomerId()==null ){
            user.setStripeCustomerId(customerId);
            userRepository.save(user);
        }

        subscriptionService.activateSubscription(userId, planId, subscription,customerId);
    }

    private void handleCustomerSubscriptionUpdated(Subscription subscription) {
        if(subscription == null){log.error("subscription is null inside handleCustomerSubscriptionUpdated"); return;}

        SubscriptionStatus status = mapStripeStatusToEnum(subscription.getStatus());
        if(status==null){
            log.warn("Unknown status: {} for subscription {}",subscription.getStatus(),subscription.getId());
            return;
        }

        SubscriptionItem item = subscription.getItems().getData().getFirst();
        Instant periodStart = toInstant(item.getCurrentPeriodStart());
        Instant periodEnd = toInstant(item.getCurrentPeriodEnd());

        Long planId = resolvePlanId(item.getPrice());

        subscriptionService.updateSubscription(subscription.getId(), status,
                periodStart, periodEnd, subscription.getCancelAtPeriodEnd(),planId);

    }

    private void handleCustomerSubscriptionDeleted(Subscription subscription) {
        if(subscription == null){log.error("subscription is null inside handleCustomerSubscriptionDeleted"); return;}

        subscriptionService.cancelSubscription(subscription.getId());
    }

    private void handleInvoicePaid(Invoice invoice) {
        if(invoice == null){log.error("invoice is null inside handleInvoicePaid"); return;}

        String subId = extractSubscriptionId(invoice);
        if(subId==null)return;

        try {
            Subscription subscription = Subscription.retrieve(subId);//sdk calls the stripe servers
            SubscriptionItem item = subscription.getItems().getData().getFirst();

            Instant periodStart = toInstant(item.getCurrentPeriodStart());
            Instant periodEnd = toInstant(item.getCurrentPeriodEnd());

            subscriptionService.renewSubscriptionPeriod(subId,periodStart,periodEnd);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleInvoicePaymentFailed(Invoice invoice) {
        if(invoice == null){log.error("invoice is null inside handleInvoicePaymentFailed"); return;}


        String subId = extractSubscriptionId(invoice);
        if(subId==null)return;

        subscriptionService.markSubscriptionPastDue(subId);
    }

    private @NonNull User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    private SubscriptionStatus mapStripeStatusToEnum(String status) {
        return switch(status){
            case "active" -> SubscriptionStatus.ACTIVE;
            case "trialing" -> SubscriptionStatus.TRIALING;
            case "past_due","unpaid","paused", "incomplete_expired" -> SubscriptionStatus.PAST_DUE;
            case "canceled" -> SubscriptionStatus.CANCELLED;
            case "incomplete" -> SubscriptionStatus.INCOMPLETE;
            default -> {
                log.warn("Unmapped stripe status: {}", status);
                yield null;
            }
        };
    }

    private Instant toInstant(Long epoc){
        return epoc!=null ? Instant.ofEpochSecond(epoc) : null;
    }

    private Long resolvePlanId(Price price){
        if(price == null || price.getId()==null){return null;}
        return planRepository.findByStripePriceId(price.getId())
                .map(Plan::getId)
                .orElse(null);
    }

    private String extractSubscriptionId(Invoice invoice) {
        var parent = invoice.getParent();
        if(parent == null){return null;}

        var subscriptionDetails = parent.getSubscriptionDetails();
        if(subscriptionDetails == null){return null;}

        return subscriptionDetails.getSubscription();
    }
}
