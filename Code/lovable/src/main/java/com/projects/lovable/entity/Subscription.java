package com.projects.lovable.entity;

import com.projects.lovable.enums.SubscriptionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class Subscription {

    private Long id;

    private User user;

    private Plan plan;

    private SubscriptionStatus status;

    private String stripeSubscription;
    private Instant currentPeriodStart;
    private Instant currentPeriodEnd;
    private Boolean cancelAtPeriodEnd=false;

    private Instant createdAt;
    private Instant updatedAt;
}
