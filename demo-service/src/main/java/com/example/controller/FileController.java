package com.example.controller;

import com.example.dto.response.ApiResponse;
import com.example.spec.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/upload/employees", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadEmployees(@RequestParam("file") MultipartFile file) throws IOException {
        fileService.uploadEmployees(file);
        return ApiResponse.success("Employees uploaded successfully from " + file.getOriginalFilename());
    }
}
