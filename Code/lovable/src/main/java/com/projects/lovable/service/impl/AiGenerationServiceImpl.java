package com.projects.lovable.service.impl;

import com.projects.lovable.llm.PromptUtils;
import com.projects.lovable.security.AuthUtil;
import com.projects.lovable.service.AiGenerationService;
import com.projects.lovable.service.ProjectFileService;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiGenerationServiceImpl implements AiGenerationService {

    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);
    private final ProjectFileService projectFileService;

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<String> streamResponse(String message, Long projectId) {

        Long userId = authUtil.getCurrentUserId();
        createChatSessionIfNotExists(projectId, userId);

        Map<String, Object> advisorParams = new HashMap<>();
        advisorParams.put("projectId", projectId);
        advisorParams.put("userId", userId);

        StringBuilder fullContentBuffer = new StringBuilder();

        return chatClient.prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
                .user(message)
                .advisors(advisorSpec -> {
                    advisorSpec.params(advisorParams);
                })
                .stream()
//                .content() //-- it only returns the content
                .chatResponse()//-- returns the whole object
                // where in it has the info how many tokens were used and more info
                .doOnNext(response -> {
                    String content = Objects.requireNonNull(response.getResult().getOutput().getText());
                    fullContentBuffer.append(content);
                })
                .doOnComplete(() -> {
                    Schedulers.boundedElastic().schedule(() -> {
                        parseAndSaveFiles(fullContentBuffer.toString(), projectId);
                    });
                })
                .doOnError(error -> log.error("Error during streaming for projectId: {}", projectId))
                .map(response -> Objects.requireNonNull(response.getResult().getOutput().getText()));
    }

    private void parseAndSaveFiles(String fullResponse, Long projectId) {
        String dummy = """
                <message>This is going to read the files and generate the code</message>
                <file path="src/App.jsx">
                    import App from './App.jsx'
                    .....
                </file>
                <message>This is going to read the files and generate the code</message>
                <file path="src/App.jsx">
                    import App from './App.jsx'
                    .....
                </file>
                """;

        Matcher matcher = FILE_TAG_PATTERN.matcher(fullResponse);

        while (matcher.find()) {
            String filePath = matcher.group(1);
            String fileContent = matcher.group(2).trim();

            projectFileService.saveFile(projectId, filePath, fileContent);
        }
    }

    private void createChatSessionIfNotExists(Long projectId, Long userId) {
    }
}
