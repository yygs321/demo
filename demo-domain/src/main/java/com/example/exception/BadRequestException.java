package com.example.exception;

import com.example.enums.ResultCode;

public class BadRequestException extends BaseException {
    public BadRequestException() {
        super(ResultCode.BAD_REQUEST);
    }

    public BadRequestException(String message) {
        super(ResultCode.BAD_REQUEST, message);
    }
}