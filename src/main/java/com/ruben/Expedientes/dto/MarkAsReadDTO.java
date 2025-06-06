package com.ruben.Expedientes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// DTO para marcar mensajes como leídos
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkAsReadDTO {
    private Long senderId;
    private List<Long> messageIds; // Lista de IDs de mensajes a marcar como leídos
}
