package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("NIF")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PeticionarioNIF extends Peticionario{
    private String nif;

    public PeticionarioNIF(Long peticionarioId) {
    }

    @Override
    public String getTipoPeticionario() {
        return "NIF";
    }
}
