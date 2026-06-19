package com.example.AuthServiceApp.interfaces;

import com.example.AuthServiceApp.DTO.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * The @FeignClient annotation marks this as a communication bridge.
 * 'name' should match the spring.application.name of your User Microservice.
 * 'url' is where the User Microservice is currently running.
 */
@FeignClient(name = "UserServiceApp", url = "https://userserviceapp-1.onrender.com/register")
public interface UserClient {

    /**
     * This method tells Feign to send a GET request to the User Service.
     * It maps the local 'email' variable into the URL path.
     */
    @GetMapping("/api/users/email/{email}")
    UserResponse findByEmail(@PathVariable("email") String email);

    @GetMapping("/api/users/username/{username}")
    UserResponse findByUsername(@PathVariable("username") String username);
}
