package com.projects.lovable.service.impl;

import com.projects.lovable.dto.member.InviteMemberRequest;
import com.projects.lovable.dto.member.MemberResponse;
import com.projects.lovable.dto.member.UpdateMemberRoleRequest;
import com.projects.lovable.entity.Project;
import com.projects.lovable.entity.ProjectMember;
import com.projects.lovable.entity.ProjectMemberId;
import com.projects.lovable.entity.User;
import com.projects.lovable.error.ResourceNotFoundException;
import com.projects.lovable.mapper.ProjectMemberMapper;
import com.projects.lovable.repository.ProjectMemberRepository;
import com.projects.lovable.repository.ProjectRepository;
import com.projects.lovable.repository.UserRepository;
import com.projects.lovable.security.AuthUtil;
import com.projects.lovable.service.ProjectMemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberMapper projectMemberMapper;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    @Override
    public List<MemberResponse> getProjectMembers(long projectId) {
        Project project = getAccessibleProjectById(projectId);

        return projectMemberRepository.findByIdProjectId(projectId)
                .stream()
                .map(projectMemberMapper::toProjectMemberResponseFromProjectMember)
                .toList();
    }

    @Override
    public MemberResponse inviteMember(InviteMemberRequest request, Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId);

//        if(!project.getOwner().getId().equals(userId)) {
//            throw new RuntimeException("Not allowed to invite member.");
//        }

        User invitee = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResourceNotFoundException("User Name", request.username()));

        if (invitee.getId().equals(userId)) {
            throw new RuntimeException("Cannot invite yourself.");
        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, invitee.getId());

        if (projectMemberRepository.existsById(projectMemberId)) {
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
    public MemberResponse updateMemberRole(UpdateMemberRoleRequest request, Long memberId, Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId);

//        if(!project.getOwner().getId().equals(userId)) {
//            throw new RuntimeException("Not allowed to invite member.");
//        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);

        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project ID and Member ID", projectId + " and " + userId));

        projectMember.setProjectRole(request.role());

        projectMemberRepository.save(projectMember);

        return projectMemberMapper.toProjectMemberResponseFromProjectMember(projectMember);

    }

    @Override
    public void removeProjectMember(Long memberId, Long projectId) {

        Project project = getAccessibleProjectById(projectId);

//        if(!project.getOwner().getId().equals(userId)) {
//            throw new RuntimeException("Not allowed to invite member.");
//        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);

        if (!projectMemberRepository.existsById(projectMemberId)) {
            throw new RuntimeException("Member does not exist.");
        }

        projectMemberRepository.deleteById(projectMemberId);
    }

    public Project getAccessibleProjectById(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        return projectRepository.findAccessibleProjectById(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project and User", projectId + " and " + userId));
    }
}
