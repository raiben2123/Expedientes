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
public class ChatStatisticsDTO {
    private Long totalMessagesSent;
    private Long totalMessagesReceived;
    private Long totalUnreadMessages;
    private Long totalConversations;
    private LocalDateTime lastMessageTime;

    // Métodos requeridos por el controller
    public Long getTotalMessages() {
        Long sent = totalMessagesSent != null ? totalMessagesSent : 0L;
        Long received = totalMessagesReceived != null ? totalMessagesReceived : 0L;
        return sent + received;
    }

    public Long getTotalConversations() {
        return totalConversations != null ? totalConversations : 0L;
    }
    
    public Long getTodayMessages() {
        // Por ahora devolvemos 0, pero se puede implementar en el futuro
        return 0L;
    }
    
    public Long getUnreadMessages() {
        return totalUnreadMessages != null ? totalUnreadMessages : 0L;
    }
    
    // Asegurar valores no nulos en todos los getters
    public Long getTotalMessagesSent() {
        return totalMessagesSent != null ? totalMessagesSent : 0L;
    }
    
    public Long getTotalMessagesReceived() {
        return totalMessagesReceived != null ? totalMessagesReceived : 0L;
    }
    
    public Long getTotalUnreadMessages() {
        return totalUnreadMessages != null ? totalUnreadMessages : 0L;
    }
}