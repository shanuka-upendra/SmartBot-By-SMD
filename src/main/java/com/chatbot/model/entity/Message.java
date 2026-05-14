package com.chatbot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private Long id;

    private Chat chat;

    private SenderType senderType;

    private String content;

    private LocalDateTime createdAt;

    private BotResponse botResponse;

    public enum SenderType {
        USER,
        BOT
    }
}
