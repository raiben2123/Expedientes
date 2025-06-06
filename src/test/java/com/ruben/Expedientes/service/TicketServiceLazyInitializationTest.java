package com.ruben.Expedientes.service;

import com.ruben.Expedientes.dto.TicketDTO;
import com.ruben.Expedientes.model.Ticket;
import com.ruben.Expedientes.model.User;
import com.ruben.Expedientes.model.Role;
import com.ruben.Expedientes.repository.TicketRepository;
import com.ruben.Expedientes.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para verificar que la solución de LazyInitializationException funciona correctamente
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TicketServiceLazyInitializationTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Crear usuario de prueba
        testUser = new User();
        testUser.setUsername("test_user_lazy_fix");
        testUser.setPassword("password");
        testUser.setRole(Role.USER);
        testUser = userRepository.save(testUser);
    }

    @Test
    void testGetAllTicketsWithLazyLoadedUsers() {
        // Crear algunos tickets de prueba
        Ticket ticket1 = new Ticket();
        ticket1.setTitle("Test Ticket 1 - Lazy Loading Fix");
        ticket1.setDescription("Testing lazy loading solution");
        ticket1.setStatus("OPEN");
        ticket1.setCreatedBy(testUser);
        ticket1.setCreatedAt(LocalDateTime.now());

        Ticket ticket2 = new Ticket();
        ticket2.setTitle("Test Ticket 2 - Lazy Loading Fix");
        ticket2.setDescription("Another test for lazy loading");
        ticket2.setStatus("IN_PROGRESS");
        ticket2.setCreatedBy(testUser);
        ticket2.setCreatedAt(LocalDateTime.now());

        ticketRepository.save(ticket1);
        ticketRepository.save(ticket2);

        // Probar el método que anteriormente fallaba
        assertDoesNotThrow(() -> {
            List<TicketDTO> tickets = ticketService.getAllTickets();
            
            // Verificar que se obtuvieron tickets
            assertNotNull(tickets);
            assertTrue(tickets.size() >= 2);
            
            // Verificar que los datos del usuario están presentes
            boolean foundTestTicket = false;
            for (TicketDTO ticketDTO : tickets) {
                if (ticketDTO.getTitle().contains("Test Ticket")) {
                    assertNotNull(ticketDTO.getCreatedByUsername(), 
                        "Username should not be null - LazyInitializationException should be fixed");
                    assertEquals(testUser.getUsername(), ticketDTO.getCreatedByUsername());
                    assertNotNull(ticketDTO.getCreatedById());
                    assertEquals(testUser.getId(), ticketDTO.getCreatedById());
                    foundTestTicket = true;
                }
            }
            
            assertTrue(foundTestTicket, "Should have found at least one test ticket");
        }, "getAllTickets should not throw LazyInitializationException");
    }

    @Test
    void testUpdateTicketStatusWithLazyLoadedUser() {
        // Crear ticket de prueba
        Ticket ticket = new Ticket();
        ticket.setTitle("Test Update - Lazy Loading");
        ticket.setDescription("Testing update with lazy loading");
        ticket.setStatus("OPEN");
        ticket.setCreatedBy(testUser);
        ticket.setCreatedAt(LocalDateTime.now());
        
        Ticket savedTicket = ticketRepository.save(ticket);

        // Probar actualización de estado
        assertDoesNotThrow(() -> {
            Ticket updatedTicket = ticketService.updateTicketStatus(savedTicket.getId(), "CLOSED");
            
            assertNotNull(updatedTicket);
            assertEquals("CLOSED", updatedTicket.getStatus());
            assertNotNull(updatedTicket.getCreatedBy());
            assertEquals(testUser.getUsername(), updatedTicket.getCreatedBy().getUsername());
        }, "updateTicketStatus should not throw LazyInitializationException");
    }

    @Test
    void testCreateTicketAndRetrieveWithUser() {
        // Crear ticket usando el servicio
        Ticket newTicket = new Ticket();
        newTicket.setTitle("Service Created Ticket");
        newTicket.setDescription("Created through service method");
        newTicket.setStatus("OPEN");

        assertDoesNotThrow(() -> {
            Ticket createdTicket = ticketService.createTicket(newTicket, testUser.getUsername());
            
            assertNotNull(createdTicket);
            assertNotNull(createdTicket.getId());
            assertEquals(testUser.getId(), createdTicket.getCreatedBy().getId());
            
            // Verificar que se puede obtener en la lista sin errores
            List<TicketDTO> allTickets = ticketService.getAllTickets();
            
            boolean foundCreatedTicket = allTickets.stream()
                .anyMatch(dto -> dto.getTitle().equals("Service Created Ticket") 
                    && dto.getCreatedByUsername().equals(testUser.getUsername()));
            
            assertTrue(foundCreatedTicket, "Created ticket should be retrievable with user data");
        }, "Creating and retrieving ticket should not throw LazyInitializationException");
    }

    @Test
    void testTicketDTOConversionWithNullUser() {
        // Probar comportamiento con usuario nulo (edge case)
        Ticket ticketWithoutUser = new Ticket();
        ticketWithoutUser.setTitle("Ticket Without User");
        ticketWithoutUser.setDescription("Testing null user scenario");
        ticketWithoutUser.setStatus("OPEN");
        ticketWithoutUser.setCreatedAt(LocalDateTime.now());
        // No establecer createdBy (null)

        assertDoesNotThrow(() -> {
            TicketDTO dto = new TicketDTO(ticketWithoutUser);
            
            assertNotNull(dto);
            assertEquals("Ticket Without User", dto.getTitle());
            assertNull(dto.getCreatedById());
            assertNull(dto.getCreatedByUsername());
        }, "TicketDTO conversion should handle null user gracefully");
    }
}
