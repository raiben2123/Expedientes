package com.ruben.Expedientes.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EstadoExpedienteDTO {
    private Long id;
    private String name;
    private List<Long> expedientePrincipalList; // Solo el ID, para evitar sobrecarga de datos
    private List<Long> expedienteSecundarioList; // Solo el ID, para evitar sobrecarga de datos

    public EstadoExpedienteDTO(Long id, String name) {
    }
}