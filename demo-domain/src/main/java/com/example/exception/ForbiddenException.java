package com.example.exception;

import com.example.enums.ResultCode;

public class ForbiddenException extends BaseException {
    public ForbiddenException() {
        super(ResultCode.FORBIDDEN);
    }

    public ForbiddenException(String message) {
        super(ResultCode.FORBIDDEN, message);
    }
}