package com.projects.lovable.service.impl;

import com.projects.lovable.dto.member.InviteMemberRequest;
import com.projects.lovable.dto.member.MemberResponse;
import com.projects.lovable.dto.member.UpdateMemberRoleRequest;
import com.projects.lovable.entity.Project;
import com.projects.lovable.entity.ProjectMember;
import com.projects.lovable.entity.ProjectMemberId;
import com.projects.lovable.entity.User;
import com.projects.lovable.mapper.ProjectMemberMapper;
import com.projects.lovable.repository.ProjectMemberRepository;
import com.projects.lovable.repository.ProjectRepository;
import com.projects.lovable.repository.UserRepository;
import com.projects.lovable.service.ProjectMemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberMapper projectMemberMapper;
    private final UserRepository userRepository;

    @Override
    public List<MemberResponse> getProjectMembers(long projectId, Long userId) {
        Project project = getAccessibleProjectById(userId, projectId);

        List<MemberResponse> memberResponseList = new ArrayList<>();

        memberResponseList.add(projectMemberMapper
                .toProjectMemberResponseFromOwner(project.getOwner()));

        memberResponseList.addAll(projectMemberRepository.findByIdProjectId(projectId)
                .stream()
                .map(projectMemberMapper::toProjectMemberResponseFromProjectMember)
                .toList());
        return memberResponseList;
    }

    @Override
    public MemberResponse inviteMember(InviteMemberRequest request, Long userId, Long projectId) {
        Project project = getAccessibleProjectById(userId, projectId);

        if(!project.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Not allowed to invite member.");
        }

        User invitee = userRepository.findByEmail(request.email()).orElseThrow();

        if(invitee.getId().equals(userId)) {
            throw new RuntimeException("Cannot invite yourself.");
        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId,invitee.getId());

        if(projectMemberRepository.existsById(projectMemberId)) {
            throw new RuntimeException("Cannot invite again.");
        }

        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .project(project)
                .user(invitee)
                .projectRole(request.role())
                .invitedAt(Instant.now())
                .build();

        projectMemberRepository.save(projectMember);

        return projectMemberMapper.toProjectMemberResponseFromProjectMember(projectMember);
    }

    @Override
    public MemberResponse updateMemberRole(UpdateMemberRoleRequest request, Long memberId, Long userId, Long projectId) {
        Project project = getAccessibleProjectById(userId, projectId);

        if(!project.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Not allowed to invite member.");
        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId,memberId);

        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId).orElseThrow();

        projectMember.setProjectRole(request.role());

        projectMemberRepository.save(projectMember);

        return projectMemberMapper.toProjectMemberResponseFromProjectMember(projectMember);

    }

    @Override
    public void removeProjectMember(Long memberId, Long userId, Long projectId) {

        Project project = getAccessibleProjectById(userId, projectId);

        if(!project.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Not allowed to invite member.");
        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId,memberId);

        if(!projectMemberRepository.existsById(projectMemberId)) {
            throw new RuntimeException("Member does not exist.");
        }

        projectMemberRepository.deleteById(projectMemberId);
    }

    public Project getAccessibleProjectById(Long userId, Long projectId) {
        return projectRepository.findAccessibleProjectById(projectId, userId).orElseThrow();
    }
}
