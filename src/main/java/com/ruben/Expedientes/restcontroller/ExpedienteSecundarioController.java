package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.dto.ExpedienteSecundarioDTO;
import com.ruben.Expedientes.dto.UpdateEstadoSecundariosDTO; // Nuevo DTO
import com.ruben.Expedientes.model.WebSocketMessage;
import com.ruben.Expedientes.service.ExpedienteSecundarioService;
import com.ruben.Expedientes.service.WebSocketNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expedientessecundarios")
public class ExpedienteSecundarioController {

    @Autowired
    private ExpedienteSecundarioService expedienteSecundarioService;

    @Autowired
    private WebSocketNotificationService notificationService;

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
        notificationService.notifyCreated(WebSocketNotificationService.EntityType.EXPEDIENTES_SECUNDARIOS, savedDTO);
        return ResponseEntity.ok(savedDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpedienteSecundarioDTO> updateExpedienteSecundario(@PathVariable Long id, @RequestBody ExpedienteSecundarioDTO expedienteSecundarioDTO) {
        ExpedienteSecundarioDTO updatedDTO = expedienteSecundarioService.update(id, expedienteSecundarioDTO);
        if (updatedDTO == null) {
            return ResponseEntity.notFound().build();
        }
        notificationService.notifyUpdated(WebSocketNotificationService.EntityType.EXPEDIENTES_SECUNDARIOS, updatedDTO);
        return ResponseEntity.ok(updatedDTO);
    }

    // Nuevo endpoint para actualizar estados de múltiples expedientes secundarios
    @PutMapping("/update-estados")
    public ResponseEntity<List<ExpedienteSecundarioDTO>> updateMultipleEstados(@RequestBody UpdateEstadoSecundariosDTO updateDTO) {
        List<ExpedienteSecundarioDTO> updatedDTOs = expedienteSecundarioService.updateMultipleEstados(updateDTO.getIds(), updateDTO.getEstadoExpedienteId());
        updatedDTOs.forEach(dto -> notificationService.notifyUpdated(WebSocketNotificationService.EntityType.EXPEDIENTES_SECUNDARIOS, dto));
        return ResponseEntity.ok(updatedDTOs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpedienteSecundario(@PathVariable Long id) {
        expedienteSecundarioService.deleteSecundario(id);
        notificationService.notifyDeleted(WebSocketNotificationService.EntityType.EXPEDIENTES_SECUNDARIOS, id);
        return ResponseEntity.noContent().build();
    }
}