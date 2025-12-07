package com.woh.udp.errors;

import com.woh.udp.enums.ErrorCode;

public class BusinessException extends RuntimeException{

    private ErrorCode errorCode;


    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
