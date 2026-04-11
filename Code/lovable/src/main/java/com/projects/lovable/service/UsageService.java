package com.projects.lovable.service;

import com.projects.lovable.dto.subscription.PlanLimitsResponse;
import com.projects.lovable.dto.subscription.UsageTodayResponse;

public interface UsageService {
    UsageTodayResponse getTodayUsageOfUser(Long userId);

    PlanLimitsResponse getCurrentSubscriptionLimitsOfUser(Long userId);
}
