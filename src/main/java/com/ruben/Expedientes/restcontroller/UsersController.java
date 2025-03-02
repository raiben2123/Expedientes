package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.model.User;
import com.ruben.Expedientes.model.WebSocketMessage;
import com.ruben.Expedientes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            updatedUser.setId(id);
            User user = userService.updateUser(updatedUser);
            messagingTemplate.convertAndSend("/topic/users",
                    new WebSocketMessage("UPDATE", user));
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el usuario");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            messagingTemplate.convertAndSend("/topic/users",
                    new WebSocketMessage("DELETE", id));
            return ResponseEntity.ok("Usuario eliminado con éxito");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el usuario");
        }
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id) {
        try {
            String newPassword = userService.resetPassword(id);
            Optional<User> updatedUser = userService.findById(id); // Obtener el usuario actualizado
            messagingTemplate.convertAndSend("/topic/users",
                    new WebSocketMessage("UPDATE", updatedUser));
            return ResponseEntity.ok(new ResetPasswordResponse(newPassword));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al restablecer la contraseña");
        }
    }

    // Clase interna para la respuesta de reset-password
    private static class ResetPasswordResponse {
        private String newPassword;

        public ResetPasswordResponse(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }
    }
}