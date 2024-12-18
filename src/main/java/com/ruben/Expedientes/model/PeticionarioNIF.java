package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@DiscriminatorValue("NIF")
@Data
public class PeticionarioNIF extends Peticionario{
    private String nif;
}
