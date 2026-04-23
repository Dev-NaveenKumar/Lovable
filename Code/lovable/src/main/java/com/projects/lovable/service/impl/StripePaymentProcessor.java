package com.projects.lovable.service.impl;

import com.projects.lovable.dto.subscription.CheckoutRequest;
import com.projects.lovable.dto.subscription.CheckoutResponse;
import com.projects.lovable.dto.subscription.PortalResponse;
import com.projects.lovable.entity.Plan;
import com.projects.lovable.entity.User;
import com.projects.lovable.error.ResourceNotFoundException;
import com.projects.lovable.repository.PlanRepository;
import com.projects.lovable.repository.UserRepository;
import com.projects.lovable.security.AuthUtil;
import com.projects.lovable.service.PaymentProcessor;
import com.stripe.exception.StripeException;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentProcessor implements PaymentProcessor {

    private final AuthUtil authUtil;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    @Value("${client.url}")
    private String frontEndUrl;

    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request) {

        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(()-> new ResourceNotFoundException("Plan", request.planId()));
        Long userId = authUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User", userId));

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
        log.debug("Type: {}",type);

        switch (type){
            case "checkout.sessioin.completed": handleCheckoutSessionCompleted();
            case "customer.subscription.updated": handleCustomerSubscriptionUpdated();
            case "customer.subscription.deleted": handleCustomerSubscriptionDeleted();
            case "invoid.paid": handleInviocePaid();
            case "invoid.payment_failed": handleInviocePaymentFailed();
            default:log.debug("Ignoring the event: {}",type);
        }
    }

    private void handleInviocePaymentFailed() {
    }

    private void handleInviocePaid() {
    }

    private void handleCustomerSubscriptionDeleted() {
    }

    private void handleCustomerSubscriptionUpdated() {
    }

    private void handleCheckoutSessionCompleted() {

    }
}
