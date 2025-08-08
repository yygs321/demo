package com.example.exception;

import com.example.enums.ResultCode;

public class BadGatewayException extends BaseException {
    public BadGatewayException() {
        super(ResultCode.BAD_GATEWAY);
    }

    public BadGatewayException(String message) {
        super(ResultCode.BAD_GATEWAY, message);
    }
}