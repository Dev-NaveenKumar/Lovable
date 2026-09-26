package com.projects.lovable.repository;

import com.projects.lovable.entity.ChatSession;
import com.projects.lovable.entity.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {
}
