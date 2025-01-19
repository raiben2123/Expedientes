package com.ruben.Expedientes.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExpedientePrincipalDTO extends ExpedienteDTO {
    private List<Long> expedienteSecundarioIds; // Solo los IDs
}