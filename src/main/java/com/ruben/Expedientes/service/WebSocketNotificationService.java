package com.ruben.Expedientes.service;

import com.ruben.Expedientes.model.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    // Enum para tipos de operaciones
    public enum Operation {
        CREATE, UPDATE, DELETE
    }

    // Enum para tipos de entidades
    public enum EntityType {
        EXPEDIENTES_PRINCIPALES("/topic/expedientes-principales"),
        EXPEDIENTES_SECUNDARIOS("/topic/expedientes-secundarios"),
        CLASIFICACIONES("/topic/clasificaciones"),
        DEPARTAMENTOS("/topic/departamentos"),
        ESTADOS_EXPEDIENTES("/topic/estados-expedientes"),
        EMPRESAS("/topic/empresas"),
        PETICIONARIOS("/topic/peticionarios"),
        TICKETS("/topic/tickets"),
        USERS("/topic/users");

        private final String topic;

        EntityType(String topic) {
            this.topic = topic;
        }

        public String getTopic() {
            return topic;
        }
    }

    /**
     * Envía una notificación WebSocket para una operación específica
     */
    public void sendNotification(EntityType entityType, Operation operation, Object data) {
        try {
            WebSocketMessage message = new WebSocketMessage(operation.name(), data);
            messagingTemplate.convertAndSend(entityType.getTopic(), message);
            log.debug("WebSocket notification sent: {} {} to {}",
                    operation.name(),
                    data.getClass().getSimpleName(),
                    entityType.getTopic());
        } catch (Exception e) {
            log.error("Error sending WebSocket notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Envía notificación de creación
     */
    public void notifyCreated(EntityType entityType, Object data) {
        sendNotification(entityType, Operation.CREATE, data);
    }

    /**
     * Envía notificación de actualización
     */
    public void notifyUpdated(EntityType entityType, Object data) {
        sendNotification(entityType, Operation.UPDATE, data);
    }

    /**
     * Envía notificación de eliminación
     */
    public void notifyDeleted(EntityType entityType, Object data) {
        sendNotification(entityType, Operation.DELETE, data);
    }

    /**
     * Envía notificación a un usuario específico
     */
    public void sendToUser(String username, String destination, Object data) {
        try {
            messagingTemplate.convertAndSendToUser(username, destination, data);
            log.debug("Personal notification sent to user {}: {}", username, destination);
        } catch (Exception e) {
            log.error("Error sending personal notification to user {}: {}", username, e.getMessage(), e);
        }
    }

    /**
     * Envía mensaje broadcast a todos los usuarios conectados
     */
    public void broadcast(String destination, Object data) {
        try {
            messagingTemplate.convertAndSend(destination, data);
            log.debug("Broadcast message sent to: {}", destination);
        } catch (Exception e) {
            log.error("Error sending broadcast message: {}", e.getMessage(), e);
        }
    }
}