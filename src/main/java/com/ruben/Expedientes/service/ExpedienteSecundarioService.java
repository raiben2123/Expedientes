package com.ruben.Expedientes.service;

import com.ruben.Expedientes.dto.ExpedienteSecundarioDTO;
import com.ruben.Expedientes.model.*;
import com.ruben.Expedientes.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpedienteSecundarioService {
    @Autowired
    private ExpedienteSecundarioRepository expedienteSecundarioRepository;

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
    private ExpedientePrincipalRepository expedientePrincipalRepository;

    public ExpedienteSecundarioDTO findByIdSecundario(Long id) {
        return expedienteSecundarioRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public List<ExpedienteSecundarioDTO> findByExpediente(String expediente) {
        return expedienteSecundarioRepository.findByExpediente(expediente)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ExpedienteSecundarioDTO> findAll() {
        return expedienteSecundarioRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ExpedienteSecundarioDTO saveSecundario(ExpedienteSecundarioDTO expedienteSecundarioDTO) {
        ExpedienteSecundario expedienteSecundario = convertToEntity(expedienteSecundarioDTO);
        return convertToDTO(expedienteSecundarioRepository.save(expedienteSecundario));
    }

    public void deleteSecundario(Long id) {
        expedienteSecundarioRepository.deleteById(id);
    }

    public ExpedienteSecundarioDTO update(Long id, ExpedienteSecundarioDTO expedienteSecundarioDetails) {
        return expedienteSecundarioRepository.findById(id)
                .map(existingExpediente -> {
                    existingExpediente.setExpediente(expedienteSecundarioDetails.getExpediente());
                    existingExpediente.setSolicitud(expedienteSecundarioDetails.getSolicitud());
                    existingExpediente.setRegistro(expedienteSecundarioDetails.getRegistro());
                    existingExpediente.setFechaRegistro(expedienteSecundarioDetails.getFechaRegistro());
                    existingExpediente.setObjeto(expedienteSecundarioDetails.getObjeto());
                    existingExpediente.setReferenciaCatastral(expedienteSecundarioDetails.getReferenciaCatastral());
                    existingExpediente.setFechaInicio(expedienteSecundarioDetails.getFechaInicio());

                    // Update relationships
                    existingExpediente.setEstadoExpediente(estadoExpedienteRepository.findById(expedienteSecundarioDetails.getEstadoExpedienteId())
                            .orElseThrow(() -> new RuntimeException("EstadoExpediente not found with id: " + expedienteSecundarioDetails.getEstadoExpedienteId())));
                    existingExpediente.setDepartamento(departamentoRepository.findById(expedienteSecundarioDetails.getDepartamentoId())
                            .orElseThrow(() -> new RuntimeException("Departamento not found with id: " + expedienteSecundarioDetails.getDepartamentoId())));
                    existingExpediente.setClasificacion(clasificacionRepository.findById(expedienteSecundarioDetails.getClasificacionId())
                            .orElseThrow(() -> new RuntimeException("Clasificacion not found with id: " + expedienteSecundarioDetails.getClasificacionId())));

                    // Handle Empresa
                    if (expedienteSecundarioDetails.getEmpresaId() != null) {
                        existingExpediente.setEmpresa(empresaRepository.findById(expedienteSecundarioDetails.getEmpresaId())
                                .orElseThrow(() -> new RuntimeException("Empresa not found with id: " + expedienteSecundarioDetails.getEmpresaId())));
                    } else {
                        existingExpediente.setEmpresa(null);
                    }

                    // Handle Peticionario, allowing null values
                    if (expedienteSecundarioDetails.getPeticionarioId() != null) {
                        Peticionario peticionario = peticionarioRepository.findById(expedienteSecundarioDetails.getPeticionarioId())
                                .orElseThrow(() -> new RuntimeException("Peticionario not found with id: " + expedienteSecundarioDetails.getPeticionarioId()));
                        existingExpediente.setPeticionario(peticionario);
                    } else {
                        existingExpediente.setPeticionario(null);
                    }

                    // Handle ExpedientePrincipal
                    existingExpediente.setExpedientePrincipal(expedientePrincipalRepository.findById(expedienteSecundarioDetails.getExpedientePrincipalId())
                            .orElseThrow(() -> new RuntimeException("ExpedientePrincipal not found with id: " + expedienteSecundarioDetails.getExpedientePrincipalId())));

                    return convertToDTO(expedienteSecundarioRepository.save(existingExpediente));
                })
                .orElse(null);
    }

    private ExpedienteSecundarioDTO convertToDTO(ExpedienteSecundario expedienteSecundario) {
        ExpedienteSecundarioDTO dto = new ExpedienteSecundarioDTO();
        dto.setId(expedienteSecundario.getId());
        dto.setExpediente(expedienteSecundario.getExpediente());
        dto.setSolicitud(expedienteSecundario.getSolicitud());
        dto.setRegistro(expedienteSecundario.getRegistro());
        dto.setFechaRegistro(expedienteSecundario.getFechaRegistro());
        dto.setObjeto(expedienteSecundario.getObjeto());
        dto.setReferenciaCatastral(expedienteSecundario.getReferenciaCatastral());

        // Check for null before accessing getId to prevent NullPointerExceptions
        dto.setEstadoExpedienteId(expedienteSecundario.getEstadoExpediente() != null ? expedienteSecundario.getEstadoExpediente().getId() : null);
        dto.setDepartamentoId(expedienteSecundario.getDepartamento() != null ? expedienteSecundario.getDepartamento().getId() : null);
        dto.setClasificacionId(expedienteSecundario.getClasificacion() != null ? expedienteSecundario.getClasificacion().getId() : null);
        dto.setEmpresaId(expedienteSecundario.getEmpresa() != null ? expedienteSecundario.getEmpresa().getId() : null);
        dto.setPeticionarioId(expedienteSecundario.getPeticionario() != null ? expedienteSecundario.getPeticionario().getId() : null);
        dto.setFechaInicio(expedienteSecundario.getFechaInicio());
        dto.setExpedientePrincipalId(expedienteSecundario.getExpedientePrincipal() != null ? expedienteSecundario.getExpedientePrincipal().getId() : null);
        return dto;
    }

    private ExpedienteSecundario convertToEntity(ExpedienteSecundarioDTO dto) {
        ExpedienteSecundario entity = new ExpedienteSecundario();
        entity.setId(dto.getId());
        entity.setExpediente(dto.getExpediente());
        entity.setSolicitud(dto.getSolicitud());
        entity.setRegistro(dto.getRegistro());
        entity.setFechaRegistro(dto.getFechaRegistro());
        entity.setObjeto(dto.getObjeto());
        entity.setReferenciaCatastral(dto.getReferenciaCatastral());
        entity.setFechaInicio(dto.getFechaInicio());

        entity.setEstadoExpediente(estadoExpedienteRepository.findById(dto.getEstadoExpedienteId())
                .orElseThrow(() -> new RuntimeException("EstadoExpediente not found with id: " + dto.getEstadoExpedienteId())));
        entity.setDepartamento(departamentoRepository.findById(dto.getDepartamentoId())
                .orElseThrow(() -> new RuntimeException("Departamento not found with id: " + dto.getDepartamentoId())));
        entity.setClasificacion(clasificacionRepository.findById(dto.getClasificacionId())
                .orElseThrow(() -> new RuntimeException("Clasificacion not found with id: " + dto.getClasificacionId())));

        // Handle Empresa, allowing null values
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

        entity.setExpedientePrincipal(expedientePrincipalRepository.findById(dto.getExpedientePrincipalId())
                .orElseThrow(() -> new RuntimeException("ExpedientePrincipal not found with id: " + dto.getExpedientePrincipalId())));

        return entity;
    }
}