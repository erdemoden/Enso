package com.woh.udp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woh.udp.Util.RedisCacheStore;
import com.woh.udp.dto.LocalRoomDTO;
import com.woh.udp.dto.RoomDTO;
import com.woh.udp.dto.ServerRequestResponse;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ServerService {
    private final RedisCacheStore redisCacheStore;
    private final LocalRoomService localRoomService;
    private final ObjectMapper objectMapperPack = new ObjectMapper(new MessagePackFactory());
    private final ObjectMapper objectMapper = new ObjectMapper();

    ServerService(RedisCacheStore redisCacheStore, LocalRoomService localRoomService) {
        this.redisCacheStore = redisCacheStore;
        this.localRoomService = localRoomService;
    }

    public void sendMessageToUser(InetSocketAddress userAddress, ServerRequestResponse message, DatagramSocket server) {
        try {
            byte[] buffer = objectMapperPack.writeValueAsBytes(message);
            if (buffer.length > 1200) {
                throw new IllegalArgumentException("Message size exceeds maximum limit of 1200 bytes");
            }
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, userAddress.getAddress(), userAddress.getPort());
            server.send(packet);
        } catch (Exception e) {
            // -TODO: user a mesajı gönderebilirim hata aldığıyla ilgili panelde
            log.error("Error sending message to user {}: {}", userAddress, e.getMessage(), e); // Stack trace ekle

        }
    }

    private void sendMessageToUserTCP(Map<String, Object> message, Socket socketOfUser) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            PrintWriter writer = new PrintWriter(socketOfUser.getOutputStream(), true);
            writer.println(jsonMessage);
        } catch (Exception e) {
            log.error("Error sending message to user {}: {}", socketOfUser, e.getMessage());
        }
    }

    public void sendMessage(DatagramSocket server, ServerRequestResponse serverRequestResponse) {
        // TODO : room yoksa falan filan bişeyler
        RoomDTO roomDTO = redisCacheStore.get(serverRequestResponse.getRoomCode());
        Map<String, InetSocketAddress> udpUsersInRoom = localRoomService.getUdpUsersFromRoom(roomDTO.getRoomName());
        if (udpUsersInRoom != null && !udpUsersInRoom.isEmpty()) {
            for (String user : udpUsersInRoom.keySet()) {
                if ((serverRequestResponse.getIncludeMe() == null || !serverRequestResponse.getIncludeMe()) && user.equals(serverRequestResponse.getUserCode())) {
                    continue;
                }
                this.sendMessageToUser(udpUsersInRoom.get(user), serverRequestResponse, server);
            }
        }
    }

    public void sendTcpMessage(ServerRequestResponse serverRequestResponse) {
        RoomDTO roomDTO = redisCacheStore.get(serverRequestResponse.getRoomCode());
        Map<String, Socket> tcpUsersInRoom = localRoomService.getTcpUsersFromRoom(roomDTO.getRoomName());
        if (tcpUsersInRoom != null && !tcpUsersInRoom.isEmpty()) {
            for (String user : tcpUsersInRoom.keySet()) {
                if ((serverRequestResponse.getIncludeMe() == null || !serverRequestResponse.getIncludeMe()) && user.equals(serverRequestResponse.getUserCode())) {
                    continue;
                }
                this.sendMessageToUserTCP(serverRequestResponse.getContent(), tcpUsersInRoom.get(user));
            }
        }
    }
    @Scheduled(fixedDelay = 15000)
    public void healthCheckOfTcp() {
        log.info("HealthCheck started");
        Set<LocalRoomDTO> localRoomDTOSet = localRoomService.getLocalRoomDTOS();
        Set<LocalRoomDTO> usersToRemove = ConcurrentHashMap.newKeySet();
        Map<String, Object> message = new HashMap<>();
        message.put("healthCheck","ping");
        for(LocalRoomDTO localRoomDTO : localRoomDTOSet){
            try {
                String jsonMessage = objectMapper.writeValueAsString(message);
                PrintWriter writer = new PrintWriter(localRoomDTO.getSocket().getOutputStream(), true);
                writer.println(jsonMessage);
            }catch (Exception e){
                log.error("hata");
            }
        }
    }

    public void joinRoomUdp(DatagramSocket server, ServerRequestResponse serverRequestResponse, DatagramPacket packet) {
        try {
            RoomDTO roomDTO = redisCacheStore.get(serverRequestResponse.getRoomCode());
            InetSocketAddress inetSocketAddress = new InetSocketAddress(packet.getAddress(), packet.getPort());
            localRoomService.addUdpConnection(serverRequestResponse.getUserCode(), roomDTO.getRoomName(), inetSocketAddress);
            ServerRequestResponse response = new ServerRequestResponse();
            response.getContent().put("join", "true");
            byte[] buffer = objectMapper.writeValueAsBytes(response);
            DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
            server.send(sendPacket);
            log.info("Welcome user : " + localRoomService.getUdpUsersFromRoom(serverRequestResponse.getRoomCode()).get(serverRequestResponse.getUserCode()));
        } catch (Exception e) {
            log.error("Something went wrong join room udp ");
        }
    }

    public void deletedRoomUsers(ServerRequestResponse serverRequestResponse) {
        RoomDTO roomDTO = redisCacheStore.get(serverRequestResponse.getRoomCode());
        String roomName = roomDTO.getRoomName();
        sendTcpMessage(serverRequestResponse);
        localRoomService.removeAllTcpConnections(roomName);
        localRoomService.removeAllUdpConnections(roomName);
        localRoomService.deleteAllLocalRoomDTOsInARoom(roomName);

    }
}
