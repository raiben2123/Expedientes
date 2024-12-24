package com.ruben.Expedientes.restcontroller;


import com.ruben.Expedientes.model.Empresa;
import com.ruben.Expedientes.model.Peticionario;
import com.ruben.Expedientes.service.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @GetMapping
    public List<Empresa> getAllEmpresas() {
        return empresaService.findAll();
    }

    @GetMapping("/{id}")
    public Empresa getEmpresaId(@PathVariable Long id) {
        return empresaService.findById(id);
    }

    @GetMapping("/cif/{cif}")
    public List<Empresa> getEmpresaCif(@PathVariable String cif) {
        return empresaService.findByCif(cif);
    }

    @GetMapping("/name/{name}")
    public List<Empresa> getEmpresaNombre(@PathVariable String name) {
        return empresaService.findByName(name);
    }

    @GetMapping("/address/{address}")
    public List<Empresa> getEmpresaAddress(@PathVariable String address){
        return empresaService.findByAddress(address);
    }

    @GetMapping("/tlf/{tlf}")
    public List<Empresa> getEmpresaTlf(@PathVariable String tlf){
        return empresaService.findByTlf(tlf);
    }

    @GetMapping("/email/{email}")
    public List<Empresa> getEmpresaEmail(@PathVariable String email){
        return empresaService.findByEmail(email);
    }

    @GetMapping("/representante")
    public List<Empresa> getEmpresaRepresentante(@RequestBody Peticionario peticionario){
        return empresaService.findByRepresentante(peticionario);
    }

    @PostMapping
    public Empresa createEmpresa(@RequestBody Empresa empresa){
        return empresaService.saveEmpresa(empresa);
    }

    @PutMapping("/{id}")
    public Empresa updateEmpresa(@PathVariable Long id, @RequestBody Empresa empresa){
        return empresaService.update(id, empresa);
    }

    @DeleteMapping("/{id}")
    public void deleteEmpresa(@PathVariable Long id){
        empresaService.deleteEmpresa(id);
    }
}
