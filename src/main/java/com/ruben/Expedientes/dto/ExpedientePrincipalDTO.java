package com.ruben.Expedientes.dto;

import lombok.*;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExpedientePrincipalDTO {
    private Long id;
    private String expediente;
    private String solicitud;
    private String registro;
    private Date fechaRegistro;
    private String objeto;
    private String referenciaCatastral;
    private Long estadoExpedienteId;
    private Long departamentoId;
    private Long clasificacionId;
    private Long empresaId;
    private Long peticionarioId;
    private Date fechaInicio;
    private List<Long> expedienteSecundarioIds; // Only IDs for secondary expedientes linked to this principal
}