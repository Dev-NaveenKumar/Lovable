package com.projects.lovable.mapper;

import com.projects.lovable.dto.chat.ChatResponse;
import com.projects.lovable.entity.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    List<ChatResponse> fromListOfChatMessages(List<ChatMessage> messages);
}
