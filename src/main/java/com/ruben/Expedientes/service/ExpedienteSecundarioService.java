package com.ruben.Expedientes.service;

import com.ruben.Expedientes.model.*;
import com.ruben.Expedientes.repository.ExpedienteSecundarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ExpedienteSecundarioService {

    @Autowired
    private ExpedienteSecundarioRepository expedienteSecundarioRepository;

    public ExpedienteSecundario findByIdSecundario(Long id){
        return expedienteSecundarioRepository.findById(id).orElse(null);
    }

    public List<ExpedienteSecundario> findByExpediente(String expediente){
        return expedienteSecundarioRepository.findByExpediente(expediente);
    }

    public List<ExpedienteSecundario> findBySolicitud(String solicitud){
        return expedienteSecundarioRepository.findBySolicitud(solicitud);
    }

    public List<ExpedienteSecundario> findByRegistro(String registro){
        return expedienteSecundarioRepository.findByRegistro(registro);
    }

    public List<ExpedienteSecundario> findByFechaRegistro(Date fechaRegistro){
        return expedienteSecundarioRepository.findByFechaRegistro(fechaRegistro);
    }

    public List<ExpedienteSecundario> findByObjeto(String objeto){
        return expedienteSecundarioRepository.findByObjeto(objeto);
    }

    public List<ExpedienteSecundario> findByReferenciaCatastral(String referenciaCatastral){
        return expedienteSecundarioRepository.findByReferenciaCatastral(referenciaCatastral);
    }

    public List<ExpedienteSecundario> findByEstadoExpediente(EstadoExpediente estadoExpediente){
        return expedienteSecundarioRepository.findByEstadoExpediente(estadoExpediente);
        //TODO
    }

    public List<ExpedienteSecundario> findByDepartamento(Departamento departamento){
        return expedienteSecundarioRepository.findByDepartamento(departamento);
        //TODO
    }

    public List<ExpedienteSecundario> findByClasificacion(Clasificacion clasificacion){
        return expedienteSecundarioRepository.findByClasificacion(clasificacion);
        //TODO
    }

    public List<ExpedienteSecundario> findByEmpresa(Empresa empresa){
        return expedienteSecundarioRepository.findByEmpresa(empresa);
        //TODO
    }

    public List<ExpedienteSecundario> findByPeticionario(Peticionario peticionario){
        return expedienteSecundarioRepository.findByPeticionario(peticionario);
        //TODO
    }

    public List<ExpedienteSecundario> findByFechaInicio(Date fechaInicio){
        return expedienteSecundarioRepository.findByFechaInicio(fechaInicio);
    }

    public List<ExpedienteSecundario> findByPrincipal(ExpedientePrincipal expedientePrincipal){
        return expedienteSecundarioRepository.findByExpedientePrincipal(expedientePrincipal);
        //TODO
    }

    public List<ExpedienteSecundario> findAll(){
        return expedienteSecundarioRepository.findAll();
    }

    public ExpedienteSecundario saveSecundario(ExpedienteSecundario expedienteSecundario){
        return expedienteSecundarioRepository.save(expedienteSecundario);
    }

    public void deleteSecundario(Long id){
        expedienteSecundarioRepository.deleteById(id);
    }

    public ExpedienteSecundario update(Long id, ExpedienteSecundario expedienteSecundarioDetails){
        ExpedienteSecundario expedienteSecundario = expedienteSecundarioRepository.findById(id).orElse(null);
        if (expedienteSecundario != null){
            expedienteSecundario.setExpediente(expedienteSecundarioDetails.getExpediente());
            expedienteSecundario.setSolicitud(expedienteSecundarioDetails.getSolicitud());
            expedienteSecundario.setRegistro(expedienteSecundarioDetails.getRegistro());
            expedienteSecundario.setFechaRegistro(expedienteSecundarioDetails.getFechaRegistro());
            expedienteSecundario.setObjeto(expedienteSecundarioDetails.getObjeto());
            expedienteSecundario.setReferenciaCatastral(expedienteSecundario.getReferenciaCatastral());
            expedienteSecundario.setEstadoExpediente(expedienteSecundarioDetails.getEstadoExpediente());
            expedienteSecundario.setDepartamento(expedienteSecundarioDetails.getDepartamento());
            expedienteSecundario.setClasificacion(expedienteSecundarioDetails.getClasificacion());
            expedienteSecundario.setEmpresa(expedienteSecundarioDetails.getEmpresa());
            expedienteSecundario.setPeticionario(expedienteSecundarioDetails.getPeticionario());
            expedienteSecundario.setFechaInicio(expedienteSecundarioDetails.getFechaInicio());
            return expedienteSecundarioRepository.save(expedienteSecundario);
        }
        return null;
    }

}
