package com.ruben.Expedientes.service;

import com.ruben.Expedientes.model.*;
import com.ruben.Expedientes.repository.ExpedientePrincipalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ExpedientePrincipalService {

    @Autowired
    private ExpedientePrincipalRepository expedientePrincipalRepository;

    public List<ExpedientePrincipal> findAll(){
        return expedientePrincipalRepository.findAll();
    }

    public ExpedientePrincipal findByIdPrincipal(Long id){
        return expedientePrincipalRepository.findById(id).orElse(null);
    }

    public List<ExpedientePrincipal> findByExpediente(String expediente){
        return expedientePrincipalRepository.findByExpediente(expediente);
    }

    public List<ExpedientePrincipal> findBySolicitud(String solicitud){
        return expedientePrincipalRepository.findBySolicitud(solicitud);
    }

    public List<ExpedientePrincipal> findByRegistro(String registro){
        return expedientePrincipalRepository.findByRegistro(registro);
    }

    public List<ExpedientePrincipal> findByFechaRegistro(Date fechaRegistro){
        return expedientePrincipalRepository.findByFechaRegistro(fechaRegistro);
    }

    public List<ExpedientePrincipal> findByObjeto(String objeto){
        return expedientePrincipalRepository.findByObjeto(objeto);
    }

    public List<ExpedientePrincipal> findByReferenciaCatastral(String referenciaCatastral){
        return expedientePrincipalRepository.findByReferenciaCatastral(referenciaCatastral);
    }

    public List<ExpedientePrincipal> findByEstadoExpediente(EstadoExpediente estadoExpediente){
        return expedientePrincipalRepository.findByEstadoExpediente(estadoExpediente);
        //TODO
    }

    public List<ExpedientePrincipal> findByDepartamento(Departamento departamento){
        return expedientePrincipalRepository.findByDepartamento(departamento);
        //TODO
    }

    public List<ExpedientePrincipal> findByClasificacion(Clasificacion clasificacion){
        return expedientePrincipalRepository.findByClasificacion(clasificacion);
        //TODO
    }

    public List<ExpedientePrincipal> findByEmpresa(Empresa empresa){
        return expedientePrincipalRepository.findByEmpresa(empresa);
        //TODO
    }

    public List<ExpedientePrincipal> findByPeticionario(Peticionario peticionario){
        return expedientePrincipalRepository.findByPeticionario(peticionario);
        //TODO
    }

    public List<ExpedientePrincipal> findByFechaInicio(Date fechaInicio){
        return expedientePrincipalRepository.findByFechaInicio(fechaInicio);
    }

    public List<ExpedientePrincipal> findBySecundario(ExpedienteSecundario expedienteSecundario){
        return expedientePrincipalRepository.findByExpedienteSecundario(expedienteSecundario);
        //TODO
    }

    public ExpedientePrincipal savePrincipal(ExpedientePrincipal expedientePrincipal){
        return expedientePrincipalRepository.save(expedientePrincipal);
    }

    public void deletePrincipal(Long id){
        expedientePrincipalRepository.deleteById(id);
    }

    public ExpedientePrincipal update(Long id, ExpedientePrincipal expedientePrincipalDetails){
        ExpedientePrincipal expedientePrincipal = expedientePrincipalRepository.findById(id).orElse(null);
        if (expedientePrincipal != null){
            expedientePrincipal.setExpediente(expedientePrincipalDetails.getExpediente());
            expedientePrincipal.setSolicitud(expedientePrincipalDetails.getSolicitud());
            expedientePrincipal.setRegistro(expedientePrincipalDetails.getRegistro());
            expedientePrincipal.setFechaRegistro(expedientePrincipalDetails.getFechaRegistro());
            expedientePrincipal.setObjeto(expedientePrincipalDetails.getObjeto());
            expedientePrincipal.setReferenciaCatastral(expedientePrincipalDetails.getReferenciaCatastral());
            expedientePrincipal.setEstadoExpediente(expedientePrincipalDetails.getEstadoExpediente());
            expedientePrincipal.setDepartamento(expedientePrincipalDetails.getDepartamento());
            expedientePrincipal.setClasificacion(expedientePrincipalDetails.getClasificacion());
            expedientePrincipal.setEmpresa(expedientePrincipalDetails.getEmpresa());
            expedientePrincipal.setPeticionario(expedientePrincipalDetails.getPeticionario());
            expedientePrincipal.setFechaInicio(expedientePrincipalDetails.getFechaInicio());

            return expedientePrincipalRepository.save(expedientePrincipal);
        }
        return null;
    }
}