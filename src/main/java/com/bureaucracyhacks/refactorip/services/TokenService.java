package com.bureaucracyhacks.refactorip.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;


public class TokenService {
    private static final String SECRET_KEY = "fdeaa31457c1366bd885e8641e19f7718c602e68551f353735c4a388a7d0bc25fdeaa31457c1366bd885e8641e19f7718c602e68551f353735c4a388a7d0bc25";


    public static String extractUsername(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public static String extractRole(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
        return (String) claims.get("roles");
    }

    public static boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()));
    }

}
