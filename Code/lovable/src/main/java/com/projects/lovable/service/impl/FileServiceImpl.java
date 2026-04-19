package com.projects.lovable.service.impl;

import com.projects.lovable.dto.project.FileContentResponse;
import com.projects.lovable.dto.project.FileNode;
import com.projects.lovable.service.FileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public List<FileNode> getFileTree(Long projectId) {
        return List.of();
    }

    @Override
    public FileContentResponse getFile(Long projectId, String path) {
        return null;
    }
}
