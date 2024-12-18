package com.ruben.Expedientes.restcontroller;


import com.ruben.Expedientes.model.*;
import com.ruben.Expedientes.service.ExpedientePrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/expedientesprincipales")
public class ExpedientePrincipalController {

    @Autowired
    private ExpedientePrincipalService expedientePrincipalService;

    @GetMapping
    public List<ExpedientePrincipal> getAllExpedientesPrincipales(){
        return expedientePrincipalService.findAll();
    }

    @GetMapping("/{id}")
    public ExpedientePrincipal getExpedientePrincipalId(@PathVariable Long id){
        return expedientePrincipalService.findByIdPrincipal(id);
    }

    @GetMapping("/{expediente}")
    public List<ExpedientePrincipal> getExpedientePrincipalExpediente(@PathVariable String expediente){
        return expedientePrincipalService.findByExpediente(expediente);
    }

    @GetMapping("/{solicitud}")
    public List<ExpedientePrincipal> getExpedientePrincipalSolicitud(@PathVariable String solicitud){
        return expedientePrincipalService.findBySolicitud(solicitud);
    }

    @GetMapping("/{registro}")
    public List<ExpedientePrincipal> getExpedientePrincipalRegistro(@PathVariable String registro){
        return expedientePrincipalService.findByRegistro(registro);
    }

    @GetMapping("/{fechaRegistro}")
    public List<ExpedientePrincipal> getExpedientePrincipalFechaRegistro(@PathVariable Date fechaRegistro){
        return expedientePrincipalService.findByFechaRegistro(fechaRegistro);
    }

    @GetMapping("/{objeto}")
    public List<ExpedientePrincipal> getExpedientePrincipalObjeto(@PathVariable String objeto){
        return expedientePrincipalService.findByObjeto(objeto);
    }

    @GetMapping("/{referenciaCatastral}")
    public List<ExpedientePrincipal> getExpedientePrincipalReferenciaCatastral(@PathVariable String referenciaCatastral){
        return expedientePrincipalService.findByReferenciaCatastral(referenciaCatastral);
    }

    @GetMapping("/{estadoExpediente}")
    public List<ExpedientePrincipal> getExpedientePrincipalEstadoExpediente(@RequestBody EstadoExpediente estadoExpediente){
        return expedientePrincipalService.findByEstadoExpediente(estadoExpediente);
    }

    @GetMapping("/{departamento}")
    public List<ExpedientePrincipal> getExpedientePrincipalDepartamento(@RequestBody Departamento departamento){
        return expedientePrincipalService.findByDepartamento(departamento);
    }

    @GetMapping("/{clasificacion}")
    public List<ExpedientePrincipal> getExpedientePrincipalClasificacion(@RequestBody Clasificacion clasificacion){
        return expedientePrincipalService.findByClasificacion(clasificacion);
    }

    @GetMapping("/{empresa}")
    public List<ExpedientePrincipal> getExpedientePrincipalEmpresa(@RequestBody Empresa empresa){
        return expedientePrincipalService.findByEmpresa(empresa);
    }

    @GetMapping("/{peticionario}")
    public List<ExpedientePrincipal> getExpedientePrincipalPeticionario(@RequestBody Peticionario peticionario){
        return expedientePrincipalService.findByPeticionario(peticionario);
    }

    @GetMapping("/{fechaInicio}")
    public List<ExpedientePrincipal> getExpedientePrincipalFechaInicio(@PathVariable Date fechaInicio){
        return expedientePrincipalService.findByFechaInicio(fechaInicio);
    }

    @GetMapping("/{secundario}")
    public List<ExpedientePrincipal> getExpedientePrincipalSecundario(@RequestBody ExpedienteSecundario secundario){
        return expedientePrincipalService.findBySecundario(secundario);
    }

    @PostMapping
    public ExpedientePrincipal createExpedientePrincipal(@RequestBody ExpedientePrincipal expedientePrincipal){
        return expedientePrincipalService.savePrincipal(expedientePrincipal);
    }

//    @PutMapping("/{id}")
//    public ExpedientePrincipal updateExpedientePrincipal(@PathVariable Long id, @RequestBody ExpedientePrincipal expedientePrincipal){
//        return expedientePrincipalService.update(id, expedientePrincipal);
//    }

    @DeleteMapping("/{id}")
    public void deleteExpedientePrincipal(@PathVariable Long id){
        expedientePrincipalService.deletePrincipal(id);
    }
}
