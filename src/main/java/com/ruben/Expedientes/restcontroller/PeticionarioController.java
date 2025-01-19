package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.dto.PeticionarioDNIDTO;
import com.ruben.Expedientes.dto.PeticionarioDTO;
import com.ruben.Expedientes.dto.PeticionarioNIFDTO;
import com.ruben.Expedientes.service.PeticionarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/peticionarios")
@RequiredArgsConstructor
public class PeticionarioController {

    private final PeticionarioService peticionarioService;

    @GetMapping
    public List<PeticionarioDTO> getAllPeticionarios() {
        return peticionarioService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeticionarioDTO> getPeticionarioId(@PathVariable Long id) {
        return ResponseEntity.ok(peticionarioService.findById(id));
    }

    // ... otros métodos de búsqueda ...

    @PostMapping
    public ResponseEntity<PeticionarioDTO> createPeticionario(@RequestBody PeticionarioDTO peticionarioDTO) {
        return ResponseEntity.ok(peticionarioService.save(peticionarioDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PeticionarioDTO> updatePeticionario(@PathVariable Long id, @RequestBody PeticionarioDTO peticionarioDetails) {
        return ResponseEntity.ok(peticionarioService.update(id, peticionarioDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePeticionario(@PathVariable Long id) {
        peticionarioService.deletePeticionario(id);
        return ResponseEntity.noContent().build();
    }

    // Cambiado a un mapeo único para obtener el tipo de peticionario
    @GetMapping("/tipo/{id}")
    public ResponseEntity<String> getTipoPeticionario(@PathVariable Long id) {
        PeticionarioDTO peticionarioDTO = peticionarioService.findById(id);
        if (peticionarioDTO instanceof PeticionarioDNIDTO) {
            return ResponseEntity.ok("DNI");
        } else if (peticionarioDTO instanceof PeticionarioNIFDTO) {
            return ResponseEntity.ok("NIF");
        }
        return ResponseEntity.notFound().build();
    }
}