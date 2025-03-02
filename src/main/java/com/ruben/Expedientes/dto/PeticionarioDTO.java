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
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class PeticionarioDTO {

    private Long id;

    private String name;

    private String surname;

    private String address;

    private String tlf;

    private String email;

    private Long representaId; // ID de la empresa que representa

    // Solo para consultas - no se usan en creación/actualización
    private List<Long> expedientePrincipalList;
    private List<Long> expedienteSecundarioList;

    // Método abstracto que implementarán las subclases
    public abstract String getTipoDocumento();
    public abstract String getNumeroDocumento();
}