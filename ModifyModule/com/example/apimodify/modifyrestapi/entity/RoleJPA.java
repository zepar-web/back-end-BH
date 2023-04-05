package com.example.apimodify.modifyrestapi.entity;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "user_roles")
public class RoleJPA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int user_id;
    private int role_id;

}
