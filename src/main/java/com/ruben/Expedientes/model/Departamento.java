package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departamentos",
        indexes = {
                @Index(name = "idx_departamento_name", columnList = "name"),
                @Index(name = "idx_departamento_active", columnList = "active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

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
    @OneToMany(mappedBy = "departamento", fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<ExpedientePrincipal> expedientesPrincipales = new ArrayList<>();

    @OneToMany(mappedBy = "departamento", fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<ExpedienteSecundario> expedientesSecundarios = new ArrayList<>();

    // Constructor personalizado
    public Departamento(String name) {
        this.name = name;
        this.active = true;
    }

    @Override
    public String toString() {
        return "Departamento{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", active=" + active +
                '}';
    }
}