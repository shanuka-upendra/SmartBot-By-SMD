package com.chatbot.repository;

import com.chatbot.model.entity.Message;
import com.chatbot.model.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatOrderByCreatedAtAsc(Chat chat);
    List<Message> findByChatId(Long chatId);
}
