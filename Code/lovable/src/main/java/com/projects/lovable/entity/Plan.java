package com.projects.lovable.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Plan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String stripePriceId;
    private Integer maxProjects;
    private Integer maxTokenPerDay;
    private Integer maxPreviews;
    private Boolean unlimitedAi;

    private Boolean active;
}
