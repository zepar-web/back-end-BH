package com.example.apimodify.modifyrestapi.controller;

import com.example.apimodify.modifyrestapi.entity.UserJPA;
import com.example.apimodify.modifyrestapi.payload.Modify;
import com.example.apimodify.modifyrestapi.repository.RoleRepository;
import com.example.apimodify.modifyrestapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/mdf")
public class ModifyController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/modify")
    public ResponseEntity<?> modifyUser(@RequestBody Modify modify) {

        System.out.println(modify);
        System.out.println(userRepository.count());

        UserJPA user;
        try {
            user = userRepository.findByUsername(modify.getUsername()).orElseThrow();
        }
        catch(NoSuchElementException e)
        {
            return new ResponseEntity<>("User not found!", HttpStatus.BAD_REQUEST);
        }

        user.setEmail(modify.getEmail());

        userRepository.save(user);

        return new ResponseEntity<>("User modified successfully!", HttpStatus.OK);
    }

}
