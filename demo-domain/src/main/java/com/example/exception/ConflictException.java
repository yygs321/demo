package com.example.exception;

import com.example.enums.ResultCode;

public class ConflictException extends BaseException {
    public ConflictException() {
        super(ResultCode.CONFLICT);
    }

    public ConflictException(String message) {
        super(ResultCode.CONFLICT, message);
    }
}