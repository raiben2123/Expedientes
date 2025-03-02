package com.ruben.Expedientes.repository;// src/main/java/com/example/demo/repository/UserRepository.java

import com.ruben.Expedientes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
