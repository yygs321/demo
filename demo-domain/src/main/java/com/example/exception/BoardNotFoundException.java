package com.example.exception;

public class BoardNotFoundException extends BusinessException {
    public BoardNotFoundException(String message) {
        super(message);
    }
}
