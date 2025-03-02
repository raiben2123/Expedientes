package com.ruben.Expedientes.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpedienteFileDTO {
    private Long id;
    private String originalFileName;
    private String contentType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
    private String uploadedBy;
    private String description;
    private String category;
    private String expedienteType;
    private Long expedienteId;

    // Solo se incluye cuando se descarga el archivo
    private byte[] fileData;

    // Campos calculados para la UI
    @Builder.Default
    private String fileSizeHuman = "";

    public String getFileSizeHuman() {
        if (fileSize == null) return "";

        double bytes = fileSize.doubleValue();
        if (bytes < 1024) return String.format("%.0f B", bytes);
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024 * 1024));
        return String.format("%.1f GB", bytes / (1024 * 1024 * 1024));
    }
}