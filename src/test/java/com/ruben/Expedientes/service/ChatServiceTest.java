package com.ruben.Expedientes.service;

import com.ruben.Expedientes.dto.ChatMessageDTO;
import com.ruben.Expedientes.dto.CreateChatMessageDTO;
import com.ruben.Expedientes.dto.UserConversationsDTO;
import com.ruben.Expedientes.model.ChatMessage;
import com.ruben.Expedientes.model.User;
import com.ruben.Expedientes.repository.ChatMessageRepository;
import com.ruben.Expedientes.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChatServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WebSocketNotificationService notificationService;

    @InjectMocks
    private ChatService chatService;

    private User senderUser;
    private User receiverUser;
    private ChatMessage chatMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        senderUser = new User();
        senderUser.setId(1L);
        senderUser.setUsername("sender");

        receiverUser = new User();
        receiverUser.setId(2L);
        receiverUser.setUsername("receiver");

        chatMessage = ChatMessage.builder()
                .id(1L)
                .content("Test message")
                .sender(senderUser)
                .receiver(receiverUser)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .messageType(ChatMessage.MessageType.TEXT)
                .build();
    }

    @Test
    void testSendMessage() {
        // Given
        CreateChatMessageDTO messageDTO = CreateChatMessageDTO.builder()
                .content("Test message")
                .receiverId(2L)
                .messageType(ChatMessage.MessageType.TEXT)
                .build();

        when(userRepository.findByUsername("sender")).thenReturn(senderUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiverUser));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(chatMessage);

        // When
        ChatMessageDTO result = chatService.sendMessage("sender", messageDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test message", result.getContent());
        assertEquals(1L, result.getSenderId());
        assertEquals(2L, result.getReceiverId());
        assertEquals("sender", result.getSenderUsername());
        assertEquals("receiver", result.getReceiverUsername());
        assertFalse(result.getIsRead());

        verify(chatMessageRepository).save(any(ChatMessage.class));
        verify(notificationService).sendToUser(eq("receiver"), eq("/topic/chat"), any());
    }

    @Test
    void testGetUserConversations() {
        // Given
        when(userRepository.findByUsername("sender")).thenReturn(senderUser);
        when(chatMessageRepository.findUserConversations(1L)).thenReturn(Arrays.asList(chatMessage));
        when(chatMessageRepository.countUnreadMessages(senderUser)).thenReturn(5L);
        when(chatMessageRepository.countUnreadMessagesBetweenUsers(senderUser, receiverUser)).thenReturn(2L);

        // When
        UserConversationsDTO result = chatService.getUserConversations("sender");

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("sender", result.getUsername());
        assertEquals(5L, result.getTotalUnreadMessages());
        assertEquals(1, result.getConversations().size());
        
        var conversation = result.getConversations().get(0);
        assertEquals(2L, conversation.getOtherUserId());
        assertEquals("receiver", conversation.getOtherUsername());
        assertEquals(2L, conversation.getUnreadCount());
    }

    @Test
    void testSendMessageToSameUser() {
        // Given
        CreateChatMessageDTO messageDTO = CreateChatMessageDTO.builder()
                .content("Test message")
                .receiverId(1L) // Same as sender
                .messageType(ChatMessage.MessageType.TEXT)
                .build();

        when(userRepository.findByUsername("sender")).thenReturn(senderUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(senderUser));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            chatService.sendMessage("sender", messageDTO);
        });
    }

    @Test
    void testMarkMessagesAsRead() {
        // Given
        when(userRepository.findByUsername("receiver")).thenReturn(receiverUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(senderUser));
        when(chatMessageRepository.markMessagesAsReadBetweenUsers(any(), any(), any())).thenReturn(3);

        // When
        chatService.markMessagesAsRead("receiver", 1L);

        // Then
        verify(chatMessageRepository).markMessagesAsReadBetweenUsers(eq(receiverUser), eq(senderUser), any(LocalDateTime.class));
        verify(notificationService).sendToUser(eq("sender"), eq("/topic/chat"), any());
    }

    @Test
    void testDeleteMessage() {
        // Given
        when(userRepository.findByUsername("sender")).thenReturn(senderUser);
        when(chatMessageRepository.softDeleteMessage(eq(1L), eq(senderUser), any(LocalDateTime.class))).thenReturn(1);

        // When
        boolean result = chatService.deleteMessage("sender", 1L);

        // Then
        assertTrue(result);
        verify(chatMessageRepository).softDeleteMessage(eq(1L), eq(senderUser), any(LocalDateTime.class));
        verify(notificationService).broadcast(eq("/topic/chat/messages/deleted"), any());
    }
}
