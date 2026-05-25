package com.projects.lovable.service;

import com.projects.lovable.dto.project.FileContentResponse;
import com.projects.lovable.dto.project.FileNode;

import java.util.List;

public interface ProjectFileService {
    List<FileNode> getFileTree(Long projectId);

    FileContentResponse getFile(Long projectId, String path);

    void saveFile(Long projectId, String filePath, String fileContent);
}
