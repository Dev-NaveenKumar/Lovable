package com.projects.lovable.controller;

import com.projects.lovable.dto.project.ProjectRequest;
import com.projects.lovable.dto.project.ProjectResponse;
import com.projects.lovable.dto.project.ProjectSummaryResponse;
import com.projects.lovable.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectSummaryResponse>> getMyProjects() {
        Long userId=1L;
        return ResponseEntity.ok(projectService.getUserProjects(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id){
        Long userId=1L;
        return ResponseEntity.ok(projectService.getUserProjectById(userId,id));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectRequest request){
        Long userId=1L;
        return ResponseEntity.ok(projectService.getCreateProject(request,userId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id,
                                                         @RequestBody @Valid ProjectRequest request){
        Long userId=1L;
        return ResponseEntity.ok(projectService.updateProject(id,userId,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id){
        Long userId=1L;
        projectService.softDeleteProject(id,userId);
        return ResponseEntity.noContent().build();
    }
}

