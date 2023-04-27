package com.bureaucracyhacks.refactorip.services;

import com.bureaucracyhacks.refactorip.exceptions.DocumentNotFoundException;
import com.bureaucracyhacks.refactorip.exceptions.UserNotFoundException;
import com.bureaucracyhacks.refactorip.models.DocumentJPA;
import com.bureaucracyhacks.refactorip.models.RoleJPA;
import com.bureaucracyhacks.refactorip.models.UserJPA;
import com.bureaucracyhacks.refactorip.repositories.DocumentRepository;
import com.bureaucracyhacks.refactorip.repositories.RoleRepository;
import com.bureaucracyhacks.refactorip.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.crypto.Cipher.SECRET_KEY;

@Service
public class UserService implements UserDetailsService {

    private static final long EXPIRE_DURATION = 360000;

    private static final String key = "fdeaa31457c1366bd885e8641e19f7718c602e68551f353735c4a388a7d0bc25fdeaa31457c1366bd885e8641e19f7718c602e68551f353735c4a388a7d0bc25";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DocumentRepository documentRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, DocumentRepository documentRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.documentRepository = documentRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        UserJPA user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail));

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole_id() == 0 ? "USER" : "ADMIN"))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    public String generateAccessToken(UserJPA user) {

        return Jwts.builder()
                .setSubject(String.format("%s", user.getUsername()))
                .setIssuer("CodeJava")
                .claim("roles", getUserRole(user.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    public void registerUser(String name, String surname, String username, String email, String password, String phone_number) {
        UserJPA user = new UserJPA();
        user.setName(name);
        user.setSurname(surname);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone_number(phone_number);
        String text = "2011-10-02 18:48:05.123456";
        Timestamp ts = Timestamp.valueOf(text);
        user.setCreated_at(ts.toString());
        RoleJPA userRole = roleRepository.findByName("ROLE_USER").orElseThrow();
        user.setRoles(Collections.singleton(userRole));
        userRepository.save(user);
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void updateUser(String username, String email, String phone_number, String password, String name, String surname) {
        UserJPA user;
        try {
            user = userRepository.findByUsername(username).orElseThrow();
        }
        catch(NoSuchElementException e)
        {
            throw new UserNotFoundException();
        }

        if(email != null)
            user.setEmail(email);

        if(phone_number != null)
            user.setPhone_number(phone_number);

        if(name != null)
            user.setName(name);

        if(surname != null)
            user.setSurname(surname);

        if(password != null)
            user.setPassword(passwordEncoder.encode(password));


        userRepository.save(user);
    }

    public void deleteUser(String username) {
        UserJPA user;
        try {
            user = userRepository.findByUsername(username).orElseThrow();
        }
        catch(NoSuchElementException e)
        {
            throw new UserNotFoundException();
        }
        userRepository.delete(user);
    }

    public boolean isAdmin(String usernameOrEmail) {
        UserJPA user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail));

        return user.getRoles().stream().anyMatch(role -> role.getRole_id() == 1L);
    }

    public void addDocument(String username, String documentName) {
        UserJPA user;
        try {
            user = userRepository.findByUsername(username).orElseThrow();
        }
        catch(NoSuchElementException e)
        {
            throw new UserNotFoundException();
        }

        DocumentJPA document;
        try {
            document = documentRepository.findByName(documentName).orElseThrow();
        }
        catch(NoSuchElementException e)
        {
            throw new DocumentNotFoundException();
        }

        user.addDocument(document);

        userRepository.save(user);
    }

    public String getUserRole(String username) {
        UserJPA user;
        try {
            user = userRepository.findByUsername(username).orElseThrow();
        }
        catch(NoSuchElementException e)
        {
            throw new UserNotFoundException();
        }
        return user.getRoles().stream().findFirst().get().getName();
    }
}
