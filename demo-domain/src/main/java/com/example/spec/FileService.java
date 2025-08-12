package com.example.spec;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    void uploadEmployees(MultipartFile file) throws IOException;
    void exportEmployees(Long employeeId);
}
