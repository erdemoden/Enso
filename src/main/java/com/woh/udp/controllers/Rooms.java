package com.woh.udp.controllers;

import com.woh.udp.Util.RedisCacheStore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class Rooms {

    private final RedisCacheStore redisCacheStore;

    @PostMapping
    public void createRoom() {
        // Logic to create a room
        // This is a placeholder method, implement the actual logic as needed
    }
}
