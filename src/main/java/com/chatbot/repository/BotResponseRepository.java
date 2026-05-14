package com.chatbot.repository;

import com.chatbot.model.entity.BotResponse;
import com.chatbot.model.entity.Message;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class BotResponseRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<BotResponse> rowMapper = (rs, rowNum) -> {
        Message message = Message.builder().id(rs.getLong("message_id")).build();
        return BotResponse.builder()
                .id(rs.getLong("id"))
                .message(message)
                .responseText(rs.getString("response_text"))
                .confidenceScore(rs.getObject("confidence_score", Double.class))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    };

    public BotResponseRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<BotResponse> findByMessageId(Long messageId) {
        String sql = "SELECT id, message_id, response_text, confidence_score, created_at FROM bot_responses WHERE message_id = ?";
        return jdbcTemplate.query(sql, rowMapper, messageId).stream().findFirst();
    }
}
