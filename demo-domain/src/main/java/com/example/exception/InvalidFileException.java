package com.example.exception;

import com.example.enums.ResultCode;
import lombok.Getter;

@Getter
public class InvalidFileException extends BaseException {

    public InvalidFileException(ResultCode code) {
        super(code);
    }

    public InvalidFileException(ResultCode code, String customMessage) {
        super(code, customMessage);
    }
}