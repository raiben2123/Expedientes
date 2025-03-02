package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.dto.EstadoExpedienteDTO;
import com.ruben.Expedientes.model.WebSocketMessage;
import com.ruben.Expedientes.service.EstadoExpedienteService;
import com.ruben.Expedientes.service.WebSocketNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estadosexpedientes")
public class EstadoExpedienteController {

    @Autowired
    private EstadoExpedienteService estadoExpedienteService;

    @Autowired
    private WebSocketNotificationService notificationService;

    @GetMapping
    public List<EstadoExpedienteDTO> getAllEstadosExpedientes() {
        return estadoExpedienteService.findAll();
    }

    @GetMapping("/{id}")
    public EstadoExpedienteDTO getEstadoExpedienteId(@PathVariable Long id) {
        return estadoExpedienteService.findById(id);
    }

    @GetMapping("/name/{name}")
    public List<EstadoExpedienteDTO> getEstadoExpedienteName(@PathVariable String name) {
        return estadoExpedienteService.findByName(name);
    }

    @PostMapping
    public EstadoExpedienteDTO createEstadoExpediente(@RequestBody EstadoExpedienteDTO estadoExpedienteDTO) {
        EstadoExpedienteDTO savedEstado = estadoExpedienteService.saveEstadoExpediente(estadoExpedienteDTO);
        notificationService.notifyCreated(WebSocketNotificationService.EntityType.ESTADOS_EXPEDIENTES, savedEstado);
        return savedEstado;
    }

    @PutMapping("/{id}")
    public EstadoExpedienteDTO updateEstadoExpediente(@PathVariable Long id, @RequestBody EstadoExpedienteDTO estadoExpedienteDTO) {
        EstadoExpedienteDTO updatedEstado = estadoExpedienteService.update(id, estadoExpedienteDTO);
        notificationService.notifyUpdated(WebSocketNotificationService.EntityType.ESTADOS_EXPEDIENTES, updatedEstado);
        return updatedEstado;
    }

    @DeleteMapping("/{id}")
    public void deleteEstadoExpediente(@PathVariable Long id) {
        estadoExpedienteService.deleteEstadoExpediente(id);
        notificationService.notifyDeleted(WebSocketNotificationService.EntityType.ESTADOS_EXPEDIENTES, id);
    }
}