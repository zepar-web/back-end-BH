package com.bureaucracyhacks.refactorip.services;

import com.bureaucracyhacks.refactorip.exceptions.TaskNotFoundException;
import com.bureaucracyhacks.refactorip.models.*;
import com.bureaucracyhacks.refactorip.repositories.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import kotlin.Pair;
import org.apache.commons.validator.routines.EmailValidator;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;


import com.bureaucracyhacks.refactorip.exceptions.DocumentNotFoundException;
import com.bureaucracyhacks.refactorip.exceptions.UserNotFoundException;
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
import java.security.Key;
import java.sql.Timestamp;
import java.util.*;
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

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private InstitutionRepository institutionRepository;


    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       DocumentRepository documentRepository,
                       TaskRepository taskRepository,
                       InstitutionRepository institutionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.documentRepository = documentRepository;
        this.taskRepository = taskRepository;
        this.institutionRepository = institutionRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        UserJPA user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail));

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole_id() == 2 ? "USER" : "ADMIN"))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
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

    public HashMap<String, String> generateTodoList(String taskName){
        try {
            TaskJPA task = taskRepository.findByName(taskName).orElseThrow();

            List<DocumentJPA> documents = task.getDocuments();

            //List<Pair<String, String>> documentsAndInstitutionLocations = new ArrayList<>();

            HashMap<String, String> documentsAndInstitutionLocations = new HashMap<>();

            for (DocumentJPA document : documents) {
                //Pair<String, String> documentAndInstitutionLocation = new Pair<>(document.getName(), institutionRepository.findByInstitutionId((long) document.getInstitution_id()).orElseThrow().getAddress());
                //documentsAndInstitutionLocations.add(documentAndInstitutionLocation);
                documentsAndInstitutionLocations.put(document.getName(), institutionRepository.findByInstitutionId((long) document.getInstitution_id()).orElseThrow().getAddress());
            }
            return documentsAndInstitutionLocations;
        }
        catch(NoSuchElementException e)
        {
            throw new TaskNotFoundException();
        }

    }
}
