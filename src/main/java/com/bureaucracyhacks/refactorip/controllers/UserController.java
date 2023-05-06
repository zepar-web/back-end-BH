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
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;


    @Autowired
    private final AuthenticationService authenticationService;

    @Autowired
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestParam String usernameOrEmail, @RequestParam String password) {
        return ResponseEntity.ok(authenticationService.authenticateUser(usernameOrEmail, password));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestParam String name, @RequestParam String surname, @RequestParam String username, @RequestParam String email, @RequestParam String password, @RequestParam String phone_number, @RequestParam String city) throws IOException {
        if (userService.isUsernameTaken(username)) {
            return new ResponseEntity<>("Username '" + username + "' is already taken!", HttpStatus.BAD_REQUEST);
        }

        if (userService.isEmailTaken(email)) {
            return new ResponseEntity<>("There already exists an user with the email '" + email + "'!", HttpStatus.BAD_REQUEST);
        }

        if (!userService.isValidName(name)) {
            return new ResponseEntity<>("Name '" + name + "' is not valid!", HttpStatus.BAD_REQUEST);
        }
        if (!userService.isValidSurname(surname)) {
            return new ResponseEntity<>("Surname '" + surname + "' is not valid!", HttpStatus.BAD_REQUEST);
        }
        if (!userService.isValidUsername(username)) {
            return new ResponseEntity<>("Username '" + username + "' is not valid! (It should only contain letters, numbers and '_' and be at least 6 characters long.)", HttpStatus.BAD_REQUEST);
        }
        if (!userService.isValidEmail(email)) {
            return new ResponseEntity<>("Email '" + email + "' is not valid!", HttpStatus.BAD_REQUEST);
        }
        if (!userService.isValidPassword(password)) {
            return new ResponseEntity<>("Password is not valid! (It should contain at least one upper case, one lower case, one number and be at least 8 characters long.)", HttpStatus.BAD_REQUEST);
        }
        if (!userService.isValidPhoneNumber(phone_number)) {
            return new ResponseEntity<>("Phone number '" + phone_number + "' is not valid!", HttpStatus.BAD_REQUEST);
        }
        if (!userService.isValidCity(city)) {
            return new ResponseEntity<>("City '" + city + "' does not exist in Romania!", HttpStatus.BAD_REQUEST);
        }

        UserJPA user = new UserJPA();
        user.setName(name);
        //var jwtToken = tokenService.generateToken(user);
        userService.registerUser(name, surname, username, email, password, phone_number, city);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.OK);
    }

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

    @PostMapping("/todo-list")
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
