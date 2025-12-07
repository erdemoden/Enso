package com.woh.udp.errors;

import com.woh.udp.dto.GenericResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BusinessException.class})
    public <T> GenericResponse<T> handleBusinessException (BusinessException ex){
        return GenericResponse.error(ex.getErrorCode());
    }

}
