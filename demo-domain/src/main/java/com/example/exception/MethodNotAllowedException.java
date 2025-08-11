package com.example.exception;

import com.example.enums.ResultCode;

public class MethodNotAllowedException extends BaseException {
    public MethodNotAllowedException() {
        super(ResultCode.METHOD_NOT_ALLOWED);
    }

    public MethodNotAllowedException(String message) {
        super(ResultCode.METHOD_NOT_ALLOWED, message);
    }
}