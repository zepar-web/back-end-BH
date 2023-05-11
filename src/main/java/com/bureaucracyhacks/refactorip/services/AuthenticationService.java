package com.bureaucracyhacks.refactorip.services;

import com.bureaucracyhacks.refactorip.controllers.AuthenticationResponse;
import com.bureaucracyhacks.refactorip.exceptions.UserNotFoundException;
import com.bureaucracyhacks.refactorip.models.UserJPA;
import com.bureaucracyhacks.refactorip.repositories.UserRepository;
import japa.parser.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    public AuthenticationResponse authenticateUser(String usernameOrEmail, String password) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usernameOrEmail, password));

        var user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElseThrow();

        var jwtToken = tokenService.generateToken(user);

        UserJPA userjpa = new UserJPA();

        userjpa.setName(user.getName());
        userjpa.setSurname(user.getSurname());
        userjpa.setUsername(user.getUsername());
        userjpa.setEmail(user.getEmail());
        userjpa.setCity(user.getCity());
        userjpa.setPhone_number(user.getPhone_number());

        return AuthenticationResponse.builder().token(jwtToken).user(userjpa).build();
    }

}
