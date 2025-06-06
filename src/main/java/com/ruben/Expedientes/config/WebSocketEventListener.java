package com.ruben.Expedientes.config;

import com.ruben.Expedientes.dto.ChatWebSocketEventDTO;
import com.ruben.Expedientes.service.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final WebSocketNotificationService notificationService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        log.info("Nueva conexión WebSocket establecida. Session ID: {}", sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        // Obtener información del usuario de la sesión
        Object usernameObj = headerAccessor.getSessionAttributes().get("username");
        Object userIdObj = headerAccessor.getSessionAttributes().get("userId");
        
        if (usernameObj != null && userIdObj != null) {
            String username = (String) usernameObj;
            Long userId = (Long) userIdObj;
            
            log.info("Usuario {} ({}) desconectado. Session ID: {}", username, userId, sessionId);
            
            // Notificar a todos los usuarios que este usuario está offline
            try {
                ChatWebSocketEventDTO presenceEvent = ChatWebSocketEventDTO.builder()
                        .eventType("USER_OFFLINE")
                        .userId(userId)
                        .username(username)
                        .data(Map.of("isOnline", false, "reason", "disconnect"))
                        .build();
                
                notificationService.broadcast("/topic/user/presence", presenceEvent);
            } catch (Exception e) {
                log.error("Error enviando notificación de desconexión para usuario {}", username, e);
            }
        } else {
            log.info("WebSocket desconectado sin información de usuario. Session ID: {}", sessionId);
        }
    }
}