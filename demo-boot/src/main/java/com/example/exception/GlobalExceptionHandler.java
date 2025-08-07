package com.example.exception;

import com.example.common.ErrorCode;
import com.example.dto.response.ApiResponse;
import com.example.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmployeeNotFoundException.class)
    protected ResponseEntity<ApiResponse<Object>> handleEmployeeNotFoundException(EmployeeNotFoundException e) {
        log.error("handleEmployeeNotFoundException", e);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.EMPLOYEE_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail(errorResponse));
    }

    @ExceptionHandler(BoardNotFoundException.class)
    protected ResponseEntity<ApiResponse<Object>> handleBoardNotFoundException(BoardNotFoundException e) {
        log.error("handleBoardNotFoundException", e);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.BOARD_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail(errorResponse));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
        return ResponseEntity.badRequest().body(ApiResponse.fail(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        log.error("handleException", e);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.internalServerError().body(ApiResponse.fail(errorResponse));
    }
}

