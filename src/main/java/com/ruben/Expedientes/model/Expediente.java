package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
public abstract class Expediente {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    private String expediente;
    private String solicitud;
    private String registro;
    private Date fechaRegistro;
    private String objeto;
    private String referenciaCatastral;
    @ManyToOne
    @JoinColumn(name = "estadoExpediente_id")
    private EstadoExpediente estadoExpediente;
    @ManyToOne
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;
    @ManyToOne
    @JoinColumn(name = "clasificacion_id")
    private Clasificacion clasificacion;
    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;
    @ManyToOne
    @JoinColumn(name = "peticionario_id")
    private Peticionario peticionario;
    private Date fechaInicio;

}
