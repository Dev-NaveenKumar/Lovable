package com.projects.lovable.service.impl;

import com.projects.lovable.entity.Project;
import com.projects.lovable.entity.ProjectFile;
import com.projects.lovable.error.ResourceNotFoundException;
import com.projects.lovable.repository.ProjectFileRepository;
import com.projects.lovable.repository.ProjectRepository;
import com.projects.lovable.service.ProjectService;
import com.projects.lovable.service.ProjectTemplateService;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectTemplateServiceImpl implements ProjectTemplateService {

    private final MinioClient minioClient;
    private final ProjectRepository projectRepository;

    private final String TEMPLATE_BUCKET = "starter-projects";
    private final String TARGET_BUCKET = "lovable";
    private final String TEMPLATE_NAME = "react-vite-tailwind-daisyui-starter-1";
    private final ProjectFileRepository projectFileRepository;

    @Override
    public void initializeProjectFromTemplate(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(TEMPLATE_BUCKET)
                            .prefix(TEMPLATE_NAME + "/")
                            .recursive(true)
                            .build()
            );

            List<ProjectFile> filesToSave = new ArrayList<>(); //for metadata in progres db

            for (Result<Item> result : results) {
                Item item = result.get();
                String sourceKey = item.objectName();

                String cleanPath = sourceKey.replaceFirst(TEMPLATE_NAME + "/", "");
                String destKey = projectId + "/" + cleanPath;

                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(TARGET_BUCKET)
                                .object(destKey)
                                .source(
                                        CopySource.builder()
                                                .bucket(TEMPLATE_BUCKET)
                                                .object(sourceKey)
                                                .build()
                                )
                                .build()
                );

                ProjectFile projectFile = ProjectFile.builder()
                        .project(project)
                        .path(cleanPath)
                        .minIoObjectKey(destKey)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                filesToSave.add(projectFile);

            }

            projectFileRepository.saveAll(filesToSave);

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize the project from template",e);
        }
    }
}




















