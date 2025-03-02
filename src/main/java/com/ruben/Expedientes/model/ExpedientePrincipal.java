package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expedientes_principales",
        indexes = {
                @Index(name = "idx_exp_principal_numero", columnList = "numeroExpediente"),
                @Index(name = "idx_exp_principal_fecha_registro", columnList = "fechaRegistro"),
                @Index(name = "idx_exp_principal_estado", columnList = "estado_id"),
                @Index(name = "idx_exp_principal_departamento", columnList = "departamento_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_numero_expediente", columnNames = "numeroExpediente")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpedientePrincipal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String numeroExpediente;

    @Column(length = 100)
    private String numeroSolicitud;

    @Column(length = 100)
    private String numeroRegistro;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFinalizacion;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 100)
    private String referenciaCatastral;

    @Column(length = 200)
    private String ubicacion;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

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

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoExpediente estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clasificacion_id", nullable = false)
    private Clasificacion clasificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peticionario_id")
    private Peticionario peticionario;

    // Expedientes secundarios asociados
    @OneToMany(mappedBy = "expedientePrincipal", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ExpedienteSecundario> expedientesSecundarios = new ArrayList<>();

    // Archivos asociados se manejan por consulta usando expedienteType="PRINCIPAL" y expedienteId

    // Validación: debe tener peticionario O empresa, no ambos
    @PrePersist
    @PreUpdate
    private void validatePeticionarioOrEmpresa() {
        if (peticionario != null && empresa != null) {
            throw new IllegalStateException("Un expediente no puede tener tanto peticionario como empresa");
        }
        if (peticionario == null && empresa == null) {
            throw new IllegalStateException("Un expediente debe tener peticionario o empresa");
        }
    }

    @Override
    public String toString() {
        return "ExpedientePrincipal{" +
                "id=" + id +
                ", numeroExpediente='" + numeroExpediente + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                ", active=" + active +
                '}';
    }
}