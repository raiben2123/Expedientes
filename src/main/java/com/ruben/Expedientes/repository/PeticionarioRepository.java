package com.ruben.Expedientes.repository;

import com.ruben.Expedientes.model.Peticionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PeticionarioRepository extends JpaRepository<Peticionario, Long> {
    List<Peticionario> findByName(String name);
    List<Peticionario> findBySurname(String surname);
    List<Peticionario> findByAddress(String address);
    List<Peticionario> findByTlf(String tlf);
    List<Peticionario> findByEmail(String email);
    @Query("SELECT p FROM Peticionario p WHERE p.dni = :dni")
    List<Peticionario> findByDni(@Param("dni") String dni);
    @Query("SELECT p FROM Peticionario p WHERE p.nif = :nif")
    List<Peticionario> findByNif(@Param("nif") String nif);
}
