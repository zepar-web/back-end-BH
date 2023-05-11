package com.bureaucracyhacks.refactorip.controllers;

import com.bureaucracyhacks.refactorip.exceptions.DocumentNotFoundException;
import com.bureaucracyhacks.refactorip.exceptions.TaskNotFoundException;
import com.bureaucracyhacks.refactorip.exceptions.UserNotFoundException;
import com.bureaucracyhacks.refactorip.services.AuthenticationService;
import com.bureaucracyhacks.refactorip.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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
