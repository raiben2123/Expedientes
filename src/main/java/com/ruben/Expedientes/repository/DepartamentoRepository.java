package com.ruben.Expedientes.repository;

import com.ruben.Expedientes.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    List<Departamento> findByName(String name);
}
