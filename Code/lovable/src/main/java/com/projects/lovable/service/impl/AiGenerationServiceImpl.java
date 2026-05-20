package com.projects.lovable.service.impl;

import com.projects.lovable.service.AiGenerationService;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Server
@RequiredArgsConstructor
public class AiGenerationServiceImpl implements AiGenerationService {

    @Override
    public Flux<String> streamResponse(String message, Long aLong) {
        return null;
    }
}
