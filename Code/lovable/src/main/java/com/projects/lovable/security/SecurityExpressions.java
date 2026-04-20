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

    public boolean canViewProject(Long projectId) {
        Long userId = authUtil.getCurrentUserId();

        return projectMemberRepository
                .findRoleByProjectIdAndUserId(projectId, userId)
                .map(role->role.getPermissions().contains(ProjectPermission.VIEW))
                .orElse(false);
    }

    public boolean canEditProject(Long projectId) {
        Long userId = authUtil.getCurrentUserId();

        return projectMemberRepository
                .findRoleByProjectIdAndUserId(projectId, userId)
                .map(role->role.getPermissions().contains(ProjectPermission.EDIT))
                .orElse(false);
    }
}
