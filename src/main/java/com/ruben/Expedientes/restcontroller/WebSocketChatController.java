package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.dto.ChatWebSocketEventDTO;
import com.ruben.Expedientes.service.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketChatController {

    private final WebSocketNotificationService notificationService;

    /**
     * Manejar cuando un usuario se conecta
     */
    @MessageMapping("/user.online")
    public void userOnline(@Payload Map<String, Object> userData, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String username = (String) userData.get("username");
            Long userId = Long.valueOf(userData.get("userId").toString());
            
            log.info("Usuario {} ({}) se conectó al chat", username, userId);
            
            // Notificar a todos los usuarios que este usuario está online
            ChatWebSocketEventDTO presenceEvent = ChatWebSocketEventDTO.builder()
                    .eventType("USER_ONLINE")
                    .userId(userId)
                    .username(username)
                    .data(Map.of("isOnline", true))
                    .build();
            
            notificationService.broadcast("/topic/user/presence", presenceEvent);
            
            // Guardar información del usuario en la sesión
            headerAccessor.getSessionAttributes().put("username", username);
            headerAccessor.getSessionAttributes().put("userId", userId);
            
        } catch (Exception e) {
            log.error("Error processing user.online event", e);
        }
    }

    /**
     * Manejar cuando un usuario se desconecta
     */
    @MessageMapping("/user.offline")
    public void userOffline(@Payload Map<String, Object> userData, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String username = (String) userData.get("username");
            Long userId = Long.valueOf(userData.get("userId").toString());
            
            log.info("Usuario {} ({}) se desconectó del chat", username, userId);
            
            // Notificar a todos los usuarios que este usuario está offline
            ChatWebSocketEventDTO presenceEvent = ChatWebSocketEventDTO.builder()
                    .eventType("USER_OFFLINE")
                    .userId(userId)
                    .username(username)
                    .data(Map.of("isOnline", false))
                    .build();
            
            notificationService.broadcast("/topic/user/presence", presenceEvent);
            
        } catch (Exception e) {
            log.error("Error processing user.offline event", e);
        }
    }

    /**
     * Manejar indicador de escritura
     */
    @MessageMapping("/user.typing")
    public void userTyping(@Payload Map<String, Object> typingData, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String username = (String) headerAccessor.getSessionAttributes().get("username");
            Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
            String receiverUsername = (String) typingData.get("receiverUsername");
            Boolean isTyping = (Boolean) typingData.get("isTyping");
            
            log.debug("Usuario {} está escribiendo a {}: {}", username, receiverUsername, isTyping);
            
            // Notificar al receptor específico
            ChatWebSocketEventDTO typingEvent = ChatWebSocketEventDTO.builder()
                    .eventType("USER_TYPING")
                    .userId(userId)
                    .username(username)
                    .data(Map.of("isTyping", isTyping, "senderUsername", username))
                    .build();
            
            notificationService.sendToUser(receiverUsername, "/topic/chat", typingEvent);
            
        } catch (Exception e) {
            log.error("Error processing user.typing event", e);
        }
    }

    /**
     * Ping para mantener la conexión viva
     */
    @MessageMapping("/ping")
    public void ping(@Payload Map<String, Object> pingData, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String username = (String) headerAccessor.getSessionAttributes().get("username");
            log.debug("Ping recibido de {}", username);
            
            // Responder con pong al usuario específico
            notificationService.sendToUser(username, "/topic/ping", Map.of("type", "pong", "timestamp", System.currentTimeMillis()));
            
        } catch (Exception e) {
            log.error("Error processing ping", e);
        }
    }
}