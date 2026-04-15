package com.projects.lovable.dto.member;

import com.projects.lovable.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest( @NotNull ProjectRole role) {
}
