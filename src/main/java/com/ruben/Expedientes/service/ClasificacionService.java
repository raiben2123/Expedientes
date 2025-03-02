package com.ruben.Expedientes.service;

import com.ruben.Expedientes.dto.ClasificacionDTO;
import com.ruben.Expedientes.model.Clasificacion;
import com.ruben.Expedientes.repository.ClasificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClasificacionService {

    @Autowired
    private ClasificacionRepository clasificacionRepository;

    // Eliminamos la inyección de SimpMessagingTemplate ya que no lo usaremos aquí

    public ClasificacionDTO findById(Long id) {
        return clasificacionRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public List<ClasificacionDTO> findByName(String name) {
        return clasificacionRepository.findByName(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ClasificacionDTO> findByAcronym(String acronym) {
        return clasificacionRepository.findByAcronym(acronym)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ClasificacionDTO saveClasificacion(ClasificacionDTO clasificacionDTO) {
        Clasificacion clasificacion = convertToEntity(clasificacionDTO);
        Clasificacion savedClasificacion = clasificacionRepository.save(clasificacion);
        return convertToDTO(savedClasificacion);
        // Eliminamos notifyClasificacionesUpdate
    }

    public List<ClasificacionDTO> findAll() {
        List<Clasificacion> clasificaciones = clasificacionRepository.findAll();
        return clasificaciones.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteClasificacion(Long id) {
        clasificacionRepository.deleteById(id);
        // Eliminamos notifyClasificacionesUpdate
    }

    public ClasificacionDTO update(Long id, ClasificacionDTO clasificacionDetails) {
        return clasificacionRepository.findById(id)
                .map(clasificacion -> {
                    clasificacion.setName(clasificacionDetails.getName());
                    clasificacion.setAcronym(clasificacionDetails.getAcronym());
                    ClasificacionDTO updatedDTO = convertToDTO(clasificacionRepository.save(clasificacion));
                    // Eliminamos notifyClasificacionesUpdate
                    return updatedDTO;
                })
                .orElse(null);
    }

    private ClasificacionDTO convertToDTO(Clasificacion clasificacion) {
        ClasificacionDTO clasificacionDTO = new ClasificacionDTO();
        clasificacionDTO.setId(clasificacion.getId());
        clasificacionDTO.setName(clasificacion.getName());
        clasificacionDTO.setAcronym(clasificacion.getAcronym());
        return clasificacionDTO;
    }

    private Clasificacion convertToEntity(ClasificacionDTO clasificacionDTO) {
        Clasificacion clasificacion = new Clasificacion();
        clasificacion.setId(clasificacionDTO.getId());
        clasificacion.setName(clasificacionDTO.getName());
        clasificacion.setAcronym(clasificacionDTO.getAcronym());
        return clasificacion;
    }
}