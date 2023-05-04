package com.bureaucracyhacks.refactorip.controllers;

import com.bureaucracyhacks.refactorip.exceptions.DocumentNotFoundException;
import com.bureaucracyhacks.refactorip.exceptions.UserNotFoundException;
import com.bureaucracyhacks.refactorip.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestParam String usernameOrEmail, @RequestParam String password) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            usernameOrEmail,
                            password
                    )
            );
            if(userService.isAdmin(usernameOrEmail))
            {
                return ResponseEntity.ok("Logged in as admin!");
            }
            else
            {
                return ResponseEntity.ok("Logged in as user!");
            }
        }
        catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed!");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestParam String name, @RequestParam String surname, @RequestParam String username, @RequestParam String email, @RequestParam String password, @RequestParam String phone_number, @RequestParam String city) throws IOException {
        if (userService.isUsernameTaken(username)) {
            return new ResponseEntity<>("Username '" + username + "' is already taken!", HttpStatus.BAD_REQUEST);
        }

        if (userService.isEmailTaken(email)) {
            return new ResponseEntity<>("There already exists an user with the email '" + email + "'!", HttpStatus.BAD_REQUEST);
        }

        if(!userService.isValidName(name))
        {
            return new ResponseEntity<>("Name '" + name + "' is not valid!", HttpStatus.BAD_REQUEST);
        }
        if(!userService.isValidSurname(surname))
        {
            return new ResponseEntity<>("Surname '" + surname + "' is not valid!", HttpStatus.BAD_REQUEST);
        }
        if(!userService.isValidUsername(username))
        {
            return new ResponseEntity<>("Username '" + username + "' is not valid! (It should only contain letters, numbers and '_' and be at least 6 characters long.)", HttpStatus.BAD_REQUEST);
        }
        if(!userService.isValidEmail(email))
        {
            return new ResponseEntity<>("Email '" + email + "' is not valid!", HttpStatus.BAD_REQUEST);
        }
        if(!userService.isValidPassword(password))
        {
            return new ResponseEntity<>("Password is not valid! (It should contain at least one upper case, one lower case, one number and be at least 8 characters long.)", HttpStatus.BAD_REQUEST);
        }
        if(!userService.isValidPhoneNumber(phone_number))
        {
            return new ResponseEntity<>("Phone number '" + phone_number + "' is not valid!", HttpStatus.BAD_REQUEST);
        }
        if(!userService.isValidCity(city))
        {
            return new ResponseEntity<>("City '" + city + "' does not exist in Romania!", HttpStatus.BAD_REQUEST);
        }

        userService.registerUser(name, surname, username, email, password, phone_number, city);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestParam String username, @RequestParam String email, @RequestParam String phone_number, @RequestParam String password, @RequestParam String name, @RequestParam String surname, @RequestParam String city) {

        try {
            userService.updateUser(username, email, phone_number, password, name, surname, city);
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
