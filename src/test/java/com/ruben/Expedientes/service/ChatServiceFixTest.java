package com.ruben.Expedientes.service;

import com.ruben.Expedientes.dto.ChatStatisticsDTO;
import com.ruben.Expedientes.model.User;
import com.ruben.Expedientes.repository.ChatMessageRepository;
import com.ruben.Expedientes.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatServiceFixTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private WebSocketNotificationService notificationService;
    
    @InjectMocks
    private ChatService chatService;

    @Test
    public void testGetChatStatistics_WithNormalData() {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        
        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);
        when(chatMessageRepository.getMessageStatisticsForUser(mockUser))
            .thenReturn(new Object[]{5L, 3L, 2L}); // totalSent, totalReceived, totalRead
        when(chatMessageRepository.countUnreadMessages(mockUser)).thenReturn(1L);
        when(chatMessageRepository.findUsersInConversationWith(mockUser)).thenReturn(java.util.Arrays.asList(mockUser));
        when(chatMessageRepository.findRecentMessagesForUser(any(), any())).thenReturn(java.util.Arrays.asList());

        // Act
        ChatStatisticsDTO result = chatService.getChatStatistics("testuser");

        // Assert
        assertNotNull(result);
        assertEquals(5L, result.getTotalMessagesSent());
        assertEquals(3L, result.getTotalMessagesReceived());
        assertEquals(1L, result.getTotalUnreadMessages());
        assertEquals(1L, result.getTotalConversations());
    }

    @Test
    public void testGetChatStatistics_WithNullValues() {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        
        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);
        when(chatMessageRepository.getMessageStatisticsForUser(mockUser))
            .thenReturn(new Object[]{null, null, null}); // valores nulos
        when(chatMessageRepository.countUnreadMessages(mockUser)).thenReturn(0L);
        when(chatMessageRepository.findUsersInConversationWith(mockUser)).thenReturn(java.util.Arrays.asList());
        when(chatMessageRepository.findRecentMessagesForUser(any(), any())).thenReturn(java.util.Arrays.asList());

        // Act
        ChatStatisticsDTO result = chatService.getChatStatistics("testuser");

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getTotalMessagesSent());
        assertEquals(0L, result.getTotalMessagesReceived());
        assertEquals(0L, result.getTotalUnreadMessages());
        assertEquals(0L, result.getTotalConversations());
    }

    @Test
    public void testGetChatStatistics_WithException() {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        
        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);
        when(chatMessageRepository.getMessageStatisticsForUser(mockUser))
            .thenThrow(new RuntimeException("Simulated DB error"));
        
        // Configurar métodos de respaldo
        when(chatMessageRepository.countMessagesSentByUser(mockUser)).thenReturn(2L);
        when(chatMessageRepository.countMessagesReceivedByUser(mockUser)).thenReturn(1L);
        when(chatMessageRepository.countMessagesReadByUser(mockUser)).thenReturn(1L);
        when(chatMessageRepository.countUnreadMessages(mockUser)).thenReturn(0L);
        when(chatMessageRepository.findUsersInConversationWith(mockUser)).thenReturn(java.util.Arrays.asList());
        when(chatMessageRepository.findRecentMessagesForUser(any(), any())).thenReturn(java.util.Arrays.asList());

        // Act
        ChatStatisticsDTO result = chatService.getChatStatistics("testuser");

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotalMessagesSent());
        assertEquals(1L, result.getTotalMessagesReceived());
        assertEquals(0L, result.getTotalUnreadMessages());
        assertEquals(0L, result.getTotalConversations());
        
        // Verificar que se llamaron los métodos de respaldo
        verify(chatMessageRepository).countMessagesSentByUser(mockUser);
        verify(chatMessageRepository).countMessagesReceivedByUser(mockUser);
        verify(chatMessageRepository).countMessagesReadByUser(mockUser);
    }

    @Test
    public void testGetChatStatistics_WithArrayInsteadOfNumber() {
        // Arrange - Simular el error original donde se devuelve un array
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        
        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);
        
        // Simular que se devuelve un array de arrays (el problema original)
        Object[] problematicResult = new Object[]{
            new Object[]{"unexpected", "array"}, // Esto causaría el ClassCastException original
            5L,
            3L
        };
        when(chatMessageRepository.getMessageStatisticsForUser(mockUser))
            .thenReturn(problematicResult);
            
        when(chatMessageRepository.countUnreadMessages(mockUser)).thenReturn(1L);
        when(chatMessageRepository.findUsersInConversationWith(mockUser)).thenReturn(java.util.Arrays.asList());
        when(chatMessageRepository.findRecentMessagesForUser(any(), any())).thenReturn(java.util.Arrays.asList());

        // Act - Debería manejar el error sin lanzar excepción
        ChatStatisticsDTO result = chatService.getChatStatistics("testuser");

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getTotalMessagesSent()); // Se convierte a 0 por el array
        assertEquals(5L, result.getTotalMessagesReceived()); // Este valor es válido
        assertEquals(1L, result.getTotalUnreadMessages());
    }
}
