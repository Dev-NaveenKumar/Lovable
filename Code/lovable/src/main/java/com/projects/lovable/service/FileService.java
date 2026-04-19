package com.projects.lovable.service;

import com.projects.lovable.dto.project.FileContentResponse;
import com.projects.lovable.dto.project.FileNode;
import org.springframework.stereotype.Service;

import java.util.List;

public interface FileService {
    List<FileNode> getFileTree(Long projectId);

    FileContentResponse getFile(Long projectId, String path);
}
