package com.woh.udp.strategy.actions;

import com.woh.udp.Util.RedisCacheStore;
import com.woh.udp.dto.LocalRoomDTO;
import com.woh.udp.dto.RoomDTO;
import com.woh.udp.dto.ServerRequestResponse;
import com.woh.udp.enums.ErrorCode;
import com.woh.udp.errors.BusinessException;
import com.woh.udp.services.LocalRoomService;
import com.woh.udp.services.ServerService;
import com.woh.udp.strategy.base.ActionStrategy;
import org.springframework.stereotype.Component;

import java.net.Socket;

@Component("leave_room")
public class LeaveRoomAction extends ActionStrategy {

    public LeaveRoomAction(RedisCacheStore redisCacheStore, ServerService serverService, LocalRoomService localRoomService) {
        super(redisCacheStore, serverService,localRoomService);
    }

    @Override
    public void performAction(ServerRequestResponse serverRequestResponse, Socket socket) {
        try {
            boolean lock = redisCacheStore.setIfAbsent(serverRequestResponse.getRoomCode() + "lock", "availableSlots", 2);
            RoomDTO roomDto = redisCacheStore.get(serverRequestResponse.getRoomCode());
            if (lock && roomDto != null) {
                if (roomDto.getUserIds().remove(serverRequestResponse.getUserCode())) {
                    Integer availableSlots = roomDto.getAvailableSlots();
                    roomDto.setAvailableSlots(availableSlots != null ? availableSlots + 1 : 1);
                    localRoomService.removeTcpConnection(serverRequestResponse.getRoomCode(),serverRequestResponse.getUserCode());
                    localRoomService.removeUdpConnection(serverRequestResponse.getRoomCode(),serverRequestResponse.getUserCode());
                    localRoomService.removeLocalRoomDto(serverRequestResponse.getRoomCode(),serverRequestResponse.getUserCode());
                    redisCacheStore.put(serverRequestResponse.getRoomCode(), roomDto);
                    serverService.sendTcpMessage(serverRequestResponse);
                } else {
                    throw new BusinessException(ErrorCode.USER_NOT_IN_ROOM);
                }
            } else {
                throw new BusinessException(ErrorCode.ROOM_NOT_EXIST);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.LEAVE_ROOM_ERROR);
        }
    }
}
