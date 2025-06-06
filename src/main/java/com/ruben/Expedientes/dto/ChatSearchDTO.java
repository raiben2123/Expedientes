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
public class ChatSearchDTO {
    private String searchTerm;
    private Long otherUserId;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private Integer page;
    private Integer size;

    // Método helper para obtener página con valor por defecto
    public Integer getPage() {
        return page != null ? page : 0;
    }

    // Método helper para obtener tamaño con valor por defecto
    public Integer getSize() {
        return size != null ? size : 50;
    }
}