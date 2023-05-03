package com.bureaucracyhacks.refactorip.models;


import jakarta.persistence.*;
import lombok.Data;

/**
 * This class defines the model for a Task entity
 */
@Data
@Entity
@Table(name = "tasks")
public class TaskJPA
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int task_id;
    private String name;
    private String description;
    private String created_at;



}
