package com.ruben.Expedientes.repository;

import com.ruben.Expedientes.model.EstadoExpediente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstadoExpedienteRepository extends JpaRepository<EstadoExpediente, Long> {
    List<EstadoExpediente> findByName(String name);
}
