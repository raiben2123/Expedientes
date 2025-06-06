package com.ruben.Expedientes.service;

import com.ruben.Expedientes.config.MyWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {

    private final MyWebSocketHandler webSocketHandler;

    // Enum para tipos de operaciones
    public enum Operation {
        CREATE, UPDATE, DELETE
    }

    // Enum para tipos de entidades - ✅ CORREGIDOS los nombres
    public enum EntityType {
        EXPEDIENTES_PRINCIPALES("expedientesprincipales"),
        EXPEDIENTES_SECUNDARIOS("expedientessecundarios"),
        CLASIFICACIONES("clasificaciones"),
        DEPARTAMENTOS("departamentos"),
        ESTADOS_EXPEDIENTES("estadosexpedientes"),
        EMPRESAS("empresas"),
        PETICIONARIOS("peticionarios"),
        TICKETS("tickets"),
        USERS("users");

        private final String resource;

        EntityType(String resource) {
            this.resource = resource;
        }

        public String getResource() {
            return resource;
        }
    }

    /**
     * Envía una notificación WebSocket para una operación específica
     */
    public void sendNotification(EntityType entityType, Operation operation, Object data) {
        try {
            Map<String, Object> message = Map.of(
                    "type", "CRUD",
                    "action", operation.name(),
                    "resource", entityType.getResource(),
                    "data", data != null ? data : Map.of()
            );

            log.debug("Enviando notificación WebSocket: {} {} para recurso {}",
                    operation.name(),
                    entityType.getResource(),
                    entityType.getResource());

            webSocketHandler.broadcastToResource(entityType.getResource(), message);

            log.info("Notificación WebSocket enviada exitosamente: {} {}",
                    operation.name(),
                    entityType.getResource());

        } catch (Exception e) {
            log.error("Error enviando notificación WebSocket para {} {}: {}",
                    operation.name(),
                    entityType.getResource(),
                    e.getMessage(), e);
        }
    }

    /**
     * Envía notificación de creación
     */
    public void notifyCreated(EntityType entityType, Object data) {
        log.info("Notificando creación de {}", entityType.getResource());
        sendNotification(entityType, Operation.CREATE, data);
    }

    /**
     * Envía notificación de actualización
     */
    public void notifyUpdated(EntityType entityType, Object data) {
        log.info("Notificando actualización de {}", entityType.getResource());
        sendNotification(entityType, Operation.UPDATE, data);
    }

    /**
     * Envía notificación de eliminación
     */
    public void notifyDeleted(EntityType entityType, Object data) {
        log.info("Notificando eliminación de {}", entityType.getResource());
        sendNotification(entityType, Operation.DELETE, data);
    }

    /**
     * Envía notificación a un usuario específico
     */
    public void sendToUser(String userId, String resource, Object data) {
        try {
            Map<String, Object> message = Map.of(
                    "type", "NOTIFICATION",
                    "action", "NEW_NOTIFICATION",
                    "resource", resource,
                    "data", data != null ? data : Map.of()
            );

            log.debug("Enviando notificación personal a usuario {}: {}", userId, resource);
            webSocketHandler.broadcastToUser(userId, message);
            log.info("Notificación personal enviada a usuario {}: {}", userId, resource);

        } catch (Exception e) {
            log.error("Error enviando notificación personal a usuario {}: {}", userId, e.getMessage(), e);
        }
    }

    /**
     * Envía mensaje broadcast a todos los usuarios conectados
     */
    public void broadcast(String resource, Object data) {
        try {
            Map<String, Object> message = Map.of(
                    "type", "NOTIFICATION",
                    "action", "NEW_NOTIFICATION",
                    "resource", resource,
                    "data", data != null ? data : Map.of()
            );

            log.debug("Enviando broadcast a: {}", resource);
            webSocketHandler.broadcastToAll(resource, message);
            log.info("Mensaje broadcast enviado a: {}", resource);

        } catch (Exception e) {
            log.error("Error enviando mensaje broadcast: {}", e.getMessage(), e);
        }
    }

    /**
     * ✅ NUEVO: Envía notificación de chat
     */
    public void sendChatNotification(String action, Object data) {
        try {
            Map<String, Object> message = Map.of(
                    "type", "CHAT",
                    "action", action,
                    "data", data != null ? data : Map.of()
            );

            log.debug("Enviando notificación de chat: {}", action);
            webSocketHandler.broadcastToResource("chat", message);
            log.info("Notificación de chat enviada: {}", action);

        } catch (Exception e) {
            log.error("Error enviando notificación de chat: {}", e.getMessage(), e);
        }
    }

    /**
     * ✅ NUEVO: Envía mensaje de chat a usuario específico
     */
    public void sendChatToUser(String userId, String action, Object data) {
        try {
            Map<String, Object> message = Map.of(
                    "type", "CHAT",
                    "action", action,
                    "data", data != null ? data : Map.of()
            );

            log.debug("Enviando mensaje de chat a usuario {}: {}", userId, action);
            webSocketHandler.broadcastToUser(userId, message);
            log.info("Mensaje de chat enviado a usuario {}: {}", userId, action);

        } catch (Exception e) {
            log.error("Error enviando mensaje de chat a usuario {}: {}", userId, e.getMessage(), e);
        }
    }

    /**
     * ✅ NUEVO: Notifica nuevo mensaje de chat
     */
    public void notifyNewMessage(Object messageData, String receiverId) {
        try {
            Map<String, Object> data = Map.of(
                    "message", messageData
            );

            // Enviar al receptor específico
            if (receiverId != null) {
                sendChatToUser(receiverId, "NEW_MESSAGE", data);
            }

            // También broadcast general para actualizar listas
            sendChatNotification("NEW_MESSAGE", data);

        } catch (Exception e) {
            log.error("Error notificando nuevo mensaje: {}", e.getMessage(), e);
        }
    }

    /**
     * ✅ NUEVO: Notifica mensajes leídos
     */
    public void notifyMessagesRead(String senderId, String readerId) {
        try {
            Map<String, Object> data = Map.of(
                    "senderId", senderId,
                    "readerId", readerId,
                    "readAt", System.currentTimeMillis()
            );

            // Notificar al remitente que sus mensajes fueron leídos
            sendChatToUser(senderId, "MESSAGE_READ", data);

        } catch (Exception e) {
            log.error("Error notificando mensajes leídos: {}", e.getMessage(), e);
        }
    }

    /**
     * ✅ NUEVO: Notifica eliminación de mensaje
     */
    public void notifyMessageDeleted(String messageId) {
        try {
            Map<String, Object> data = Map.of(
                    "messageId", messageId
            );

            sendChatNotification("MESSAGE_DELETED", data);

        } catch (Exception e) {
            log.error("Error notificando eliminación de mensaje: {}", e.getMessage(), e);
        }
    }

    /**
     * ✅ NUEVO: Obtener estadísticas de conexiones
     */
    public Map<String, Object> getConnectionStats() {
        try {
            return Map.of(
                    "activeSessions", webSocketHandler.getActiveSessionsCount(),
                    "identifiedUsers", webSocketHandler.getIdentifiedUsersCount(),
                    "resourceSubscriptions", webSocketHandler.getResourceSubscriptionStats(),
                    "timestamp", System.currentTimeMillis()
            );
        } catch (Exception e) {
            log.error("Error obteniendo estadísticas de conexión: {}", e.getMessage(), e);
            return Map.of(
                    "error", e.getMessage(),
                    "timestamp", System.currentTimeMillis()
            );
        }
    }
}