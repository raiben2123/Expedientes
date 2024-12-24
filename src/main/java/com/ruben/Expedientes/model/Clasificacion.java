package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Clasificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String acronym;

    public Clasificacion(String name, String acronym) {
        this.name = name;
        this.acronym = acronym;
    }

    public Clasificacion(Long id, String name, String acronym) {
        this.id = id;
        this.name = name;
        this.acronym = acronym;
    }

    @OneToMany(mappedBy = "clasificacion")
    private List<ExpedientePrincipal> expedientePrincipalList;

    @OneToMany(mappedBy = "clasificacion")
    private List<ExpedienteSecundario> expedienteSecundarioList;
}
