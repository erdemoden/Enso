package com.woh.udp.dto;

import com.woh.udp.enums.ErrorCode;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class GenericResponse <T> {
    private Boolean success;
    private String message;
    private String messageCode;
    private T data;


    public static <T> GenericResponse<T> error(ErrorCode errorCode){
        return GenericResponse.<T>builder()
                .data(null)
                .success(false)
                .message(errorCode.getErrorMessage())
                .messageCode(errorCode.getErrorMessage())
                .build();
    }

    public static <T> GenericResponse<T> success(T data,String message){
        return GenericResponse.<T>builder()
                .data(data)
                .success(false)
                .message(message)
                .messageCode("200")
                .build();
    }
}
