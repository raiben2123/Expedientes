package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.model.*;
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
    public List<ExpedienteSecundario> getAllExpedientesSecundarios(){
        return expedienteSecundarioService.findAll();
    }

    @GetMapping("/{id}")
    public ExpedienteSecundario getExpedienteSecundarioId(@PathVariable Long id){
        return expedienteSecundarioService.findByIdSecundario(id);
    }

    @GetMapping("/{expediente}")
    public List<ExpedienteSecundario> getExpedienteSecundarioExpediente(@PathVariable String expediente){
        return expedienteSecundarioService.findByExpediente(expediente);
    }

    @GetMapping("/{solicitud}")
    public List<ExpedienteSecundario> getExpedienteSecundarioSolicitud(@PathVariable String solicitud){
        return expedienteSecundarioService.findBySolicitud(solicitud);
    }

    @GetMapping("/{registro}")
    public List<ExpedienteSecundario> getExpedienteSecundarioRegistro(@PathVariable String registro){
        return expedienteSecundarioService.findByRegistro(registro);
    }

    @GetMapping("/{fechaRegistro}")
    public List<ExpedienteSecundario> getExpedienteSecundarioFechaRegistro(@PathVariable Date fechaRegistro){
        return expedienteSecundarioService.findByFechaRegistro(fechaRegistro);
    }

    @GetMapping("/{objeto}")
    public List<ExpedienteSecundario> getExpedienteSecundarioObjeto(@PathVariable String objeto){
        return expedienteSecundarioService.findByObjeto(objeto);
    }

    @GetMapping("/{referenciaCatastral}")
    public List<ExpedienteSecundario> getExpedienteSecundarioReferenciaCatastral(@PathVariable String referenciaCatastral){
        return expedienteSecundarioService.findByReferenciaCatastral(referenciaCatastral);
    }

    @GetMapping("/{estadoExpediente}")
    public List<ExpedienteSecundario> getExpedienteSecundarioEstadoExpediente(@RequestBody EstadoExpediente estadoExpediente){
        return expedienteSecundarioService.findByEstadoExpediente(estadoExpediente);
    }

    @GetMapping("/{departamento}")
    public List<ExpedienteSecundario> getExpedienteSecundarioDepartamento(@RequestBody Departamento departamento){
        return expedienteSecundarioService.findByDepartamento(departamento);
    }

    @GetMapping("/{clasificacion}")
    public List<ExpedienteSecundario> getExpedienteSecundarioClasificacion(@RequestBody Clasificacion clasificacion){
        return expedienteSecundarioService.findByClasificacion(clasificacion);
    }

    @GetMapping("/{empresa}")
    public List<ExpedienteSecundario> getExpedienteSecundarioEmpresa(@RequestBody Empresa empresa){
        return expedienteSecundarioService.findByEmpresa(empresa);
    }

    @GetMapping("/{peticionario}")
    public List<ExpedienteSecundario> getExpedienteSecundarioPeticionario(@RequestBody Peticionario peticionario){
        return expedienteSecundarioService.findByPeticionario(peticionario);
    }

    @GetMapping("/{fechaInicio}")
    public List<ExpedienteSecundario> getExpedienteSecundarioFechaInicio(@PathVariable Date fechaInicio){
        return expedienteSecundarioService.findByFechaInicio(fechaInicio);
    }

    @GetMapping("/{principal}")
    public List<ExpedienteSecundario> getExpedienteSecundarioSecundario(@RequestBody ExpedientePrincipal principal){
        return expedienteSecundarioService.findByPrincipal(principal);
    }

    @PostMapping
    public ExpedienteSecundario createExpedienteSecundario(@RequestBody ExpedienteSecundario expedienteSecundario){
        return expedienteSecundarioService.saveSecundario(expedienteSecundario);
    }

    @PutMapping("/{id}")
    public ExpedienteSecundario updateExpedienteSecundario(@PathVariable Long id, @RequestBody ExpedienteSecundario expedienteSecundario){
        return expedienteSecundarioService.update(id, expedienteSecundario);
    }

    @DeleteMapping("/{id}")
    public void deleteExpedienteSecundario(@PathVariable Long id){
        expedienteSecundarioService.deleteSecundario(id);
    }
}
