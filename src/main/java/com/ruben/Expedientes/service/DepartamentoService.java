package com.ruben.Expedientes.service;

import com.ruben.Expedientes.dto.DepartamentoDTO;
import com.ruben.Expedientes.model.Departamento;
import com.ruben.Expedientes.repository.DepartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartamentoService {
    @Autowired
    private DepartamentoRepository departamentoRepository;

    public DepartamentoDTO findById(Long id) {
        Departamento departamento = departamentoRepository.findById(id).orElse(null);
        return departamento != null ? convertToDTO(departamento) : null;
    }

    public List<DepartamentoDTO> findAll() {
        return departamentoRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DepartamentoDTO> findByName(String name) {
        return departamentoRepository.findByName(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DepartamentoDTO saveDepartamento(DepartamentoDTO departamentoDTO) {
        Departamento departamento = convertToEntity(departamentoDTO);
        Departamento savedDepartamento = departamentoRepository.save(departamento);
        return convertToDTO(savedDepartamento);
    }

    public void deleteDepartamento(Long id) {
        departamentoRepository.deleteById(id);
    }

    public DepartamentoDTO update(Long id, DepartamentoDTO departamentoDetails) {
        Departamento departamento = departamentoRepository.findById(id).orElse(null);
        if (departamento != null) {
            departamento.setName(departamentoDetails.getName());
            return convertToDTO(departamentoRepository.save(departamento));
        }
        return null;
    }

    private DepartamentoDTO convertToDTO(Departamento departamento) {
        DepartamentoDTO departamentoDTO = new DepartamentoDTO();
        departamentoDTO.setId(departamento.getId());
        departamentoDTO.setName(departamento.getName());
        return departamentoDTO;
    }

    private Departamento convertToEntity(DepartamentoDTO departamentoDTO) {
        Departamento departamento = new Departamento();
        departamento.setId(departamentoDTO.getId());
        departamento.setName(departamentoDTO.getName());
        return departamento;
    }
}
