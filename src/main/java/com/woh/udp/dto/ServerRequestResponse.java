package com.woh.udp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.HashMap;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerRequestResponse {
    String userCode;
    String roomCode;
    String token;
    Integer roomSlots;
    HashMap<String, Object> content = new HashMap<>();
    String action;
    Boolean includeMe;
}
