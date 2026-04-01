package com.projects.lovable.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserLog {

    private Long id;

    private User user;
    private Project project;

    private String action;

    private Integer tokensUsed;
    private Integer durationsMs;

    private String metaData;//JSON of {model_used , prompt_used}

    private Instant createdAt;
}
