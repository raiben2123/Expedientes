package com.ruben.Expedientes.dto;

import com.ruben.Expedientes.model.Ticket;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketDTO {
    private Long id;
    private String title;
    private String description;
    private String status;
    private Long createdById;
    private String createdByUsername;
    private LocalDateTime createdAt;

    /**
     * Constructor que maneja de forma segura la conversión desde Ticket
     * NOTA: Este constructor requiere que el createdBy esté cargado (no lazy)
     */
    public TicketDTO(Ticket ticket) {
        if (ticket == null) {
            return;
        }
        
        this.id = ticket.getId();
        this.title = ticket.getTitle();
        this.description = ticket.getDescription();
        this.status = ticket.getStatus();
        this.createdAt = ticket.getCreatedAt();
        
        // Manejo seguro del usuario - verificar que no sea un proxy lazy
        try {
            if (ticket.getCreatedBy() != null) {
                this.createdById = ticket.getCreatedBy().getId();
                this.createdByUsername = ticket.getCreatedBy().getUsername();
            }
        } catch (Exception e) {
            // Si hay error al acceder al usuario (lazy loading), dejar valores null
            this.createdById = null;
            this.createdByUsername = null;
        }
    }
    
    /**
     * Método estático para crear DTO de forma segura
     */
    public static TicketDTO fromTicket(Ticket ticket) {
        return new TicketDTO(ticket);
    }
}