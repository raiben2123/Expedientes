package com.ruben.Expedientes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private Long otherUserId;
    private String otherUsername;
    private ChatMessageDTO lastMessage;
    private Long unreadCount;
    private LocalDateTime lastActivity;

    // Método helper para unread count
    public Long getUnreadCount() {
        return unreadCount != null ? unreadCount : 0L;
    }
}