package com.ruben.Expedientes.service;

import com.ruben.Expedientes.dto.ExpedientePrincipalDTO;
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
public class ExpedientePrincipalService {

    private final ExpedientePrincipalRepository expedientePrincipalRepository;
    private final EstadoExpedienteRepository estadoExpedienteRepository;
    private final DepartamentoRepository departamentoRepository;
    private final ClasificacionRepository clasificacionRepository;
    private final EmpresaRepository empresaRepository;
    private final PeticionarioRepository peticionarioRepository;

    @Transactional(readOnly = true)
    public List<ExpedientePrincipalDTO> findAll() {
        return expedientePrincipalRepository.findAll()
                .stream()
                .filter(ExpedientePrincipal::getActive)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExpedientePrincipalDTO findByIdPrincipal(Long id) {
        return expedientePrincipalRepository.findById(id)
                .filter(ExpedientePrincipal::getActive)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ExpedientePrincipalDTO> findByExpediente(String numeroExpediente) {
        return expedientePrincipalRepository.findByNumeroExpediente(numeroExpediente)
                .stream()
                .filter(ExpedientePrincipal::getActive)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ExpedientePrincipalDTO savePrincipal(ExpedientePrincipalDTO expedientePrincipalDTO) {
        // Validar que no existe otro expediente con el mismo número
        if (expedientePrincipalDTO.getId() == null) {
            boolean exists = expedientePrincipalRepository.findByNumeroExpediente(expedientePrincipalDTO.getNumeroExpediente())
                    .stream()
                    .anyMatch(ExpedientePrincipal::getActive);

            if (exists) {
                throw new IllegalArgumentException("Ya existe un expediente con número: " + expedientePrincipalDTO.getNumeroExpediente());
            }
        }

        ExpedientePrincipal expedientePrincipal = convertToEntity(expedientePrincipalDTO);
        expedientePrincipal.setActive(true);
        expedientePrincipal.setCreatedAt(LocalDateTime.now());
        expedientePrincipal.setUpdatedAt(LocalDateTime.now());

        ExpedientePrincipal savedExpediente = expedientePrincipalRepository.save(expedientePrincipal);
        log.info("ExpedientePrincipal created: {}", savedExpediente.getNumeroExpediente());

        return convertToDTO(savedExpediente);
    }

    public void deletePrincipal(Long id) {
        ExpedientePrincipal expediente = expedientePrincipalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Expediente principal no encontrado"));

        // Verificar si tiene expedientes secundarios activos
        long expedientesSecundariosActivos = expediente.getExpedientesSecundarios()
                .stream()
                .mapToLong(es -> es.getActive() ? 1 : 0)
                .sum();

        if (expedientesSecundariosActivos > 0) {
            throw new IllegalArgumentException("No se puede eliminar un expediente principal que tiene expedientes secundarios activos");
        }

        // Soft delete
        expediente.setActive(false);
        expediente.setUpdatedAt(LocalDateTime.now());
        expedientePrincipalRepository.save(expediente);

        log.info("ExpedientePrincipal soft deleted: {}", expediente.getNumeroExpediente());
    }

    public ExpedientePrincipalDTO update(Long id, ExpedientePrincipalDTO expedientePrincipalDetails) {
        ExpedientePrincipal existingExpediente = expedientePrincipalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Expediente principal no encontrado"));

        // Validar número de expediente único (si ha cambiado)
        if (!existingExpediente.getNumeroExpediente().equals(expedientePrincipalDetails.getNumeroExpediente())) {
            boolean exists = expedientePrincipalRepository.findByNumeroExpediente(expedientePrincipalDetails.getNumeroExpediente())
                    .stream()
                    .anyMatch(ep -> ep.getActive() && !ep.getId().equals(id));

            if (exists) {
                throw new IllegalArgumentException("Ya existe un expediente con número: " + expedientePrincipalDetails.getNumeroExpediente());
            }
        }

        // Actualizar campos básicos
        existingExpediente.setNumeroExpediente(expedientePrincipalDetails.getNumeroExpediente());
        existingExpediente.setNumeroSolicitud(expedientePrincipalDetails.getNumeroSolicitud());
        existingExpediente.setNumeroRegistro(expedientePrincipalDetails.getNumeroRegistro());
        existingExpediente.setFechaRegistro(expedientePrincipalDetails.getFechaRegistro());
        existingExpediente.setFechaInicio(expedientePrincipalDetails.getFechaInicio());
        existingExpediente.setFechaFinalizacion(expedientePrincipalDetails.getFechaFinalizacion());
        existingExpediente.setDescripcion(expedientePrincipalDetails.getDescripcion());
        existingExpediente.setReferenciaCatastral(expedientePrincipalDetails.getReferenciaCatastral());
        existingExpediente.setUbicacion(expedientePrincipalDetails.getUbicacion());
        existingExpediente.setObservaciones(expedientePrincipalDetails.getObservaciones());
        existingExpediente.setUpdatedAt(LocalDateTime.now());

        // Actualizar relaciones
        existingExpediente.setEstado(estadoExpedienteRepository.findById(expedientePrincipalDetails.getEstadoExpedienteId())
                .orElseThrow(() -> new NoSuchElementException("Estado de expediente no encontrado")));

        existingExpediente.setDepartamento(departamentoRepository.findById(expedientePrincipalDetails.getDepartamentoId())
                .orElseThrow(() -> new NoSuchElementException("Departamento no encontrado")));

        existingExpediente.setClasificacion(clasificacionRepository.findById(expedientePrincipalDetails.getClasificacionId())
                .orElseThrow(() -> new NoSuchElementException("Clasificación no encontrada")));

        // Validar y actualizar peticionario/empresa (solo uno puede estar presente)
        if (expedientePrincipalDetails.getEmpresaId() != null && expedientePrincipalDetails.getPeticionarioId() != null) {
            throw new IllegalArgumentException("Un expediente no puede tener tanto empresa como peticionario");
        }

        if (expedientePrincipalDetails.getEmpresaId() != null) {
            existingExpediente.setEmpresa(empresaRepository.findById(expedientePrincipalDetails.getEmpresaId())
                    .orElseThrow(() -> new NoSuchElementException("Empresa no encontrada")));
            existingExpediente.setPeticionario(null);
        } else if (expedientePrincipalDetails.getPeticionarioId() != null) {
            existingExpediente.setPeticionario(peticionarioRepository.findById(expedientePrincipalDetails.getPeticionarioId())
                    .orElseThrow(() -> new NoSuchElementException("Peticionario no encontrado")));
            existingExpediente.setEmpresa(null);
        } else {
            throw new IllegalArgumentException("Un expediente debe tener empresa o peticionario");
        }

        ExpedientePrincipal updatedExpediente = expedientePrincipalRepository.save(existingExpediente);
        log.info("ExpedientePrincipal updated: {}", updatedExpediente.getNumeroExpediente());

        return convertToDTO(updatedExpediente);
    }
    
    public ExpedientePrincipalDTO updateEstado(Long id, Long estadoExpedienteId) {
        log.info("Actualizando estado del expediente principal ID: {} a estado ID: {}", id, estadoExpedienteId);
        
        ExpedientePrincipal existingExpediente = expedientePrincipalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Expediente principal no encontrado"));

        // Solo actualizar el estado
        existingExpediente.setEstado(estadoExpedienteRepository.findById(estadoExpedienteId)
                .orElseThrow(() -> new NoSuchElementException("Estado de expediente no encontrado")));
        existingExpediente.setUpdatedAt(LocalDateTime.now());

        ExpedientePrincipal updatedExpediente = expedientePrincipalRepository.save(existingExpediente);
        log.info("Estado del ExpedientePrincipal actualizado: {}", updatedExpediente.getNumeroExpediente());

        return convertToDTO(updatedExpediente);
    }

    private ExpedientePrincipalDTO convertToDTO(ExpedientePrincipal expedientePrincipal) {
        ExpedientePrincipalDTO dto = new ExpedientePrincipalDTO();
        dto.setId(expedientePrincipal.getId());
        dto.setNumeroExpediente(expedientePrincipal.getNumeroExpediente());
        dto.setNumeroSolicitud(expedientePrincipal.getNumeroSolicitud());
        dto.setNumeroRegistro(expedientePrincipal.getNumeroRegistro());
        dto.setFechaRegistro(expedientePrincipal.getFechaRegistro());
        dto.setFechaInicio(expedientePrincipal.getFechaInicio());
        dto.setFechaFinalizacion(expedientePrincipal.getFechaFinalizacion());
        dto.setDescripcion(expedientePrincipal.getDescripcion());
        dto.setReferenciaCatastral(expedientePrincipal.getReferenciaCatastral());
        dto.setUbicacion(expedientePrincipal.getUbicacion());
        dto.setObservaciones(expedientePrincipal.getObservaciones());

        // IDs de relaciones
        dto.setEstadoExpedienteId(expedientePrincipal.getEstado() != null ? expedientePrincipal.getEstado().getId() : null);
        dto.setDepartamentoId(expedientePrincipal.getDepartamento() != null ? expedientePrincipal.getDepartamento().getId() : null);
        dto.setClasificacionId(expedientePrincipal.getClasificacion() != null ? expedientePrincipal.getClasificacion().getId() : null);
        dto.setEmpresaId(expedientePrincipal.getEmpresa() != null ? expedientePrincipal.getEmpresa().getId() : null);
        dto.setPeticionarioId(expedientePrincipal.getPeticionario() != null ? expedientePrincipal.getPeticionario().getId() : null);

        // Lista de IDs de expedientes secundarios activos
        List<Long> expedientesSecundariosIds = expedientePrincipal.getExpedientesSecundarios()
                .stream()
                .filter(ExpedienteSecundario::getActive)
                .map(ExpedienteSecundario::getId)
                .collect(Collectors.toList());
        dto.setExpedienteSecundarioIds(expedientesSecundariosIds);

        return dto;
    }

    private ExpedientePrincipal convertToEntity(ExpedientePrincipalDTO dto) {
        ExpedientePrincipal entity = new ExpedientePrincipal();
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