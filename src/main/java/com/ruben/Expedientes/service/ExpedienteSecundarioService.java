package com.ruben.Expedientes.service;

import com.ruben.Expedientes.dto.ExpedienteSecundarioDTO;
import com.ruben.Expedientes.model.*;
import com.ruben.Expedientes.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExpedienteSecundarioService {

    private final ExpedienteSecundarioRepository expedienteSecundarioRepository;
    private final EstadoExpedienteRepository estadoExpedienteRepository;
    private final DepartamentoRepository departamentoRepository;
    private final ClasificacionRepository clasificacionRepository;
    private final EmpresaRepository empresaRepository;
    private final PeticionarioRepository peticionarioRepository;
    private final ExpedientePrincipalRepository expedientePrincipalRepository;

    @Transactional(readOnly = true)
    public ExpedienteSecundarioDTO findByIdSecundario(Long id) {
        return expedienteSecundarioRepository.findById(id)
                .filter(ExpedienteSecundario::getActive)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ExpedienteSecundarioDTO> findByExpediente(String numeroExpediente) {
        return expedienteSecundarioRepository.findByNumeroExpediente(numeroExpediente)
                .stream()
                .filter(ExpedienteSecundario::getActive)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExpedienteSecundarioDTO> findAll() {
        return expedienteSecundarioRepository.findAll()
                .stream()
                .filter(ExpedienteSecundario::getActive)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ExpedienteSecundarioDTO saveSecundario(ExpedienteSecundarioDTO expedienteSecundarioDTO) {
        // Validar que el expediente principal existe y está activo
        ExpedientePrincipal expedientePrincipal = expedientePrincipalRepository
                .findById(expedienteSecundarioDTO.getExpedientePrincipalId())
                .orElseThrow(() -> new NoSuchElementException("Expediente principal no encontrado"));

        if (!expedientePrincipal.getActive()) {
            throw new IllegalArgumentException("No se puede crear un expediente secundario para un expediente principal inactivo");
        }

        ExpedienteSecundario expedienteSecundario = convertToEntity(expedienteSecundarioDTO);
        expedienteSecundario.setActive(true);
        expedienteSecundario.setCreatedAt(LocalDateTime.now());
        expedienteSecundario.setUpdatedAt(LocalDateTime.now());

        ExpedienteSecundario savedExpediente = expedienteSecundarioRepository.save(expedienteSecundario);
        log.info("ExpedienteSecundario created: {} for principal: {}",
                savedExpediente.getNumeroExpediente(),
                expedientePrincipal.getNumeroExpediente());

        return convertToDTO(savedExpediente);
    }

    public void deleteSecundario(Long id) {
        ExpedienteSecundario expediente = expedienteSecundarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Expediente secundario no encontrado"));

        // Soft delete
        expediente.setActive(false);
        expediente.setUpdatedAt(LocalDateTime.now());
        expedienteSecundarioRepository.save(expediente);

        log.info("ExpedienteSecundario soft deleted: {}", expediente.getNumeroExpediente());
    }

    public ExpedienteSecundarioDTO update(Long id, ExpedienteSecundarioDTO expedienteSecundarioDetails) {
        ExpedienteSecundario existingExpediente = expedienteSecundarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Expediente secundario no encontrado"));

        // Actualizar campos básicos
        existingExpediente.setNumeroExpediente(expedienteSecundarioDetails.getNumeroExpediente());
        existingExpediente.setNumeroSolicitud(expedienteSecundarioDetails.getNumeroSolicitud());
        existingExpediente.setNumeroRegistro(expedienteSecundarioDetails.getNumeroRegistro());
        existingExpediente.setFechaRegistro(expedienteSecundarioDetails.getFechaRegistro());
        existingExpediente.setFechaInicio(expedienteSecundarioDetails.getFechaInicio());
        existingExpediente.setFechaFinalizacion(expedienteSecundarioDetails.getFechaFinalizacion());
        existingExpediente.setDescripcion(expedienteSecundarioDetails.getDescripcion());
        existingExpediente.setReferenciaCatastral(expedienteSecundarioDetails.getReferenciaCatastral());
        existingExpediente.setUbicacion(expedienteSecundarioDetails.getUbicacion());
        existingExpediente.setObservaciones(expedienteSecundarioDetails.getObservaciones());
        existingExpediente.setUpdatedAt(LocalDateTime.now());

        // Actualizar relaciones
        existingExpediente.setEstado(estadoExpedienteRepository.findById(expedienteSecundarioDetails.getEstadoExpedienteId())
                .orElseThrow(() -> new NoSuchElementException("Estado de expediente no encontrado")));

        existingExpediente.setDepartamento(departamentoRepository.findById(expedienteSecundarioDetails.getDepartamentoId())
                .orElseThrow(() -> new NoSuchElementException("Departamento no encontrado")));

        existingExpediente.setClasificacion(clasificacionRepository.findById(expedienteSecundarioDetails.getClasificacionId())
                .orElseThrow(() -> new NoSuchElementException("Clasificación no encontrada")));

        // Validar y actualizar peticionario/empresa (solo uno puede estar presente)
        if (expedienteSecundarioDetails.getEmpresaId() != null && expedienteSecundarioDetails.getPeticionarioId() != null) {
            throw new IllegalArgumentException("Un expediente no puede tener tanto empresa como peticionario");
        }

        if (expedienteSecundarioDetails.getEmpresaId() != null) {
            existingExpediente.setEmpresa(empresaRepository.findById(expedienteSecundarioDetails.getEmpresaId())
                    .orElseThrow(() -> new NoSuchElementException("Empresa no encontrada")));
            existingExpediente.setPeticionario(null);
        } else if (expedienteSecundarioDetails.getPeticionarioId() != null) {
            existingExpediente.setPeticionario(peticionarioRepository.findById(expedienteSecundarioDetails.getPeticionarioId())
                    .orElseThrow(() -> new NoSuchElementException("Peticionario no encontrado")));
            existingExpediente.setEmpresa(null);
        } else {
            throw new IllegalArgumentException("Un expediente debe tener empresa o peticionario");
        }

        // Actualizar expediente principal si ha cambiado
        if (!existingExpediente.getExpedientePrincipal().getId().equals(expedienteSecundarioDetails.getExpedientePrincipalId())) {
            ExpedientePrincipal nuevoPrincipal = expedientePrincipalRepository.findById(expedienteSecundarioDetails.getExpedientePrincipalId())
                    .orElseThrow(() -> new NoSuchElementException("Expediente principal no encontrado"));

            if (!nuevoPrincipal.getActive()) {
                throw new IllegalArgumentException("No se puede asociar a un expediente principal inactivo");
            }

            existingExpediente.setExpedientePrincipal(nuevoPrincipal);
        }

        ExpedienteSecundario updatedExpediente = expedienteSecundarioRepository.save(existingExpediente);
        log.info("ExpedienteSecundario updated: {}", updatedExpediente.getNumeroExpediente());

        return convertToDTO(updatedExpediente);
    }

    private ExpedienteSecundarioDTO convertToDTO(ExpedienteSecundario expedienteSecundario) {
        ExpedienteSecundarioDTO dto = new ExpedienteSecundarioDTO();
        dto.setId(expedienteSecundario.getId());
        dto.setNumeroExpediente(expedienteSecundario.getNumeroExpediente());
        dto.setNumeroSolicitud(expedienteSecundario.getNumeroSolicitud());
        dto.setNumeroRegistro(expedienteSecundario.getNumeroRegistro());
        dto.setFechaRegistro(expedienteSecundario.getFechaRegistro());
        dto.setFechaInicio(expedienteSecundario.getFechaInicio());
        dto.setFechaFinalizacion(expedienteSecundario.getFechaFinalizacion());
        dto.setDescripcion(expedienteSecundario.getDescripcion());
        dto.setReferenciaCatastral(expedienteSecundario.getReferenciaCatastral());
        dto.setUbicacion(expedienteSecundario.getUbicacion());
        dto.setObservaciones(expedienteSecundario.getObservaciones());

        // IDs de relaciones
        dto.setEstadoExpedienteId(expedienteSecundario.getEstado() != null ? expedienteSecundario.getEstado().getId() : null);
        dto.setDepartamentoId(expedienteSecundario.getDepartamento() != null ? expedienteSecundario.getDepartamento().getId() : null);
        dto.setClasificacionId(expedienteSecundario.getClasificacion() != null ? expedienteSecundario.getClasificacion().getId() : null);
        dto.setEmpresaId(expedienteSecundario.getEmpresa() != null ? expedienteSecundario.getEmpresa().getId() : null);
        dto.setPeticionarioId(expedienteSecundario.getPeticionario() != null ? expedienteSecundario.getPeticionario().getId() : null);
        dto.setExpedientePrincipalId(expedienteSecundario.getExpedientePrincipal() != null ? expedienteSecundario.getExpedientePrincipal().getId() : null);

        return dto;
    }

    private ExpedienteSecundario convertToEntity(ExpedienteSecundarioDTO dto) {
        ExpedienteSecundario entity = new ExpedienteSecundario();
        entity.setId(dto.getId());
        entity.setNumeroExpediente(dto.getNumeroExpediente());
        entity.setNumeroSolicitud(dto.getNumeroSolicitud());
        entity.setNumeroRegistro(dto.getNumeroRegistro());
        entity.setFechaRegistro(dto.getFechaRegistro());
        entity.setFechaInicio(dto.getFechaInicio());
        entity.setFechaFinalizacion(dto.getFechaFinalizacion());
        entity.setDescripcion(dto.getDescripcion());
        entity.setReferenciaCatastral(dto.getReferenciaCatastral());
        entity.setUbicacion(dto.getUbicacion());
        entity.setObservaciones(dto.getObservaciones());

        // Relaciones obligatorias
        entity.setEstado(estadoExpedienteRepository.findById(dto.getEstadoExpedienteId())
                .orElseThrow(() -> new NoSuchElementException("Estado de expediente no encontrado")));
        entity.setDepartamento(departamentoRepository.findById(dto.getDepartamentoId())
                .orElseThrow(() -> new NoSuchElementException("Departamento no encontrado")));
        entity.setClasificacion(clasificacionRepository.findById(dto.getClasificacionId())
                .orElseThrow(() -> new NoSuchElementException("Clasificación no encontrada")));
        entity.setExpedientePrincipal(expedientePrincipalRepository.findById(dto.getExpedientePrincipalId())
                .orElseThrow(() -> new NoSuchElementException("Expediente principal no encontrado")));

        // Empresa o Peticionario (solo uno)
        if (dto.getEmpresaId() != null) {
            entity.setEmpresa(empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new NoSuchElementException("Empresa no encontrada")));
        } else if (dto.getPeticionarioId() != null) {
            entity.setPeticionario(peticionarioRepository.findById(dto.getPeticionarioId())
                    .orElseThrow(() -> new NoSuchElementException("Peticionario no encontrado")));
        }

        return entity;
    }
}