package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clasificaciones",
        indexes = {
                @Index(name = "idx_clasificacion_name", columnList = "name"),
                @Index(name = "idx_clasificacion_acronym", columnList = "acronym")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clasificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 50)
    private String acronym;

    @Column(length = 500)
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
    @OneToMany(mappedBy = "clasificacion", fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<ExpedientePrincipal> expedientesPrincipales = new ArrayList<>();

    @OneToMany(mappedBy = "clasificacion", fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<ExpedienteSecundario> expedientesSecundarios = new ArrayList<>();

    // Constructor personalizado
    public Clasificacion(String name, String acronym) {
        this.name = name;
        this.acronym = acronym;
        this.active = true;
    }

    @Override
    public String toString() {
        return "Clasificacion{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", acronym='" + acronym + '\'' +
                ", active=" + active +
                '}';
    }
}