package com.projects.lovable.mapper;

import com.projects.lovable.dto.subscription.PlanResponse;
import com.projects.lovable.dto.subscription.SubscriptionResponse;
import com.projects.lovable.entity.Plan;
import com.projects.lovable.entity.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

    PlanResponse toPlanResponse(Plan plan);
}
