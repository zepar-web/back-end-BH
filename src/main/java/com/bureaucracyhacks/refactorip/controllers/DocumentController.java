package com.bureaucracyhacks.refactorip.controllers;

import com.bureaucracyhacks.refactorip.exceptions.UserNotFoundException;
import com.bureaucracyhacks.refactorip.services.DocumentService;
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

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/doc")
public class DocumentController {

    @Autowired
    private DocumentService documentService;


       @PostMapping("/findByName")
        public ResponseEntity<?> findByName(@RequestParam String name) {

             if(documentService.findByName(name).getBody()!= Optional.empty())
                {
                    return new ResponseEntity<>(documentService.findByName(name), HttpStatus.OK);
                }
                else
                {
                    return new ResponseEntity<>("Document does not exist!", HttpStatus.BAD_REQUEST);
                }
        }
}
