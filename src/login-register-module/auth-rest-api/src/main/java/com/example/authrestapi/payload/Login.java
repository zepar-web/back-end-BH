package com.example.authrestapi.payload;

import lombok.Data;

@Data
public class Login {
    private String usernameOrEmail;
    private String password;
}
