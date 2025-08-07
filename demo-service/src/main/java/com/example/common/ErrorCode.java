package com.example.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE("C001", "Invalid Input Value"),
    METHOD_NOT_ALLOWED("C002", "Method Not Allowed"),
    INTERNAL_SERVER_ERROR("C003", "Internal Server Error"),

    // Employee
    EMPLOYEE_NOT_FOUND("E001", "Employee not found"),

    // Board
    BOARD_NOT_FOUND("B001", "Board not found");

    private final String code;
    private final String message;
}
