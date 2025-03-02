package com.ruben.Expedientes.service;

import com.ruben.Expedientes.dto.ExpedientePrincipalDTO;
import com.ruben.Expedientes.model.*;
import com.ruben.Expedientes.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpedientePrincipalService {

    @Autowired
    private ExpedientePrincipalRepository expedientePrincipalRepository;

    @Autowired
    private EstadoExpedienteRepository estadoExpedienteRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private ClasificacionRepository clasificacionRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private PeticionarioRepository peticionarioRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // Add this for WebSocket notifications

    public List<ExpedientePrincipalDTO> findAll() {
        return expedientePrincipalRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ExpedientePrincipalDTO findByIdPrincipal(Long id) {
        return expedientePrincipalRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public List<ExpedientePrincipalDTO> findByExpediente(String expediente) {
        return expedientePrincipalRepository.findByExpediente(expediente)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ExpedientePrincipalDTO savePrincipal(ExpedientePrincipalDTO expedientePrincipalDTO) {
        if (expedientePrincipalDTO.getId() != null) {
            Optional<ExpedientePrincipal> existingExpediente = expedientePrincipalRepository.findById(expedientePrincipalDTO.getId());
            if (existingExpediente.isPresent()) {
                return update(expedientePrincipalDTO.getId(), expedientePrincipalDTO);
            }
        }

        ExpedientePrincipal expedientePrincipal = convertToEntity(expedientePrincipalDTO);
        ExpedientePrincipal savedExpediente = expedientePrincipalRepository.save(expedientePrincipal);
        ExpedientePrincipalDTO savedDTO = convertToDTO(savedExpediente);
        return savedDTO;
    }

    public void deletePrincipal(Long id) {
        expedientePrincipalRepository.deleteById(id);
    }

    public ExpedientePrincipalDTO update(Long id, ExpedientePrincipalDTO expedientePrincipalDetails) {
        return expedientePrincipalRepository.findById(id)
                .map(existingExpediente -> {
                    // Update fields of the existing expediente
                    existingExpediente.setExpediente(expedientePrincipalDetails.getExpediente());
                    existingExpediente.setSolicitud(expedientePrincipalDetails.getSolicitud());
                    existingExpediente.setRegistro(expedientePrincipalDetails.getRegistro());
                    existingExpediente.setFechaRegistro(expedientePrincipalDetails.getFechaRegistro());
                    existingExpediente.setObjeto(expedientePrincipalDetails.getObjeto());
                    existingExpediente.setReferenciaCatastral(expedientePrincipalDetails.getReferenciaCatastral());
                    existingExpediente.setFechaInicio(expedientePrincipalDetails.getFechaInicio());

                    // Update relationships
                    existingExpediente.setEstadoExpediente(estadoExpedienteRepository.findById(expedientePrincipalDetails.getEstadoExpedienteId())
                            .orElseThrow(() -> new RuntimeException("EstadoExpediente not found with id: " + expedientePrincipalDetails.getEstadoExpedienteId())));
                    existingExpediente.setDepartamento(departamentoRepository.findById(expedientePrincipalDetails.getDepartamentoId())
                            .orElseThrow(() -> new RuntimeException("Departamento not found with id: " + expedientePrincipalDetails.getDepartamentoId())));
                    existingExpediente.setClasificacion(clasificacionRepository.findById(expedientePrincipalDetails.getClasificacionId())
                            .orElseThrow(() -> new RuntimeException("Clasificacion not found with id: " + expedientePrincipalDetails.getClasificacionId())));

                    if (expedientePrincipalDetails.getEmpresaId() != null) {
                        existingExpediente.setEmpresa(empresaRepository.findById(expedientePrincipalDetails.getEmpresaId())
                                .orElseThrow(() -> new RuntimeException("Empresa not found with id: " + expedientePrincipalDetails.getEmpresaId())));
                    } else {
                        existingExpediente.setEmpresa(null);
                    }

                    // Handle Peticionario, allowing null values
                    if (expedientePrincipalDetails.getPeticionarioId() != null) {
                        Peticionario peticionario = peticionarioRepository.findById(expedientePrincipalDetails.getPeticionarioId())
                                .orElseThrow(() -> new RuntimeException("Peticionario not found with id: " + expedientePrincipalDetails.getPeticionarioId()));
                        existingExpediente.setPeticionario(peticionario);
                    } else {
                        existingExpediente.setPeticionario(null);
                    }

                    // Handle the list of ExpedienteSecundario IDs
                    if (expedientePrincipalDetails.getExpedienteSecundarioIds() != null) {
                        List<ExpedienteSecundario> expedienteSecundarios = expedientePrincipalDetails.getExpedienteSecundarioIds().stream()
                                .map(secundarioId -> {
                                    ExpedienteSecundario es = new ExpedienteSecundario();
                                    es.setId(secundarioId);
                                    return es;
                                })
                                .collect(Collectors.toList());
                        existingExpediente.setExpedienteSecundarios(expedienteSecundarios);
                    }

                    ExpedientePrincipalDTO updatedDTO = convertToDTO(expedientePrincipalRepository.save(existingExpediente));
                    return updatedDTO;
                })
                .orElse(null);
    }

    private ExpedientePrincipalDTO convertToDTO(ExpedientePrincipal expedientePrincipal) {
        ExpedientePrincipalDTO dto = new ExpedientePrincipalDTO();
        dto.setId(expedientePrincipal.getId());
        dto.setExpediente(expedientePrincipal.getExpediente());
        dto.setSolicitud(expedientePrincipal.getSolicitud());
        dto.setRegistro(expedientePrincipal.getRegistro());
        dto.setFechaRegistro(expedientePrincipal.getFechaRegistro());
        dto.setObjeto(expedientePrincipal.getObjeto());
        dto.setReferenciaCatastral(expedientePrincipal.getReferenciaCatastral());

        // Check for null to avoid NullPointerException
        dto.setEstadoExpedienteId(expedientePrincipal.getEstadoExpediente() != null ? expedientePrincipal.getEstadoExpediente().getId() : null);
        dto.setDepartamentoId(expedientePrincipal.getDepartamento() != null ? expedientePrincipal.getDepartamento().getId() : null);
        dto.setClasificacionId(expedientePrincipal.getClasificacion() != null ? expedientePrincipal.getClasificacion().getId() : null);
        dto.setEmpresaId(expedientePrincipal.getEmpresa() != null ? expedientePrincipal.getEmpresa().getId() : null);
        dto.setPeticionarioId(expedientePrincipal.getPeticionario() != null ? expedientePrincipal.getPeticionario().getId() : null);
        dto.setFechaInicio(expedientePrincipal.getFechaInicio());

        // If expedienteSecundarios is not null, collect their IDs
        dto.setExpedienteSecundarioIds(expedientePrincipal.getExpedienteSecundarios() != null
                ? expedientePrincipal.getExpedienteSecundarios().stream()
                .map(ExpedienteSecundario::getId).collect(Collectors.toList())
                : null);
        return dto;
    }

    private ExpedientePrincipal convertToEntity(ExpedientePrincipalDTO dto) {
        ExpedientePrincipal entity = new ExpedientePrincipal();
        entity.setId(dto.getId());
        entity.setExpediente(dto.getExpediente());
        entity.setSolicitud(dto.getSolicitud());
        entity.setRegistro(dto.getRegistro());
        entity.setFechaRegistro(dto.getFechaRegistro());
        entity.setObjeto(dto.getObjeto());
        entity.setReferenciaCatastral(dto.getReferenciaCatastral());
        entity.setFechaInicio(dto.getFechaInicio());

        // Load existing entities
        entity.setEstadoExpediente(estadoExpedienteRepository.findById(dto.getEstadoExpedienteId())
                .orElseThrow(() -> new RuntimeException("EstadoExpediente not found with id: " + dto.getEstadoExpedienteId())));
        entity.setDepartamento(departamentoRepository.findById(dto.getDepartamentoId())
                .orElseThrow(() -> new RuntimeException("Departamento not found with id: " + dto.getDepartamentoId())));
        entity.setClasificacion(clasificacionRepository.findById(dto.getClasificacionId())
                .orElseThrow(() -> new RuntimeException("Clasificacion not found with id: " + dto.getClasificacionId())));

        if (dto.getEmpresaId() != null) {
            entity.setEmpresa(empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new RuntimeException("Empresa not found with id: " + dto.getEmpresaId())));
        } else {
            entity.setEmpresa(null);
        }

        // Handle Peticionario, allowing null values
        if (dto.getPeticionarioId() != null) {
            Peticionario peticionario = peticionarioRepository.findById(dto.getPeticionarioId())
                    .orElseThrow(() -> new RuntimeException("Peticionario not found with id: " + dto.getPeticionarioId()));
            entity.setPeticionario(peticionario);
        } else {
            entity.setPeticionario(null);
        }

        // Handle ExpedienteSecundario relationship
        if (dto.getExpedienteSecundarioIds() != null) {
            List<ExpedienteSecundario> expedienteSecundarios = dto.getExpedienteSecundarioIds().stream()
                    .map(secundarioId -> {
                        ExpedienteSecundario es = new ExpedienteSecundario();
                        es.setId(secundarioId);
                        return es;
                    })
                    .collect(Collectors.toList());
            entity.setExpedienteSecundarios(expedienteSecundarios);
        }

        return entity;
    }
}