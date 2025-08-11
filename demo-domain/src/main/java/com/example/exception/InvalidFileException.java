package com.example.exception;

import com.example.enums.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidFileException extends BusinessException {

    private final ErrorCode errorCode;

    public InvalidFileException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public InvalidFileException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}