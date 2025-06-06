package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "expediente_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpedienteFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "category")
    private String category;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "description")
    private String description;

    @Column(name = "expediente_id", nullable = false)
    private Long expedienteId;

    @Column(name = "expediente_type", nullable = false)
    private String expedienteType;

    // SOLUCIONADO: Usar @Lob sin columnDefinition específica para evitar conflictos de tipos
    @Lob
    @Column(name = "file_data", nullable = false)
    private byte[] fileData;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "stored_file_name", nullable = false)
    private String storedFileName;

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "uploaded_by", nullable = false)
    private String uploadedBy;
}