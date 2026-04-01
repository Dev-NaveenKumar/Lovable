package com.projects.lovable.entity;

import com.projects.lovable.enums.PreviewStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class Preview {

    private Long id;

    private Project project;

    private PreviewStatus previewStatus;

    private String namespace;
    private String podName;
    private String previewUrl;

    private Instant startedAt;
    private Instant terminatedAt;

    private Instant createdAt;
}
