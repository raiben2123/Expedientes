package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExpedientePrincipal extends Expediente implements Serializable {
    @OneToMany(mappedBy = "expedientePrincipal")
    private List<ExpedienteSecundario> expedienteSecundario;

    public ExpedientePrincipal(Long expedientePrincipalId) {
    }
}
