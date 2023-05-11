package com.bureaucracyhacks.refactorip.controllers;

import com.bureaucracyhacks.refactorip.exceptions.DocumentNotFoundException;
import com.bureaucracyhacks.refactorip.exceptions.TaskNotFoundException;
import com.bureaucracyhacks.refactorip.exceptions.UserNotFoundException;
import com.bureaucracyhacks.refactorip.models.DocumentJPA;
import com.bureaucracyhacks.refactorip.models.RoleJPA;
import com.bureaucracyhacks.refactorip.models.UserJPA;
import com.bureaucracyhacks.refactorip.repositories.RoleRepository;
import com.bureaucracyhacks.refactorip.services.AuthenticationService;
import com.bureaucracyhacks.refactorip.services.TokenService;
import com.bureaucracyhacks.refactorip.services.UserService;
import kotlin.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user-service")
public class UserController {

    private final UserService userService;

    private final AuthenticationService authenticationService;

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestParam String username, @RequestParam String email, @RequestParam String phone_number, @RequestParam String password, @RequestParam String name, @RequestParam String surname, @RequestParam String city) {

        try {
            userService.updateUser(username, email, phone_number, password, name, surname, city);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("User modified successfully!", HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam String username) {

        try {
            userService.deleteUser(username);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("User deleted successfully!", HttpStatus.OK);
    }

    @PostMapping("/addDocument")
    public ResponseEntity<?> addDocument(@RequestParam String username, @RequestParam String documentName) {

        try {
            userService.addDocument(username, documentName);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found!", HttpStatus.BAD_REQUEST);
        } catch (DocumentNotFoundException e) {
            return new ResponseEntity<>("Document not found!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Document added successfully!", HttpStatus.OK);
    }

    @GetMapping("/todo-list")
    public ResponseEntity<?> generateTodoList(@RequestParam String userTaskName) {
        HashMap<String, String> documentsAndInstitutionLocation;
        try {
            documentsAndInstitutionLocation = userService.generateTodoList(userTaskName);
        } catch (TaskNotFoundException e) {
            return new ResponseEntity<>("Task not found!", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(documentsAndInstitutionLocation);
    }
}
