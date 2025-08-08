package com.example.exception;

import com.example.enums.ResultCode;
import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
    private final ResultCode code;
    private final String message;

    // 기본 메시지 사용
    protected BaseException(ResultCode code) {
        super(code.getUserMessage()); // ResultCode의 userMessage 필드를 사용
        this.code = code;
        this.message = code.getUserMessage();
    }

    // 커스텀 메시지 허용
    protected BaseException(ResultCode code, String customMessage) {
        super(customMessage);
        this.code = code;
        this.message = customMessage;
    }
}
