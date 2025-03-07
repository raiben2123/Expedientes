package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("DNI")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PeticionarioDNI extends Peticionario{
    private String dni;

    public PeticionarioDNI(Long peticionarioId) {
    }

    @Override
    public String getTipoPeticionario() {
        return "NIF";
    }
}
