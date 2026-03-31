package com.projects.lovable.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Plan {
    private Long id;

    private String name;

    private String stripePriceId;
    private Integer maxProjects;
    private Integer maxTokenPerDay;
    private Integer maxPreviews;
    private Boolean unlimitedAi;

    private Boolean active;
}
