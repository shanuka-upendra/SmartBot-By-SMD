package com.chatbot.repository;

import com.chatbot.model.entity.BotResponse;
import com.chatbot.model.entity.Message;
import com.chatbot.model.entity.Chat;
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
public class MessageRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Message> messageRowMapper = (rs, rowNum) -> {
        Chat chat = Chat.builder().id(rs.getLong("chat_id")).build();
        Message message = Message.builder()
                .id(rs.getLong("id"))
                .chat(chat)
                .senderType(Message.SenderType.valueOf(rs.getString("sender_type")))
                .content(rs.getString("content"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();

        Long botResponseId = rs.getObject("bot_response_id", Long.class);
        if (botResponseId != null) {
            BotResponse botResponse = BotResponse.builder()
                    .id(botResponseId)
                    .responseText(rs.getString("response_text"))
                    .confidenceScore(rs.getObject("confidence_score", Double.class))
                    .createdAt(rs.getTimestamp("bot_created_at").toLocalDateTime())
                    .build();
            message.setBotResponse(botResponse);
        }

        return message;
    };

    public MessageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Message save(Message message) {
        if (message.getId() == null) {
            String sql = "INSERT INTO messages (chat_id, sender_type, content, created_at) VALUES (?, ?, ?, NOW())";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, message.getChat().getId());
                ps.setString(2, message.getSenderType().name());
                ps.setString(3, message.getContent());
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key != null) {
                return findById(key.longValue()).orElse(message);
            }
            return message;
        }

        String sql = "UPDATE messages SET content = ? WHERE id = ?";
        jdbcTemplate.update(sql, message.getContent(), message.getId());
        return findById(message.getId()).orElse(message);
    }

    public List<Message> findByChatOrderByCreatedAtAsc(Chat chat) {
        String sql = """
                SELECT m.id, m.chat_id, m.sender_type, m.content, m.created_at,
                       br.id AS bot_response_id, br.response_text, br.confidence_score, br.created_at AS bot_created_at
                FROM messages m
                LEFT JOIN bot_responses br ON br.message_id = m.id
                WHERE m.chat_id = ?
                ORDER BY m.created_at ASC
                """;
        return jdbcTemplate.query(sql, messageRowMapper, chat.getId());
    }

    public List<Message> findByChatId(Long chatId) {
        String sql = """
                SELECT m.id, m.chat_id, m.sender_type, m.content, m.created_at,
                       br.id AS bot_response_id, br.response_text, br.confidence_score, br.created_at AS bot_created_at
                FROM messages m
                LEFT JOIN bot_responses br ON br.message_id = m.id
                WHERE m.chat_id = ?
                ORDER BY m.created_at ASC
                """;
        return jdbcTemplate.query(sql, messageRowMapper, chatId);
    }

    public int countByChatId(Long chatId) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM messages WHERE chat_id = ?", Integer.class, chatId);
        return count == null ? 0 : count;
    }

    private Optional<Message> findById(Long id) {
        String sql = """
                SELECT m.id, m.chat_id, m.sender_type, m.content, m.created_at,
                       br.id AS bot_response_id, br.response_text, br.confidence_score, br.created_at AS bot_created_at
                FROM messages m
                LEFT JOIN bot_responses br ON br.message_id = m.id
                WHERE m.id = ?
                """;
        return jdbcTemplate.query(sql, messageRowMapper, id).stream().findFirst();
    }
}
