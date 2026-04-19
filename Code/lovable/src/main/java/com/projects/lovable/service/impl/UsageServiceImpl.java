package com.projects.lovable.service.impl;

import com.projects.lovable.dto.subscription.PlanLimitsResponse;
import com.projects.lovable.dto.subscription.UsageTodayResponse;
import com.projects.lovable.security.AuthUtil;
import com.projects.lovable.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {

    private final AuthUtil authUtil;

    @Override
    public UsageTodayResponse getTodayUsageOfUser() {
        return null;
    }

    @Override
    public PlanLimitsResponse getCurrentSubscriptionLimitsOfUser() {
        return null;
    }
}
