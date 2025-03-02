package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "peticionarios",
        indexes = {
                @Index(name = "idx_peticionario_dni", columnList = "dni"),
                @Index(name = "idx_peticionario_nif", columnList = "nif"),
                @Index(name = "idx_peticionario_tipo", columnList = "tipo_peticionario"),
                @Index(name = "idx_peticionario_active", columnList = "active")
        })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_peticionario", length = 10)
@Getter
@Setter
@NoArgsConstructor

public abstract class Peticionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String surname;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 20)
    private String tlf;

    @Column(length = 255)
    private String email;

    // Campos específicos para cada tipo (DNI/NIF)
    @Column(length = 20)
    private String dni;

    @Column(length = 20)
    private String nif;

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

    // Empresa que representa (si es representante legal)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "representa_id")
    private Empresa representa;

    // Relaciones - usar FetchType.LAZY para mejor rendimiento
    @OneToMany(mappedBy = "peticionario", fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<ExpedientePrincipal> expedientesPrincipales = new ArrayList<>();

    @OneToMany(mappedBy = "peticionario", fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<ExpedienteSecundario> expedientesSecundarios = new ArrayList<>();

    // Método abstracto que implementarán las subclases
    public abstract String getTipoPeticionario();

    // Método de utilidad para obtener el documento de identificación
    public String getDocumentoIdentificacion() {
        return dni != null ? dni : nif;
    }

    // Método de utilidad para obtener el nombre completo
    public String getNombreCompleto() {
        return surname != null ? name + " " + surname : name;
    }

    @Override
    public String toString() {
        return "Peticionario{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", tipo='" + getTipoPeticionario() + '\'' +
                ", documento='" + getDocumentoIdentificacion() + '\'' +
                ", active=" + active +
                '}';
    }
}