package com.chatbot.service;

import com.chatbot.exception.ResourceNotFoundException;
import com.chatbot.exception.UnauthorizedException;
import com.chatbot.model.dto.ChatResponse;
import com.chatbot.model.dto.CreateChatRequest;
import com.chatbot.model.dto.MessageResponse;
import com.chatbot.model.dto.SendMessageRequest;
import com.chatbot.model.entity.Chat;
import com.chatbot.model.entity.Message;
import com.chatbot.model.entity.User;
import com.chatbot.repository.ChatRepository;
import com.chatbot.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    public ChatResponse createChat(CreateChatRequest request, User user) {
        log.info("Creating chat for user: {}", user.getUsername());

        Chat chat = Chat.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        chat = chatRepository.save(chat);
        log.info("Chat created with id: {}", chat.getId());

        return mapToChatResponse(chat);
    }

    public List<ChatResponse> getUserChats(User user) {
        log.info("Fetching chats for user: {}", user.getUsername());
        List<Chat> chats = chatRepository.findByUserOrderByCreatedAtDesc(user);
        return chats.stream()
                .map(this::mapToChatResponse)
                .collect(Collectors.toList());
    }

    public ChatResponse getChat(Long chatId, User user) {
        Chat chat = getChatAndValidateOwner(chatId, user);
        return mapToChatResponse(chat);
    }

    public void deleteChat(Long chatId, User user) {
        log.info("Deleting chat: {} for user: {}", chatId, user.getUsername());
        Chat chat = getChatAndValidateOwner(chatId, user);
        chatRepository.delete(chat);
        log.info("Chat deleted successfully");
    }

    public MessageResponse sendMessage(Long chatId, SendMessageRequest request, User user) {
        log.info("Sending message to chat: {} from user: {}", chatId, user.getUsername());

        Chat chat = getChatAndValidateOwner(chatId, user);

        Message message = Message.builder()
                .chat(chat)
                .senderType(Message.SenderType.USER)
                .content(request.getContent())
                .build();

        message = messageRepository.save(message);
        log.info("Message saved with id: {}", message.getId());

        return mapToMessageResponse(message);
    }

    public List<MessageResponse> getChatMessages(Long chatId, User user) {
        log.info("Fetching messages for chat: {}", chatId);
        Chat chat = getChatAndValidateOwner(chatId, user);
        List<Message> messages = messageRepository.findByChatOrderByCreatedAtAsc(chat);
        return messages.stream()
                .map(this::mapToMessageResponse)
                .collect(Collectors.toList());
    }

    public MessageResponse sendBotMessage(Long chatId, String content, User user) {
        Chat chat = getChatAndValidateOwner(chatId, user);

        Message message = Message.builder()
                .chat(chat)
                .senderType(Message.SenderType.BOT)
                .content(content)
                .build();

        message = messageRepository.save(message);
        return mapToMessageResponse(message);
    }

    private Chat getChatAndValidateOwner(Long chatId, User user) {
        Chat chat = chatRepository.findByIdAndUser(chatId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found with id: " + chatId));
        return chat;
    }

    private ChatResponse mapToChatResponse(Chat chat) {
        int messageCount = messageRepository.countByChatId(chat.getId());
        return ChatResponse.builder()
                .id(chat.getId())
                .title(chat.getTitle())
                .description(chat.getDescription())
                .createdAt(chat.getCreatedAt())
                .updatedAt(chat.getUpdatedAt())
                .messageCount(messageCount)
                .build();
    }

    private MessageResponse mapToMessageResponse(Message message) {
        String botResponse = null;
        if (message.getBotResponse() != null) {
            botResponse = message.getBotResponse().getResponseText();
        }

        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderType(message.getSenderType().name())
                .createdAt(message.getCreatedAt())
                .botResponse(botResponse)
                .build();
    }
}
