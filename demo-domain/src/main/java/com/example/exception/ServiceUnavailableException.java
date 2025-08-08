package com.example.exception;

import com.example.enums.ResultCode;

public class ServiceUnavailableException extends BaseException {
    public ServiceUnavailableException() {
        super(ResultCode.SERVICE_UNAVAILABLE);
    }

    public ServiceUnavailableException(String message) {
        super(ResultCode.SERVICE_UNAVAILABLE, message);
    }
}