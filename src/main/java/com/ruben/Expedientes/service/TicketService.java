package com.ruben.Expedientes.service;

import com.ruben.Expedientes.dto.TicketDTO;
import com.ruben.Expedientes.model.Ticket;
import com.ruben.Expedientes.model.User;
import com.ruben.Expedientes.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserService userService;

    @Transactional(readOnly = true)
    public List<TicketDTO> getAllTickets() {
        try {
            logger.debug("Iniciando obtención de todos los tickets con usuarios");
            
            // Usar el método que hace JOIN FETCH para cargar el usuario
            List<Ticket> tickets = ticketRepository.findAllWithCreatedBy();
            logger.debug("Se encontraron {} tickets en la base de datos", tickets.size());
            
            List<TicketDTO> ticketDTOs = tickets.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            logger.debug("Conversión completada exitosamente. {} DTOs creados", ticketDTOs.size());
            return ticketDTOs;
            
        } catch (Exception e) {
            logger.error("Error al obtener tickets: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener los tickets", e);
        }
    }

    private TicketDTO convertToDTO(Ticket ticket) {
        try {
            logger.debug("Convirtiendo ticket ID: {} a DTO", ticket.getId());
            
            TicketDTO dto = new TicketDTO();
            dto.setId(ticket.getId());
            dto.setTitle(ticket.getTitle());
            dto.setDescription(ticket.getDescription());
            dto.setStatus(ticket.getStatus());
            dto.setCreatedAt(ticket.getCreatedAt());
            
            // Manejo seguro del usuario
            if (ticket.getCreatedBy() != null) {
                try {
                    dto.setCreatedById(ticket.getCreatedBy().getId());
                    dto.setCreatedByUsername(ticket.getCreatedBy().getUsername());
                    logger.debug("Usuario asignado correctamente: {} (ID: {})", 
                            ticket.getCreatedBy().getUsername(), ticket.getCreatedBy().getId());
                } catch (Exception userException) {
                    logger.warn("Error al acceder a datos del usuario para ticket ID {}: {}", 
                            ticket.getId(), userException.getMessage());
                    dto.setCreatedById(null);
                    dto.setCreatedByUsername("Usuario no disponible");
                }
            } else {
                logger.warn("Ticket ID {} no tiene usuario asignado", ticket.getId());
            }
            
            return dto;
            
        } catch (Exception e) {
            logger.error("Error al convertir ticket ID {} a DTO: {}", ticket.getId(), e.getMessage(), e);
            throw new RuntimeException("Error en la conversión del ticket", e);
        }
    }

    public Ticket createTicket(Ticket ticket, String username) {
        logger.info("Creando ticket '{}' para usuario: {}", ticket.getTitle(), username);
        
        User user = userService.findByUsername(username);
        if (user == null) {
            logger.error("Usuario no encontrado: {}", username);
            throw new IllegalArgumentException("Usuario no encontrado: " + username);
        }
        
        ticket.setCreatedBy(user);
        Ticket savedTicket = ticketRepository.save(ticket);
        
        logger.info("Ticket creado exitosamente con ID: {}", savedTicket.getId());
        return savedTicket;
    }

    public Ticket updateTicketStatus(Long id, String status) {
        logger.info("Actualizando estado del ticket ID {} a: {}", id, status);
        
        Ticket ticket = ticketRepository.findByIdWithCreatedBy(id)
                .orElseThrow(() -> {
                    logger.error("Ticket no encontrado con ID: {}", id);
                    return new IllegalArgumentException("Ticket no encontrado: " + id);
                });
        
        String oldStatus = ticket.getStatus();
        ticket.setStatus(status);
        Ticket updatedTicket = ticketRepository.save(ticket);
        
        logger.info("Ticket ID {} actualizado de '{}' a '{}'", id, oldStatus, status);
        return updatedTicket;
    }

    public void deleteTicket(Long id) {
        logger.info("Eliminando ticket con ID: {}", id);
        
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Ticket no encontrado para eliminar con ID: {}", id);
                    return new IllegalArgumentException("Ticket no encontrado: " + id);
                });
        
        ticketRepository.delete(ticket);
        logger.info("Ticket ID {} eliminado exitosamente", id);
    }
}