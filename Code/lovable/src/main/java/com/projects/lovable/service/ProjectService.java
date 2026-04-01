package com.projects.lovable.service;

import com.projects.lovable.dto.project.ProjectRequest;
import com.projects.lovable.dto.project.ProjectResponse;
import com.projects.lovable.dto.project.ProjectSummaryResponse;

import java.util.List;

public interface ProjectService {

    List<ProjectSummaryResponse> getUserProjects(Long userId);

    ProjectResponse getUserProject(Long userId);

    ProjectResponse getCreateProject(ProjectRequest request, Long userId);

    ProjectService updateProject(Long id, Long userId, ProjectRequest request);

    void softDeleteProject(Long id, Long userId);
}
