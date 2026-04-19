package com.projects.lovable.controller;

import com.projects.lovable.dto.project.FileContentResponse;
import com.projects.lovable.dto.project.FileNode;
import com.projects.lovable.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/project/{projectId}/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping
    public ResponseEntity<List<FileNode>> getFiles(@PathVariable("projectId") Long projectId) {
        Long userId=1L;
        return ResponseEntity.ok(fileService.getFileTree(projectId));
    }

    @GetMapping("/{*path}")// * to get path as "/src/resource/java"
    public ResponseEntity<FileContentResponse> getFile(
            @PathVariable Long projectId,
            @PathVariable String path){
        Long  userId=1L;
        return ResponseEntity.ok(fileService.getFile(projectId,path));
    }
}
