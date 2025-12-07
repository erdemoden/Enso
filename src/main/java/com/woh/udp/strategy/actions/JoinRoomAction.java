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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.Socket;

@Component("join_room")
@Slf4j
public class JoinRoomAction extends ActionStrategy {

    public JoinRoomAction(RedisCacheStore redisCacheStore, ServerService serverService, LocalRoomService localRoomService) {
        super(redisCacheStore, serverService,localRoomService);
    }

    @Override
    public void performAction(ServerRequestResponse serverRequestResponse, Socket socket) {
        log.info("join room");
        boolean lockAcquired = false;
        String lockKey = serverRequestResponse.getRoomCode() + "lock";
        try {
            lockAcquired = redisCacheStore.setIfAbsent(lockKey, "availableSlots", 2);
            RoomDTO roomDto = redisCacheStore.get(serverRequestResponse.getRoomCode());
            if (roomDto != null && lockAcquired) {
                Integer availableSlots = roomDto.getAvailableSlots();
                if (availableSlots != null && availableSlots > 0) {
                    roomDto.setAvailableSlots(availableSlots - 1);
                    roomDto.getUserIds().add(serverRequestResponse.getUserCode());
                    localRoomService.addTcpConnection(serverRequestResponse.getUserCode(),serverRequestResponse.getRoomCode(),socket);
                    localRoomService.addLocalRoomDto(new LocalRoomDTO(serverRequestResponse.getUserCode(),serverRequestResponse.getRoomCode(),socket));
                    redisCacheStore.put(serverRequestResponse.getRoomCode(), roomDto);
                    serverService.sendTcpMessage(serverRequestResponse);
                } else {
                    throw new BusinessException(ErrorCode.ROOM_IS_FULL);
                }
            } else if (!lockAcquired) {
                throw new BusinessException(ErrorCode.LOCK_ACQUIRE_ERROR);
            } else {
                throw new BusinessException(ErrorCode.ROOM_NOT_EXIST);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.JOIN_ROOM_ERROR);
        } finally {
            if (lockAcquired) {
                redisCacheStore.delete(lockKey);
            }
        }
    }
}
