package com.ruben.Expedientes.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateEstadoSecundariosDTO {
    private List<Long> ids;
    private Long estadoExpedienteId;
}