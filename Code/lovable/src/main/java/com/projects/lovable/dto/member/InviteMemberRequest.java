package com.projects.lovable.dto.member;

import com.projects.lovable.enums.ProjectRole;

public record InviteMemberRequest(
        String email,
        ProjectRole role
) {
}
