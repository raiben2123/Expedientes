package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Departamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "departamento")
    private List<ExpedientePrincipal> expedientePrincipalList;

    @OneToMany(mappedBy = "departamento")
    private List<ExpedienteSecundario> expedienteSecundarioList;

    public Departamento(Long departamentoId) {
    }
}
