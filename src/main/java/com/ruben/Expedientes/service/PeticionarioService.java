package com.ruben.Expedientes.service;

import com.ruben.Expedientes.model.*;
import com.ruben.Expedientes.repository.PeticionarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeticionarioService {

    @Autowired
    PeticionarioRepository peticionarioRepository;

    @Autowired
    EmpresaService empresaService;

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
        // Buscar la empresa que representa al peticionario
        if (peticionario.getRepresenta() != null) {
            // Suponiendo que el peticionario tiene un campo para identificar la empresa
            String cifOrNif = peticionario.getRepresenta().getCif(); // o getNif() dependiendo del caso
            Empresa empresa = empresaService.findByCif(cifOrNif).stream().findFirst().orElse(null);

            if (empresa != null) {
                peticionario.setRepresenta(empresa);
            } else {
                // Manejar el caso en que no se encuentra la empresa
                throw new EntityNotFoundException("Empresa no encontrada para el CIF/NIF: " + cifOrNif);
            }
        }

        return peticionarioRepository.save(peticionario);
    }

    @Transactional
    public void deletePeticionario(Long id) {
        Peticionario peticionario = peticionarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Peticionario not found"));
        if(peticionario.getRepresenta() != null) {
            // Desvincular la relación
            peticionario.getRepresenta().setRepresentante(null);
            peticionario.setRepresenta(null);
        }
        peticionarioRepository.delete(peticionario);
    }

    public List<Peticionario> findAll(){
        return peticionarioRepository.findAll();
    }

    public Peticionario update(Long id, Peticionario peticionarioDetails) {
        // Buscar el peticionario existente
        Peticionario peticionario = peticionarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Peticionario no encontrado con ID: " + id));

        // Actualizar los datos comunes
        peticionario.setName(peticionarioDetails.getName());
        peticionario.setSurname(peticionarioDetails.getSurname());
        peticionario.setAddress(peticionarioDetails.getAddress());
        peticionario.setEmail(peticionarioDetails.getEmail());
        peticionario.setTlf(peticionarioDetails.getTlf());

        // Manejar la relación con la empresa
        if (peticionarioDetails.getRepresenta() != null) {
            String cifOrNif = peticionarioDetails.getRepresenta().getCif(); // o getNif(), según corresponda
            Empresa empresa = empresaService.findByCif(cifOrNif).stream().findFirst().orElse(null);

            if (empresa != null) {
                peticionario.setRepresenta(empresa);
            } else {
                throw new EntityNotFoundException("Empresa no encontrada para el CIF/NIF: " + cifOrNif);
            }
        } else {
            peticionario.setRepresenta(null); // Si ya no representa una empresa
        }

        // Actualizar los campos específicos (DNI o NIF)
        if (peticionarioDetails instanceof PeticionarioDNI) {
            ((PeticionarioDNI) peticionario).setDni(((PeticionarioDNI) peticionarioDetails).getDni());
        } else if (peticionarioDetails instanceof PeticionarioNIF) {
            ((PeticionarioNIF) peticionario).setNif(((PeticionarioNIF) peticionarioDetails).getNif());
        }

        // Guardar y devolver el peticionario actualizado
        return peticionarioRepository.save(peticionario);
    }

}
