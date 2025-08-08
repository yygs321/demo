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

    // Success with data and message
    private ApiResponse(T data, String message) {
        this.status = "success";
        this.data = data;
        this.message = message;
    }

    // Success with only message (no data)
    private ApiResponse(String message) {
        this.status = "success";
        this.message = message;
    }

    // Fail
    private ApiResponse(ErrorResponse error) {
        this.status = "fail";
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(data, message);
    }

    // Success with data and message
    private ApiResponse(String message, T data) {
        this.status = "success";
        this.data = data;
        this.message = message;
    }

    // Success with only message (no data)
    private ApiResponse(String message) {
        this.status = "success";
        this.message = message;
    }

    // Fail
    private ApiResponse(ErrorResponse error) {
        this.status = "fail";
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(null, data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(message, null);
    }

    public static ApiResponse<Object> fail(ErrorResponse error) {
        return new ApiResponse<>(error);
    }

    public static ApiResponse<Object> fail(ErrorResponse error) {
        return new ApiResponse<>(error);
    }
}
