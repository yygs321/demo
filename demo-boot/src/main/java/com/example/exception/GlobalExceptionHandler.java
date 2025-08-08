package com.example.exception;

import com.example.dto.response.ApiResponse;
import com.example.enums.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("⚠️ handleMethodArgumentNotValidException", e);
        return ResponseEntity.badRequest().body(ApiResponse.fail(ResultCode.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Object>> handleBaseException(BaseException e) {
        log.warn("⚠️ Custom Exception Occurred: {}", e.getCode().getMessage());
        return ResponseEntity
                .status(Integer.parseInt(e.getCode().getCode()))
                .body(ApiResponse.fail(e.getCode(), e.getCode().getUserMessage()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        log.error("⚠️ handleException", e);
        return ResponseEntity.internalServerError().body(ApiResponse.fail(ResultCode.INTERNAL_SERVER_ERROR, e.getMessage()));
    }
}

