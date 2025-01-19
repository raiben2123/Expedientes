package com.ruben.Expedientes.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExpedienteSecundarioDTO extends ExpedienteDTO {
    private Long expedientePrincipalId; // Solo el ID
}
