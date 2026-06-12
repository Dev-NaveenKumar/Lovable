package com.projects.lovable.repository;

import com.projects.lovable.entity.ChatEvent;
import com.projects.lovable.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
