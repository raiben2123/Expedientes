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
}
