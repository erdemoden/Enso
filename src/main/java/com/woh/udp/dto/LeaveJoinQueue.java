package com.woh.udp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LeaveJoinQueue {
    private String roomId;
    private String userId;
    private String action;
    private LocalDateTime timestamp; 
}
