package com.bureaucracyhacks.refactorip.controllers;

import com.bureaucracyhacks.refactorip.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @PostMapping("/makeadmin")
    public ResponseEntity<?> makeAdmin(@RequestParam String username) {
        System.out.println("User is now an admin");
        if (userService.isUsernameTaken(username)) {
            userService.makeAdmin(username);
            return new ResponseEntity<>("User is now admin!", HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>("User does not exist!", HttpStatus.BAD_REQUEST);
        }
    }
}
