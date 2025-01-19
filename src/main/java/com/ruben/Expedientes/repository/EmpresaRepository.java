package com.ruben.Expedientes.repository;

import com.ruben.Expedientes.model.Empresa;
import com.ruben.Expedientes.model.Peticionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    List<Empresa> findByName(String name);
    List<Empresa> findByAddress(String address);
    List<Empresa> findByTlf(String tlf);
    List<Empresa> findByEmail(String email);
    List<Empresa> findByRepresentante(Peticionario peticionario);
    List<Empresa> findByCif(String cif);
}
