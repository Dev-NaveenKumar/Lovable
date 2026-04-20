package com.projects.lovable.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


import java.util.Set;

import static com.projects.lovable.enums.ProjectPermission.*;

@RequiredArgsConstructor
@Getter
public enum ProjectRole {
    EDITOR(Set.of(EDIT,VIEW,DELETE,VIEW_MEMBERS)),
    VIEWER(Set.of(VIEW)),
    OWNER(Set.of(EDIT,VIEW,DELETE,MANAGE_MEMBERS,VIEW_MEMBERS)),;

    private final Set<ProjectPermission> permissions;
}
