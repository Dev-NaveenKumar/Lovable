package com.projects.lovable.mapper;

import com.projects.lovable.dto.project.ProjectResponse;
import com.projects.lovable.dto.project.ProjectSummaryResponse;
import com.projects.lovable.entity.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);
    ProjectSummaryResponse toProjectSummaryResponse(Project project);
    List<ProjectSummaryResponse> toProjectSummaryResponseList(List<Project> projects);
}
