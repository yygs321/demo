package com.example.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final String status;
    private String message;
    private T data;
    private ErrorResponse error;

    // Success with message and data
    private ApiResponse(String message, T data) {
        this.status = "success";
        this.message = message;
        this.data = data;
    }

    // Success with only message (no data)
    private ApiResponse(String message) {
        this.status = "success";
        this.message = message;
        this.data = null; // 명시적으로 null 설정
    }

    // Fail
    private ApiResponse(ErrorResponse error) {
        this.status = "fail";
        this.error = error;
        this.message = null; // 명시적으로 null 설정
        this.data = null;    // 명시적으로 null 설정
    }

    // Static factory methods for success
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(null, data); // 메시지 없이 데이터만 있는 경우
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(message, null); // 데이터 없이 메시지만 있는 경우
    }

    // Static factory method for fail
    public static ApiResponse<Object> fail(ErrorResponse error) {
        return new ApiResponse<>(error);
    }
}