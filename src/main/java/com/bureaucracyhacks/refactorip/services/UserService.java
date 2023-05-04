package com.bureaucracyhacks.refactorip.services;

import com.bureaucracyhacks.refactorip.models.RoleJPA;
import org.apache.commons.validator.routines.EmailValidator;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;


import com.bureaucracyhacks.refactorip.exceptions.DocumentNotFoundException;
import com.bureaucracyhacks.refactorip.exceptions.UserNotFoundException;
import com.bureaucracyhacks.refactorip.models.DocumentJPA;
import com.bureaucracyhacks.refactorip.models.UserJPA;
import com.bureaucracyhacks.refactorip.repositories.DocumentRepository;
import com.bureaucracyhacks.refactorip.repositories.RoleRepository;
import com.bureaucracyhacks.refactorip.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

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

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    public void registerUser(String name, String surname, String username, String email, String password, String phone_number, String city) {
        UserJPA user = new UserJPA();
        RoleJPA role = roleRepository.findByName("ROLE_USER").orElseThrow();

        user.setName(name);
        user.setSurname(surname);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone_number(phone_number);
        user.setCity(city);
        user.setRoles(Collections.singleton(role));

        String text = "2011-10-02 18:48:05.123456";
        Timestamp ts = Timestamp.valueOf(text);
        user.setCreated_at(ts.toString());

        userRepository.save(user);
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void updateUser(String username, String email, String phone_number, String password, String name, String surname, String city) {
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

        if(city != null)
            user.setCity(city);


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

        user.getDocuments().add(document);

        userRepository.save(user);
    }

    public boolean isValidName(String name) {
        return name.matches("^[A-Za-z]{2,}(\\s*)([A-Za-z]{1,})+$") && name.length() <= 60;
    }

    public boolean isValidSurname(String surname) {
        return surname.matches("^[A-Za-z]{2,}(\\s*)([A-Za-z]{1,})+$") && surname.length() <= 60;
    }

    public boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z0-9_]*$") && username.length() > 5 && username.length() <= 30;
    }

    public boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public boolean isValidPassword(String password) {
        return password.length() >= 8 && password.length() <= 255 && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z?!@#$%^&*\\d]{8,}$");
    }

    public boolean isValidPhoneNumber(String phone_number) {
        PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();
        PhoneNumber phoneNumber;
        try {
           phoneNumber = numberUtil.parse(phone_number, "RO");
        } catch (NumberParseException e) {
            return false;
        }
        return numberUtil.isValidNumber(phoneNumber);
    }

    public boolean isValidCity(String city) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/main/java/com/bureaucracyhacks/refactorip/utils/cities.txt"));
        String[] cities = lines.toArray(new String[0]);
        for (String s : cities) {
            if (s.equals(city))
                return true;
        }
        return false;
    }

}
