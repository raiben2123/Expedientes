package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.dto.ExpedienteSecundarioDTO;
import com.ruben.Expedientes.model.ExpedienteSecundario;
import com.ruben.Expedientes.service.ExpedienteSecundarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/expedientessecundarios")
public class ExpedienteSecundarioController {

    @Autowired
    private ExpedienteSecundarioService expedienteSecundarioService;

    @GetMapping
    public List<ExpedienteSecundarioDTO> getAllExpedientesSecundarios() {
        return expedienteSecundarioService.findAll();
    }

    @GetMapping("/{id}")
    public ExpedienteSecundarioDTO getExpedienteSecundarioId(@PathVariable Long id) {
        return expedienteSecundarioService.findByIdSecundario(id);
    }

    @GetMapping("/expediente/{expediente}")
    public List<ExpedienteSecundarioDTO> getExpedienteSecundarioExpediente(@PathVariable String expediente) {
        return expedienteSecundarioService.findByExpediente(expediente);
    }

//    // Asegúrate de que las rutas de los siguientes métodos sean únicas y correctas.
//    @GetMapping("/solicitud/{solicitud}")
//    public List<ExpedienteSecundarioDTO> getExpedienteSecundarioSolicitud(@PathVariable String solicitud) {
//        return expedienteSecundarioService.findBySolicitud(solicitud);
//    }

//    @GetMapping("/registro/{registro}")
//    public List<ExpedienteSecundarioDTO> getExpedienteSecundarioRegistro(@PathVariable String registro) {
//        return expedienteSecundarioService.findByRegistro(registro);
//    }

//    @GetMapping("/fechaRegistro/{fechaRegistro}")
//    public List<ExpedienteSecundarioDTO> getExpedienteSecundarioFechaRegistro(@PathVariable Date fechaRegistro) {
//        return expedienteSecundarioService.findByFechaRegistro(fechaRegistro);
//    }

    // Otros métodos similares...

    @PostMapping
    public ExpedienteSecundarioDTO createExpedienteSecundario(@RequestBody ExpedienteSecundarioDTO expedienteSecundarioDTO) {
        return expedienteSecundarioService.saveSecundario(expedienteSecundarioDTO);
    }

    @PutMapping("/{id}")
    public ExpedienteSecundarioDTO updateExpedienteSecundario(@PathVariable Long id, @RequestBody ExpedienteSecundarioDTO expedienteSecundarioDTO) {
        return expedienteSecundarioService.update(id, expedienteSecundarioDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteExpedienteSecundario(@PathVariable Long id) {
        expedienteSecundarioService.deleteSecundario(id);
    }
}
