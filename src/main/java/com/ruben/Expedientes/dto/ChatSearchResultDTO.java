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
public class ChatSearchResultDTO {
    private List<ChatMessageDTO> messages;
    private Long totalResults;
    private String searchTerm;

    // Método requerido por el service
    public Long getTotalCount() {
        return totalResults != null ? totalResults : 0L;
    }

    // Método helper para obtener total results
    public Long getTotalResults() {
        return totalResults != null ? totalResults : 0L;
    }
}