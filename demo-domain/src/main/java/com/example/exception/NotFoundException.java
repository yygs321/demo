package com.example.exception;

import com.example.enums.ResultCode;

public class NotFoundException extends BaseException {
    public NotFoundException() {
        super(ResultCode.NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(ResultCode.NOT_FOUND, message);
    }
}