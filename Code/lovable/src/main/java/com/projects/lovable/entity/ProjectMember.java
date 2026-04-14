package com.projects.lovable.entity;

import com.projects.lovable.enums.ProjectRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "project_members")
public class ProjectMember {

    @EmbeddedId
    private ProjectMemberId id;

    @ManyToOne
    @MapsId("userId")
    private User user;

    @ManyToOne
    @MapsId("projectId")
    //If we don't add mapsId,
    //then there will be two similar column by name project_id
    //may throw exception of ambiguous column names
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,name = "project_role")
    private ProjectRole projectRole;

    private Instant invitedAt;
    private Instant acceptedAt;

}
