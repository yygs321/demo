package com.example.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE("COMMON_001", "Invalid Input Value"),
    METHOD_NOT_ALLOWED("COMMON_002", "Method Not Allowed"),
    INTERNAL_SERVER_ERROR("COMMON_003", "Internal Server Error"),

    // Employee
    EMPLOYEE_NOT_FOUND("EMPLOYEE_001", "Employee not found"),

    // Board
    BOARD_NOT_FOUND("BOARD_001", "Board not found"),

    // Authorization
    UNAUTHORIZED("AUTH_001", "Action not authorized");

    private final String code;
    private final String message;
}
