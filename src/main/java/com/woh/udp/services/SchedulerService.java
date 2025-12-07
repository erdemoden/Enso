package com.woh.udp.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.util.Map;
import java.util.Set;

@Component
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
    @Scheduled(cron = "*/15 * * * * *")
    public void heartBeat(){
        Set<String> rooms = localRoomService.getAllRoomsForTcp();
        for(String roomName : rooms){

        }
    }
}
