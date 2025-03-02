package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.dto.ClasificacionDTO;
import com.ruben.Expedientes.model.WebSocketMessage;
import com.ruben.Expedientes.service.ClasificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/clasificaciones")
public class ClasificacionController {

    @Autowired
    private ClasificacionService clasificacionService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public List<ClasificacionDTO> getAllClasificaciones() {
        return clasificacionService.findAll();
    }

    @GetMapping("/{id}")
    public ClasificacionDTO getClasificacionId(@PathVariable Long id) {
        ClasificacionDTO clasificacion = clasificacionService.findById(id);
        if (clasificacion == null) {
            throw new NoSuchElementException("Clasificación no encontrada con id: " + id);
        }
        return clasificacion;
    }

    @GetMapping("/nombre/{name}")
    public List<ClasificacionDTO> getClasificacionNombre(@PathVariable String name) {
        return clasificacionService.findByName(name);
    }

    @GetMapping("/siglas/{acronym}")
    public List<ClasificacionDTO> getClasificacionAcronym(@PathVariable String acronym) {
        return clasificacionService.findByAcronym(acronym);
    }

    @PostMapping
    public ClasificacionDTO createClasificacion(@RequestBody ClasificacionDTO clasificacionDTO) {
        ClasificacionDTO savedClasificacion = clasificacionService.saveClasificacion(clasificacionDTO);
        messagingTemplate.convertAndSend("/topic/clasificaciones",
                new WebSocketMessage("CREATE", savedClasificacion));
        return savedClasificacion;
    }

    @PutMapping("/{id}")
    public ClasificacionDTO updateClasificacion(@PathVariable Long id, @RequestBody ClasificacionDTO clasificacionDTO) {
        ClasificacionDTO updatedClasificacion = clasificacionService.update(id, clasificacionDTO);
        messagingTemplate.convertAndSend("/topic/clasificaciones",
                new WebSocketMessage("UPDATE", updatedClasificacion));
        return updatedClasificacion;
    }

    @DeleteMapping("/{id}")
    public void deleteClasificacion(@PathVariable Long id) {
        clasificacionService.deleteClasificacion(id);
        messagingTemplate.convertAndSend("/topic/clasificaciones",
                new WebSocketMessage("DELETE", id));
    }
}