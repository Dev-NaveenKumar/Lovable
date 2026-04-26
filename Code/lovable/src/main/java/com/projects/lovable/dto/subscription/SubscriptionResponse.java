package com.projects.lovable.dto.subscription;

import java.time.Instant;

public record SubscriptionResponse(
        PlanResponse plan,
        String status,
        Instant currentPeriodStart,
        Long tokensUsedThisCycle
) {

}
