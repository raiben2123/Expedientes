package com.ruben.Expedientes.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruben.Expedientes.dto.ChatWebSocketEventDTO;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class MyWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> userSessions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> resourceSubscriptions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MyWebSocketHandler() {
        System.out.println("DEBUG: Inicializando MyWebSocketHandler");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        System.out.println("DEBUG: Nueva conexión WebSocket: sessionId=" + session.getId());

        // ✅ Enviar confirmación inmediata al cliente
        Map<String, Object> confirmation = Map.of(
                "type", "CONNECTION_CONFIRMED",
                "data", Map.of("sessionId", session.getId())
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(confirmation)));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
            String type = (String) payload.get("type");
            System.out.println("DEBUG: Mensaje recibido: type=" + type + ", payload=" + payload);

            switch (type) {
                case "IDENTIFY":
                    handleIdentify(session, payload);
                    break;
                case "SUBSCRIBE":
                    handleSubscribe(session, payload);
                    break;
                case "SUBSCRIBE_ALL":
                    handleSubscribeAll(session, payload);
                    break;
                case "CRUD":
                    handleCrud(session, payload);
                    break;
                case "CHAT":
                    handleChat(session, payload);
                    break;
                case "NOTIFICATION":
                    handleNotification(session, payload);
                    break;
                case "DISCONNECT":
                    handleDisconnect(session);
                    break;
                default:
                    System.out.println("WARN: Tipo de mensaje desconocido: " + type);
                    // ✅ Responder con error para tipos desconocidos
                    Map<String, Object> errorResponse = Map.of(
                            "type", "ERROR",
                            "message", "Tipo de mensaje desconocido: " + type
                    );
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorResponse)));
            }
        } catch (Exception e) {
            System.err.println("ERROR: Error procesando mensaje: " + e.getMessage());
            e.printStackTrace();

            // ✅ Enviar error al cliente
            try {
                Map<String, Object> errorResponse = Map.of(
                        "type", "ERROR",
                        "message", "Error procesando mensaje: " + e.getMessage()
                );
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorResponse)));
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = userSessions.remove(session.getId());
        sessions.remove(session.getId());
        resourceSubscriptions.values().forEach(set -> set.remove(session.getId()));
        System.out.println("DEBUG: WebSocket desconectado: sessionId=" + session.getId() + ", status=" + status);

        // ✅ Notificar desconexión de usuario si estaba identificado
        if (userId != null) {
            try {
                Map<String, Object> event = Map.of(
                        "type", "SYSTEM",
                        "action", "USER_DISCONNECTED",
                        "data", Map.of(
                                "userId", userId,
                                "username", "user" + userId,
                                "isOnline", false
                        )
                );
                broadcastToAll("users", event);
            } catch (Exception e) {
                System.err.println("ERROR: Error notificando desconexión: " + e.getMessage());
            }
        }
    }

    private void handleIdentify(WebSocketSession session, Map<String, Object> payload) throws IOException {
        String userId = String.valueOf(payload.get("userId"));
        String username = (String) payload.get("username");
        userSessions.put(session.getId(), userId);
        System.out.println("DEBUG: Usuario identificado: username=" + username + ", userId=" + userId);

        // ✅ Notificar conexión de usuario
        try {
            Map<String, Object> event = Map.of(
                    "type", "SYSTEM",
                    "action", "USER_CONNECTED",
                    "data", Map.of(
                            "userId", userId,
                            "username", username,
                            "isOnline", true
                    )
            );
            broadcastToAll("users", event);
        } catch (Exception e) {
            System.err.println("ERROR: Error notificando conexión: " + e.getMessage());
        }

        // ✅ Enviar confirmación al cliente
        Map<String, Object> confirmation = Map.of(
                "type", "CONNECTION_CONFIRMED",
                "data", Map.of(
                        "sessionId", session.getId(),
                        "userId", userId,
                        "username", username
                )
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(confirmation)));
    }

    private void handleSubscribe(WebSocketSession session, Map<String, Object> payload) {
        String resource = (String) payload.get("resource");
        if (resource != null) {
            resourceSubscriptions.computeIfAbsent(resource, k -> ConcurrentHashMap.newKeySet())
                    .add(session.getId());
            System.out.println("DEBUG: Suscripción a recurso: resource=" + resource + ", sessionId=" + session.getId());
        }
    }

    private void handleSubscribeAll(WebSocketSession session, Map<String, Object> payload) {
        @SuppressWarnings("unchecked")
        List<String> resources = (List<String>) payload.get("resources");
        if (resources != null) {
            for (String resource : resources) {
                resourceSubscriptions.computeIfAbsent(resource, k -> ConcurrentHashMap.newKeySet())
                        .add(session.getId());
            }
            System.out.println("DEBUG: Suscripción a todos los recursos: resources=" + resources + ", sessionId=" + session.getId());
        }
    }

    private void handleCrud(WebSocketSession session, Map<String, Object> payload) {
        String resource = (String) payload.get("resource");
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        String action = (String) payload.get("action");
        String userId = userSessions.get(session.getId());

        if (resource != null && action != null) {
            Map<String, Object> event = Map.of(
                    "type", "CRUD",
                    "action", action,
                    "resource", resource,
                    "data", data != null ? data : Map.of(),
                    "userId", userId != null ? userId : "unknown"
            );
            System.out.println("DEBUG: Procesando CRUD: resource=" + resource + ", action=" + action + ", userId=" + userId);
            broadcastToResource(resource, event);
        }
    }

    private void handleChat(WebSocketSession session, Map<String, Object> payload) {
        String action = (String) payload.get("action");
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) payload.get("data");

        if (action != null && data != null) {
            // ✅ Crear evento de chat estructurado
            Map<String, Object> event = Map.of(
                    "type", "CHAT",
                    "action", action,
                    "data", data
            );

            System.out.println("DEBUG: Procesando CHAT: action=" + action);

            // ✅ Si hay receiverId específico, enviar solo a ese usuario
            Object receiverIdObj = data.get("receiverId");
            if (receiverIdObj != null) {
                String receiverId = String.valueOf(receiverIdObj);
                broadcastToUser(receiverId, event);
            } else {
                // ✅ Broadcast general para chat
                broadcastToResource("chat", event);
            }
        }
    }

    private void handleNotification(WebSocketSession session, Map<String, Object> payload) {
        String action = (String) payload.get("action");
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) payload.get("data");

        if (action != null) {
            Map<String, Object> event = Map.of(
                    "type", "NOTIFICATION",
                    "action", action,
                    "resource", "notifications",
                    "data", data != null ? data : Map.of()
            );
            System.out.println("DEBUG: Procesando NOTIFICATION: action=" + action);
            broadcastToResource("notifications", event);
        }
    }

    private void handleDisconnect(WebSocketSession session) {
        String userId = userSessions.get(session.getId());
        if (userId != null) {
            try {
                Map<String, Object> event = Map.of(
                        "type", "SYSTEM",
                        "action", "USER_DISCONNECTED",
                        "data", Map.of(
                                "userId", userId,
                                "username", "user" + userId,
                                "isOnline", false
                        )
                );
                System.out.println("DEBUG: Procesando DISCONNECT: userId=" + userId);
                broadcastToAll("users", event);
            } catch (Exception e) {
                System.err.println("ERROR: Error en disconnect: " + e.getMessage());
            }
        }
    }

    // ✅ Método mejorado para broadcast a recurso específico
    public void broadcastToResource(String resource, Object event) {
        Set<String> sessionIds = resourceSubscriptions.getOrDefault(resource, ConcurrentHashMap.newKeySet());
        System.out.println("DEBUG: Enviando broadcast a recurso: resource=" + resource + ", sessionIds=" + sessionIds.size());

        for (String sessionId : sessionIds) {
            WebSocketSession session = sessions.get(sessionId);
            if (session != null && session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(event)));
                    System.out.println("DEBUG: Mensaje enviado a sessionId=" + sessionId);
                } catch (Exception e) {
                    System.err.println("ERROR: Error enviando mensaje a sessionId=" + sessionId + ": " + e.getMessage());
                    // ✅ Limpiar sesión problemática
                    sessions.remove(sessionId);
                    resourceSubscriptions.values().forEach(set -> set.remove(sessionId));
                }
            } else {
                // ✅ Limpiar sesión cerrada
                sessions.remove(sessionId);
                resourceSubscriptions.values().forEach(set -> set.remove(sessionId));
            }
        }
    }

    // ✅ Método mejorado para enviar a usuario específico
    public void broadcastToUser(String userId, Object event) {
        System.out.println("DEBUG: Enviando mensaje a usuario específico: userId=" + userId);

        userSessions.forEach((sessionId, sessionUserId) -> {
            if (sessionUserId.equals(userId)) {
                WebSocketSession session = sessions.get(sessionId);
                if (session != null && session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(event)));
                        System.out.println("DEBUG: Mensaje enviado a usuario: userId=" + userId + ", sessionId=" + sessionId);
                    } catch (Exception e) {
                        System.err.println("ERROR: Error enviando mensaje a usuario: userId=" + userId + ", sessionId=" + sessionId + ": " + e.getMessage());
                        // ✅ Limpiar sesión problemática
                        sessions.remove(sessionId);
                        userSessions.remove(sessionId);
                        resourceSubscriptions.values().forEach(set -> set.remove(sessionId));
                    }
                } else {
                    // ✅ Limpiar sesión cerrada
                    sessions.remove(sessionId);
                    userSessions.remove(sessionId);
                    resourceSubscriptions.values().forEach(set -> set.remove(sessionId));
                }
            }
        });
    }

    // ✅ Método mejorado para broadcast general
    public void broadcastToAll(String resource, Object event) {
        System.out.println("DEBUG: Enviando broadcast a todos: resource=" + resource + ", sessions=" + sessions.size());

        sessions.values().removeIf(session -> !session.isOpen());

        sessions.values().stream()
                .filter(WebSocketSession::isOpen)
                .forEach(session -> {
                    try {
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(event)));
                        System.out.println("DEBUG: Mensaje enviado a sessionId=" + session.getId());
                    } catch (Exception e) {
                        System.err.println("ERROR: Error enviando mensaje a sessionId=" + session.getId() + ": " + e.getMessage());
                    }
                });
    }

    // ✅ Métodos de utilidad para estadísticas
    public int getActiveSessionsCount() {
        return sessions.size();
    }

    public int getIdentifiedUsersCount() {
        return userSessions.size();
    }

    public Map<String, Integer> getResourceSubscriptionStats() {
        Map<String, Integer> stats = new ConcurrentHashMap<>();
        resourceSubscriptions.forEach((resource, sessions) ->
                stats.put(resource, sessions.size())
        );
        return stats;
    }
}