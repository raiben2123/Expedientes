package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.dto.DepartamentoDTO;
import java.util.NoSuchElementException;
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
    public List<DepartamentoDTO> getAllDepartamentos() {
        return departamentoService.findAll();
    }

    @GetMapping("/{id}")
    public DepartamentoDTO getDepartamentoId(@PathVariable Long id) {
        DepartamentoDTO departamento = departamentoService.findById(id);
        if (departamento == null) {
            throw new NoSuchElementException("Departamento no encontrado con id: " + id);
        }
        return departamento;
    }

    @GetMapping("/nombre/{nombre}")
    public List<DepartamentoDTO> getDepartamentoNombre(@PathVariable String nombre) {
        return departamentoService.findByName(nombre);
    }

    @PostMapping
    public DepartamentoDTO createDepartamento(@RequestBody DepartamentoDTO departamentoDTO) {
        return departamentoService.saveDepartamento(departamentoDTO);
    }

    @PutMapping("/{id}")
    public DepartamentoDTO updateDepartamento(@PathVariable Long id, @RequestBody DepartamentoDTO departamentoDTO) {
        return departamentoService.update(id, departamentoDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteDepartamento(@PathVariable Long id) {
        departamentoService.deleteDepartamento(id);
    }
}
