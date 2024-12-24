package com.ruben.Expedientes.service;

import com.ruben.Expedientes.model.Clasificacion;
import com.ruben.Expedientes.repository.ClasificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClasificacionService {
    @Autowired
    private ClasificacionRepository clasificacionRepository;

    public Clasificacion findById(Long id) {
        return clasificacionRepository.findById(id).orElse(null);
    }

    public List<Clasificacion> findByName(String name) {
        return clasificacionRepository.findByName(name);
    }

    public List<Clasificacion> findByAcronym(String acronym) {
        return clasificacionRepository.findByAcronym(acronym);
    }

    public Clasificacion saveClasificacion(Clasificacion clasificacion) {
        return clasificacionRepository.save(clasificacion);
    }

    public List<Clasificacion> findAll() {
        return clasificacionRepository.findAll();
    }

    public void deleteClasificacion(Long id){
        clasificacionRepository.deleteById(id);
    }

    public Clasificacion update(Long id, Clasificacion clasificacionDetails){
        Clasificacion clasificacion = clasificacionRepository.findById(id).orElse(null);
        if (clasificacion != null){
            clasificacion.setName(clasificacionDetails.getName());
            clasificacion.setAcronym(clasificacionDetails.getAcronym());

            return clasificacionRepository.save(clasificacion);
        }
        return null;
    }

}
