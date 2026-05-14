package com.chatbot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;

    private String username;

    private String email;

    private String passwordHash;

    private String fullName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<Chat> chats;

    private List<Conversation> conversations;
}
