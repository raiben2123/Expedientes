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
    private Long id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String storedFileName;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long fileSize;

    @Lob
    @Column(nullable = false, columnDefinition = "BYTEA")
    private byte[] fileData;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    private String uploadedBy;

    private String description;

    // Relación polimórfica - puede estar asociado a cualquier tipo de expediente
    @Column(nullable = false)
    private String expedienteType; // "PRINCIPAL" o "SECUNDARIO"

    @Column(nullable = false)
    private Long expedienteId;

    // Opcional: categoría del archivo
    private String category; // "DOCUMENTACION", "PLANOS", "FOTOGRAFIAS", etc.
}