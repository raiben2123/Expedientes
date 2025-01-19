package com.ruben.Expedientes.service;

import com.ruben.Expedientes.dto.ExpedientePrincipalDTO;
import com.ruben.Expedientes.model.*;
import com.ruben.Expedientes.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
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
        return convertToDTO(savedExpediente);
    }

    public void deletePrincipal(Long id) {
        expedientePrincipalRepository.deleteById(id);
    }

    public ExpedientePrincipalDTO update(Long id, ExpedientePrincipalDTO expedientePrincipalDetails) {
        return expedientePrincipalRepository.findById(id)
                .map(existingExpediente -> {
                    // Aquí 'id' ya está en el scope del lambda, por lo que debemos cambiar el nombre de la variable
                    Long existingId = existingExpediente.getId();

                    // Actualizar los campos del objeto existente
                    existingExpediente.setExpediente(expedientePrincipalDetails.getExpediente());
                    existingExpediente.setSolicitud(expedientePrincipalDetails.getSolicitud());
                    existingExpediente.setRegistro(expedientePrincipalDetails.getRegistro());
                    existingExpediente.setFechaRegistro(expedientePrincipalDetails.getFechaRegistro());
                    existingExpediente.setObjeto(expedientePrincipalDetails.getObjeto());
                    existingExpediente.setReferenciaCatastral(expedientePrincipalDetails.getReferenciaCatastral());
                    existingExpediente.setFechaInicio(expedientePrincipalDetails.getFechaInicio());

                    // Actualizar relaciones
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

                    // Manejo de Peticionario
                    Peticionario peticionario = peticionarioRepository.findById(expedientePrincipalDetails.getPeticionarioId())
                            .orElseThrow(() -> new RuntimeException("Peticionario not found with id: " + expedientePrincipalDetails.getPeticionarioId()));
                    existingExpediente.setPeticionario(peticionario); // Asignar el peticionario existente

                    // Manejar la lista de IDs de ExpedienteSecundario
                    if (expedientePrincipalDetails.getExpedienteSecundarioIds() != null) {
                        List<ExpedienteSecundario> expedienteSecundarios = expedientePrincipalDetails.getExpedienteSecundarioIds().stream()
                                .map(secundarioId -> {
                                    ExpedienteSecundario es = new ExpedienteSecundario();
                                    es.setId(secundarioId);
                                    return es;
                                })
                                .collect(Collectors.toList());
                        existingExpediente.setExpedienteSecundario(expedienteSecundarios);
                    }

                    return convertToDTO(expedientePrincipalRepository.save(existingExpediente));
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
        dto.setEstadoExpedienteId(expedientePrincipal.getEstadoExpediente().getId());
        dto.setDepartamentoId(expedientePrincipal.getDepartamento().getId());
        dto.setClasificacionId(expedientePrincipal.getClasificacion().getId());
        if (expedientePrincipal.getEmpresa() != null) {
            dto.setEmpresaId(expedientePrincipal.getEmpresa().getId());
        }
        dto.setPeticionarioId(expedientePrincipal.getPeticionario().getId());
        dto.setFechaInicio(expedientePrincipal.getFechaInicio());
        dto.setExpedienteSecundarioIds(expedientePrincipal.getExpedienteSecundario().stream()
                .map(ExpedienteSecundario::getId).collect(Collectors.toList()));
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

        // Cargar entidades existentes
        entity.setEstadoExpediente(estadoExpedienteRepository.findById(dto.getEstadoExpedienteId()).orElseThrow());
        entity.setDepartamento(departamentoRepository.findById(dto.getDepartamentoId()).orElseThrow());
        entity.setClasificacion(clasificacionRepository.findById(dto.getClasificacionId()).orElseThrow());

        if (dto.getEmpresaId() != null) {
            entity.setEmpresa(empresaRepository.findById(dto.getEmpresaId()).orElseThrow());
        }

        // Manejo de Peticionario
        Peticionario peticionario = peticionarioRepository.findById(dto.getPeticionarioId())
                .orElseThrow(() -> new RuntimeException("Peticionario not found with id: " + dto.getPeticionarioId()));
        entity.setPeticionario(peticionario); // Asignar el peticionario existente

        if (dto.getExpedienteSecundarioIds() != null) {
            List<ExpedienteSecundario> expedienteSecundarios = dto.getExpedienteSecundarioIds().stream()
                    .map(secundarioId -> {
                        ExpedienteSecundario es = new ExpedienteSecundario();
                        es.setId(secundarioId);
                        return es;
                    })
                    .collect(Collectors.toList());
            entity.setExpedienteSecundario(expedienteSecundarios);
        }

        return entity;
    }
}