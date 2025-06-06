package com.ruben.Expedientes.service;

import com.ruben.Expedientes.dto.*;
import com.ruben.Expedientes.model.ChatMessage;
import com.ruben.Expedientes.model.User;
import com.ruben.Expedientes.repository.ChatMessageRepository;
import com.ruben.Expedientes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final WebSocketNotificationService notificationService;

    /**
     * Enviar un nuevo mensaje
     */
    public ChatMessageDTO sendMessage(String senderUsername, CreateChatMessageDTO messageDTO) {
        User sender = userRepository.findByUsername(senderUsername);
        if (sender == null) {
            throw new IllegalArgumentException("Usuario emisor no encontrado");
        }

        User receiver = userRepository.findById(messageDTO.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario receptor no encontrado"));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("No puedes enviarte mensajes a ti mismo");
        }

        ChatMessage message = ChatMessage.builder()
                .content(messageDTO.getContent())
                .sender(sender)
                .receiver(receiver)
                .messageType(messageDTO.getMessageType() != null ? messageDTO.getMessageType() : ChatMessage.MessageType.TEXT)
                .attachmentUrl(messageDTO.getAttachmentUrl())
                .attachmentName(messageDTO.getAttachmentName())
                .attachmentSize(messageDTO.getAttachmentSize())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);
        log.info("Message sent from {} to {}", sender.getUsername(), receiver.getUsername());

        ChatMessageDTO responseDTO = convertToDTO(savedMessage);

        // Enviar notificación WebSocket al receptor
        ChatWebSocketEventDTO webSocketEvent = ChatWebSocketEventDTO.builder()
                .eventType("NEW_MESSAGE")
                .message(responseDTO)
                .userId(sender.getId())
                .username(sender.getUsername())
                .build();

        notificationService.sendToUser(
                receiver.getUsername(),
                "/topic/chat",
                webSocketEvent
        );

        return responseDTO;
    }

    /**
     * Obtener conversación entre dos usuarios - MÉTODO CORREGIDO
     */
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getConversation(String currentUsername, Long otherUserId, int page, int size) {
        User currentUser = userRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new IllegalArgumentException("Usuario actual no encontrado");
        }

        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messagesPage = chatMessageRepository.findConversationBetweenUsersPageable(
                currentUser, otherUser, pageable);

        List<ChatMessage> messages = messagesPage.getContent();

        // ✅ CORRECCIÓN: Crear una nueva lista mutable antes de revertir
        List<ChatMessage> mutableMessages = new ArrayList<>(messages);
        Collections.reverse(mutableMessages);

        return mutableMessages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener todas las conversaciones de un usuario
     */
    @Transactional(readOnly = true)
    public UserConversationsDTO getUserConversations(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        List<ChatMessage> lastMessages = chatMessageRepository.findUserConversations(user.getId());
        long totalUnread = chatMessageRepository.countUnreadMessages(user);

        List<ConversationDTO> conversations = lastMessages.stream()
                .map(message -> {
                    User otherUser = message.getSender().getId().equals(user.getId())
                            ? message.getReceiver()
                            : message.getSender();

                    long unreadCount = chatMessageRepository.countUnreadMessagesBetweenUsers(user, otherUser);

                    return ConversationDTO.builder()
                            .otherUserId(otherUser.getId())
                            .otherUsername(otherUser.getUsername())
                            .lastMessage(convertToDTO(message))
                            .unreadCount(unreadCount)
                            .lastActivity(message.getCreatedAt())
                            .build();
                })
                .sorted((c1, c2) -> c2.getLastActivity().compareTo(c1.getLastActivity()))
                .collect(Collectors.toList());

        return UserConversationsDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .conversations(conversations)
                .totalUnreadMessages(totalUnread)
                .build();
    }

    /**
     * Marcar mensajes como leídos
     */
    public void markMessagesAsRead(String receiverUsername, Long senderId) {
        User receiver = userRepository.findByUsername(receiverUsername);
        if (receiver == null) {
            throw new IllegalArgumentException("Usuario receptor no encontrado");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario emisor no encontrado"));

        chatMessageRepository.markMessagesAsReadBetweenUsers(receiver, sender, LocalDateTime.now());

        // Notificar al emisor que sus mensajes fueron leídos
        ChatWebSocketEventDTO webSocketEvent = ChatWebSocketEventDTO.builder()
                .eventType("MESSAGES_READ")
                .userId(receiver.getId())
                .username(receiver.getUsername())
                .data(Map.of("senderId", senderId, "readAt", LocalDateTime.now()))
                .build();

        notificationService.sendToUser(sender.getUsername(), "/topic/chat", webSocketEvent);

        log.info("Messages marked as read between {} and {}", receiver.getUsername(), sender.getUsername());
    }

    /**
     * Buscar mensajes
     */
    @Transactional(readOnly = true)
    public ChatSearchResultDTO searchMessages(String username, ChatSearchDTO searchDTO) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        List<ChatMessage> messages = chatMessageRepository.searchMessagesForUser(user, searchDTO.getSearchTerm());

        // Filtrar por usuario específico si se proporciona
        if (searchDTO.getOtherUserId() != null) {
            User otherUser = userRepository.findById(searchDTO.getOtherUserId()).orElse(null);
            if (otherUser != null) {
                messages = messages.stream()
                        .filter(m -> m.getSender().getId().equals(otherUser.getId()) ||
                                m.getReceiver().getId().equals(otherUser.getId()))
                        .collect(Collectors.toList());
            }
        }

        // Filtrar por fechas si se proporcionan
        if (searchDTO.getDateFrom() != null) {
            messages = messages.stream()
                    .filter(m -> m.getCreatedAt().isAfter(searchDTO.getDateFrom()))
                    .collect(Collectors.toList());
        }

        if (searchDTO.getDateTo() != null) {
            messages = messages.stream()
                    .filter(m -> m.getCreatedAt().isBefore(searchDTO.getDateTo()))
                    .collect(Collectors.toList());
        }

        List<ChatMessageDTO> messageDTOs = messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ChatSearchResultDTO.builder()
                .messages(messageDTOs)
                .totalResults((long) messageDTOs.size())
                .searchTerm(searchDTO.getSearchTerm())
                .build();
    }

    /**
     * Eliminar mensaje (soft delete)
     */
    public boolean deleteMessage(String username, Long messageId) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        int deletedCount = chatMessageRepository.softDeleteMessage(messageId, user, LocalDateTime.now());

        if (deletedCount > 0) {
            log.info("Message {} soft deleted by {}", messageId, username);

            // Notificar via WebSocket sobre la eliminación
            ChatWebSocketEventDTO webSocketEvent = ChatWebSocketEventDTO.builder()
                    .eventType("MESSAGE_DELETED")
                    .userId(user.getId())
                    .username(user.getUsername())
                    .data(Map.of("messageId", messageId))
                    .build();

            // Notificar a todos los usuarios en la conversación
            notificationService.broadcast("/topic/chat/messages/deleted", webSocketEvent);

            return true;
        }

        return false;
    }

    /**
     * Obtener estadísticas de chat para un usuario
     */
    @Transactional(readOnly = true)
    public ChatStatisticsDTO getChatStatistics(String username) {
        try {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }

            long totalSent = 0;
            long totalReceived = 0;
            long totalRead = 0;
            
            try {
                // Intentar usar el método de estadísticas combinadas
                Object[] stats = chatMessageRepository.getMessageStatisticsForUser(user);
                
                if (stats != null && stats.length >= 3) {
                    totalSent = convertToLong(stats[0]);
                    totalReceived = convertToLong(stats[1]);
                    totalRead = convertToLong(stats[2]);
                }
            } catch (Exception e) {
                log.warn("Error con getMessageStatisticsForUser, usando métodos alternativos: {}", e.getMessage());
                
                // Métodos de respaldo más robustos
                Long sentCount = chatMessageRepository.countMessagesSentByUser(user);
                Long receivedCount = chatMessageRepository.countMessagesReceivedByUser(user);
                Long readCount = chatMessageRepository.countMessagesReadByUser(user);
                
                // Asegurar valores no nulos
                totalSent = sentCount != null ? sentCount : 0L;
                totalReceived = receivedCount != null ? receivedCount : 0L;
                totalRead = readCount != null ? readCount : 0L;
            }

            long totalUnread = chatMessageRepository.countUnreadMessages(user);
            List<User> conversationUsers = chatMessageRepository.findUsersInConversationWith(user);

            // Buscar el último mensaje del usuario
            List<ChatMessage> recentMessages = chatMessageRepository.findRecentMessagesForUser(
                    user, LocalDateTime.now().minusDays(30));

            LocalDateTime lastMessageTime = recentMessages.isEmpty()
                    ? null
                    : recentMessages.get(0).getCreatedAt();

            return ChatStatisticsDTO.builder()
                    .totalMessagesSent(totalSent)
                    .totalMessagesReceived(totalReceived)
                    .totalUnreadMessages(totalUnread)
                    .totalConversations((long) conversationUsers.size())
                    .lastMessageTime(lastMessageTime)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error obteniendo estadísticas de chat para usuario: {}", username, e);
            
            // Devolver estadísticas por defecto en caso de error
            return ChatStatisticsDTO.builder()
                    .totalMessagesSent(0L)
                    .totalMessagesReceived(0L)
                    .totalUnreadMessages(0L)
                    .totalConversations(0L)
                    .lastMessageTime(null)
                    .build();
        }
    }
    
    /**
     * Método utilitario para convertir Object a Long de forma segura
     */
    private long convertToLong(Object value) {
        if (value == null) {
            return 0L;
        }
        
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                log.warn("No se pudo convertir String a Long: {}", value);
                return 0L;
            }
        }
        
        // Si es un array (caso del error original)
        if (value.getClass().isArray()) {
            log.warn("Se recibió un array cuando se esperaba un Number: {}. Tipo: {}", 
                    value, value.getClass().getName());
            return 0L;
        }
        
        log.warn("Tipo de dato no esperado para conversión a Long: {}. Tipo: {}", 
                value, value.getClass().getName());
        return 0L;
    }

    /**
     * Obtener usuarios disponibles para chat (todos los usuarios excepto el actual)
     */
    @Transactional(readOnly = true)
    public List<UserStatusDTO> getAvailableUsers(String currentUsername) {
        User currentUser = userRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        return userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(currentUser.getId()))
                .map(user -> UserStatusDTO.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .isOnline(false) // TODO: Implementar sistema de presencia
                        .lastSeen(null) // TODO: Implementar último visto
                        .build())
                .collect(Collectors.toList());
    }

    // Métodos de utilidad privados

    private ChatMessageDTO convertToDTO(ChatMessage message) {
        return ChatMessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(message.getSender().getId())
                .senderUsername(message.getSender().getUsername())
                .receiverId(message.getReceiver().getId())
                .receiverUsername(message.getReceiver().getUsername())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .readAt(message.getReadAt())
                .messageType(message.getMessageType())
                .attachmentUrl(message.getAttachmentUrl())
                .attachmentName(message.getAttachmentName())
                .attachmentSize(message.getAttachmentSize())
                .conversationId(message.getConversationId())
                .build();
    }
}