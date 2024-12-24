package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.model.Peticionario;
import com.ruben.Expedientes.service.PeticionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/peticionarios")
public class PeticionarioController {

    @Autowired
    private PeticionarioService peticionarioService;

    @GetMapping
    public List<Peticionario> getAllPeticionarios(){
        return peticionarioService.findAll();
    }

    @GetMapping("/{id}")
    public Peticionario getPeticionarioId(@PathVariable Long id){
        return peticionarioService.findById(id);
    }

    @GetMapping("/{name}")
    public List<Peticionario> getPeticionarioName(@PathVariable String name){
        return peticionarioService.findByName(name);
    }

    @GetMapping("/{surname}")
    public List<Peticionario> getPeticionarioSurname(@PathVariable String surname){
        return peticionarioService.findBySurname(surname);
    }

    @GetMapping("/{address}")
    public List<Peticionario> getPeticionarioAddress(@PathVariable String address){
        return peticionarioService.findByAddress(address);
    }

    @GetMapping("/{tlf}")
    public List<Peticionario> getPeticionarioTlf(@PathVariable String tlf){
        return peticionarioService.findByTlf(tlf);
    }

    @GetMapping("/{email}")
    public List<Peticionario> getPeticionarioEmail(@PathVariable String email){
        return peticionarioService.findByEmail(email);
    }

    @GetMapping("/{dni}")
    public List<Peticionario> getPeticionarioDni(@PathVariable String dni){
        return peticionarioService.findByDni(dni);
    }

    @GetMapping("/{nif}")
    public List<Peticionario> getPeticionarioNif(@PathVariable String nif){
        return peticionarioService.findByNif(nif);
    }

    @PostMapping
    public Peticionario createPeticionario(@RequestBody Peticionario peticionario){
        return peticionarioService.save(peticionario);
    }

    @PutMapping("/{id}")
    public Peticionario updatePeticionario(@PathVariable Long id, @RequestBody Peticionario peticionario){
        return peticionarioService.update(id, peticionario);
    }

    @DeleteMapping("/{id}")
    public void deletePeticionario(@PathVariable Long id){
        peticionarioService.deletePeticionario(id);
    }

}
