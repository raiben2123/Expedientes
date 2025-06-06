package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.dto.ExpedientePrincipalDTO;
import com.ruben.Expedientes.dto.UpdateEstadoDTO;
import com.ruben.Expedientes.model.WebSocketMessage;
import com.ruben.Expedientes.service.ExpedientePrincipalService;
import com.ruben.Expedientes.service.WebSocketNotificationService;
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
    private WebSocketNotificationService notificationService;

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
        notificationService.notifyCreated(WebSocketNotificationService.EntityType.EXPEDIENTES_PRINCIPALES, savedDTO);
        return ResponseEntity.ok(savedDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpedientePrincipalDTO> updateExpedientePrincipal(@PathVariable Long id, @RequestBody ExpedientePrincipalDTO expedientePrincipal) {
        ExpedientePrincipalDTO updatedDTO = expedientePrincipalService.update(id, expedientePrincipal);
        if (updatedDTO == null) {
            return ResponseEntity.notFound().build();
        }
        notificationService.notifyUpdated(WebSocketNotificationService.EntityType.EXPEDIENTES_PRINCIPALES, updatedDTO);
        return ResponseEntity.ok(updatedDTO);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<ExpedientePrincipalDTO> updateEstadoExpedientePrincipal(@PathVariable Long id, @RequestBody UpdateEstadoDTO updateEstadoDTO) {
        ExpedientePrincipalDTO updatedDTO = expedientePrincipalService.updateEstado(id, updateEstadoDTO.getEstadoExpedienteId());
        if (updatedDTO == null) {
            return ResponseEntity.notFound().build();
        }
        notificationService.notifyUpdated(WebSocketNotificationService.EntityType.EXPEDIENTES_PRINCIPALES, updatedDTO);
        return ResponseEntity.ok(updatedDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpedientePrincipal(@PathVariable Long id) {
        expedientePrincipalService.deletePrincipal(id);
        notificationService.notifyDeleted(WebSocketNotificationService.EntityType.EXPEDIENTES_PRINCIPALES, id);
        return ResponseEntity.noContent().build();
    }
}