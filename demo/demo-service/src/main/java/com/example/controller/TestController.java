package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test-error")
    public String testError() {
        // 일부러 예외를 던짐
        throw new RuntimeException("테스트용 런타임 예외");
        //http://localhost:8080/test-error
    }
}
