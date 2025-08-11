package com.example.exception;

import com.example.enums.ResultCode;

public class NotAcceptableException extends BaseException {
    public NotAcceptableException() {
        super(ResultCode.NOT_ACCEPTABLE);
    }

    public NotAcceptableException(String message) {
        super(ResultCode.NOT_ACCEPTABLE, message);
    }
}