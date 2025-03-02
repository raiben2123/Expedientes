package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "estados_expedientes",
        indexes = {
                @Index(name = "idx_estado_name", columnList = "name"),
                @Index(name = "idx_estado_active", columnList = "active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoExpediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 7)
    private String color; // Código de color hex para la UI

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(length = 100)
    private String createdBy;

    @Column(length = 100)
    private String updatedBy;

    // Relaciones - usar FetchType.LAZY para mejor rendimiento
    @OneToMany(mappedBy = "estado", fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<ExpedientePrincipal> expedientesPrincipales = new ArrayList<>();

    @OneToMany(mappedBy = "estado", fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<ExpedienteSecundario> expedientesSecundarios = new ArrayList<>();

    // Constructor personalizado
    public EstadoExpediente(String name, String color) {
        this.name = name;
        this.color = color;
        this.active = true;
    }

    @Override
    public String toString() {
        return "EstadoExpediente{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", active=" + active +
                '}';
    }
}