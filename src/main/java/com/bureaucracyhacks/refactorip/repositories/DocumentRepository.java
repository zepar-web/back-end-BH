package com.bureaucracyhacks.refactorip.repositories;

import com.bureaucracyhacks.refactorip.models.DocumentJPA;
import com.bureaucracyhacks.refactorip.models.RoleJPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<DocumentJPA, Integer> {
    Optional<DocumentJPA> findByName(String name);
    Boolean existsByName(String name);
    Boolean existsById(int institution_id);
}
