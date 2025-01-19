package com.ruben.Expedientes.service;

import com.ruben.Expedientes.dto.EmpresaDTO;
import com.ruben.Expedientes.model.Empresa;
import com.ruben.Expedientes.model.Peticionario;
import com.ruben.Expedientes.repository.EmpresaRepository;
import com.ruben.Expedientes.repository.PeticionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class EmpresaService {
    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private PeticionarioRepository peticionarioRepository;

    public EmpresaDTO findById(Long id) {
        Empresa empresa = empresaRepository.findById(id).orElse(null);
        return empresa != null ? convertToDTO(empresa) : null;
    }

    public List<EmpresaDTO> findByCif(String cif) {
        return empresaRepository.findByCif(cif)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EmpresaDTO> findByName(String name) {
        return empresaRepository.findByName(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EmpresaDTO> findByAddress(String address) {
        return empresaRepository.findByAddress(address)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EmpresaDTO> findByTlf(String tlf) {
        return empresaRepository.findByTlf(tlf)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EmpresaDTO> findByEmail(String email) {
        return empresaRepository.findByEmail(email)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EmpresaDTO> findByRepresentante(Long representanteId) {
        Peticionario representante = peticionarioRepository.findById(representanteId)
                .orElseThrow(() -> new NoSuchElementException("Peticionario no encontrado"));
        return empresaRepository.findByRepresentante(representante)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EmpresaDTO saveEmpresa(EmpresaDTO empresaDTO) {
        Empresa empresa = convertToEntity(empresaDTO);
        Empresa savedEmpresa = empresaRepository.save(empresa);
        return convertToDTO(savedEmpresa);
    }

    public List<EmpresaDTO> findAll() {
        return empresaRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteEmpresa(Long id) {
        empresaRepository.deleteById(id);
    }

    public EmpresaDTO update(Long id, EmpresaDTO empresaDetails) {
        Empresa empresa = empresaRepository.findById(id).orElse(null);
        if (empresa != null) {
            empresa.setCif(empresaDetails.getCif());
            empresa.setName(empresaDetails.getName());
            empresa.setAddress(empresaDetails.getAddress());
            empresa.setTlf(empresaDetails.getTlf());
            empresa.setEmail(empresaDetails.getEmail());
            if (empresaDetails.getRepresentanteId() != null) {
                Peticionario representante = peticionarioRepository.findById(empresaDetails.getRepresentanteId())
                        .orElseThrow(() -> new NoSuchElementException("Peticionario no encontrado"));
                empresa.setRepresentante(representante);
            } else {
                empresa.setRepresentante(null); // Establecemos el representante a null si el ID es null
            }
            return convertToDTO(empresaRepository.save(empresa));
        }
        return null;
    }

    private EmpresaDTO convertToDTO(Empresa empresa) {
        EmpresaDTO empresaDTO = new EmpresaDTO();
        empresaDTO.setId(empresa.getId());
        empresaDTO.setName(empresa.getName());
        empresaDTO.setEmail(empresa.getEmail());
        empresaDTO.setCif(empresa.getCif());
        empresaDTO.setAddress(empresa.getAddress());
        empresaDTO.setTlf(empresa.getTlf());
        empresaDTO.setRepresentanteId(empresa.getRepresentante() != null ? empresa.getRepresentante().getId() : null);
        return empresaDTO;
    }

    private Empresa convertToEntity(EmpresaDTO empresaDTO) {
        Empresa empresa = new Empresa();
        empresa.setId(empresaDTO.getId());
        empresa.setCif(empresaDTO.getCif());
        empresa.setName(empresaDTO.getName());
        empresa.setAddress(empresaDTO.getAddress());
        empresa.setTlf(empresaDTO.getTlf());
        empresa.setEmail(empresaDTO.getEmail());

        if (empresaDTO.getRepresentanteId() != null) {
            Peticionario representante = peticionarioRepository.findById(empresaDTO.getRepresentanteId())
                    .orElseThrow(() -> new NoSuchElementException("Peticionario no encontrado"));
            empresa.setRepresentante(representante);
        } else {
            empresa.setRepresentante(null); // Si no hay ID de representante, se establece en null
        }

        return empresa;
    }
}