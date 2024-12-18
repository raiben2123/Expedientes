package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
public class Clasificacion implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String acronym;

    public Clasificacion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public List<ExpedientePrincipal> getExpedientePrincipalList() {
        return expedientePrincipalList;
    }

    public void setExpedientePrincipalList(List<ExpedientePrincipal> expedientePrincipalList) {
        this.expedientePrincipalList = expedientePrincipalList;
    }

    public List<ExpedienteSecundario> getExpedienteSecundarioList() {
        return expedienteSecundarioList;
    }

    public void setExpedienteSecundarioList(List<ExpedienteSecundario> expedienteSecundarioList) {
        this.expedienteSecundarioList = expedienteSecundarioList;
    }

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
