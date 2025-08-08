package com.example.exception;

import com.example.enums.ResultCode;

public class GatewayTimeoutException extends BaseException {
    public GatewayTimeoutException() {
        super(ResultCode.GATEWAY_TIMEOUT);
    }

    public GatewayTimeoutException(String message) {
        super(ResultCode.GATEWAY_TIMEOUT, message);
    }
}