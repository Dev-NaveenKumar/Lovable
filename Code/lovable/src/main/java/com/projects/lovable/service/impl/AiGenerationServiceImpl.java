package com.projects.lovable.service.impl;

import com.projects.lovable.entity.*;
import com.projects.lovable.enums.MessageRole;
import com.projects.lovable.error.ResourceNotFoundException;
import com.projects.lovable.llm.LlmResponseParser;
import com.projects.lovable.llm.PromptUtils;
import com.projects.lovable.llm.advisors.FileTreeContextAdvisor;
import com.projects.lovable.llm.tools.CodeGenerationTools;
import com.projects.lovable.repository.ChatMessageRepository;
import com.projects.lovable.repository.ChatSessionRepository;
import com.projects.lovable.repository.ProjectRepository;
import com.projects.lovable.repository.UserRepository;
import com.projects.lovable.security.AuthUtil;
import com.projects.lovable.service.AiGenerationService;
import com.projects.lovable.service.ProjectFileService;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.stringtemplate.v4.compiler.CodeGenerator;
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
    private final FileTreeContextAdvisor fileTreeContextAdvisor;
    private final LlmResponseParser llmResponseParser;
    private final ChatSessionRepository chatSessionRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<String> streamResponse(String userMessage, Long projectId) {

        Long userId = authUtil.getCurrentUserId();
        ChatSession chatSession = createChatSessionIfNotExists(projectId, userId);

        Map<String, Object> advisorParams = new HashMap<>();
        advisorParams.put("projectId", projectId);
        advisorParams.put("userId", userId);

        StringBuilder fullContentBuffer = new StringBuilder();

        CodeGenerationTools codeGenerationTools = new CodeGenerationTools(projectFileService,projectId);
        return chatClient.prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
                .user(userMessage)
                .tools(codeGenerationTools)
                .advisors(advisorSpec -> {
                    advisorSpec.params(advisorParams);
                    advisorSpec.advisors(fileTreeContextAdvisor);
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
//                        parseAndSaveFiles(fullContentBuffer.toString(), projectId);
                        finalizeChats(userMessage,chatSession,fullContentBuffer.toString(),projectId);
                    });
                })
                .doOnError(error -> log.error("Error during streaming for projectId: {}", projectId))
                .map(response -> Objects.requireNonNull(response.getResult().getOutput().getText()));
    }

    private void finalizeChats(String userMessage, ChatSession chatSession, String fullText, Long projectId) {
        chatMessageRepository.save(
                ChatMessage.builder()
                        .chatSession(chatSession)
                        .messageRole(MessageRole.USER)
                        .content(userMessage)
                        .build()
        );
        ChatMessage assistedChatMessage = ChatMessage.builder()
                .messageRole(MessageRole.ASSISTANT)
                .chatSession(chatSession)
                .build();
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

    private ChatSession createChatSessionIfNotExists(Long projectId, Long userId) {
        ChatSessionId chatSessionId = new ChatSessionId(projectId, userId);
        ChatSession chatSession = chatSessionRepository.findById(chatSessionId).orElse(null);
        if (chatSession == null) {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(()->new ResourceNotFoundException("Project",projectId));
            User user = userRepository.findById(userId)
                    .orElseThrow(()->new ResourceNotFoundException("User",userId));

            chatSession = ChatSession.builder()
                    .id(chatSessionId)
                    .user(user)
                    .project(project)
                    .build();
            chatSession = chatSessionRepository.save(chatSession);
        }
        return chatSession;
    }
}
