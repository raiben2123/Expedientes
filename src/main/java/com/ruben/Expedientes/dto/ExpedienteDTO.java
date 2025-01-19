package com.ruben.Expedientes.dto;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class ExpedienteDTO {
    private Long id;
    private String expediente;
    private String solicitud;
    private String registro;
    private Date fechaRegistro;
    private String objeto;
    private String referenciaCatastral;
    private Long estadoExpedienteId; // Solo el ID
    private Long departamentoId; // Solo el ID
    private Long clasificacionId; // Solo el ID
    private Long empresaId; // Solo el ID
    private Long peticionarioId; // Solo el ID
    private Date fechaInicio;
}