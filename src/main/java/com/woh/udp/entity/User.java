package com.woh.udp.entity;

import com.woh.udp.entity.enums.Plans;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;
    private String username;
    private String password;
    private String email;
    @Enumerated(value = jakarta.persistence.EnumType.STRING)
    private Plans plan;
}
