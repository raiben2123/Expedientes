package com.ruben.Expedientes.repository;

import com.ruben.Expedientes.model.*;
import com.ruben.Expedientes.service.EstadoExpedienteService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ExpedientePrincipalRepository extends JpaRepository<ExpedientePrincipal, Long> {
    List<ExpedientePrincipal> findByExpediente(String expediente);
    List<ExpedientePrincipal> findBySolicitud(String solicitud);
    List<ExpedientePrincipal> findByRegistro(String registro);
    List<ExpedientePrincipal> findByFechaRegistro(Date fechaRegistro);
    List<ExpedientePrincipal> findByObjeto(String objeto);
    List<ExpedientePrincipal> findByReferenciaCatastral(String referenciaCatastral);
    List<ExpedientePrincipal> findByEstadoExpediente(EstadoExpediente estadoExpediente);
    List<ExpedientePrincipal> findByDepartamento(Departamento departamento);
    List<ExpedientePrincipal> findByClasificacion(Clasificacion clasificacion);
    List<ExpedientePrincipal> findByEmpresa(Empresa empresa);
    List<ExpedientePrincipal> findByPeticionario(Peticionario peticionario);
    List<ExpedientePrincipal> findByFechaInicio(Date fechaInicio);
    List<ExpedientePrincipal> findByExpedienteSecundario(ExpedienteSecundario expedienteSecundario);

}
