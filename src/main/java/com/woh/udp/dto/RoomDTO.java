package com.woh.udp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    private Integer availableSlots;
    private Integer totalSlots;
    private List<String> userIds = new ArrayList<>();
    private String ownerUser;
    private String roomName;
}
