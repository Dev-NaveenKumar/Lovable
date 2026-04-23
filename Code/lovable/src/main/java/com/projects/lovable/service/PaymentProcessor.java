package com.projects.lovable.service;

import com.projects.lovable.dto.subscription.CheckoutRequest;
import com.projects.lovable.dto.subscription.CheckoutResponse;
import com.projects.lovable.dto.subscription.PortalResponse;
import com.stripe.model.StripeObject;

import java.util.Map;

public interface PaymentProcessor {

    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request);

    PortalResponse openCustomerPortal();

    void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata);
}
