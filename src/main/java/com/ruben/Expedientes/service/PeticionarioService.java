package com.ruben.Expedientes.service;

import com.ruben.Expedientes.model.ExpedientePrincipal;
import com.ruben.Expedientes.model.Peticionario;
import com.ruben.Expedientes.repository.PeticionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeticionarioService {

    @Autowired
    PeticionarioRepository peticionarioRepository;

    public Peticionario findById(Long id){
        return peticionarioRepository.findById(id).orElse(null);
    }

    public List<Peticionario> findByName(String name){
        return peticionarioRepository.findByName(name);
    }

    public List<Peticionario> findBySurname(String surname){
        return peticionarioRepository.findBySurname(surname);
    }

    public List<Peticionario> findByAddress(String address){
        return peticionarioRepository.findByAddress(address);
    }

    public List<Peticionario> findByTlf(String tlf){
        return peticionarioRepository.findByTlf(tlf);
    }

    public List<Peticionario> findByEmail(String email){
        return peticionarioRepository.findByEmail(email);
    }

    public List<Peticionario> findByDni(String dni){
        return peticionarioRepository.findByDni(dni);
        //TODO
    }

    public List<Peticionario> findByNif(String nif){
        return peticionarioRepository.findByNif(nif);
        //TODO
    }

    public Peticionario save(Peticionario peticionario){
        return peticionarioRepository.save(peticionario);
    }

    public void deletePeticionario(Long id){
        peticionarioRepository.deleteById(id);
    }

    public List<Peticionario> findAll(){
        return peticionarioRepository.findAll();
    }

//    public Peticionario update(Long id, Peticionario peticionarioDetails){
//        Peticionario peticionario = peticionarioRepository.findById(id).orElse(null);
//        if (peticionario != null){
//            peticionario.setName(peticionarioDetails.getName());
//            peticionario.setSurname1(peticionarioDetails.getSurname1());
//            peticionario.setSurname2(peticionarioDetails.getSurname2());
//            peticionario.setAddress(peticionarioDetails.getAddress());
//            peticionario.setEmail(peticionarioDetails.getEmail());
//            peticionario.setRepresenta(peticionarioDetails.getRepresenta());
//            //TODO falta ver como poner el nif y el dni
//            return peticionarioRepository.save(peticionario);
//        }
//        return null;
//    }
}
