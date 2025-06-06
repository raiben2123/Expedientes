package com.ruben.Expedientes.dto;

import com.ruben.Expedientes.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para crear un nuevo mensaje
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateChatMessageDTO {
    private String content;
    private Long receiverId;
    private ChatMessage.MessageType messageType;
    private String attachmentUrl;
    private String attachmentName;
    private Long attachmentSize;
}
