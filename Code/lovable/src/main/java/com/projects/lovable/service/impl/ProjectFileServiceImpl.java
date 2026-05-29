package com.projects.lovable.service.impl;

import com.projects.lovable.dto.project.FileContentResponse;
import com.projects.lovable.dto.project.FileNode;
import com.projects.lovable.entity.Project;
import com.projects.lovable.entity.ProjectFile;
import com.projects.lovable.error.ResourceNotFoundException;
import com.projects.lovable.mapper.ProjectFileMapper;
import com.projects.lovable.repository.ProjectFileRepository;
import com.projects.lovable.repository.ProjectRepository;
import com.projects.lovable.service.ProjectFileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectFileServiceImpl implements ProjectFileService {

    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;
    private final MinioClient minioClient;
    private final ProjectFileMapper projectFileMapper;

    @Value("${minio.project-bucket}")
    private String projectBucketName;

    @Override
    public List<FileNode> getFileTree(Long projectId) {

        List<ProjectFile> projectFiles = projectFileRepository.findByProjectId(projectId);
        return projectFileMapper.toListOfFileNode(projectFiles);
    }

    @Override
    public FileContentResponse getFile(Long projectId, String path) {
        return null;
    }

    @Override
    public void saveFile(Long projectId, String filePath, String fileContent) {
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ResourceNotFoundException("Project", projectId)
        );

        String cleanPath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        String objectKey = projectId + "/" + cleanPath;

        try {
            byte[] contentByte = fileContent.getBytes(StandardCharsets.UTF_8);
            InputStream inputStream = new ByteArrayInputStream(contentByte);
            //Saving file content
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(projectBucketName)
                            .object(objectKey)
                            .stream(inputStream, contentByte.length, -1)
                            .contentType(determineContentType(filePath))
                            .build()
            );
            //Saving File metadata
            ProjectFile projectFile = projectFileRepository.findByProjectIdAndPath(projectId, cleanPath)
                    .orElseGet(() ->
                            ProjectFile.builder()
                                    .project(project)
                                    .path(cleanPath)
                                    .minIoObjectKey(objectKey)
                                    .createdAt(Instant.now())
                                    .build()
                    );

            projectFile.setUpdatedAt(Instant.now());

            projectFileRepository.save(projectFile);

            log.info("File saved: {}",objectKey);
        } catch (Exception e) {
            log.info("Failed to save file {}/{}",projectId,cleanPath,e);
            throw new RuntimeException("File save failed",e);
        }
    }

    private static String determineContentType(String path) {
        String type = URLConnection.guessContentTypeFromName(path);
        if (type != null) {
            return type;
        }
        if (path.endsWith(".ts") || path.endsWith(".jsx") || path.endsWith(".tsx")) return "text/javascript";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".css")) return "text/css";

        return "plain/text";
    }
}
