package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.dto.*;
import com.ruben.Expedientes.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    /**
     * Enviar un nuevo mensaje
     */
    @PostMapping("/messages")
    public ResponseEntity<ChatMessageDTO> sendMessage(
            @RequestBody CreateChatMessageDTO messageDTO,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("Enviando mensaje desde usuario: {}", username);

            ChatMessageDTO sentMessage = chatService.sendMessage(username, messageDTO);
            log.info("Mensaje enviado exitosamente: {}", sentMessage.getId());

            return ResponseEntity.ok(sentMessage);
        } catch (IllegalArgumentException e) {
            log.warn("Error sending message: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error interno enviando mensaje", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtener conversación con otro usuario
     */
    @GetMapping("/conversations/{otherUserId}")
    public ResponseEntity<List<ChatMessageDTO>> getConversation(
            @PathVariable Long otherUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("Obteniendo conversación entre {} y usuario {}", username, otherUserId);

            List<ChatMessageDTO> messages = chatService.getConversation(username, otherUserId, page, size);
            log.info("Conversación obtenida: {} mensajes", messages.size());

            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            log.warn("Error getting conversation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error interno obteniendo conversación", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtener todas las conversaciones del usuario actual
     */
    @GetMapping("/conversations")
    public ResponseEntity<UserConversationsDTO> getUserConversations(Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("Obteniendo conversaciones para usuario: {}", username);

            UserConversationsDTO conversations = chatService.getUserConversations(username);
            log.info("Conversaciones obtenidas: {} conversaciones, {} mensajes no leídos",
                    conversations.getConversations().size(),
                    conversations.getTotalUnreadMessages());

            return ResponseEntity.ok(conversations);
        } catch (IllegalArgumentException e) {
            log.warn("Error getting user conversations: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error interno obteniendo conversaciones", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Marcar mensajes como leídos
     */
    @PostMapping("/messages/read/{senderId}")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable Long senderId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("Marcando mensajes como leídos de usuario {} para {}", senderId, username);

            chatService.markMessagesAsRead(username, senderId);
            log.info("Mensajes marcados como leídos exitosamente");

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("Error marking messages as read: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error interno marcando mensajes como leídos", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Buscar mensajes
     */
    @PostMapping("/search")
    public ResponseEntity<ChatSearchResultDTO> searchMessages(
            @RequestBody ChatSearchDTO searchDTO,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("Buscando mensajes para usuario: {} con término: {}", username, searchDTO.getSearchTerm());

            ChatSearchResultDTO results = chatService.searchMessages(username, searchDTO);
            log.info("Búsqueda completada: {} resultados encontrados", results.getTotalCount());

            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            log.warn("Error searching messages: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error interno buscando mensajes", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Eliminar un mensaje
     */
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long messageId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("Eliminando mensaje {} por usuario {}", messageId, username);

            boolean deleted = chatService.deleteMessage(username, messageId);
            if (deleted) {
                log.info("Mensaje {} eliminado exitosamente", messageId);
                return ResponseEntity.ok().build();
            } else {
                log.warn("Mensaje {} no encontrado o sin permisos", messageId);
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            log.warn("Error deleting message: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error interno eliminando mensaje", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtener estadísticas de chat del usuario
     */
    @GetMapping("/statistics")
    public ResponseEntity<ChatStatisticsDTO> getChatStatistics(Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("Obteniendo estadísticas de chat para usuario: {}", username);

            ChatStatisticsDTO statistics = chatService.getChatStatistics(username);
            log.info("Estadísticas obtenidas para {}: {} mensajes totales, {} conversaciones",
                    username, statistics.getTotalMessages(), statistics.getTotalConversations());

            return ResponseEntity.ok(statistics);
        } catch (IllegalArgumentException e) {
            log.warn("Error getting chat statistics: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error interno obteniendo estadísticas", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtener usuarios disponibles para chat
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserStatusDTO>> getAvailableUsers(Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("Obteniendo usuarios disponibles para chat (solicitado por: {})", username);

            List<UserStatusDTO> users = chatService.getAvailableUsers(username);
            log.info("Usuarios disponibles obtenidos: {} usuarios", users.size());

            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            log.warn("Error getting available users: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error interno obteniendo usuarios disponibles", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtener mensajes no leídos (para notificaciones)
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(Authentication authentication) {
        try {
            String username = authentication.getName();
            log.debug("Obteniendo contador de mensajes no leídos para: {}", username);

            UserConversationsDTO conversations = chatService.getUserConversations(username);
            Long unreadCount = conversations.getTotalUnreadMessages();

            Map<String, Object> response = Map.of("count", unreadCount);

            log.debug("Contador no leídos para {}: {}", username, unreadCount);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Error getting unread count: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error interno obteniendo contador no leídos", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint para verificar el estado del servicio de chat
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        try {
            Map<String, Object> health = Map.of(
                    "status", "UP",
                    "service", "chat",
                    "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            log.error("Error obteniendo estado del servicio", e);
            Map<String, Object> health = Map.of(
                    "status", "DOWN",
                    "error", e.getMessage(),
                    "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.internalServerError().body(health);
        }
    }

    /**
     * Endpoint para marcar usuario como en línea (ping)
     */
    @PostMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping(Authentication authentication) {
        try {
            String username = authentication.getName();
            log.debug("Ping recibido de usuario: {}", username);

            Map<String, Object> response = Map.of(
                    "status", "pong",
                    "timestamp", System.currentTimeMillis(),
                    "user", username
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error procesando ping", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}