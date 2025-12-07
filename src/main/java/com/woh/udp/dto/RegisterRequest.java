package com.woh.udp.dto;

import com.woh.udp.entity.enums.Plans;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequest {
    private String email;
    private String password;
    private String username;
    private Plans plan;
}
