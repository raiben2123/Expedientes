package com.ruben.Expedientes.service;

import com.ruben.Expedientes.dto.EstadoExpedienteDTO;
import com.ruben.Expedientes.model.EstadoExpediente;
import com.ruben.Expedientes.repository.EstadoExpedienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EstadoExpedienteService {

    @Autowired
    private EstadoExpedienteRepository estadoExpedienteRepository;

    public EstadoExpedienteDTO findById(Long id) {
        return estadoExpedienteRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("EstadoExpediente not found with id: " + id));
    }

    public List<EstadoExpedienteDTO> findByName(String name) {
        return estadoExpedienteRepository.findByName(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EstadoExpedienteDTO saveEstadoExpediente(EstadoExpedienteDTO estadoExpedienteDTO) {
        EstadoExpediente estadoExpediente = convertToEntity(estadoExpedienteDTO);
        EstadoExpediente savedEstadoExpediente = estadoExpedienteRepository.save(estadoExpediente);
        return convertToDTO(savedEstadoExpediente);
    }

    public List<EstadoExpedienteDTO> findAll() {
        return estadoExpedienteRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteEstadoExpediente(Long id) {
        estadoExpedienteRepository.deleteById(id);
    }

    public EstadoExpedienteDTO update(Long id, EstadoExpedienteDTO estadoExpedienteDTO) {
        return estadoExpedienteRepository.findById(id)
                .map(existingEstado -> {
                    existingEstado.setName(estadoExpedienteDTO.getName());
                    return convertToDTO(estadoExpedienteRepository.save(existingEstado));
                })
                .orElseThrow(() -> new RuntimeException("EstadoExpediente not found with id: " + id));
    }

    private EstadoExpedienteDTO convertToDTO(EstadoExpediente estadoExpediente) {
        EstadoExpedienteDTO dto = new EstadoExpedienteDTO();
        dto.setId(estadoExpediente.getId());
        dto.setName(estadoExpediente.getName());
        return dto;
    }

    private EstadoExpediente convertToEntity(EstadoExpedienteDTO dto) {
        EstadoExpediente entity = new EstadoExpediente();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }
}