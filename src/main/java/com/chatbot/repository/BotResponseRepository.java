package com.chatbot.repository;

import com.chatbot.model.entity.BotResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BotResponseRepository extends JpaRepository<BotResponse, Long> {
    Optional<BotResponse> findByMessageId(Long messageId);
}
