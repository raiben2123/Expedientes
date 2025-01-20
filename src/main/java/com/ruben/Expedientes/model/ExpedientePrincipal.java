package com.ruben.Expedientes.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExpedientePrincipal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String expediente;
    private String solicitud;
    private String registro;
    private Date fechaRegistro;
    private String objeto;
    private String referenciaCatastral;

    @ManyToOne
    @JoinColumn(name = "estadoExpediente_id")
    @JsonIgnoreProperties({"expedientePrincipalList","expedienteSecundarioList"})
    private EstadoExpediente estadoExpediente;

    @ManyToOne
    @JoinColumn(name = "departamento_id")
    @JsonIgnoreProperties({"expedientePrincipalList","expedienteSecundarioList"})
    private Departamento departamento;

    @ManyToOne
    @JoinColumn(name = "clasificacion_id")
    @JsonIgnoreProperties({"expedientePrincipalList","expedienteSecundarioList"})
    private Clasificacion clasificacion;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    @JsonIgnoreProperties({"expedientePrincipalList","expedienteSecundarioList"})
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "peticionario_id")
    @JsonIgnoreProperties({"expedientePrincipalList","expedienteSecundarioList"})
    private Peticionario peticionario;

    private Date fechaInicio;

    @OneToMany(mappedBy = "expedientePrincipal")
    private List<ExpedienteSecundario> expedienteSecundarios;
}