package com.ruben.Expedientes.repository;

import com.ruben.Expedientes.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.createdBy")
    List<Ticket> findAllWithCreatedBy();
    
    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.createdBy WHERE t.id = :id")
    Optional<Ticket> findByIdWithCreatedBy(Long id);
}
