package com.example.authrestapi.repository;

import com.example.authrestapi.entity.UserJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Example;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserJPA, Long> {
    Optional<UserJPA> findByUsername(String username);
    Optional<UserJPA> findByEmail(String email);
    Optional<UserJPA> findByUsernameOrEmail(String username, String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
