package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.model.Clasificacion;
import com.ruben.Expedientes.service.ClasificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clasificaciones")
public class ClasificacionController {

    @Autowired
    private ClasificacionService clasificacionService;

    @GetMapping
    public List<Clasificacion> getAllClasificaciones() {
        return clasificacionService.findAll();
    }

    @GetMapping("/{id}")
    public Clasificacion getClasificacionId(@PathVariable Long id) {
        return clasificacionService.findById(id);
    }

    @GetMapping("/nombre/{name}")
    public List<Clasificacion> getClasificacionNombre(@PathVariable String name) {
        return clasificacionService.findByName(name);
    }

    @GetMapping("/siglas/{acronym}")
    public List<Clasificacion> getClasificacionAcronym(@PathVariable String acronym) {
        return clasificacionService.findByAcronym(acronym);
    }

    @PostMapping
    public Clasificacion createClasificacion(@RequestBody Clasificacion clasificacion) {
        return clasificacionService.saveClasificacion(clasificacion);
    }

    @DeleteMapping("/{id}")
    public void deleteClasificacion(@PathVariable Long id) {
        clasificacionService.deleteClasificacion(id);
    }

    @PutMapping("/{id}")
    public Clasificacion updateClasificacion(@PathVariable Long id, @RequestBody Clasificacion clasificacion){
        return clasificacionService.update(id, clasificacion);
    }

}
