package com.projects.lovable.service;

import com.projects.lovable.dto.member.InviteMemberRequest;
import com.projects.lovable.dto.member.MemberResponse;
import com.projects.lovable.dto.member.UpdateMemberRoleRequest;

import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(long projectId);

    MemberResponse inviteMember(InviteMemberRequest request, Long projectId);

    MemberResponse updateMemberRole(UpdateMemberRoleRequest request, Long memberId, Long projectId);

    void removeProjectMember(Long memberId, Long projectId);
}
