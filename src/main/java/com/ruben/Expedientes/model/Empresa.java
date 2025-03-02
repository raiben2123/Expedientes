package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empresas",
        indexes = {
                @Index(name = "idx_empresa_cif", columnList = "cif"),
                @Index(name = "idx_empresa_name", columnList = "name"),
                @Index(name = "idx_empresa_active", columnList = "active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String cif;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 20)
    private String tlf;

    @Column(length = 255)
    private String email;

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

    // Representante legal de la empresa
    @OneToOne(mappedBy = "representa", cascade = CascadeType.DETACH)
    private Peticionario representante;

    // Relaciones - usar FetchType.LAZY para mejor rendimiento
    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<ExpedientePrincipal> expedientesPrincipales = new ArrayList<>();

    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<ExpedienteSecundario> expedientesSecundarios = new ArrayList<>();

    // Constructor personalizado
    public Empresa(String cif, String name) {
        this.cif = cif;
        this.name = name;
        this.active = true;
    }

    @Override
    public String toString() {
        return "Empresa{" +
                "id=" + id +
                ", cif='" + cif + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                '}';
    }
}