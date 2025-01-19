package com.ruben.Expedientes.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class Expediente implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    private String expediente;
    private String solicitud;
    private String registro;
    private Date fechaRegistro;
    private String objeto;
    private String referenciaCatastral;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "estadoExpediente_id")
    @JsonIgnoreProperties({"expedientePrincipalList","expedienteSecundarioList"})
    private EstadoExpediente estadoExpediente;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "departamento_id")
    @JsonIgnoreProperties({"expedientePrincipalList","expedienteSecundarioList"})
    private Departamento departamento;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "clasificacion_id")
    @JsonIgnoreProperties({"expedientePrincipalList","expedienteSecundarioList"})
    private Clasificacion clasificacion;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "empresa_id")
    @JsonIgnoreProperties({"expedientePrincipalList","expedienteSecundarioList"})
    private Empresa empresa;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "peticionario_id")
    @JsonIgnoreProperties({"expedientePrincipalList","expedienteSecundarioList"})
    private Peticionario peticionario;
    private Date fechaInicio;
}
