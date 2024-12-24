package com.ruben.Expedientes.service;

import com.ruben.Expedientes.model.Departamento;
import com.ruben.Expedientes.repository.DepartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartamentoService {

    @Autowired
    private DepartamentoRepository departamentoRepository;

    public Departamento findById(Long id){
        return departamentoRepository.findById(id).orElse(null);
    }

    public List<Departamento> findAll(){
        return departamentoRepository.findAll();
    }

    public List<Departamento> findByName(String name){
        return departamentoRepository.findByName(name);
    }

    public Departamento saveDepartamento(Departamento departamento){
        return departamentoRepository.save(departamento);
    }

    public void deleteDepartamento(Long id){
        departamentoRepository.deleteById(id);
    }

    public Departamento update(Long id, Departamento departamentoDetails){
        Departamento departamento = departamentoRepository.findById(id).orElse(null);
        if (departamento != null){
            departamento.setName(departamentoDetails.getName());

            return departamentoRepository.save(departamento);
        }
        return null;
    }
}
