package com.projects.lovable.service.impl;

import com.projects.lovable.dto.project.ProjectRequest;
import com.projects.lovable.dto.project.ProjectResponse;
import com.projects.lovable.dto.project.ProjectSummaryResponse;
import com.projects.lovable.entity.Project;
import com.projects.lovable.entity.User;
import com.projects.lovable.error.ResourceNotFoundException;
import com.projects.lovable.mapper.ProjectMapper;
import com.projects.lovable.repository.ProjectRepository;
import com.projects.lovable.repository.UserRepository;
import com.projects.lovable.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    @Override
    public ProjectResponse getCreateProject(ProjectRequest request, Long userId) {

        User owner = userRepository.findById(userId).orElseThrow();

        Project project = Project.builder()
                .name(request.name())
                .owner(owner)
                .build();
        Project createdProject = projectRepository.save(project);

        return projectMapper.toProjectResponse(createdProject);
    }

    @Override
    public List<ProjectSummaryResponse> getUserProjects(Long userId) {
//        return projectRepository.findAllAccessibleByUser(userId)
//                .stream()
//                .map(projectMapper::toProjectSummaryResponse)
//                .toList();
        return projectMapper.
                toProjectSummaryResponseList(projectRepository
                        .findAllAccessibleByUser(userId));
    }

    @Override
    public ProjectResponse getUserProjectById(Long userId, Long projectId) {
        Project project = getAccessibleProjectById(userId, projectId);
        return projectMapper.toProjectResponse(project);
    }


    @Override
    public ProjectResponse updateProject(Long projectId, Long userId, ProjectRequest request) {
        Project project = getAccessibleProjectById(userId, projectId);
        if(!project.getOwner().getId().equals(userId)){
            throw new RuntimeException("You're not allowed to update this project.");
        }
        project.setName(request.name());
//        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public void softDeleteProject(Long projectId, Long userId) {
        Project project = getAccessibleProjectById(userId, projectId);
        if(!project.getOwner().getId().equals(userId)){
            throw new RuntimeException("You're not allowed to delete this project.");
        }
        project.setDeletedAt(Instant.now());
//        projectRepository.save(project);
    }

    public Project getAccessibleProjectById(Long userId, Long projectId) {
        return projectRepository.findAccessibleProjectById(projectId, userId)
                .orElseThrow(()-> new ResourceNotFoundException("Project and User", projectId+" and "+userId));
    }
}
