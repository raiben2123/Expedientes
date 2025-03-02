package com.ruben.Expedientes.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpedienteSecundarioDTO {

    private Long id;

    private String numeroExpediente;

    private String numeroSolicitud;
    private String numeroRegistro;

    private LocalDateTime fechaRegistro;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinalizacion;
    private String descripcion;
    private String referenciaCatastral;
    private String ubicacion;
    private String observaciones;

    private Long estadoExpedienteId;

    private Long departamentoId;

    private Long clasificacionId;

    private Long empresaId;
    private Long peticionarioId;

    private Long expedientePrincipalId; // ID of the principal expediente this secondary one is related to
}