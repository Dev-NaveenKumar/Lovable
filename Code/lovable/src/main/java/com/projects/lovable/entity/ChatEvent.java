package com.projects.lovable.entity;

import com.projects.lovable.enums.ChatEventType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_events")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChatEvent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ChatMessage chatMessage;

    @Column(columnDefinition = "text")
    private String content;

    @Column(nullable = false)
    private Integer sequenceOrder;

    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatEventType type;

    @Column(columnDefinition = "text")
    private String metadata;

}
