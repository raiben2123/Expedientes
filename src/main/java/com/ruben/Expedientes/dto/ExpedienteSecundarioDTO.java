package com.ruben.Expedientes.dto;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExpedienteSecundarioDTO {
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
    private Long expedientePrincipalId; // ID of the principal expediente this secondary one is related to
}