package com.projects.lovable.entity;

import com.projects.lovable.enums.SubscriptionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class Project {
    private Long  id;

    private String name;

    private User user;

    private Boolean isPublic=false;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
}
