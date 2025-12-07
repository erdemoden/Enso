package com.woh.udp.dto;

import java.net.Socket;
import java.time.LocalDateTime;

public class LocalRoomDTO {
    private String roomCode;
    private String userCode;
    private Socket socket;
    private LocalDateTime whenMessageArrived;

    public LocalRoomDTO(String userCode,String roomCode,Socket socket) {
        this.roomCode = roomCode;
        this.userCode = userCode;
        this.socket = socket;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public LocalDateTime getWhenMessageArrived() {
        return whenMessageArrived;
    }

    public void setWhenMessageArrived(LocalDateTime whenMessageArrived) {
        this.whenMessageArrived = whenMessageArrived;
    }
}
