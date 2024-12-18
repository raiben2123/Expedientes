package com.ruben.Expedientes.service;

import com.ruben.Expedientes.model.Empresa;
import com.ruben.Expedientes.model.Peticionario;
import com.ruben.Expedientes.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    public Empresa findById(Long id){
        return empresaRepository.findById(id).orElse(null);
    }

    public List<Empresa> findByCif(String cif){
        return empresaRepository.findByCif(cif);
    }

    public List<Empresa> findByName(String name){
        return empresaRepository.findByName(name);
    }

    public List<Empresa> findByAddress(String address){
        return empresaRepository.findByAddress(address);
    }

    public List<Empresa> findByTlf(String tlf){
        return empresaRepository.findByTlf(tlf);
    }

    public List<Empresa> findByEmail(String email){
        return empresaRepository.findByEmail(email);
    }

    public List<Empresa> findByRepresentante(Peticionario peticionario){
        //TODO hay que pasar el dni y que busque el dni en la tabla de peticionarios...
        return empresaRepository.findByRepresentante(peticionario);
    }

    public Empresa saveEmpresa(Empresa empresa){
        return empresaRepository.save(empresa);
    }

    public List<Empresa> findAll(){
        return empresaRepository.findAll();
    }

    public void deleteEmpresa(Long id){
        empresaRepository.deleteById(id);
    }

//    public Empresa update(Long id, Empresa empresaDetails){
//        Empresa empresa = empresaRepository.findById(id).orElse(null);
//        if (empresa != null){
//            empresa.setCif(empresaDetails.getCif());
//            empresa.setName(empresaDetails.getName());
//            empresa.setAddress(empresaDetails.getAddress());
//            empresa.setTlf(empresaDetails.getTlf());
//            empresa.setEmail(empresaDetails.getEmail());
//            empresa.setRepresentante(empresaDetails.getRepresentante());
//
//            return empresaRepository.save(empresa);
//        }
//        return null;
//    }
}
