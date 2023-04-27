package com.bureaucracyhacks.refactorip.controllers;

import com.bureaucracyhacks.refactorip.exceptions.DocumentNotFoundException;
import com.bureaucracyhacks.refactorip.exceptions.UserNotFoundException;
import com.bureaucracyhacks.refactorip.models.RoleJPA;
import com.bureaucracyhacks.refactorip.models.UserJPA;
import com.bureaucracyhacks.refactorip.repositories.RoleRepository;
import com.bureaucracyhacks.refactorip.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestParam String usernameOrEmail, @RequestParam String password) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            usernameOrEmail,
                            password
                    )
            );
            UserJPA user = new UserJPA();
            user.setUsername(usernameOrEmail);

            if(userService.isAdmin(usernameOrEmail))
            {
                RoleJPA userRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
                user.setRoles(Collections.singleton(userRole));
                System.out.println(userService.generateAccessToken(user));
                return ResponseEntity.ok("Logged in as admin!");
            }
            else
            {
                RoleJPA userRole = roleRepository.findByName("ROLE_USER").orElseThrow();
                user.setRoles(Collections.singleton(userRole));
                System.out.println(userService.generateAccessToken(user));
                return ResponseEntity.ok("Logged in as user!");
            }
        }
        catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed!");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestParam String name, @RequestParam String surname, @RequestParam String username, @RequestParam String email, @RequestParam String password, @RequestParam String phone_number) {
        if (userService.isUsernameTaken(username)) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        if (userService.isEmailTaken(email)) {
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }

       userService.registerUser(name, surname, username, email, password, phone_number);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestParam String username, @RequestParam String email, @RequestParam String phone_number, @RequestParam String password, @RequestParam String name, @RequestParam String surname) {

        try {
            userService.updateUser(username, email, phone_number, password, name, surname);
        }
        catch(UserNotFoundException e)
        {
            return new ResponseEntity<>("User not found!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("User modified successfully!", HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam String username) {

        try {
            userService.deleteUser(username);
        }
        catch(UserNotFoundException e)
        {
            return new ResponseEntity<>("User not found!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("User deleted successfully!", HttpStatus.OK);
    }

    @PostMapping("/addDocument")
    public ResponseEntity<?> addDocument(@RequestParam String username, @RequestParam String documentName) {

        try {
            userService.addDocument(username, documentName);
        }
        catch(UserNotFoundException e)
        {
            return new ResponseEntity<>("User not found!", HttpStatus.BAD_REQUEST);
        }
        catch(DocumentNotFoundException e)
        {
            return new ResponseEntity<>("Document not found!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Document added successfully!", HttpStatus.OK);
    }

}
