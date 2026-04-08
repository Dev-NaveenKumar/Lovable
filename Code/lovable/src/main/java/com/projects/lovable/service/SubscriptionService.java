package com.projects.lovable.service;

import com.projects.lovable.dto.subscription.CheckoutRequest;
import com.projects.lovable.dto.subscription.CheckoutResponse;
import com.projects.lovable.dto.subscription.PortalResponse;
import com.projects.lovable.dto.subscription.SubscriptionResponse;

public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription(Long userId);

    CheckoutResponse createCheckoutSessionUrl(Long userId, CheckoutRequest request);

    PortalResponse openCustomerPortal(Long userId);
}
