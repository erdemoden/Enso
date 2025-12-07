package com.woh.udp.repository;

import com.woh.udp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Boolean existsByEmail(String email);
    User findByEmail(String email);
}
