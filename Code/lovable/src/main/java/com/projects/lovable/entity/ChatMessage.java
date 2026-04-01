package com.projects.lovable.entity;

import com.projects.lovable.enums.MessageRole;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ChatMessage {

    private Long id;

    private ChatSession chatSession;

    private String content;

    private String toolCalls;//JSON Array of Tools Called

    private Integer tokensUsed;

    private Instant cratedAt;

    private MessageRole messageRole;
}
