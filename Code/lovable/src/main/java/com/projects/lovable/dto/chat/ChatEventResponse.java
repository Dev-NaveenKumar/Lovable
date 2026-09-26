package com.projects.lovable.dto.chat;

import com.projects.lovable.entity.ChatMessage;
import com.projects.lovable.enums.ChatEventType;
import jakarta.persistence.*;

public record ChatEventResponse(
        Long id,
        String content,
        Integer sequenceOrder,
        String filePath,
        ChatEventType type,
        String metadata
) {
}
