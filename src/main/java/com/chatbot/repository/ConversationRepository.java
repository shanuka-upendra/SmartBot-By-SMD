package com.chatbot.repository;

import com.chatbot.model.entity.Conversation;
import com.chatbot.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByUserAndIsActiveTrue(User user);
    Optional<Conversation> findBySessionToken(String sessionToken);
    List<Conversation> findByUser(User user);
}
