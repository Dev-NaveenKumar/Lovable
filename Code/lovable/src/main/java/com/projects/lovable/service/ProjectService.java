package com.projects.lovable.service;

import com.projects.lovable.dto.project.ProjectRequest;
import com.projects.lovable.dto.project.ProjectResponse;
import com.projects.lovable.dto.project.ProjectSummaryResponse;

import java.util.List;

public interface ProjectService {

    List<ProjectSummaryResponse> getUserProjects();

    ProjectResponse getUserProjectById(Long projectId);

    ProjectResponse getCreateProject(ProjectRequest request);

    ProjectResponse updateProject(Long id, ProjectRequest request);

    void softDeleteProject(Long id);
}
