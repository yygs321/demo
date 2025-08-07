package com.example.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final String status;
    private T data;
    private ErrorResponse error;

    // Success
    private ApiResponse(T data) {
        this.status = "success";
        this.data = data;
    }

    // Fail
    private ApiResponse(ErrorResponse error) {
        this.status = "fail";
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data);
    }

    public static ApiResponse<Object> fail(ErrorResponse error) {
        return new ApiResponse<>(error);
    }
}
