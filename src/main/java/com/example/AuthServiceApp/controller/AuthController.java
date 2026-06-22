package com.example.AuthServiceApp.controller;

import com.example.AuthServiceApp.DTO.LoginRequest;
import com.example.AuthServiceApp.DTO.UserResponse;
import com.example.AuthServiceApp.interfaces.UserClient;
import com.example.AuthServiceApp.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;
    private  final UserClient userClient;

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest loginRequest,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        RedirectAttributes redirectAttributes) {
        try {
            String token = authService.login(loginRequest, request, response);

            if (token != null) {

                // Create a cookie with the token
                Cookie authCookie = new Cookie("AUTH_TOKEN", token);
                authCookie.setHttpOnly(true);   // safer, prevents JS access
                authCookie.setSecure(true);    // set true for rander(HTTPS)
                authCookie.setPath("/");        // available to all paths
                // If both MS are under same domain, you can also setDomain("localhost")
                response.addCookie(authCookie);
                //Get the authorities (roles) of the newly logged-in user
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                boolean isAdmin = auth.getAuthorities().stream()
                        .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

                //Direct to .xhtml page on Port 8083
                if (isAdmin) {

                    return "redirect:https://dashboard-bl7h.onrender.com/admin.xhtml";
                } else {

                    return "redirect:https://dashboard-bl7h.onrender.com/dashboard.xhtml";
                }

            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid Email or Password");
                return "redirect:/api/auth/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Login failed: " + e.getMessage());
            return "redirect:/api/auth/login";
        }
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // This returns your login.html template
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Clear the Security context/session
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        //Redirect to log in with the '?logout' trigger
        return "redirect:/api/auth/login?logout";
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getLoggedInUser(Authentication authentication) {
        UserResponse userResponse = new UserResponse();
        if (authentication == null || !authentication.isAuthenticated()) {
            userResponse.setId(0L);
            userResponse.setEmail("");
            userResponse.setPassword("");
            userResponse.setRole("");
            userResponse.setUsername("");


            return ResponseEntity.status(401).body(userResponse);
        }
//        userResponse = (UserResponse) authentication.getPrincipal();

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserResponse) {
            userResponse = (UserResponse) principal;
        } else if (principal instanceof String) {
            // Look up user by username
            userResponse = userClient.findByUsername((String) principal);
        } else {
            return ResponseEntity.status(401).build();
        }

        // This returns the logged-in user object as JSON
        // It will include email, role, etc.
        return ResponseEntity.ok(userResponse);
    }


}

