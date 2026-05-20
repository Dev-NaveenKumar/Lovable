package com.projects.lovable.entity;

import com.projects.lovable.enums.MessageRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "chat_messages")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChatMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "project_id", referencedColumnName = "project_id",nullable = false),
            @JoinColumn(name = "user_id", referencedColumnName = "user_id",nullable = false)
    })
    private ChatSession chatSession;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageRole messageRole;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

//    private String toolCalls;//JSON Array of Tools Called

    private Integer tokensUsed=0;

    @CreationTimestamp
    private Instant cratedAt;

}
