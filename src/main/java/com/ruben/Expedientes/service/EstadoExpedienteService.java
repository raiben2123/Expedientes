package com.ruben.Expedientes.service;

import com.ruben.Expedientes.model.Clasificacion;
import com.ruben.Expedientes.model.EstadoExpediente;
import com.ruben.Expedientes.repository.EstadoExpedienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoExpedienteService {
    @Autowired
    private EstadoExpedienteRepository estadoExpedienteRepository;

    public EstadoExpediente findById(Long id){
        return estadoExpedienteRepository.findById(id).orElse(null);
    }

    public List<EstadoExpediente> findByName(String name){
        return estadoExpedienteRepository.findByName(name);
    }

    public EstadoExpediente saveExpediente(EstadoExpediente estadoExpediente){
        return estadoExpedienteRepository.save(estadoExpediente);
    }

    public List<EstadoExpediente> findAll(){
        return estadoExpedienteRepository.findAll();
    }

    public void deleteEstadoExpediente(Long id){
        estadoExpedienteRepository.deleteById(id);
    }

//    public EstadoExpediente update(Long id, EstadoExpediente estadoExpedienteDetails){
//        EstadoExpediente estadoExpediente = estadoExpedienteRepository.findById(id).orElse(null);
//        if (estadoExpediente != null){
//            estadoExpediente.setName(estadoExpedienteDetails.getName());
//
//            return estadoExpedienteRepository.save(estadoExpediente);
//        }
//        return null;
//    }
}
