package com.example.proyectoaplicaciones.api_server.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);

    // Método añadido para buscar por email
    User findByEmail(String email);
}