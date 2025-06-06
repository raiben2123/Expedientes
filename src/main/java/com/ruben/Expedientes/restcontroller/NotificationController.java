package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.dto.NotificationRequestDTO;
import com.ruben.Expedientes.dto.NotificationResponseDTO;
import com.ruben.Expedientes.service.JwtService;
import com.ruben.Expedientes.service.NotificationService;
import com.ruben.Expedientes.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    
    private final NotificationService notificationService;
    private final JwtService jwtService;
    private final UserService userService;
    
    // Crear una nueva notificación
    @PostMapping
    public ResponseEntity<?> createNotification(
            @RequestBody NotificationRequestDTO requestDTO,
            @RequestHeader("Authorization") String token) {
        
        try {
            String username = extractUsernameFromToken(token);
            Long userId = userService.findByUsername(username).getId();
            
            NotificationResponseDTO responseDTO = notificationService.createNotification(requestDTO, userId);
            
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating notification: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al crear la notificación");
        }
    }
    
    // Obtener todas las notificaciones del usuario actual
    @GetMapping
    public ResponseEntity<?> getUserNotifications(@RequestHeader("Authorization") String token) {
        try {
            String username = extractUsernameFromToken(token);
            Long userId = userService.findByUsername(username).getId();
            
            List<NotificationResponseDTO> notifications = notificationService.getUserNotifications(userId);
            
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error getting notifications: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al obtener las notificaciones");
        }
    }
    
    // Obtener notificaciones no leídas
    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadNotifications(@RequestHeader("Authorization") String token) {
        try {
            String username = extractUsernameFromToken(token);
            Long userId = userService.findByUsername(username).getId();
            
            List<NotificationResponseDTO> notifications = notificationService.getUnreadNotifications(userId);
            
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error getting unread notifications: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al obtener las notificaciones no leídas");
        }
    }
    
    // Obtener conteo de notificaciones no leídas
    @GetMapping("/unread/count")
    public ResponseEntity<?> getUnreadCount(@RequestHeader("Authorization") String token) {
        try {
            String username = extractUsernameFromToken(token);
            Long userId = userService.findByUsername(username).getId();
            
            long count = notificationService.getUnreadCount(userId);
            
            return ResponseEntity.ok(Map.of("count", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error getting unread count: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al obtener el conteo de notificaciones");
        }
    }
    
    // Marcar una notificación como leída
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long notificationId,
            @RequestHeader("Authorization") String token) {
        
        try {
            String username = extractUsernameFromToken(token);
            Long userId = userService.findByUsername(username).getId();
            
            NotificationResponseDTO responseDTO = notificationService.markAsRead(notificationId, userId);
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage().contains("No tienes permisos") || e.getMessage().contains("no encontrada")) {
                return ResponseEntity.status(404).body(e.getMessage());
            }
            return ResponseEntity.status(500).body("Error al marcar como leída");
        } catch (Exception e) {
            log.error("Error marking notification as read: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al marcar como leída");
        }
    }
    
    // Marcar todas las notificaciones como leídas
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(@RequestHeader("Authorization") String token) {
        try {
            String username = extractUsernameFromToken(token);
            Long userId = userService.findByUsername(username).getId();
            
            int updatedCount = notificationService.markAllAsRead(userId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Notificaciones marcadas como leídas",
                "updatedCount", updatedCount
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error marking all notifications as read: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al marcar todas como leídas");
        }
    }
    
    // Eliminar una notificación
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(
            @PathVariable Long notificationId,
            @RequestHeader("Authorization") String token) {
        
        try {
            String username = extractUsernameFromToken(token);
            Long userId = userService.findByUsername(username).getId();
            
            notificationService.deleteNotification(notificationId, userId);
            return ResponseEntity.ok(Map.of("message", "Notificación eliminada correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage().contains("No tienes permisos") || e.getMessage().contains("no encontrada")) {
                return ResponseEntity.status(404).body(e.getMessage());
            }
            return ResponseEntity.status(500).body("Error al eliminar la notificación");
        } catch (Exception e) {
            log.error("Error deleting notification: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al eliminar la notificación");
        }
    }
    
    // Obtener notificaciones de un expediente específico
    @GetMapping("/expediente/{expedienteId}")
    public ResponseEntity<?> getExpedienteNotifications(@PathVariable Long expedienteId) {
        try {
            List<NotificationResponseDTO> notifications = notificationService.getExpedienteNotifications(expedienteId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error getting expediente notifications: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al obtener las notificaciones del expediente");
        }
    }
    
    // Buscar notificaciones
    @GetMapping("/search")
    public ResponseEntity<?> searchNotifications(
            @RequestParam String q,
            @RequestHeader("Authorization") String token) {
        
        try {
            String username = extractUsernameFromToken(token);
            Long userId = userService.findByUsername(username).getId();
            
            List<NotificationResponseDTO> notifications = notificationService.searchNotifications(userId, q);
            
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error searching notifications: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al buscar notificaciones");
        }
    }
    
    // Endpoint para testing (eliminar en producción)
    @PostMapping("/test")
    public ResponseEntity<?> createTestNotification(@RequestHeader("Authorization") String token) {
        try {
            String username = extractUsernameFromToken(token);
            Long userId = userService.findByUsername(username).getId();
            
            NotificationRequestDTO requestDTO = new NotificationRequestDTO();
            requestDTO.setTitle("Notificación de prueba");
            requestDTO.setMessage("Esta es una notificación de prueba para verificar el sistema");
            requestDTO.setType(com.ruben.Expedientes.model.NotificationType.INFO);
            
            NotificationResponseDTO responseDTO = notificationService.createNotification(requestDTO, userId);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating test notification: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al crear notificación de prueba");
        }
    }
    
    // Método helper para obtener el username desde el token JWT
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