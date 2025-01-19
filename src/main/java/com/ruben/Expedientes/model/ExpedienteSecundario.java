package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExpedienteSecundario extends Expediente implements Serializable {
    @ManyToOne
    @JoinColumn(name = "expediente_principal_id")
    private ExpedientePrincipal expedientePrincipal;
}
