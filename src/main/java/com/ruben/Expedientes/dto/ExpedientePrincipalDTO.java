package com.ruben.Expedientes.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpedientePrincipalDTO {

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

    // IDs de relaciones obligatorias
    private Long estadoExpedienteId;

    private Long departamentoId;

    private Long clasificacionId;

    // Solo uno de estos dos puede estar presente
    private Long empresaId;
    private Long peticionarioId;

    // Lista de IDs de expedientes secundarios (solo para consulta)
    private List<Long> expedienteSecundarioIds;
}