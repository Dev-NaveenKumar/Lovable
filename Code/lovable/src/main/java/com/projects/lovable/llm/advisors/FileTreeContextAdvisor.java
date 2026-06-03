package com.projects.lovable.llm.advisors;

import com.projects.lovable.dto.project.FileNode;
import com.projects.lovable.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class FileTreeContextAdvisor implements StreamAdvisor {

    private final ProjectFileService projectFileService;

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain streamAdvisorChain) {
        Map<String, Object> context = request.context();

        Long projectId = Long.parseLong(context.getOrDefault("projectId", 0L).toString());
        ChatClientRequest augmentedChatClientRequest = augmentRequestWithFileTree(request, projectId);
        return streamAdvisorChain.nextStream(augmentedChatClientRequest);
    }

    private ChatClientRequest augmentRequestWithFileTree(ChatClientRequest request, Long projectId) {
        List<Message> incomingMessages = request.prompt().getInstructions();

        Message systemMessage = incomingMessages.stream()
                .filter(m-> m.getMessageType()== MessageType.SYSTEM)
                .findFirst()
                .orElse(null);
        //take out the system message

        List<Message> userMessages = incomingMessages.stream()
                .filter(m-> m.getMessageType()== MessageType.USER)
                .toList();
        // take out user message
        List<Message> allMessages = new ArrayList<>();

        if(systemMessage != null) {
            allMessages.add(systemMessage);
        }
        //take a empty list, then first put the system prompt

        List<FileNode> fileTree = projectFileService.getFileTree(projectId);
        String fileTreeContext = "\n\n ---- FILE_TREE ----\n"+ fileTree.toString();
        //put file tree context in the all messages
        allMessages.add(new SystemMessage(fileTreeContext));

        //then add the user messages so that llm caching can work properly
        allMessages.addAll(userMessages);

        //SYSTEM_PROMPT + FILE_TREE_CONTEXT + USER_PROMPT
        return request.mutate()
                .prompt(new Prompt(allMessages,request.prompt().getOptions()))
                .build();
    }

    @Override
    public String getName() {
        return "FileTreeContextAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
