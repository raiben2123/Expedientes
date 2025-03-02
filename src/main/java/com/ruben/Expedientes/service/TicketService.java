package com.ruben.Expedientes.service;

import com.ruben.Expedientes.model.Ticket;
import com.ruben.Expedientes.model.User;
import com.ruben.Expedientes.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserService userService;

    public Ticket createTicket(Ticket ticket, String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        ticket.setCreatedBy(user);
        Ticket savedTicket = ticketRepository.save(ticket);
        return savedTicket;
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket updateTicketStatus(Long id, String status) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado"));
        ticket.setStatus(status);
        Ticket savedTicket = ticketRepository.save(ticket);
        return savedTicket;
    }

    public void deleteTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado"));
        ticketRepository.delete(ticket);
    }
}