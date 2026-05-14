package com.chatbot.repository;

import com.chatbot.model.entity.Chat;
import com.chatbot.model.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ChatRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Chat> chatRowMapper = (rs, rowNum) -> {
        User user = User.builder().id(rs.getLong("user_id")).build();
        return Chat.builder()
                .id(rs.getLong("id"))
                .user(user)
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    };

    public ChatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Chat save(Chat chat) {
        if (chat.getId() == null) {
            String sql = "INSERT INTO chats (user_id, title, description, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, chat.getUser().getId());
                ps.setString(2, chat.getTitle());
                ps.setString(3, chat.getDescription());
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key != null) {
                return findById(key.longValue()).orElse(chat);
            }
            return chat;
        }

        String sql = "UPDATE chats SET title = ?, description = ?, updated_at = NOW() WHERE id = ?";
        jdbcTemplate.update(sql, chat.getTitle(), chat.getDescription(), chat.getId());
        return findById(chat.getId()).orElse(chat);
    }

    public List<Chat> findByUserOrderByCreatedAtDesc(User user) {
        String sql = "SELECT id, user_id, title, description, created_at, updated_at FROM chats WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, chatRowMapper, user.getId());
    }

    public Optional<Chat> findByIdAndUser(Long id, User user) {
        String sql = "SELECT id, user_id, title, description, created_at, updated_at FROM chats WHERE id = ? AND user_id = ?";
        return jdbcTemplate.query(sql, chatRowMapper, id, user.getId()).stream().findFirst();
    }

    public void delete(Chat chat) {
        jdbcTemplate.update("DELETE FROM chats WHERE id = ?", chat.getId());
    }

    private Optional<Chat> findById(Long id) {
        String sql = "SELECT id, user_id, title, description, created_at, updated_at FROM chats WHERE id = ?";
        return jdbcTemplate.query(sql, chatRowMapper, id).stream().findFirst();
    }
}
