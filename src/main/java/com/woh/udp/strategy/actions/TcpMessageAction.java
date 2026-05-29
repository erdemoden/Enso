package com.woh.udp.strategy.actions;

import com.woh.udp.Util.RedisCacheStore;
import com.woh.udp.dto.ServerRequestResponse;
import com.woh.udp.services.LocalRoomService;
import com.woh.udp.services.ServerService;
import com.woh.udp.strategy.base.ActionStrategy;
import org.springframework.stereotype.Component;

import java.net.Socket;
@Component("tcp_message")
public class TcpMessageAction extends ActionStrategy {
    public TcpMessageAction(RedisCacheStore redisCacheStore, ServerService serverService, LocalRoomService localRoomService) {
        super(redisCacheStore, serverService, localRoomService);
    }

    @Override
    public void performAction(ServerRequestResponse serverRequestResponse, Socket socket) {
        serverService.sendTcpMessage(serverRequestResponse);
    }
}
