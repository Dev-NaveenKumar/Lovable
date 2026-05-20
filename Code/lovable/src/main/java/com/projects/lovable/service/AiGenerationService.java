package com.projects.lovable.service;

import aj.org.objectweb.asm.commons.Remapper;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

public interface AiGenerationService {
    Flux<String> streamResponse(String message, Long aLong);
}
