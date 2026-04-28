package com.projects.lovable.service.impl;

import com.projects.lovable.dto.subscription.CheckoutRequest;
import com.projects.lovable.dto.subscription.CheckoutResponse;
import com.projects.lovable.dto.subscription.PortalResponse;
import com.projects.lovable.dto.subscription.SubscriptionResponse;
import com.projects.lovable.entity.Plan;
import com.projects.lovable.entity.Subscription;
import com.projects.lovable.entity.User;
import com.projects.lovable.enums.SubscriptionStatus;
import com.projects.lovable.error.ResourceNotFoundException;
import com.projects.lovable.mapper.SubscriptionMapper;
import com.projects.lovable.repository.PlanRepository;
import com.projects.lovable.repository.ProjectMemberRepository;
import com.projects.lovable.repository.SubscriptionRepository;
import com.projects.lovable.repository.UserRepository;
import com.projects.lovable.security.AuthUtil;
import com.projects.lovable.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final AuthUtil authUtil;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final Integer FREE_TIER_PROJECT_LIMIT = 1;

    @Override
    public SubscriptionResponse getCurrentSubscription() {
        Long userId = authUtil.getCurrentUserId();

        var currSubscription = subscriptionRepository.findByUserIdAndStatusIn(userId, Set.of(
                SubscriptionStatus.ACTIVE, SubscriptionStatus.PAST_DUE,
                SubscriptionStatus.TRIALING
        )).orElse(new Subscription());

        return subscriptionMapper.toSubscriptionResponse(currSubscription);
    }

    @Override
    public void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId) {
        boolean exists = subscriptionRepository.existsByStripeSubscription(subscriptionId);
        if (exists) return;
        User user = getUser(userId);
        Plan plan = getPlan(planId);

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .stripeSubscription(subscriptionId)
                .status(SubscriptionStatus.INCOMPLETE)
                .build();
        subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status,
                                   Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);
        boolean isSubscriptionHasBeenUpdated = false;

        if (status != null && status != subscription.getStatus()) {
            subscription.setStatus(status);
            isSubscriptionHasBeenUpdated = true;
        }

        if (periodStart != null && !periodStart.equals(subscription.getCurrentPeriodStart())) {
            subscription.setCurrentPeriodStart(periodStart);
            isSubscriptionHasBeenUpdated = true;
        }

        if (periodEnd != null && !periodEnd.equals(subscription.getCurrentPeriodEnd())) {
            subscription.setCurrentPeriodEnd(periodEnd);
            isSubscriptionHasBeenUpdated = true;
        }

        if (cancelAtPeriodEnd != null && !cancelAtPeriodEnd.equals(subscription.getCancelAtPeriodEnd())) {
            subscription.setCancelAtPeriodEnd(cancelAtPeriodEnd);
            isSubscriptionHasBeenUpdated = true;
        }

        if (planId != null && !planId.equals(subscription.getPlan().getId())) {
            subscription.setPlan(getPlan(planId));
            isSubscriptionHasBeenUpdated = true;
        }

        if (isSubscriptionHasBeenUpdated) {
            log.debug("Subscription updated: {}", gatewaySubscriptionId);
            subscriptionRepository.save(subscription);
        }
    }

    @Override
    public void cancelSubscription(String gatewaySubscriptionId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            log.debug("Subscription is already Cancelled, GatewaySubscriptionId: {}", gatewaySubscriptionId);
            return;
        }
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);
    }

    @Override
    public void renewSubscriptionPeriod(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        Instant newStart = periodStart != null ? periodStart : subscription.getCurrentPeriodEnd();
        subscription.setCurrentPeriodStart(newStart);
        subscription.setCurrentPeriodEnd(periodEnd);

        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE || subscription.getStatus() == SubscriptionStatus.INCOMPLETE) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }

        subscriptionRepository.save(subscription);
    }

    @Override
    public void markSubscriptionPastDue(String gatewaySubscriptionId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE) {
            log.debug("Subscription is already PastDue, GatewaySubscriptionId: {}", gatewaySubscriptionId);
            return;
        }
        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        subscriptionRepository.save(subscription);
        //Notify user with email
    }


    @Override
    public boolean canCreateNewProject() {
        SubscriptionResponse currentSubscription = getCurrentSubscription();
        Long userId = authUtil.getCurrentUserId();

        int countOfOwnedProjects = projectMemberRepository.countProjectOwnedByUser(userId);

        if (currentSubscription.plan() == null) {
            return countOfOwnedProjects < FREE_TIER_PROJECT_LIMIT;
        }

        return  countOfOwnedProjects < currentSubscription.plan().maxProjects();
    }

    //Utility methods

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    private Plan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", planId));
    }

    private @NonNull Subscription getSubscription(String gatewaySubscriptionId) {
        return subscriptionRepository.findByStripeSubscription(gatewaySubscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", gatewaySubscriptionId));
    }
}
