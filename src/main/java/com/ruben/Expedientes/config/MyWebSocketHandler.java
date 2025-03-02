package com.ruben.Expedientes.config;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MyWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Aquí puedes manejar los mensajes que llegan al WebSocket
        System.out.println("Received message: " + message.getPayload());

        // Si deseas enviar un mensaje de vuelta al cliente:
        try {
            session.sendMessage(new TextMessage("Message received: " + message.getPayload()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Otros métodos si es necesario (por ejemplo, para abrir o cerrar la conexión)
}
