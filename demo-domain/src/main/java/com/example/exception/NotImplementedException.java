package com.example.exception;

import com.example.enums.ResultCode;

public class NotImplementedException extends BaseException {
    public NotImplementedException() {
        super(ResultCode.NOT_IMPLEMENTED);
    }

    public NotImplementedException(String message) {
        super(ResultCode.NOT_IMPLEMENTED, message);
    }
}