package com.woh.udp.services;

import com.woh.udp.dto.LocalRoomDTO;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LocalRoomService {
    private final Map<String, ConcurrentHashMap<String, InetSocketAddress>> udpRoomUsers = new ConcurrentHashMap<>();
    private final Map<String, ConcurrentHashMap<String, Socket>> tcpRoomUsers = new ConcurrentHashMap<>();
    private final Set<LocalRoomDTO> localRoomDTOS = ConcurrentHashMap.newKeySet();

    public void addUdpConnection(String userName, String roomName, InetSocketAddress address) {
        udpRoomUsers.computeIfAbsent(roomName, k -> new ConcurrentHashMap<>())
                .putIfAbsent(userName, address);
    }

    public void removeUdpConnection(String roomName, String userName) {
        Map<String, InetSocketAddress> roomUdpUsers = udpRoomUsers.get(roomName);
        if (roomUdpUsers != null) {
            roomUdpUsers.remove(userName);
            if (roomUdpUsers.isEmpty()) {
                udpRoomUsers.remove(roomName);
            }
        }
    }

    public void removeAllUdpConnections(String roomName) {
        Set<String> udpUsersInRoom = getUdpUsersFromRoom(roomName).keySet();
        for (String user : udpUsersInRoom) {
            removeUdpConnection(roomName, user);
        }
    }

    public void addTcpConnection(String userName, String roomName, Socket socket) {
        tcpRoomUsers.computeIfAbsent(roomName, k -> new ConcurrentHashMap<>())
                .putIfAbsent(userName, socket);
    }

    public void removeTcpConnection(String roomName, String userName) {
        Map<String, Socket> roomTcpUsers = tcpRoomUsers.get(roomName);
        if (roomTcpUsers != null) {
            roomTcpUsers.remove(userName);
            if (roomTcpUsers.isEmpty()) {
                tcpRoomUsers.remove(roomName);
            }
        }
    }

    public void removeAllTcpConnections(String roomName) {
        Set<String> tcpUsersInRoom = getTcpUsersFromRoom(roomName).keySet();
        for (String user : tcpUsersInRoom) {
            removeTcpConnection(roomName, user);
        }
    }

    public ConcurrentHashMap<String, InetSocketAddress> getUdpUsersFromRoom(String roomName) {
        return udpRoomUsers.get(roomName);
    }

    public ConcurrentHashMap<String, Socket> getTcpUsersFromRoom(String roomName) {
        return tcpRoomUsers.get(roomName);
    }

    public Set<String> getAllRoomsForTcp() {
        return tcpRoomUsers.keySet();
    }

    public void addLocalRoomDto(LocalRoomDTO localRoomDTO) {
        localRoomDTOS.add(localRoomDTO);
    }

    public void removeLocalRoomDto(LocalRoomDTO localRoomDTO) {
        localRoomDTOS.remove(localRoomDTO);
    }
    public void removeLocalRoomDto(String roomCode,String userCode){
        Optional<LocalRoomDTO> localRoomDTOToDelete = localRoomDTOS.stream().filter(localRoomDTO -> localRoomDTO.getRoomCode().equals(roomCode) && localRoomDTO.getUserCode().equals(userCode)).findFirst();
        localRoomDTOToDelete.ifPresent(this::removeLocalRoomDto);
    }

    public Set<LocalRoomDTO> allLocalRoomsByRoomCode(String roomName) {
        Set<LocalRoomDTO> userCodes = ConcurrentHashMap.newKeySet();
        localRoomDTOS.forEach(localRoomDTO -> {
            if (localRoomDTO.getRoomCode().equals(roomName)) {
                userCodes.add(localRoomDTO);
            }
        });
        return userCodes;
    }

    public void deleteAllLocalRoomDTOsInARoom(String roomName) {
        Set<LocalRoomDTO> localRoomDTOS = allLocalRoomsByRoomCode(roomName);
        for (LocalRoomDTO localRoomDTO : localRoomDTOS) {
            removeLocalRoomDto(localRoomDTO);
        }
    }
    public void removeDisconnectedSocket(Socket socket) {
        tcpRoomUsers.forEach((roomCode, users) -> {
            users.entrySet().removeIf(entry -> entry.getValue().equals(socket));
            if (users.isEmpty()) {
                tcpRoomUsers.remove(roomCode);
            }
        });
        localRoomDTOS.removeIf(dto -> dto.getSocket().equals(socket));
    }

    public void updateLastSeen(String roomCode, String userCode) {
        localRoomDTOS.stream()
                .filter(dto -> dto.getRoomCode().equals(roomCode) && dto.getUserCode().equals(userCode))
                .findFirst()
                .ifPresent(dto -> dto.setWhenMessageArrived(LocalDateTime.now()));
    }

    //getters
    public Set<LocalRoomDTO> getLocalRoomDTOS() {
        return localRoomDTOS;
    }
}
