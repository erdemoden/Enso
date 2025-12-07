package com.woh.udp.strategy.actions;

import com.woh.udp.Util.RedisCacheStore;
import com.woh.udp.dto.RoomDTO;
import com.woh.udp.dto.ServerRequestResponse;
import com.woh.udp.services.LocalRoomService;
import com.woh.udp.services.ServerService;
import com.woh.udp.strategy.base.ActionStrategy;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Component("create_room")
public class CreateRoomAction extends ActionStrategy {

    public CreateRoomAction(RedisCacheStore redisCacheStore, ServerService serverService, LocalRoomService localRoomService) {
        super(redisCacheStore, serverService,localRoomService);
    }

    @Override
    public void performAction(ServerRequestResponse serverRequestResponse, Socket socket) {
        boolean lock = redisCacheStore.setIfAbsent(serverRequestResponse.getRoomCode() + "lock", "roomLockCreate", 2);
        if (lock) {
            RoomDTO roomDTO = RoomDTO.builder()
                    .ownerUser(serverRequestResponse.getUserCode())
                    .userIds(new ArrayList<>(List.of(serverRequestResponse.getUserCode())))
                    .roomName(serverRequestResponse.getRoomCode())
                    .totalSlots(serverRequestResponse.getRoomSlots())
                    .build();

            redisCacheStore.put(serverRequestResponse.getRoomCode(),roomDTO);
        }
    }
}
