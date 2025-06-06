package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.dto.TicketDTO;
import com.ruben.Expedientes.model.Ticket;
import com.ruben.Expedientes.service.JwtService;
import com.ruben.Expedientes.service.TicketService;
import com.ruben.Expedientes.service.WebSocketNotificationService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private WebSocketNotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<TicketDTO>> getAllTickets(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Token ausente o inválido");
            }
            String username = extractUsernameFromToken(token);
            return ResponseEntity.ok(ticketService.getAllTickets());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody Ticket ticket, @RequestHeader("Authorization") String token) {
        try {
            String username = extractUsernameFromToken(token);
            Ticket createdTicket = ticketService.createTicket(ticket, username);
            notificationService.notifyCreated(WebSocketNotificationService.EntityType.TICKETS, createdTicket);
            return ResponseEntity.ok(createdTicket);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear el ticket");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTicketStatus(@PathVariable Long id, @RequestBody Ticket updatedTicket) {
        try {
            Ticket ticket = ticketService.updateTicketStatus(id, updatedTicket.getStatus());
            notificationService.notifyUpdated(WebSocketNotificationService.EntityType.TICKETS, ticket);
            return ResponseEntity.ok(ticket);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el ticket");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicket(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            String username = extractUsernameFromToken(token);
            ticketService.deleteTicket(id);
            notificationService.notifyDeleted(WebSocketNotificationService.EntityType.TICKETS, id);
            return ResponseEntity.ok("Ticket eliminado con éxito");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar el ticket");
        }
    }

    private String extractUsernameFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            throw new IllegalArgumentException("Token inválido o ausente");
        }
        try {
            return jwtService.extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            throw new IllegalArgumentException("Token inválido o no se pudo extraer el usuario");
        }
    }
}