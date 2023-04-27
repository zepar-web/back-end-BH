package com.bureaucracyhacks.refactorip.repositories;

import com.bureaucracyhacks.refactorip.models.ReviewJPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<ReviewJPA, Long> {
    Optional<ReviewJPA> findByRating(Integer rating);
}
