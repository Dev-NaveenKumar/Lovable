package com.projects.lovable.controller;

import com.projects.lovable.dto.subscription.*;
import com.projects.lovable.service.PlanService;
import com.projects.lovable.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BillingController {
    private final SubscriptionService subscriptionService;
    private final PlanService planService;

    @GetMapping("/api/plan")
     public ResponseEntity<List<PlanResponse>> getAllPlans(){
        return ResponseEntity.ok(planService.getAllActivePlans());
    }

    @GetMapping("/api/me/subscription")
    public ResponseEntity<SubscriptionResponse> getMySubscription(){
        Long userId=1L;
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription(userId));
    }

    @PostMapping("/api/stripe/checkout")
    public ResponseEntity<CheckoutResponse> createCheckoutResponse(
            @RequestBody CheckoutRequest request
    ){
        Long userId=1L;
        return ResponseEntity.ok(subscriptionService.createCheckoutSessionUrl(userId,request));

    }

    @PostMapping("/api/stripe/portal")
    public ResponseEntity<PortalResponse> openCustomerPortal(){
        Long userId=1L;
        return ResponseEntity.ok(subscriptionService.openCustomerPortal(userId));

    }
}
