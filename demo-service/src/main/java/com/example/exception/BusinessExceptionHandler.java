package com.example.exception;

import com.example.enums.ErrorCode;
import com.example.dto.response.ApiResponse;
import com.example.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class BusinessExceptionHandler {

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

    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<ApiResponse<Object>> handleUnauthorizedException(UnauthorizedException e) {
        log.error("handleUnauthorizedException", e);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.fail(errorResponse));
    }
}
