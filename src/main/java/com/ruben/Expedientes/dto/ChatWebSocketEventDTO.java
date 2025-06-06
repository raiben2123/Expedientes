// ChatWebSocketEventDTO.java
package com.ruben.Expedientes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatWebSocketEventDTO {
    private String eventType;
    private Long userId;
    private String username;
    private ChatMessageDTO message;
    private Map<String, Object> data;
}