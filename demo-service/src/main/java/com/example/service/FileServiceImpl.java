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
        // EmployeeFileHandler에 파일 처리 위임 (DB 저장 및 오류 파일 생성 포함)
        employeeFileHandler.handleFile(file.getInputStream(), file.getOriginalFilename());
        log.info("파일 {} 업로드 및 처리가 완료되었습니다.", file.getOriginalFilename());
    }
}