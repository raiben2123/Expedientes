package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Departamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "departamento")
    private List<ExpedientePrincipal> expedientePrincipalList;

    @OneToMany(mappedBy = "departamento")
    private List<ExpedienteSecundario> expedienteSecundarioList;
}
