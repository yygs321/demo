package com.example.exception;

public class EmployeeNotFoundException extends BusinessException {
    public EmployeeNotFoundException(Long id) {
        super("Employee not found with id: " + id);
    }
}
