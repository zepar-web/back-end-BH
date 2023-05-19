package com.bureaucracyhacks.refactorip.controllers;

import com.bureaucracyhacks.refactorip.exceptions.DocumentNotFoundException;
import com.bureaucracyhacks.refactorip.exceptions.TaskNotFoundException;
import com.bureaucracyhacks.refactorip.exceptions.UserNotFoundException;
import com.bureaucracyhacks.refactorip.services.AuthenticationService;
import com.bureaucracyhacks.refactorip.services.UserService;
import kotlin.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/todo-list/{userTaskName}")
    public ResponseEntity<?> generateTodoList(@PathVariable String userTaskName, @RequestParam String username) {
        List<Map<String, String>> documentsAndInstitutionLocation;
        /* JSON format:
        * isDone:
        * DocName:
        * InstitutionLocation:
        */
        try {
            //generate the to do list based on the task and the user who requested it
            documentsAndInstitutionLocation = userService.generateTodoList(userTaskName, username);
        } catch (TaskNotFoundException e) {
            return new ResponseEntity<>("Task not found!", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(documentsAndInstitutionLocation);
    }

    @PostMapping("/updateDocument")
    public ResponseEntity<?> updateDocument(@RequestParam String userTaskName, @RequestParam String documentName, @RequestParam String status, @RequestParam String username)
    {
        try
        {
            //update the status of the document
            /* ONLY "pending" -> "done" ON STATUS */
            userService.updateDocument(documentName, status, username,userTaskName);
        } catch (DocumentNotFoundException e)
        {
            return new ResponseEntity<>("Document not found!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Document updated successfully!", HttpStatus.OK);
    }
}
