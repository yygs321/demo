package com.example.service.handler;

import com.example.dto.ParsingResult;
import com.example.entity.Employee;
import com.example.enums.ResultCode;
import com.example.exception.InvalidFileException;
import com.example.mapper.EmployeeMapper;
import com.example.util.CsvUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeFileHandler {

    private final EmployeeMapper employeeMapper;

    @Value("${file.download.error-dir}")
    private String errorDir;

    /**
     * 파일을 처리하는 메서드.
     * CSV 또는 Excel 확장자에 따라 파싱 후 중복 검사 및 DB 저장 수행.
     * 오류가 발생한 라인이 있으면 오류 파일을 생성한다.
     */
    @Transactional
    public boolean handleFile(InputStream inputStream, String fileName) throws IOException {
        String extension = getFileExtension(fileName);
        ParsingResult parsingResult;

        // 파일 확장자에 따라 파싱 방식 분기
        if ("csv".equalsIgnoreCase(extension)) {
            parsingResult = parseCsv(inputStream);
        } else if ("xlsx".equalsIgnoreCase(extension)) {
            parsingResult = parseExcel(inputStream);
        } else {
            throw new InvalidFileException(ResultCode.INVALID_FILE_FORMAT);
        }

        // 파싱에 실패한 라인들을 최종 오류 리스트에 미리 추가
        List<String> finalErrorLines = new ArrayList<>(parsingResult.getRawErrorLines());
        Map<String, Employee> employeesToProcess = parsingResult.getEmployeesToSave();

        // 저장된 직원 없을 경우 종료
        if (employeesToProcess == null || employeesToProcess.isEmpty()) {
            if (!finalErrorLines.isEmpty()) {
                log.warn("파일 {} 처리 중 {}개의 오류가 발견되었습니다.", fileName, finalErrorLines.size());
                writeErrorFile(fileName, finalErrorLines);
                return true;
            }
            return false;
        }

        // DB 중복 검사를 위해 ID들을 추출
        List<Long> idsToCheck = employeesToProcess.values().stream()
                .map(Employee::getId)
                .collect(Collectors.toList());

        // DB에서 이미 존재하는 ID들을 조회
        Set<Long> existingIds = new HashSet<>(employeeMapper.findExistingIds(idsToCheck));

        List<Employee> employeesToInsert = new ArrayList<>();

        // 파싱된 데이터를 순회하며 DB 중복 검사
        for (Map.Entry<String, Employee> entry : employeesToProcess.entrySet()) {
            String rawLine = entry.getKey();
            Employee employee = entry.getValue();

            if (existingIds.contains(employee.getId())) {
                // 이미 존재하는 ID이면 오류 리스트에 추가
                finalErrorLines.add(rawLine);
            } else {
                // 새로운 직원이면 저장할 리스트에 추가
                employeesToInsert.add(employee);
            }
        }

        // 저장할 직원이 있는 경우, saveAll로 한 번에 저장
        if (!employeesToInsert.isEmpty()) {
            employeeMapper.saveAll(employeesToInsert);
            log.info("{}명의 직원을 파일 {}에서 저장했습니다.", employeesToInsert.size(), fileName);
        }

        // 최종적으로 오류가 하나라도 있으면 오류 파일 생성
        if (!finalErrorLines.isEmpty()) {
            log.warn("파일 {} 처리 중 {}개의 오류가 발견되었습니다.", fileName, finalErrorLines.size());
            writeErrorFile(fileName, finalErrorLines);
            return true; // 오류가 있었음을 반환
        }

        return false;  // 오류가 없었음을 반환
    }

    /**
     Excel 파일 파싱
     */
    private ParsingResult parseExcel(InputStream inputStream) throws IOException {
        Map<String, Employee> employeesToSave = new HashMap<>();
        List<String> errorLines = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) {
                rows.next(); // 헤더 행 건너뛰기
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                if (isRowEmpty(currentRow)) {
                    continue;
                }

                // 현재 행의 모든 셀 데이터를 CSV 형식 문자열로 변환 (오류 기록용)
                String rawLine = getRowDataAsString(currentRow);
                try {
                    // 0번째 셀(ID)과 1번째 셀(Name) 값을 문자열로 읽어와 공백 제거
                    DataFormatter dataFormatter = new DataFormatter();
                    String idString = dataFormatter.formatCellValue(currentRow.getCell(0)).trim();
                    String name = dataFormatter.formatCellValue(currentRow.getCell(1)).trim();

                    // ID나 이름이 비어있으면 오류 리스트에 추가하고 다음 행 처리
                    if (idString.isEmpty() || name.isEmpty()) {
                        errorLines.add(rawLine);
                        continue;
                    }

                    // 정상 데이터면 employeesToSave 맵에 저장 (rawLine을 키로, Employee 객체를 값으로)
                    Long id = Long.parseLong(idString);
                    employeesToSave.put(rawLine, Employee.builder().id(id).name(name).build());

                } catch (NumberFormatException e) {
                    errorLines.add(rawLine);
                } catch (Exception e) {
                    errorLines.add(rawLine);
                }
            }
        }
        return new ParsingResult(employeesToSave, errorLines);
    }

    /**
     CSV 파일 파싱
     */
    private ParsingResult parseCsv(InputStream inputStream) throws IOException {
        Map<String, Employee> employeesToSave = new HashMap<>();
        List<String> errorLines = new ArrayList<>();

        try (Reader reader = new InputStreamReader(inputStream);
             CSVParser csvParser = new CSVParser(reader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            for (CSVRecord csvRecord : csvParser) {
                // 현재 레코드의 모든 필드 값을 쉼표로 이어서 원본 라인 문자열 생성 (오류 기록용)
                String rawLine = String.join(",", csvRecord.values());
                try {
                    // 레코드가 일관된 필드 수를 가지고 있는지 체크 (필드 개수 다르면 오류)
                    if (!csvRecord.isConsistent()) {
                        errorLines.add(rawLine);
                        continue;
                    }

                    String idStr = csvRecord.get("ID");
                    String name = csvRecord.get("Name");

                    // 필수값이 비어있으면 오류 처리
                    if (idStr == null || idStr.trim().isEmpty() || name == null || name.trim().isEmpty()) {
                        errorLines.add(rawLine);
                        continue;
                    }

                    Long id = Long.parseLong(idStr);

                    // 정상 데이터면 맵에 저장
                    employeesToSave.put(rawLine, Employee.builder().id(id).name(name).build());

                } catch (NumberFormatException e) {
                    errorLines.add(rawLine);
                } catch (IllegalArgumentException e) {
                    errorLines.add(rawLine);
                } catch (Exception e) {
                    errorLines.add(rawLine);
                }
            }
        }
        return new ParsingResult(employeesToSave, errorLines);
    }

    /**
     분리해서 만든 errorLines로 에러 파일 생성
     파일명 : error_원본파일명.csv
     */
    private void writeErrorFile(String originalFileName, List<String> errorLines) {
        //파일명 설정
        String errorFileName = "error_" + originalFileName;
        Path errorFilePath = Paths.get(errorDir, errorFileName);

        try (OutputStream outputStream = new FileOutputStream(errorFilePath.toFile())) {
            // CSV 파일로 변환
            CsvUtil.writeRawLinesToCsv(errorLines, outputStream);
            log.info("오류 파일이 생성되었습니다: {}", errorFilePath);
        } catch (IOException e) {
            log.error("오류 파일을 쓰는 데 실패했습니다 {}: {}", errorFileName, e.getMessage());
        }
    }

    /**
     파일 확장자 추출
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    /**
     해당 행의 모든 셀 데이터를 CSV 형식 문자열로 변환
     */
    private String getRowDataAsString(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        List<String> cellValues = new ArrayList<>();
        for (int i = 0; i < row.getLastCellNum(); i++) {
            cellValues.add(dataFormatter.formatCellValue(row.getCell(i)));
        }
        return String.join(",", cellValues);
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK && !cell.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}