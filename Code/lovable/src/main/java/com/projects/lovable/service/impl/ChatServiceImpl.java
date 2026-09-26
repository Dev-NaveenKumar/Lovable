package com.projects.lovable.service.impl;

import com.projects.lovable.dto.chat.ChatResponse;
import com.projects.lovable.entity.ChatMessage;
import com.projects.lovable.entity.ChatSession;
import com.projects.lovable.entity.ChatSessionId;
import com.projects.lovable.mapper.ChatMapper;
import com.projects.lovable.repository.ChatMessageRepository;
import com.projects.lovable.repository.ChatSessionRepository;
import com.projects.lovable.security.AuthUtil;
import com.projects.lovable.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private ChatMessageRepository chatMessageRepository;
    private AuthUtil authUtil;
    private ChatSessionRepository chatSessionRepository;
    private ChatMapper chatMapper;

    @Override
    public List<ChatResponse> getProjectChatHistory(Long projectId) {
        Long userId = authUtil.getCurrentUserId();

        ChatSession chatSession = chatSessionRepository.getReferenceById(
                new ChatSessionId(projectId,userId)
        );

        List<ChatMessage> chatMessages = chatMessageRepository.findByChatSession(chatSession);
        return chatMapper.fromListOfChatMessages(chatMessages);
    }
}
