package com.ruben.Expedientes.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClasificacionDTO {
    private Long id;
    private String name;
    private String acronym;
    private List<Long> expedientePrincipalList; // Solo el ID, para evitar sobrecarga de datos
    private List<Long> expedienteSecundarioList; // Solo el ID, para evitar sobrecarga de datos

    public ClasificacionDTO(Long id, String name, String acronym) {
    }

    @Override
    public String toString() {
        return "ClasificacionDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", acronym='" + acronym + '\'' +
                '}';
    }
}
