package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@DiscriminatorValue("DNI")
@Data
public class PeticionarioDNI extends Peticionario{
    private String dni;
}
