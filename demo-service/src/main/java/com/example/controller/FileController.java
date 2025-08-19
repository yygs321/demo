package com.example.controller;

import com.example.response.ApiResponse;
import com.example.spec.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PathVariable; // New import

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

        @PostMapping(value = "/upload/employees", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadEmployees(@RequestParam("file") MultipartFile file) throws IOException {
        fileService.uploadEmployees(file);
        return ApiResponse.success("File uploaded successfully", "Employees uploaded successfully from " + file.getOriginalFilename());
    }

    @GetMapping("/download/employees")
    public ApiResponse<String> downloadAllEmployees() {
        fileService.exportEmployees(null);
        return ApiResponse.success("CSV export job started.", "CSV export for all employees has been initiated.");
    }

    @GetMapping("/download/employees/{employeeId}")
    public ApiResponse<String> downloadEmployeeById(@PathVariable Long employeeId) {
        fileService.exportEmployees(employeeId);
        return ApiResponse.success("CSV export job started.", "CSV export for employee with id " + employeeId + " has been initiated.");
    }
}
