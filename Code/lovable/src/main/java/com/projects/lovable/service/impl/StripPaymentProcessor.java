package com.projects.lovable.service.impl;

import com.projects.lovable.dto.subscription.CheckoutRequest;
import com.projects.lovable.dto.subscription.CheckoutResponse;
import com.projects.lovable.dto.subscription.PortalResponse;
import com.projects.lovable.service.PaymentProcessor;
import org.springframework.stereotype.Service;

@Service
public class StripPaymentProcessor implements PaymentProcessor {
    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request) {
        return null;
    }

    @Override
    public PortalResponse openCustomerPortal() {
        return null;
    }
}
