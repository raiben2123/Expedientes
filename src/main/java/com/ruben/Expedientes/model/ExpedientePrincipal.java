package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class ExpedientePrincipal extends Expediente{
    @OneToMany(mappedBy = "expedientePrincipal")
    private List<ExpedienteSecundario> expedienteSecundario;
}
