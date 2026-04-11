package com.projects.lovable.service.impl;

import com.projects.lovable.dto.subscription.PlanResponse;
import com.projects.lovable.service.PlanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {
    @Override
    public List<PlanResponse> getAllActivePlans() {
        return List.of();
    }
}
