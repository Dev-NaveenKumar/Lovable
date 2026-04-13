package com.projects.lovable.mapper;

import com.projects.lovable.dto.project.ProjectResponse;
import com.projects.lovable.entity.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);
}
