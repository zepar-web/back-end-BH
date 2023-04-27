package com.bureaucracyhacks.refactorip.controllers;

import com.bureaucracyhacks.refactorip.services.TokenHelper;
import com.bureaucracyhacks.refactorip.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DialectOverride;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenHelper tokenHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestToken = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        if (requestToken != null && requestToken.startsWith("Bearer ")) {
            jwtToken = requestToken.substring(7);
            System.out.println("JWT Token: " + jwtToken);

            try {
                username = TokenHelper.extractUsername(jwtToken);
                System.out.println("Username: " + username);
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        } else {
            System.out.println("Invalid token, not starting with Bearer");
        }

        if (username != null) {
            UserDetails userDetails = this.userService.loadUserByUsername(username);
            if (TokenHelper.validateToken(jwtToken, userDetails)) {
                System.out.println(username + " granted authorization" + userDetails.getAuthorities());
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                System.out.println("User is authenticated");
            } else {
                System.out.println("Token is invalid");
            }
        } else {
            System.out.println("Token is null or user is already authenticated");
        }

        filterChain.doFilter(request, response);
    }
}
