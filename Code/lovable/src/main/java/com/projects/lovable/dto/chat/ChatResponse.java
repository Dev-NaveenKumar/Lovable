package com.projects.lovable.dto.chat;

import com.projects.lovable.entity.ChatEvent;
import com.projects.lovable.entity.ChatSession;
import com.projects.lovable.enums.MessageRole;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

public record ChatResponse(
        Long id,
        ChatSession chatSession,
        MessageRole messageRole,
        String content,
        Integer tokensUsed,
        Instant createdAt,
        List<ChatEvent>events

) {
}
