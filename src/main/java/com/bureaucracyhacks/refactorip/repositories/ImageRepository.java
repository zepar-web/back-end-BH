package com.bureaucracyhacks.refactorip.repositories;

import java.util.Optional;

import com.bureaucracyhacks.refactorip.models.DocumentImageJPA;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<DocumentImageJPA, Long>{
    Optional<DocumentImageJPA> findByName(String imageName);
}