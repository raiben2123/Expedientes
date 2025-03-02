package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.dto.EstadoExpedienteDTO;
import com.ruben.Expedientes.model.WebSocketMessage;
import com.ruben.Expedientes.service.EstadoExpedienteService;
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
    private SimpMessagingTemplate messagingTemplate;

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
        messagingTemplate.convertAndSend("/topic/estados-expediente",
                new WebSocketMessage("CREATE", savedEstado));
        return savedEstado;
    }

    @PutMapping("/{id}")
    public EstadoExpedienteDTO updateEstadoExpediente(@PathVariable Long id, @RequestBody EstadoExpedienteDTO estadoExpedienteDTO) {
        EstadoExpedienteDTO updatedEstado = estadoExpedienteService.update(id, estadoExpedienteDTO);
        messagingTemplate.convertAndSend("/topic/estados-expediente",
                new WebSocketMessage("UPDATE", updatedEstado));
        return updatedEstado;
    }

    @DeleteMapping("/{id}")
    public void deleteEstadoExpediente(@PathVariable Long id) {
        estadoExpedienteService.deleteEstadoExpediente(id);
        messagingTemplate.convertAndSend("/topic/estados-expediente",
                new WebSocketMessage("DELETE", id));
    }
}