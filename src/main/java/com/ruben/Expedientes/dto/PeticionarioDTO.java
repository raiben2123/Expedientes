package com.ruben.Expedientes.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo_peticionario")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PeticionarioDNIDTO.class, name = "DNI"),
        @JsonSubTypes.Type(value = PeticionarioNIFDTO.class, name = "NIF")
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class PeticionarioDTO {
    private Long id;
    private String name;
    private String surname;
    private String address;
    private String tlf;
    private String email;
    private Long representaId; // Solo el ID
    private List<Long> expedientePrincipalList; // Solo el ID, para evitar sobrecarga de datos
    private List<Long> expedienteSecundarioList; // Solo el ID, para evitar sobrecarga de datos
}
