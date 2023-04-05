package com.example.apimodify.modifyrestapi.repository;

import com.example.apimodify.modifyrestapi.entity.UserJPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserJPA, Integer> {
    Optional<UserJPA> findByUsername(String username);
    Optional<UserJPA> findByEmail(String email);
    Optional<UserJPA> findByUsernameOrEmail(String username, String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
