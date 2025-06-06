package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.dto.DepartamentoDTO;
import com.ruben.Expedientes.service.DepartamentoService;
import com.ruben.Expedientes.service.WebSocketNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    @Autowired
    private DepartamentoService departamentoService;

    @Autowired
    private WebSocketNotificationService notificationService;

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
        DepartamentoDTO savedDepartamento = departamentoService.saveDepartamento(departamentoDTO);
        notificationService.notifyCreated(WebSocketNotificationService.EntityType.DEPARTAMENTOS, savedDepartamento);
        return savedDepartamento;
    }

    @PutMapping("/{id}")
    public DepartamentoDTO updateDepartamento(@PathVariable Long id, @RequestBody DepartamentoDTO departamentoDTO) {
        DepartamentoDTO updatedDepartamento = departamentoService.update(id, departamentoDTO);
        notificationService.notifyUpdated(WebSocketNotificationService.EntityType.DEPARTAMENTOS, updatedDepartamento);
        return updatedDepartamento;
    }

    @DeleteMapping("/{id}")
    public void deleteDepartamento(@PathVariable Long id) {
        departamentoService.deleteDepartamento(id);
        notificationService.notifyDeleted(WebSocketNotificationService.EntityType.DEPARTAMENTOS, id);
    }
}