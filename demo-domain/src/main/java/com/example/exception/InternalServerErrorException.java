package com.example.exception;

import com.example.enums.ResultCode;

public class InternalServerErrorException extends BaseException {
    public InternalServerErrorException() {
        super(ResultCode.INTERNAL_SERVER_ERROR);
    }

    public InternalServerErrorException(String message) {
        super(ResultCode.INTERNAL_SERVER_ERROR, message);
    }
}
