package com.example.service.handler;

import com.example.entity.Employee;
import com.example.enums.ResultCode;
import com.example.exception.InvalidFileException;
import com.example.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class EmployeeFileHandler {

    private final EmployeeMapper employeeMapper;

    @Transactional
    public void handleFile(InputStream inputStream, String fileName) throws IOException {
        String extension = getFileExtension(fileName);
        List<Employee> employees;

        if ("csv".equalsIgnoreCase(extension)) {
            employees = parseCsv(inputStream);
        } else if ("xlsx".equalsIgnoreCase(extension)) {
            employees = parseExcel(inputStream);
        } else {
            throw new InvalidFileException(ResultCode.INVALID_FILE_FORMAT);
        }

        if (employees != null && !employees.isEmpty()) {
            employeeMapper.saveAll(employees);
            log.info("{} employees saved from file {}", employees.size(), fileName);
        }
    }

    private List<Employee> parseExcel(InputStream inputStream) throws IOException {
        List<Employee> employees = new ArrayList<>();

        //.xlsx 파일 읽어오기
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) {
                rows.next(); // Skip header row
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                Iterator<Cell> cellsInRow = currentRow.iterator();

                if (!cellsInRow.hasNext()) continue;
                Cell idCell = cellsInRow.next();
                if (!cellsInRow.hasNext()) continue;
                Cell nameCell = cellsInRow.next();

                Long id = null;
                if (idCell.getCellType() == CellType.NUMERIC) {
                    id = (long) idCell.getNumericCellValue();
                } else if (idCell.getCellType() == CellType.STRING) {
                    try {
                        id = Long.parseLong(idCell.getStringCellValue());
                    } catch (NumberFormatException e) {
                        String errorMessage = String.format(
                                "Row %d: Invalid ID format for value '%s'",
                                currentRow.getRowNum() + 1,
                                idCell.getStringCellValue()
                        );
                        log.error(errorMessage, e);
                        throw new InvalidFileException(ResultCode.UNPROCESSABLE_ENTITY, errorMessage);

                    }
                }

                String name = nameCell.getStringCellValue();

                if (id != null && name != null && !name.trim().isEmpty()) {
                    employees.add(Employee.builder().id(id).name(name).build());
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
                        employees.add(Employee.builder().id(id).name(name).build());
                    }
                } catch (NumberFormatException e) {
                    String errorMessage = String.format(
                            "Row %d: Invalid ID format in CSV for value '%s'",
                            csvRecord.getRecordNumber() + 1,
                            csvRecord.get("ID")
                    );
                    log.error(errorMessage, e);
                    throw new InvalidFileException(ResultCode.UNPROCESSABLE_ENTITY, errorMessage);

                } catch (IllegalArgumentException e) {
                    String errorMessage = "Required column 'ID' or 'Name' is missing.";
                    log.error(errorMessage, e);
                    throw new InvalidFileException(ResultCode.UNPROCESSABLE_ENTITY, errorMessage);
                }

            }
        }
        return employees;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
}