package com.ruben.Expedientes.repository;

import com.ruben.Expedientes.model.Clasificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClasificacionRepository extends JpaRepository<Clasificacion, Long> {
    List<Clasificacion> findByName(String name);
    List<Clasificacion> findByAcronym(String siglas);
}
