package com.projects.lovable.service.impl;

import com.projects.lovable.dto.project.ProjectRequest;
import com.projects.lovable.dto.project.ProjectResponse;
import com.projects.lovable.dto.project.ProjectSummaryResponse;
import com.projects.lovable.entity.Project;
import com.projects.lovable.entity.ProjectMember;
import com.projects.lovable.entity.ProjectMemberId;
import com.projects.lovable.entity.User;
import com.projects.lovable.enums.ProjectRole;
import com.projects.lovable.error.ResourceNotFoundException;
import com.projects.lovable.mapper.ProjectMapper;
import com.projects.lovable.repository.ProjectMemberRepository;
import com.projects.lovable.repository.ProjectRepository;
import com.projects.lovable.repository.UserRepository;
import com.projects.lovable.security.AuthUtil;
import com.projects.lovable.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final ProjectMemberRepository projectMemberRepository;
    private final AuthUtil authUtil;

    @Override
    public ProjectResponse getCreateProject(ProjectRequest request) {
        Long userId = authUtil.getCurrentUserId();
//        User owner = userRepository.findById(userId)
//                .orElseThrow(()->new ResourceNotFoundException("User",userId));
        User owner = userRepository.getReferenceById(userId);
        // getReferenceById just puts the proxy of the object,
        // and there's only the id not whole object
        // when we call owner.getName(), then it make a db call and fetch

        Project project = Project.builder()
                .name(request.name())
                .isPublic(false)
                .build();
        Project createdProject = projectRepository.save(project);

        ProjectMemberId  projectMemberId = new ProjectMemberId(project.getId(),owner.getId());
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .projectRole(ProjectRole.OWNER)
                //we only need owner id only
                // that is why getReferenceById is used
                .user(owner)
                .project(createdProject)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .build();
        projectMemberRepository.save(projectMember);

        return projectMapper.toProjectResponse(createdProject);
    }

    @Override
    public List<ProjectSummaryResponse> getUserProjects() {
        Long userId = authUtil.getCurrentUserId();
//        return projectRepository.findAllAccessibleByUser(userId)
//                .stream()
//                .map(projectMapper::toProjectSummaryResponse)
//                .toList();
        return projectMapper.
                toProjectSummaryResponseList(projectRepository
                        .findAllAccessibleByUser(userId));
    }

    @Override
    // must add @EnableMethodSecurity on any Configuration class to set the AOP interceptors
    @PreAuthorize("@security.canViewProject(#projectId)")
    // Bypasses AOP proxy — @PreAuthorize won't trigger -- this.getUserProjectById(projectId);
    // Called from controller via injected bean — works fine -- projectService.getUserProjectById(projectId);
    public ProjectResponse getUserProjectById(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId);
        return projectMapper.toProjectResponse(project);
    }


    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId);
//        if(!project.getOwner().getId().equals(userId)){
//            throw new RuntimeException("You're not allowed to update this project.");
//        }
        project.setName(request.name());
//        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public void softDeleteProject(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId);
//        if(!project.getOwner().getId().equals(userId)){
//            throw new RuntimeException("You're not allowed to delete this project.");
//        }
        project.setDeletedAt(Instant.now());
//        projectRepository.save(project);
    }

    public Project getAccessibleProjectById(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        return projectRepository.findAccessibleProjectById(projectId, userId)
                .orElseThrow(()-> new ResourceNotFoundException("Project and User", projectId+" and "+userId));
    }
}
