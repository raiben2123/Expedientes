package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class EstadoExpediente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "estadoExpediente")
    private List<ExpedientePrincipal> expedientePrincipalList;

    @OneToMany(mappedBy = "estadoExpediente")
    private List<ExpedienteSecundario> expedienteSecundarioList;
}
