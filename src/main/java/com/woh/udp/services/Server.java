package com.woh.udp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woh.udp.Util.RedisCacheStore;
import com.woh.udp.dto.GenericResponse;
import com.woh.udp.dto.RoomDTO;
import com.woh.udp.dto.ServerRequestResponse;
import com.woh.udp.strategy.base.ActionStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
@Slf4j
@RequiredArgsConstructor
public class Server implements CommandLineRunner {
    DatagramSocket server;
    ServerSocket tcpServer;
    Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    private final ApplicationContext applicationContext;
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectMapper objectMapperPack = new ObjectMapper(new MessagePackFactory());
    private final ServerService serverService;
    private final RedisCacheStore redisCacheStore;

    @Override
    public void run(String... args) {
        System.out.println("DENEME :: " + System.getProperty("java.vm.name"));
        try {
            RoomDTO roomDTO = RoomDTO.builder()
                    .roomName("room_123")
                    .availableSlots(2)
                    .totalSlots(2)
                    .userIds(new ArrayList<>())
                    .ownerUser("erdem").build();
            redisCacheStore.put("room_123", roomDTO);
            server = new DatagramSocket(11907);
            System.out.println("UDP Server started");
            tcpServer = new ServerSocket(11908);
            System.out.println("TCP Server started");
            executor.execute(this::listenUdp);
            executor.execute(this::listenTcp);
        } catch (Exception e) {
            log.error("Error in server: {}", e.getMessage());
        }
    }

    private void listenUdp() {
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(new byte[1200], 1200);
                server.receive(packet);
                executor.execute(() -> handlePacket(packet));
            } catch (Exception e) {
                log.error("Error in udp server: {}", e.getMessage());
            }
        }

    }

    private void listenTcp() {
        while (true) {
            try {
                final Socket socket = tcpServer.accept();
                socket.setKeepAlive(true);
                socket.setSoTimeout(5000);
                executor.execute(() -> {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String message;
                        while ((message = reader.readLine()) != null) {
                            handleMessageTcp(message, socket);
                        }
                    } catch (Exception e) {
                        log.error("Error in client thread: {}", e.getMessage());
                    }
                });
            } catch (Exception e) {
                log.error("Error in tcp server: {}", e.getMessage());
            }
        }
    }

    private void handlePacket(DatagramPacket packet) {
        try {
            byte[] data = Arrays.copyOf(packet.getData(), packet.getLength());
            ServerRequestResponse serverRequestResponse = objectMapperPack.readValue(data, ServerRequestResponse.class);
            if (serverRequestResponse.getAction().equals("join_room") || serverRequestResponse.getAction().equals("create_room")) {
                serverService.joinRoomUdp(server, serverRequestResponse, packet);
            } else {
                serverService.sendMessage(server, serverRequestResponse);
            }
        } catch (Exception e) {
            log.error("Error : {}", e.getMessage());
        }
        // log.info("Received packet from {}: {}", packet.getAddress(), server.getPort());
    }

    private void handleMessageTcp(String message, Socket socket) {
        try {
            log.info("Gelen TCP mesajı: {}", message);
            ServerRequestResponse serverRequestResponse = objectMapper.readValue(message, ServerRequestResponse.class);
            ActionStrategy actionStrategy = (ActionStrategy) applicationContext.getBean(serverRequestResponse.getAction());
            actionStrategy.performAction(serverRequestResponse, socket);
        } catch (Exception e) {
            log.error("Error sending response: {}", e.getMessage());
        }
    }

}
