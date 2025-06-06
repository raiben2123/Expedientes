package com.ruben.Expedientes.dto;

import com.ruben.Expedientes.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Long id;
    private Long senderId;
    private String senderUsername;
    private Long receiverId;
    private String receiverUsername;
    private String content;
    private ChatMessage.MessageType messageType;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private String attachmentUrl;
    private String attachmentName;
    private Long attachmentSize;
    private String conversationId;

    // Método helper para isRead
    public Boolean getIsRead() {
        return isRead != null ? isRead : false;
    }
}