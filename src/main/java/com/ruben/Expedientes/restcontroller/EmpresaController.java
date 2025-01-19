package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.dto.EmpresaDTO;
import com.ruben.Expedientes.service.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;

import java.rmi.NotBoundException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @GetMapping
    public List<EmpresaDTO> getAllEmpresas() {
        return empresaService.findAll();
    }

    @GetMapping("/{id}")
    public EmpresaDTO getEmpresaId(@PathVariable Long id) {
        return empresaService.findById(id);
    }

    @GetMapping("/cif/{cif}")
    public List<EmpresaDTO> getEmpresaCif(@PathVariable String cif) {
        List<EmpresaDTO> empresas = empresaService.findByCif(cif);
        if (empresas.isEmpty()) {
            return Collections.emptyList(); // Devuelve una lista vacía si no se encuentra ninguna empresa
        }
        return empresas;
    }

    @GetMapping("/name/{name}")
    public List<EmpresaDTO> getEmpresaNombre(@PathVariable String name) {
        return empresaService.findByName(name);
    }

    @GetMapping("/address/{address}")
    public List<EmpresaDTO> getEmpresaAddress(@PathVariable String address) {
        return empresaService.findByAddress(address);
    }

    @GetMapping("/tlf/{tlf}")
    public List<EmpresaDTO> getEmpresaTlf(@PathVariable String tlf) {
        return empresaService.findByTlf(tlf);
    }

    @GetMapping("/email/{email}")
    public List<EmpresaDTO> getEmpresaEmail(@PathVariable String email) {
        return empresaService.findByEmail(email);
    }

    @GetMapping("/representante/{representanteId}")
    public List<EmpresaDTO> getEmpresaRepresentante(@PathVariable Long representanteId) {
        return empresaService.findByRepresentante(representanteId);
    }

    @PostMapping
    public EmpresaDTO createEmpresa(@RequestBody EmpresaDTO empresaDTO) {
        return empresaService.saveEmpresa(empresaDTO);
    }

    @PutMapping("/{id}")
    public EmpresaDTO updateEmpresa(@PathVariable Long id, @RequestBody EmpresaDTO empresaDTO) {
        return empresaService.update(id, empresaDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteEmpresa(@PathVariable Long id) {
        empresaService.deleteEmpresa(id);
    }
}