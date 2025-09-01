package com.example.service;

import com.example.entity.Employee;
import com.example.enums.ResultCode;
import com.example.exception.BaseException;
import com.example.exception.BadRequestException;
import com.example.exception.InvalidFileException;
import com.example.exception.NotFoundException;
import com.example.exception.InternalServerErrorException; // New import
import com.example.mapper.EmployeeMapper;
import com.example.service.handler.EmployeeFileHandler;
import com.example.spec.FileService;
import com.example.util.CsvUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final EmployeeFileHandler employeeFileHandler;
    private final EmployeeMapper employeeMapper;

    @Value("${file.download.processed-dir}")
    private String processedDir;

    /**
     * API 요청으로 업로드된 Employee 관련 파일을 처리하여 DB에 저장
     * 1. 유효성 검사
     * 2. 파일 type(csv,excel) 파악
     * 3. 데이터 파싱 및 저장(중복 검사 포함)
     * 4. 오류 발생 시 오류파일 생성
     */
    @Override
    @Transactional
    public void uploadEmployees(MultipartFile file) throws IOException {
        // 파일 유효성 검사
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException(ResultCode.FILE_IS_EMPTY);
        }
        // EmployeeFileHandler에 파일 처리 위임 (DB 저장 및 오류 파일 생성)
        employeeFileHandler.handleFile(file.getInputStream(), file.getOriginalFilename());
        log.info("파일 {} 업로드 및 처리가 완료되었습니다.", file.getOriginalFilename());
    }


    /**
     * Employee 데이터를 CSV 파일로 내보냅니다.
     *    - employeeId가 있으면 해당 직원만
     *    - 없으면 전체 직원 조회
     */
    @Override
    public void exportEmployees(Long employeeId) {
        // 파일명 생성
        String fileName = (employeeId == null) ? "all_employees.csv" : "employee_" + employeeId + ".csv";

        // 내보낼 데이터 조회
        List<Employee> employees;
        if (employeeId != null) {
            Optional<Employee> employeeOpt = employeeMapper.findById(employeeId);
            employees = employeeOpt.map(Collections::singletonList).orElse(Collections.emptyList());
            if (employees.isEmpty()) {
                throw new NotFoundException("Employee with ID " + employeeId + " not found.");
            }
        } else {
            employees = employeeMapper.findAll();
        }

        // 데이터가 없으면 예외 처리
        if (employees.isEmpty()) {
            throw new BadRequestException("No employee data available for export.");
        }

        // 생성될 CSV 파일을 저장할 경로 설정
        Path filePath = Paths.get(processedDir, fileName);
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {

            //직원 데이터를 Csv 파일로 만드는 메서드 실행
            CsvUtil.writeEmployeesToCsv(employees, fos);
            log.info("CSV 파일이 성공적으로 생성되었습니다: {}", filePath);

        } catch (IOException e) {
            log.error("Failed to write CSV file: {}", fileName, e);
            throw new InternalServerErrorException("Failed to generate CSV file: " + e.getMessage());
        }
    }
}