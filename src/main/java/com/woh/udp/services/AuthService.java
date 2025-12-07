package com.woh.udp.services;

import com.woh.udp.dto.GenericResponse;
import com.woh.udp.dto.LoginRequest;
import com.woh.udp.dto.RegisterRequest;
import com.woh.udp.entity.User;
import com.woh.udp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public GenericResponse login(LoginRequest loginRequest) {
        Boolean isUserExists = userRepository.existsByEmail(loginRequest.getEmail());
        if(!isUserExists) {
            return GenericResponse.builder()
                    .success(false)
                    .message("User does not exist")
                    .build();
        }
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return GenericResponse.builder()
                    .success(false)
                    .message("Invalid password")
                    .build();
        }
        return GenericResponse.builder()
                .success(true)
                .message("Login successful")
                .build();
    }
    public GenericResponse register(RegisterRequest registerRequest) {
        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            return GenericResponse.builder()
                    .success(false)
                    .message("User already exists")
                    .build();
        }
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPlan(registerRequest.getPlan());
        user.setUsername(registerRequest.getUsername());
        userRepository.save(user);
        return GenericResponse.builder()
                .success(true)
                .message("Registration successful")
                .build();
    }
}
