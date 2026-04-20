package com.projects.lovable.security;

import com.projects.lovable.enums.ProjectPermission;
import com.projects.lovable.enums.ProjectRole;
import com.projects.lovable.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("security")
@RequiredArgsConstructor
public class SecurityExpressions {

    private final AuthUtil authUtil;
    private final ProjectMemberRepository projectMemberRepository;

    public boolean hasPermission(Long projectId, ProjectPermission projectPermission) {
        Long userId = authUtil.getCurrentUserId();

        return projectMemberRepository
                .findRoleByProjectIdAndUserId(projectId, userId)
                .map(role -> role.getPermissions().contains(projectPermission))
                .orElse(false);
    }

    public boolean canViewProject(Long projectId) {
        return hasPermission(projectId, ProjectPermission.VIEW);
    }

    public boolean canEditProject(Long projectId) {
        return hasPermission(projectId, ProjectPermission.EDIT);
    }

    public boolean canDeleteProject(Long projectId) {return hasPermission(projectId, ProjectPermission.DELETE);}

    public boolean canViewMembers(Long projectId) {return hasPermission(projectId, ProjectPermission.VIEW_MEMBERS);}

    public boolean canManageMembers(Long projectId) {return hasPermission(projectId, ProjectPermission.MANAGE_MEMBERS);}
}
