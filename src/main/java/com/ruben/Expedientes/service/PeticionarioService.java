package com.ruben.Expedientes.service;

import com.ruben.Expedientes.dto.PeticionarioDTO;
import com.ruben.Expedientes.dto.PeticionarioDNIDTO;
import com.ruben.Expedientes.dto.PeticionarioNIFDTO;
import com.ruben.Expedientes.model.Empresa;
import com.ruben.Expedientes.model.Peticionario;
import com.ruben.Expedientes.model.PeticionarioDNI;
import com.ruben.Expedientes.model.PeticionarioNIF;
import com.ruben.Expedientes.repository.EmpresaRepository;
import com.ruben.Expedientes.repository.PeticionarioRepository;
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
public class PeticionarioService {

    private final PeticionarioRepository peticionarioRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public List<PeticionarioDTO> findAll() {
        return peticionarioRepository.findAll().stream()
                .filter(Peticionario::getActive) // Solo activos
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PeticionarioDTO findById(Long id) {
        Peticionario peticionario = peticionarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Peticionario no encontrado con ID: " + id));

        if (!peticionario.getActive()) {
            throw new NoSuchElementException("Peticionario no encontrado (inactivo)");
        }

        return convertToDTO(peticionario);
    }

    public PeticionarioDTO save(PeticionarioDTO peticionarioDTO) {
        // Validar que no existe un peticionario con el mismo documento
        validateUniqueDocument(peticionarioDTO, null);

        Peticionario peticionario = convertToEntity(peticionarioDTO);
        peticionario.setCreatedAt(LocalDateTime.now());
        peticionario.setUpdatedAt(LocalDateTime.now());

        Peticionario savedPeticionario = peticionarioRepository.save(peticionario);
        log.info("Peticionario created: {} ({})", savedPeticionario.getNombreCompleto(),
                savedPeticionario.getTipoPeticionario());

        return convertToDTO(savedPeticionario);
    }

    public PeticionarioDTO update(Long id, PeticionarioDTO peticionarioDetails) {
        Peticionario existingPeticionario = peticionarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Peticionario no encontrado con ID: " + id));

        // Validar que no existe otro peticionario con el mismo documento
        validateUniqueDocument(peticionarioDetails, id);

        // Actualizar campos comunes
        existingPeticionario.setName(peticionarioDetails.getName());
        existingPeticionario.setSurname(peticionarioDetails.getSurname());
        existingPeticionario.setAddress(peticionarioDetails.getAddress());
        existingPeticionario.setTlf(peticionarioDetails.getTlf());
        existingPeticionario.setEmail(peticionarioDetails.getEmail());
        existingPeticionario.setUpdatedAt(LocalDateTime.now());

        // Actualizar empresa que representa
        if (peticionarioDetails.getRepresentaId() != null) {
            Empresa empresa = empresaRepository.findById(peticionarioDetails.getRepresentaId())
                    .orElseThrow(() -> new NoSuchElementException("Empresa no encontrada"));
            existingPeticionario.setRepresenta(empresa);
        } else {
            existingPeticionario.setRepresenta(null);
        }

        // Actualizar documento según el tipo
        if (existingPeticionario instanceof PeticionarioDNI && peticionarioDetails instanceof PeticionarioDNIDTO) {
            PeticionarioDNI dniPeticionario = (PeticionarioDNI) existingPeticionario;
            String dni = ((PeticionarioDNIDTO) peticionarioDetails).getDni();
            dniPeticionario.setDni(dni);

        } else if (existingPeticionario instanceof PeticionarioNIF && peticionarioDetails instanceof PeticionarioNIFDTO) {
            PeticionarioNIF nifPeticionario = (PeticionarioNIF) existingPeticionario;
            String nif = ((PeticionarioNIFDTO) peticionarioDetails).getNif();
            nifPeticionario.setNif(nif);
        } else {
            throw new IllegalArgumentException("Tipo de peticionario no coincide con el DTO proporcionado");
        }

        Peticionario updatedPeticionario = peticionarioRepository.save(existingPeticionario);
        log.info("Peticionario updated: {} ({})", updatedPeticionario.getNombreCompleto(),
                updatedPeticionario.getTipoPeticionario());

        return convertToDTO(updatedPeticionario);
    }

    public void deletePeticionario(Long id) {
        Peticionario peticionario = peticionarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Peticionario no encontrado con ID: " + id));

        // Soft delete
        peticionario.setActive(false);
        peticionario.setUpdatedAt(LocalDateTime.now());
        peticionarioRepository.save(peticionario);

        log.info("Peticionario soft deleted: {} ({})", peticionario.getNombreCompleto(),
                peticionario.getTipoPeticionario());
    }

    // Métodos de utilidad privados

    private void validateUniqueDocument(PeticionarioDTO dto, Long excludeId) {
        String document = null;
        String tipoDocumento = null;

        if (dto instanceof PeticionarioDNIDTO) {
            document = ((PeticionarioDNIDTO) dto).getDni();
            tipoDocumento = "DNI";
        } else if (dto instanceof PeticionarioNIFDTO) {
            document = ((PeticionarioNIFDTO) dto).getNif();
            tipoDocumento = "NIF";
        }

        if (document != null) {
            List<Peticionario> existing = tipoDocumento.equals("DNI")
                    ? peticionarioRepository.findByDni(document)
                    : peticionarioRepository.findByNif(document);

            boolean documentExists = existing.stream()
                    .anyMatch(p -> p.getActive() && !p.getId().equals(excludeId));

            if (documentExists) {
                throw new IllegalArgumentException("Ya existe un peticionario con " + tipoDocumento + ": " + document);
            }
        }
    }

    private PeticionarioDTO convertToDTO(Peticionario peticionario) {
        PeticionarioDTO dto;

        if (peticionario instanceof PeticionarioDNI) {
            PeticionarioDNI dniPeticionario = (PeticionarioDNI) peticionario;
            dto = new PeticionarioDNIDTO();
            ((PeticionarioDNIDTO) dto).setDni(dniPeticionario.getDni());
        } else if (peticionario instanceof PeticionarioNIF) {
            PeticionarioNIF nifPeticionario = (PeticionarioNIF) peticionario;
            dto = new PeticionarioNIFDTO();
            ((PeticionarioNIFDTO) dto).setNif(nifPeticionario.getNif());
        } else {
            throw new IllegalStateException("Tipo de peticionario no reconocido: " + peticionario.getClass());
        }

        // Campos comunes
        dto.setId(peticionario.getId());
        dto.setName(peticionario.getName());
        dto.setSurname(peticionario.getSurname());
        dto.setAddress(peticionario.getAddress());
        dto.setTlf(peticionario.getTlf());
        dto.setEmail(peticionario.getEmail());
        dto.setRepresentaId(peticionario.getRepresenta() != null ? peticionario.getRepresenta().getId() : null);

        return dto;
    }

    private Peticionario convertToEntity(PeticionarioDTO dto) {
        Peticionario entity;

        if (dto instanceof PeticionarioDNIDTO) {
            PeticionarioDNIDTO dniDTO = (PeticionarioDNIDTO) dto;
            entity = new PeticionarioDNI();
            ((PeticionarioDNI) entity).setDni(dniDTO.getDni());
        } else if (dto instanceof PeticionarioNIFDTO) {
            PeticionarioNIFDTO nifDTO = (PeticionarioNIFDTO) dto;
            entity = new PeticionarioNIF();
            ((PeticionarioNIF) entity).setNif(nifDTO.getNif());
        } else {
            throw new IllegalArgumentException("Tipo de DTO de peticionario no soportado: " + dto.getClass());
        }

        // Campos comunes
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setAddress(dto.getAddress());
        entity.setTlf(dto.getTlf());
        entity.setEmail(dto.getEmail());
        entity.setActive(true);

        // Empresa que representa
        if (dto.getRepresentaId() != null) {
            Empresa empresa = empresaRepository.findById(dto.getRepresentaId())
                    .orElseThrow(() -> new NoSuchElementException("Empresa no encontrada con ID: " + dto.getRepresentaId()));
            entity.setRepresenta(empresa);
        }

        return entity;
    }
}