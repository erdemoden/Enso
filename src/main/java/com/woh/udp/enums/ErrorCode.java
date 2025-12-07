package com.woh.udp.enums;

public enum ErrorCode {
    JOIN_ROOM_LOCK("ERROR_0001","Please try to join room again. Room joins are experiencing heavy load"),
    ROOM_IS_FULL("ERROR_0002","Room is full you can not join"),
    JOIN_ROOM_ERROR("ERROR_0003","Something went wrong when joining room"),
    USER_NOT_IN_ROOM("ERROR_0004","User is not in the room"),
    ROOM_NOT_EXIST("ERROR_0005","Room is not exist"),
    LEAVE_ROOM_ERROR("ERROR_0006","Error Leaving the room"),
    LOCK_ACQUIRE_ERROR("ERROR_0007","failed to acquire lock");

    private final String errorCode;
    private final String errorMessage;

    ErrorCode(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
