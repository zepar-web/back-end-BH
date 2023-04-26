package com.bureaucracyhacks.refactorip.services;

import com.bureaucracyhacks.refactorip.exceptions.UserNotFoundException;
import com.bureaucracyhacks.refactorip.models.RoleJPA;
import com.bureaucracyhacks.refactorip.models.UserJPA;
import com.bureaucracyhacks.refactorip.repositories.RoleRepository;
import com.bureaucracyhacks.refactorip.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public AdminService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public void makeAdmin(String name)
    {
        UserJPA user;
        try {
            user = userRepository.findByUsername(name).orElseThrow();
        }
        catch(NoSuchElementException e)
        {
            throw new UserNotFoundException();
        }
        RoleJPA role = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
        user.setRole(role);
        userRepository.save(user);
    }

    public void removeAdmin(String username)
    {
        UserJPA user;
        try {
            user = userRepository.findByUsername(username).orElseThrow();
        }
        catch(NoSuchElementException e)
        {
            throw new UserNotFoundException();
        }
        RoleJPA role = roleRepository.findByName("ROLE_USER").orElseThrow();
        user.setRole(role);
        userRepository.save(user);
    }
}
