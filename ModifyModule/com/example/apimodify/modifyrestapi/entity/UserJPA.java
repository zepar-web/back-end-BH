package com.example.apimodify.modifyrestapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.domain.Persistable;

import java.sql.Timestamp;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class UserJPA implements Persistable<Integer>{

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_id;

    private String username;
    private String password;
    private String email;
    private String phone_number;
    private String adress;
    private Timestamp created_at;
    private String name;
    private String surname;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
    private Set<RoleJPA> roles;

    @Override
    public Integer getId() {
        return user_id;
    }

    @Override
    public boolean isNew() {
        return false;
    }
}
