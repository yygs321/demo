package com.example.exception;

import com.example.enums.ResultCode;

public class UnprocessableEntityException extends BaseException {
    public UnprocessableEntityException() {
        super(ResultCode.UNPROCESSABLE_ENTITY);
    }

    public UnprocessableEntityException(String message) {
        super(ResultCode.UNPROCESSABLE_ENTITY, message);
    }
}