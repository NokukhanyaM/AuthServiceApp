package com.example.AuthServiceApp.controller;

import com.example.AuthServiceApp.DTO.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ViewController {

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        // We add an empty Request object for the login form
        model.addAttribute("loginRequest", new LoginRequest());
        return "login"; // Points to src/main/resources/templates/login.html
    }


}
