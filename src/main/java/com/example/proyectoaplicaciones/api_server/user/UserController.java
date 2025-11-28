package com.example.proyectoaplicaciones.api_server.user;

import com.example.proyectoaplicaciones.api_server.dto.LoginRequest;
import com.example.proyectoaplicaciones.api_server.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest registerRequest) {
        // Verificar si el usuario o el email ya existen
        if (userRepository.findByUsername(registerRequest.getUsername()) != null) {
            return ResponseEntity.badRequest().build(); // Username ya existe
        }
        if (userRepository.findByEmail(registerRequest.getEmail()) != null) {
            return ResponseEntity.badRequest().build(); // Email ya existe
        }

        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        // Aquí podrías asignar un rol por defecto si tu clase User tiene roles

        User savedUser = userRepository.save(newUser);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        // Buscamos al usuario por su email
        User user = userRepository.findByEmail(loginRequest.getEmail());

        // Verificamos si el usuario existe y si la contraseña coincide usando BCrypt
        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.ok(user); // Credenciales correctas
        }

        // Si no, devolvemos un error de no autorizado
        return ResponseEntity.status(401).build();
    }
}