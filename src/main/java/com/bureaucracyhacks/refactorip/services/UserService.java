package com.bureaucracyhacks.refactorip.services;

import com.bureaucracyhacks.refactorip.exceptions.DocumentNotFoundException;
import com.bureaucracyhacks.refactorip.exceptions.TaskNotFoundException;
import com.bureaucracyhacks.refactorip.exceptions.UserNotFoundException;
import com.bureaucracyhacks.refactorip.models.DocumentJPA;
import com.bureaucracyhacks.refactorip.models.RoleJPA;
import com.bureaucracyhacks.refactorip.models.TaskJPA;
import com.bureaucracyhacks.refactorip.models.UserJPA;
import com.bureaucracyhacks.refactorip.repositories.*;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final DocumentRepository documentRepository;

    private final TaskRepository taskRepository;

    private final InstitutionRepository institutionRepository;

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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        user.setCreated_at(sdf.format(now));

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

            HashMap<String, String> documentsAndInstitutionLocations = new HashMap<>();

            for (DocumentJPA document : documents) {
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
