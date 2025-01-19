package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.dto.ExpedientePrincipalDTO;
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
    public List<ExpedientePrincipalDTO> getAllExpedientesPrincipales(){
        return expedientePrincipalService.findAll();
    }

    @GetMapping("/{id}")
    public ExpedientePrincipalDTO getExpedientePrincipalId(@PathVariable Long id){
        return expedientePrincipalService.findByIdPrincipal(id);
    }

    @GetMapping("/expediente/{expediente}")
    public List<ExpedientePrincipalDTO> getExpedientePrincipalExpediente(@PathVariable String expediente){
        return expedientePrincipalService.findByExpediente(expediente);
    }

    @PostMapping
    public ExpedientePrincipalDTO createExpedientePrincipal(@RequestBody ExpedientePrincipalDTO expedientePrincipal){
        return expedientePrincipalService.savePrincipal(expedientePrincipal);
    }

    @PutMapping("/{id}")
    public ExpedientePrincipalDTO updateExpedientePrincipal(@PathVariable Long id, @RequestBody ExpedientePrincipalDTO expedientePrincipal){
        return expedientePrincipalService.update(id, expedientePrincipal);
    }

    @DeleteMapping("/{id}")
    public void deleteExpedientePrincipal(@PathVariable Long id){
        expedientePrincipalService.deletePrincipal(id);
    }
}
