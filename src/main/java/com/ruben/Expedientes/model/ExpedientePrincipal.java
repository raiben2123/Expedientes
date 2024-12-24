package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExpedientePrincipal extends Expediente{
    @OneToMany(mappedBy = "expedientePrincipal")
    private List<ExpedienteSecundario> expedienteSecundario;
}
