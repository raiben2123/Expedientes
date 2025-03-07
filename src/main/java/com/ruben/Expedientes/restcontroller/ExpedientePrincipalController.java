package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.dto.ExpedientePrincipalDTO;
import com.ruben.Expedientes.model.WebSocketMessage;
import com.ruben.Expedientes.service.ExpedientePrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expedientesprincipales")
public class ExpedientePrincipalController {

    @Autowired
    private ExpedientePrincipalService expedientePrincipalService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public ResponseEntity<List<ExpedientePrincipalDTO>> getAllExpedientesPrincipales() {
        List<ExpedientePrincipalDTO> expedientes = expedientePrincipalService.findAll();
        return ResponseEntity.ok(expedientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpedientePrincipalDTO> getExpedientePrincipalById(@PathVariable Long id) {
        ExpedientePrincipalDTO dto = expedientePrincipalService.findByIdPrincipal(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/expediente/{expediente}")
    public ResponseEntity<List<ExpedientePrincipalDTO>> getExpedientePrincipalByExpediente(@PathVariable String expediente) {
        List<ExpedientePrincipalDTO> expedientes = expedientePrincipalService.findByExpediente(expediente);
        return ResponseEntity.ok(expedientes);
    }

    @PostMapping
    public ResponseEntity<ExpedientePrincipalDTO> createExpedientePrincipal(@RequestBody ExpedientePrincipalDTO expedientePrincipal) {
        ExpedientePrincipalDTO savedDTO = expedientePrincipalService.savePrincipal(expedientePrincipal);
        messagingTemplate.convertAndSend("/topic/expedientes-principales",
                new WebSocketMessage("CREATE", savedDTO));
        return ResponseEntity.ok(savedDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpedientePrincipalDTO> updateExpedientePrincipal(@PathVariable Long id, @RequestBody ExpedientePrincipalDTO expedientePrincipal) {
        ExpedientePrincipalDTO updatedDTO = expedientePrincipalService.update(id, expedientePrincipal);
        if (updatedDTO == null) {
            return ResponseEntity.notFound().build();
        }
        messagingTemplate.convertAndSend("/topic/expedientes-principales",
                new WebSocketMessage("UPDATE", updatedDTO));
        return ResponseEntity.ok(updatedDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpedientePrincipal(@PathVariable Long id) {
        expedientePrincipalService.deletePrincipal(id);
        messagingTemplate.convertAndSend("/topic/expedientes-principales",
                new WebSocketMessage("DELETE", id));
        return ResponseEntity.noContent().build();
    }
}