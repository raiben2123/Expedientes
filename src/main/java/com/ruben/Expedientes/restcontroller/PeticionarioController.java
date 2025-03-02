package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.dto.PeticionarioDNIDTO;
import com.ruben.Expedientes.dto.PeticionarioDTO;
import com.ruben.Expedientes.dto.PeticionarioNIFDTO;
import com.ruben.Expedientes.model.WebSocketMessage;
import com.ruben.Expedientes.service.PeticionarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/peticionarios")
@RequiredArgsConstructor
public class PeticionarioController {

    private final PeticionarioService peticionarioService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public List<PeticionarioDTO> getAllPeticionarios() {
        return peticionarioService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeticionarioDTO> getPeticionarioId(@PathVariable Long id) {
        return ResponseEntity.ok(peticionarioService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PeticionarioDTO> createPeticionario(@RequestBody PeticionarioDTO peticionarioDTO) {
        PeticionarioDTO savedDTO = peticionarioService.save(peticionarioDTO);
        messagingTemplate.convertAndSend("/topic/peticionarios",
                new WebSocketMessage("CREATE", savedDTO));
        return ResponseEntity.ok(savedDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PeticionarioDTO> updatePeticionario(@PathVariable Long id, @RequestBody PeticionarioDTO peticionarioDetails) {
        PeticionarioDTO updatedDTO = peticionarioService.update(id, peticionarioDetails);
        messagingTemplate.convertAndSend("/topic/peticionarios",
                new WebSocketMessage("UPDATE", updatedDTO));
        return ResponseEntity.ok(updatedDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePeticionario(@PathVariable Long id) {
        peticionarioService.deletePeticionario(id);
        messagingTemplate.convertAndSend("/topic/peticionarios",
                new WebSocketMessage("DELETE", id));
        return ResponseEntity.noContent().build();
    }

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