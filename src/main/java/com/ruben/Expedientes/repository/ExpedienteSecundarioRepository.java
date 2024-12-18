package com.ruben.Expedientes.repository;

import com.ruben.Expedientes.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ExpedienteSecundarioRepository extends JpaRepository<ExpedienteSecundario, Long> {
    List<ExpedienteSecundario> findByExpediente(String expediente);
    List<ExpedienteSecundario> findBySolicitud(String solicitud);
    List<ExpedienteSecundario> findByRegistro(String registro);
    List<ExpedienteSecundario> findByFechaRegistro(Date fechaRegistro);
    List<ExpedienteSecundario> findByObjeto(String objeto);
    List<ExpedienteSecundario> findByReferenciaCatastral(String referenciaCatastral);
    List<ExpedienteSecundario> findByEstadoExpediente(EstadoExpediente estadoExpediente);
    List<ExpedienteSecundario> findByDepartamento(Departamento departamento);
    List<ExpedienteSecundario> findByClasificacion(Clasificacion clasificacion);
    List<ExpedienteSecundario> findByEmpresa(Empresa empresa);
    List<ExpedienteSecundario> findByPeticionario(Peticionario peticionario);
    List<ExpedienteSecundario> findByFechaInicio(Date fechaInicio);
    List<ExpedienteSecundario> findByExpedientePrincipal(ExpedientePrincipal expedientePrincipal);
}
