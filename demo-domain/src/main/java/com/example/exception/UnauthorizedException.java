package com.example.exception;

import com.example.enums.ResultCode;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException() {
        super(ResultCode.UNAUTHORIZED, "Action not authorized");
    }
}
