package com.chatbot.controller;

import com.chatbot.model.dto.*;
import com.chatbot.model.entity.User;
import com.chatbot.service.AuthService;
import com.chatbot.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chats")
@Tag(name = "Chat", description = "Chat management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private AuthService authService;

    @PostMapping
    @Operation(summary = "Create a new chat", description = "Create a new chat session for the authenticated user")
    public ResponseEntity<ApiResponse<ChatResponse>> createChat(@Valid @RequestBody CreateChatRequest request) {
        User user = getCurrentUser();
        log.info("Creating chat for user: {}", user.getUsername());
        ChatResponse response = chatService.createChat(request, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Chat created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all chats", description = "Retrieve all chats for the authenticated user")
    public ResponseEntity<ApiResponse<List<ChatResponse>>> getAllChats() {
        User user = getCurrentUser();
        log.info("Fetching all chats for user: {}", user.getUsername());
        List<ChatResponse> chats = chatService.getUserChats(user);
        return ResponseEntity.ok(ApiResponse.success("Chats retrieved successfully", chats));
    }

    @GetMapping("/{chatId}")
    @Operation(summary = "Get chat by ID", description = "Retrieve a specific chat by its ID")
    public ResponseEntity<ApiResponse<ChatResponse>> getChat(@PathVariable Long chatId) {
        User user = getCurrentUser();
        log.info("Fetching chat: {} for user: {}", chatId, user.getUsername());
        ChatResponse response = chatService.getChat(chatId, user);
        return ResponseEntity.ok(ApiResponse.success("Chat retrieved successfully", response));
    }

    @DeleteMapping("/{chatId}")
    @Operation(summary = "Delete chat", description = "Delete a chat and all its messages")
    public ResponseEntity<ApiResponse<?>> deleteChat(@PathVariable Long chatId) {
        User user = getCurrentUser();
        log.info("Deleting chat: {} for user: {}", chatId, user.getUsername());
        chatService.deleteChat(chatId, user);
        return ResponseEntity.ok(ApiResponse.success("Chat deleted successfully", null));
    }

    @PostMapping("/{chatId}/messages")
    @Operation(summary = "Send message", description = "Send a message to a specific chat")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @PathVariable Long chatId,
            @Valid @RequestBody SendMessageRequest request) {
        User user = getCurrentUser();
        log.info("Sending message to chat: {} from user: {}", chatId, user.getUsername());
        MessageResponse response = chatService.sendMessage(chatId, request, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Message sent successfully", response));
    }

    @GetMapping("/{chatId}/messages")
    @Operation(summary = "Get chat messages", description = "Retrieve all messages in a specific chat")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getChatMessages(@PathVariable Long chatId) {
        User user = getCurrentUser();
        log.info("Fetching messages for chat: {}", chatId);
        List<MessageResponse> messages = chatService.getChatMessages(chatId, user);
        return ResponseEntity.ok(ApiResponse.success("Messages retrieved successfully", messages));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return authService.getUserByUsername(username);
    }
}
