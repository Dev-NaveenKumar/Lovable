package com.projects.lovable.service.impl;

import com.projects.lovable.dto.project.ProjectRequest;
import com.projects.lovable.dto.project.ProjectResponse;
import com.projects.lovable.dto.project.ProjectSummaryResponse;
import com.projects.lovable.entity.Project;
import com.projects.lovable.entity.User;
import com.projects.lovable.mapper.ProjectMapper;
import com.projects.lovable.repository.ProjectRepository;
import com.projects.lovable.repository.UserRepository;
import com.projects.lovable.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return List.of();
    }

    @Override
    public ProjectResponse getUserProject(Long userId) {
        return null;
    }


    @Override
    public ProjectService updateProject(Long id, Long userId, ProjectRequest request) {
        return null;
    }

    @Override
    public void softDeleteProject(Long id, Long userId) {

    }
}
