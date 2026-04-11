package com.projects.lovable.service.impl;

import com.projects.lovable.dto.member.InviteMemberRequest;
import com.projects.lovable.dto.member.MemberResponse;
import com.projects.lovable.dto.member.UpdateMemberRoleRequest;
import com.projects.lovable.service.ProjectMemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectMemberServiceImpl implements ProjectMemberService {
    @Override
    public List<MemberResponse> getProjectMembers(long projectId, Long userId) {
        return List.of();
    }

    @Override
    public MemberResponse inviteMember(InviteMemberRequest request, Long userId, Long projectId) {
        return null;
    }

    @Override
    public MemberResponse updateMemberRole(UpdateMemberRoleRequest request, Long memberId, Long userId, Long projectId) {
        return null;
    }

    @Override
    public MemberResponse deleteProjectMember(Long memberId, Long userId, Long projectId) {
        return null;
    }
}
