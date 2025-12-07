package com.woh.udp.strategy.actions;

import com.woh.udp.Util.RedisCacheStore;
import com.woh.udp.dto.RoomDTO;
import com.woh.udp.dto.ServerRequestResponse;
import com.woh.udp.services.LocalRoomService;
import com.woh.udp.services.ServerService;
import com.woh.udp.strategy.base.ActionStrategy;
import org.springframework.stereotype.Component;

import java.net.Socket;

@Component("delete_room")
public class DeleteRoomAction extends ActionStrategy {

    public DeleteRoomAction(RedisCacheStore redisCacheStore, ServerService serverService, LocalRoomService localRoomService) {
        super(redisCacheStore, serverService,localRoomService);
    }

    @Override
    public void performAction(ServerRequestResponse serverRequestResponse, Socket socket) {
        boolean lock = redisCacheStore.setIfAbsent(serverRequestResponse.getRoomCode() + "lock", "roomLockDelete", 2);
        RoomDTO roomDto = redisCacheStore.get(serverRequestResponse.getRoomCode());
        if(roomDto != null && lock && serverRequestResponse.getUserCode().equals(roomDto.getOwnerUser())){
            serverService.deletedRoomUsers(serverRequestResponse);
        }

    }
}
