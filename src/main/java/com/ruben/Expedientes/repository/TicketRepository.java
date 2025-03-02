package com.ruben.Expedientes.repository;

import com.ruben.Expedientes.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
