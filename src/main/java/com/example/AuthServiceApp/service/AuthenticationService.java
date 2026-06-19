package com.example.AuthServiceApp.service;

import com.example.AuthServiceApp.DTO.LoginRequest;
import com.example.AuthServiceApp.DTO.UserResponse;
import com.example.AuthServiceApp.interfaces.UserClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // This allows us to save the user session to the browser
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public String login(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        //Fetch user from Microservice
        UserResponse user = userClient.findByEmail(loginRequest.getEmail());

        //Validate Password
        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {

            //Create Authorities (Role) - Add "ROLE_" prefix if your DB doesn't have it
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

            //Create Authentication Token
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user, null, authorities);

            //Create and Save Security Context
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            //CRITICAL: Save to Session so Spring remembers the login
            securityContextRepository.saveContext(context, request, response);

            return jwtUtil.generateToken(user.getUsername(), authorities);
        } else {
            return "Invalid email or password";
        }
    }
}



