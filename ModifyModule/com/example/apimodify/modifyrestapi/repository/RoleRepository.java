package com.example.apimodify.modifyrestapi.repository;

import com.example.apimodify.modifyrestapi.entity.RoleJPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleJPA, Integer> {
    Optional<RoleJPA> findById(int role_id);
}
