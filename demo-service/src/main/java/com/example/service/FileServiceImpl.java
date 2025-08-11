package com.example.service;

import com.example.enums.ResultCode;
import com.example.exception.InvalidFileException;
import com.example.service.handler.EmployeeFileHandler;
import com.example.spec.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final EmployeeFileHandler employeeFileHandler;

    @Override
    @Transactional
    public void uploadEmployees(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException(ResultCode.FILE_IS_EMPTY);
        }
        employeeFileHandler.handleFile(file.getInputStream(), file.getOriginalFilename());
        log.info("File {} uploaded and processed successfully.", file.getOriginalFilename());
    }
}