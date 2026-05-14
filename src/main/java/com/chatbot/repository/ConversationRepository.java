package com.chatbot.repository;

import com.chatbot.model.entity.Chat;
import com.chatbot.model.entity.Conversation;
import com.chatbot.model.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ConversationRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Conversation> rowMapper = (rs, rowNum) -> {
        User user = User.builder().id(rs.getLong("user_id")).build();
        Long chatId = rs.getObject("chat_id", Long.class);
        Chat chat = chatId == null ? null : Chat.builder().id(chatId).build();
        return Conversation.builder()
                .id(rs.getLong("id"))
                .user(user)
                .chat(chat)
                .sessionToken(rs.getString("session_token"))
                .isActive(rs.getBoolean("is_active"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    };

    public ConversationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Conversation> findByUserAndIsActiveTrue(User user) {
        String sql = "SELECT id, user_id, chat_id, session_token, is_active, created_at, updated_at FROM conversations WHERE user_id = ? AND is_active = TRUE";
        return jdbcTemplate.query(sql, rowMapper, user.getId());
    }

    public Optional<Conversation> findBySessionToken(String sessionToken) {
        String sql = "SELECT id, user_id, chat_id, session_token, is_active, created_at, updated_at FROM conversations WHERE session_token = ?";
        return jdbcTemplate.query(sql, rowMapper, sessionToken).stream().findFirst();
    }

    public List<Conversation> findByUser(User user) {
        String sql = "SELECT id, user_id, chat_id, session_token, is_active, created_at, updated_at FROM conversations WHERE user_id = ?";
        return jdbcTemplate.query(sql, rowMapper, user.getId());
    }
}
