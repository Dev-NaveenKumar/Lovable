package com.projects.lovable.service.impl;

import com.projects.lovable.dto.subscription.CheckoutRequest;
import com.projects.lovable.dto.subscription.CheckoutResponse;
import com.projects.lovable.dto.subscription.PortalResponse;
import com.projects.lovable.dto.subscription.SubscriptionResponse;
import com.projects.lovable.repository.PlanRepository;
import com.projects.lovable.security.AuthUtil;
import com.projects.lovable.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final AuthUtil authUtil;

    @Override
    public SubscriptionResponse getCurrentSubscription() {
        return null;
    }
}
