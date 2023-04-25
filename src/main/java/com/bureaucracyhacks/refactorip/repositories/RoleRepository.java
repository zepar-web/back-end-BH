package com.bureaucracyhacks.refactorip.repositories;

import com.bureaucracyhacks.refactorip.models.RoleJPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleJPA, Long> {

    Optional<RoleJPA> findByName(String name);

}