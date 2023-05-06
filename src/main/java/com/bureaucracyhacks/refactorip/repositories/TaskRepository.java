package com.bureaucracyhacks.refactorip.repositories;

import com.bureaucracyhacks.refactorip.models.TaskJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface TaskRepository extends JpaRepository<TaskJPA, Integer> {
    Optional<TaskJPA> findByName(String name);
}
