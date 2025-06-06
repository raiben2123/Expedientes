package com.ruben.Expedientes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserConversationsDTO {
    private Long userId;
    private String username;
    private List<ConversationDTO> conversations;
    private Long totalUnreadMessages;

    // Método requerido por el controller
    public Long getTotalUnreadMessages() {
        return totalUnreadMessages != null ? totalUnreadMessages : 0L;
    }

    // Método helper para obtener conversaciones con valor por defecto
    public List<ConversationDTO> getConversations() {
        return conversations != null ? conversations : List.of();
    }
}