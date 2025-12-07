package com.woh.udp.strategy.base;

import com.woh.udp.Util.RedisCacheStore;
import com.woh.udp.dto.ServerRequestResponse;
import com.woh.udp.services.LocalRoomService;
import com.woh.udp.services.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.Socket;

@Component
@RequiredArgsConstructor
public abstract class ActionStrategy {
    protected final RedisCacheStore redisCacheStore;
    protected final ServerService serverService;
    protected final LocalRoomService localRoomService;

    public abstract void performAction(ServerRequestResponse serverRequestResponse, Socket socket);
}
