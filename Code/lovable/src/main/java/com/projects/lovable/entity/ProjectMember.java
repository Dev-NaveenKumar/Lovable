package com.projects.lovable.entity;

import com.projects.lovable.enums.ProjectRole;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ProjectMember {
    private ProjectMemberId id;

    private User userId;
    private Project projectId;

    private ProjectRole roleId;

    private User invitedBy;
    private Instant invitedAt;
    private Instant acceptedAt;

}
