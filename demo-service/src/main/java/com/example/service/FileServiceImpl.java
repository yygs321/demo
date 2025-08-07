package com.example.service;

import com.example.entity.Employee;
import com.example.mapper.EmployeeMapper;
import com.example.spec.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final EmployeeMapper employeeMapper;

    @Transactional
    @Override
    public void uploadEmployees(MultipartFile file) throws IOException {
        String extension = getFileExtension(file.getOriginalFilename());

        if ("xlsx".equalsIgnoreCase(extension)) {
            List<Employee> employees = parseExcel(file.getInputStream());
            if (!employees.isEmpty()) {
                employeeMapper.saveAll(employees);
                log.info("Employees uploaded successfully from {}",extension);
            }
        } else if ("csv".equalsIgnoreCase(extension)) {
            List<Employee> employees = parseCsv(file.getInputStream());
            if (!employees.isEmpty()) {
                employeeMapper.saveAll(employees);
                log.info("Employees uploaded successfully from {}",extension);
            }
        } else {
            throw new IllegalArgumentException("Unsupported file format: " + extension);
        }
    }

    private List<Employee> parseExcel(InputStream inputStream) throws IOException {
        List<Employee> employees = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트
            Iterator<Row> rows = sheet.iterator();

            // 헤더 행은 건너뜀
            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                Iterator<Cell> cellsInRow = currentRow.iterator();

                // 셀이 충분히 있는지 확인 (id, name)
                if (!cellsInRow.hasNext()) continue; // 빈 행 건너뛰기
                Cell idCell = cellsInRow.next();
                if (!cellsInRow.hasNext()) continue; // name 셀이 없으면 건너뛰기
                Cell nameCell = cellsInRow.next();

                // 셀 타입에 따라 값 읽기
                Long id = null;
                if (idCell.getCellType() == CellType.NUMERIC) {
                    id = (long) idCell.getNumericCellValue();
                } else if (idCell.getCellType() == CellType.STRING) {
                    try {
                        id = Long.parseLong(idCell.getStringCellValue());
                    } catch (NumberFormatException e) {
                        // 숫자 형식 오류 처리
                        log.error("Invalid ID format: {}", idCell.getStringCellValue());
                        continue; // 이 행은 건너뛰기
                    }
                }

                String name = nameCell.getStringCellValue();

                if (id != null && name != null && !name.trim().isEmpty()) {
                    Employee employee = Employee.builder()
                            .id(id)
                            .name(name)
                            .build();
                    employees.add(employee);
                }
            }
        }

        return employees;
    }

    private List<Employee> parseCsv(InputStream inputStream) throws IOException {
        List<Employee> employees = new ArrayList<>();
        try (Reader reader = new InputStreamReader(inputStream);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            for (CSVRecord csvRecord : csvParser) {
                try {
                    Long id = Long.parseLong(csvRecord.get("ID"));
                    String name = csvRecord.get("Name");

                    if (id != null && name != null && !name.trim().isEmpty()) {
                        Employee employee = Employee.builder()
                                .id(id)
                                .name(name)
                                .build();
                        employees.add(employee);
                    }
                } catch (NumberFormatException e) {
                    log.error("Invalid ID format in CSV: {}", csvRecord.get("ID"));
                    continue;
                } catch (IllegalArgumentException e) {
                    log.error("Missing header in CSV (ID or Name): {}", e.getMessage());
                    continue;
                }
            }
        }
        return employees;
    }

    //파일명에서 확장자 분리
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
}