package com.woh.udp.controllers;

import com.woh.udp.dto.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    @PostMapping("/login")
    public GenericResponse login() {
        log.info("Login endpoint hit");
        GenericResponse response = GenericResponse.builder()
                .success(true)
                .message("Login successful")
                .build();
        return response;
    }
    @PostMapping("/register")
    public GenericResponse register() {
        log.info("Register endpoint hit");
        GenericResponse response = GenericResponse.builder()
                .success(true)
                .message("Registration successful")
                .build();
        return response;
    }
}
