package com.projects.lovable.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
public class User {
    private Long id;

    private String name;
    private String email;
    private String passwordHash;

    private String avatarUrl;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
}
