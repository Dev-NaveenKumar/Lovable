package com.projects.lovable.service.impl;

import com.projects.lovable.dto.project.FileContentResponse;
import com.projects.lovable.dto.project.FileNode;
import com.projects.lovable.service.ProjectFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProjectFileServiceImpl implements ProjectFileService {

    @Override
    public List<FileNode> getFileTree(Long projectId) {
        return List.of();
    }

    @Override
    public FileContentResponse getFile(Long projectId, String path) {
        return null;
    }

    @Override
    public void saveFile(Long projectId, String filePath, String fileContent) {
      log.info("Saving file: {}", filePath);
      //Save metadata in the postgres
      //Save the content inside minio
    }
}
