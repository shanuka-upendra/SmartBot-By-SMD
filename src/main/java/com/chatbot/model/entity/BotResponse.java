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
public class BotResponse {
    private Long id;

    private Message message;

    private String responseText;

    private Double confidenceScore;

    private LocalDateTime createdAt;
}
