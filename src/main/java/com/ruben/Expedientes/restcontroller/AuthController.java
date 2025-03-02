package com.ruben.Expedientes.restcontroller;

import com.ruben.Expedientes.model.User;
import com.ruben.Expedientes.service.JwtService;
import com.ruben.Expedientes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        if (userService.validateUser(username, password)) { // Verifica las credenciales
            User user = userService.findByUsername(username); // Obtenemos el usuario completo
            String token = jwtService.generateToken(username);
            return ResponseEntity.ok(new AuthResponse(token, user.getRole().name())); // Devolvemos token y rol
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User createdUser = userService.registerUser(user);
        return ResponseEntity.ok(createdUser);
    }

    private static class AuthResponse {
        private String token;
        private String role;

        public AuthResponse(String token, String role) {
            this.token = token;
            this.role = role;
        }

        public String getToken() {
            return token;
        }

        public String getRole() {
            return role;
        }
    }
}