package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.model.Departamento;
import com.ruben.Expedientes.service.DepartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    @Autowired
    private DepartamentoService departamentoService;

    @GetMapping
    public List<Departamento> getAllDepartamentos() {
        return departamentoService.findAll();
    }

    @GetMapping("/{id}")
    public Departamento getDepartamentoId(@PathVariable Long id){
        return departamentoService.findById(id);
    }

    @GetMapping("/{nombre}")
    public List<Departamento> getDepartamentoNombre(@PathVariable String nombre){
        return departamentoService.findByName(nombre);
    }

    @PostMapping
    public Departamento createDepartamento(@RequestBody Departamento departamento){
        return departamentoService.saveDepartamento(departamento);
    }

    @PutMapping("/{id}")
    public Departamento updateDepartamento(@PathVariable Long id, @RequestBody Departamento departamento){
        return departamentoService.update(id, departamento);
    }

    @DeleteMapping("/{id}")
    public void deleteDepartamento(@PathVariable Long id){
        departamentoService.deleteDepartamento(id);
    }
}
