package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cif;
    private String name;
    private String address;
    private String tlf;
    private String email;
    private Peticionario representante;

    @OneToMany(mappedBy = "empresa")
    private List<ExpedientePrincipal> expedientePrincipalList;

    @OneToMany(mappedBy = "empresa")
    private List<ExpedienteSecundario> expedienteSecundarioList;
}
