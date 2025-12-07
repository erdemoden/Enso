package com.woh.udp.Util;

import com.woh.udp.dto.GenericResponse;
import com.woh.udp.dto.ServerErrorResponse;

import java.util.HashMap;
import java.util.Map;

public class GenericResponseMapper {

    public static <T> Map<String,Object> mapToErrorMap (GenericResponse<T> genericResponse){
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("error", ServerErrorResponse.builder()
                .message(genericResponse.getMessage())
                .errorCode(genericResponse.getMessageCode())
                .build());
        return errorMap;
    }
}
