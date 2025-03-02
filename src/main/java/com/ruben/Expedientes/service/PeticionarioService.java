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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class PeticionarioService {

    @Autowired
    private PeticionarioRepository peticionarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // Inyectamos SimpMessagingTemplate para WebSocket

    public List<PeticionarioDTO> findAll() {
        return peticionarioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PeticionarioDTO findById(Long id) {
        return convertToDTO(peticionarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Peticionario no encontrado")));
    }

    public PeticionarioDTO save(PeticionarioDTO peticionarioDTO) {
        Peticionario peticionario = convertToEntity(peticionarioDTO);
        Peticionario savedPeticionario = peticionarioRepository.save(peticionario);
        return convertToDTO(savedPeticionario);
    }

    public PeticionarioDTO update(Long id, PeticionarioDTO peticionarioDetails) {
        Peticionario peticionario = peticionarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Peticionario no encontrado"));

        // Actualiza campos comunes
        peticionario.setName(peticionarioDetails.getName());
        peticionario.setSurname(peticionarioDetails.getSurname());
        peticionario.setAddress(peticionarioDetails.getAddress());
        peticionario.setTlf(peticionarioDetails.getTlf());
        peticionario.setEmail(peticionarioDetails.getEmail());

        // Actualiza el representante
        if (peticionarioDetails.getRepresentaId() != null) {
            Empresa empresa = empresaRepository.findById(peticionarioDetails.getRepresentaId())
                    .orElseThrow(() -> new NoSuchElementException("Empresa no encontrada"));
            peticionario.setRepresenta(empresa);
        } else {
            peticionario.setRepresenta(null);
        }

        // Actualiza según el tipo de Peticionario
        if (peticionario instanceof PeticionarioDNI && peticionarioDetails instanceof PeticionarioDNIDTO) {
            PeticionarioDNI dniPeticionario = (PeticionarioDNI) peticionario;
            dniPeticionario.setDni(((PeticionarioDNIDTO) peticionarioDetails).getDni());
        } else if (peticionario instanceof PeticionarioNIF && peticionarioDetails instanceof PeticionarioNIFDTO) {
            PeticionarioNIF nifPeticionario = (PeticionarioNIF) peticionario;
            nifPeticionario.setNif(((PeticionarioNIFDTO) peticionarioDetails).getNif());
        }

        Peticionario updatedPeticionario = peticionarioRepository.save(peticionario);
        return convertToDTO(updatedPeticionario);
    }

    public void deletePeticionario(Long id) {
        if (!peticionarioRepository.existsById(id)) {
            throw new NoSuchElementException("Peticionario no encontrado");
        }
        peticionarioRepository.deleteById(id);
    }

    private PeticionarioDTO convertToDTO(Peticionario peticionario) {
        PeticionarioDTO dto = null;

        if (peticionario instanceof PeticionarioDNI) {
            PeticionarioDNI dniPeticionario = (PeticionarioDNI) peticionario;
            dto = new PeticionarioDNIDTO();
            ((PeticionarioDNIDTO) dto).setDni(dniPeticionario.getDni());
        } else if (peticionario instanceof PeticionarioNIF) {
            PeticionarioNIF nifPeticionario = (PeticionarioNIF) peticionario;
            dto = new PeticionarioNIFDTO();
            ((PeticionarioNIFDTO) dto).setNif(nifPeticionario.getNif());
        }

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
            entity = new PeticionarioDNI();
            ((PeticionarioDNI) entity).setDni(((PeticionarioDNIDTO) dto).getDni());
        } else if (dto instanceof PeticionarioNIFDTO) {
            entity = new PeticionarioNIF();
            ((PeticionarioNIF) entity).setNif(((PeticionarioNIFDTO) dto).getNif());
        } else {
            entity = new Peticionario() {
                @Override
                public String getTipoPeticionario() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
        }

        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setAddress(dto.getAddress());
        entity.setTlf(dto.getTlf());
        entity.setEmail(dto.getEmail());

        if (dto.getRepresentaId() != null) {
            entity.setRepresenta(empresaRepository.findById(dto.getRepresentaId())
                    .orElseThrow(() -> new RuntimeException("Empresa not found with id: " + dto.getRepresentaId())));
        } else {
            entity.setRepresenta(null);
        }

        return entity;
    }
}