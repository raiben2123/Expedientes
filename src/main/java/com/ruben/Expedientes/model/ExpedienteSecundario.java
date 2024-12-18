package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ExpedienteSecundario extends Expediente{
    @ManyToOne
    @JoinColumn(name = "expediente_principal_id")
    private ExpedientePrincipal expedientePrincipal;
}
