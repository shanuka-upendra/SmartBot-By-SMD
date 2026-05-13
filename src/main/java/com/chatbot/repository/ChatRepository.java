package com.chatbot.repository;

import com.chatbot.model.entity.Chat;
import com.chatbot.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByUserOrderByCreatedAtDesc(User user);
    Optional<Chat> findByIdAndUser(Long id, User user);
}
