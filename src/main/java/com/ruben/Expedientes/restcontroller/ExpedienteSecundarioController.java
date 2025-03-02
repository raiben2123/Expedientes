package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.dto.ExpedienteSecundarioDTO;
import com.ruben.Expedientes.model.WebSocketMessage;
import com.ruben.Expedientes.service.ExpedienteSecundarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expedientessecundarios")
public class ExpedienteSecundarioController {

    @Autowired
    private ExpedienteSecundarioService expedienteSecundarioService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public ResponseEntity<List<ExpedienteSecundarioDTO>> getAllExpedientesSecundarios() {
        List<ExpedienteSecundarioDTO> expedientes = expedienteSecundarioService.findAll();
        return ResponseEntity.ok(expedientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpedienteSecundarioDTO> getExpedienteSecundarioById(@PathVariable Long id) {
        ExpedienteSecundarioDTO dto = expedienteSecundarioService.findByIdSecundario(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/expediente/{expediente}")
    public ResponseEntity<List<ExpedienteSecundarioDTO>> getExpedienteSecundarioByExpediente(@PathVariable String expediente) {
        List<ExpedienteSecundarioDTO> expedientes = expedienteSecundarioService.findByExpediente(expediente);
        return ResponseEntity.ok(expedientes);
    }

    @PostMapping
    public ResponseEntity<ExpedienteSecundarioDTO> createExpedienteSecundario(@RequestBody ExpedienteSecundarioDTO expedienteSecundarioDTO) {
        ExpedienteSecundarioDTO savedDTO = expedienteSecundarioService.saveSecundario(expedienteSecundarioDTO);
        messagingTemplate.convertAndSend("/topic/expedientes",
                new WebSocketMessage("CREATE", savedDTO));
        return ResponseEntity.ok(savedDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpedienteSecundarioDTO> updateExpedienteSecundario(@PathVariable Long id, @RequestBody ExpedienteSecundarioDTO expedienteSecundarioDTO) {
        ExpedienteSecundarioDTO updatedDTO = expedienteSecundarioService.update(id, expedienteSecundarioDTO);
        if (updatedDTO == null) {
            return ResponseEntity.notFound().build();
        }
        messagingTemplate.convertAndSend("/topic/expedientes",
                new WebSocketMessage("UPDATE", updatedDTO));
        return ResponseEntity.ok(updatedDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpedienteSecundario(@PathVariable Long id) {
        expedienteSecundarioService.deleteSecundario(id);
        messagingTemplate.convertAndSend("/topic/expedientes",
                new WebSocketMessage("DELETE", id));
        return ResponseEntity.noContent().build();
    }
}