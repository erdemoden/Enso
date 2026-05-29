package com.woh.udp.services;

import com.woh.udp.dto.LocalRoomDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class SchedulerService {
    private final LocalRoomService localRoomService;
    private final ServerService serverService;
    public SchedulerService(LocalRoomService localRoomService,ServerService serverService) {
        this.localRoomService = localRoomService;
        this.serverService = serverService;
    }

    @Scheduled(fixedRate = 500)
    public void joinLeaveRoomScheduler() {

    }
    @Scheduled(fixedRate = 10000)
    public void heartBeat() {
        log.info("heartBeat started");
        Set<String> rooms = localRoomService.getAllRoomsForTcp();
        for (String roomName : rooms) {
            Set<LocalRoomDTO> users = localRoomService.allLocalRoomsByRoomCode(roomName);
            if (users == null) continue;

            for (LocalRoomDTO dto : users) {
                if (dto.getWhenMessageArrived() != null &&
                        LocalDateTime.now().minusSeconds(30).isAfter(dto.getWhenMessageArrived())) {

                    log.info("Kullanıcı timeout: {}", dto.getUserCode());
                    localRoomService.removeDisconnectedSocket(dto.getSocket());
                    continue;
                }

                try {
                    PrintWriter writer = new PrintWriter(dto.getSocket().getOutputStream(), true);
                    writer.println("{\"action\":\"ping\"}");
                } catch (Exception e) {
                    log.info("Socket ölü, temizleniyor: {}", dto.getUserCode());
                    localRoomService.removeDisconnectedSocket(dto.getSocket());
                }
            }
        }
    }
}
