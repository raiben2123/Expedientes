package com.ruben.Expedientes.restcontroller;


import com.ruben.Expedientes.model.EstadoExpediente;
import com.ruben.Expedientes.service.EstadoExpedienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estadosexpedientes")
public class EstadoExpedienteController {

    @Autowired
    private EstadoExpedienteService estadoExpedienteService;

    @GetMapping
    public List<EstadoExpediente> getAllEstadosExpedientes(){
        return estadoExpedienteService.findAll();
    }

    @GetMapping("/{id}")
    public EstadoExpediente getEstadoExpedienteId(@PathVariable Long id){
        return estadoExpedienteService.findById(id);
    }

    @GetMapping("/{name}")
    public List<EstadoExpediente> getEstadoExpedienteName(@PathVariable String name){
        return estadoExpedienteService.findByName(name);
    }

    @PostMapping
    public EstadoExpediente createEstadoExpediente(@RequestBody EstadoExpediente estadoExpediente){
        return estadoExpedienteService.saveExpediente(estadoExpediente);
    }

//    @PutMapping("/{id}")
//    public EstadoExpediente updateEstadoExpediente(@PathVariable Long id, @RequestBody EstadoExpediente estadoExpediente){
//        return estadoExpedienteService.update(id, estadoExpediente);
//    }

    @DeleteMapping("/{id}")
    public void deleteEstadoExpediente(@PathVariable Long id){
        estadoExpedienteService.deleteEstadoExpediente(id);
    }

}
