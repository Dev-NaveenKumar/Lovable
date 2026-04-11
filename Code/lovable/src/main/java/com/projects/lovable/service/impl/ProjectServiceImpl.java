package com.projects.lovable.service.impl;

import com.projects.lovable.dto.project.ProjectRequest;
import com.projects.lovable.dto.project.ProjectResponse;
import com.projects.lovable.dto.project.ProjectSummaryResponse;
import com.projects.lovable.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    @Override
    public List<ProjectSummaryResponse> getUserProjects(Long userId) {
        return List.of();
    }

    @Override
    public ProjectResponse getUserProject(Long userId) {
        return null;
    }

    @Override
    public ProjectResponse getCreateProject(ProjectRequest request, Long userId) {
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
