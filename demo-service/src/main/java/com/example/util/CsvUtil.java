package com.example.util;

import com.example.entity.Employee;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

/**
 * CSV 파일 입출력을 지원하는 유틸리티 클래스.
 * <p>
 * - 직원 데이터(Employee) 리스트를 CSV 파일로 내보내기
 * - 원본 문자열 라인을 CSV 형식 파일에 그대로 기록
 */
public class CsvUtil {
    /**
     * 직원 데이터(Employee) 리스트를 CSV 파일로 내보내기
     */
    public static void writeEmployeesToCsv(List<Employee> employees, OutputStream outputStream) throws IOException {
        // OutputStream → Writer 변환
        try (Writer writer = new OutputStreamWriter(outputStream);
             // CSVPrinter 생성 및 헤더 지정
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader("ID", "Name"))) { // header 지정

            // 직원 리스트를 순회하며 각 레코드를 CSV에 기록
            for (Employee employee : employees) {
                csvPrinter.printRecord(employee.getId(), employee.getName());
            }
        }
    }

    /**
     * 오류 발생한 원본 문자열 라인을 CSV 형식 파일에 그대로 기록
     * - CSVPrinter 헤더: "ID", "Name"
     */
    public static void writeRawLinesToCsv(List<String> rawLines, OutputStream outputStream) throws IOException {
        try (Writer writer = new OutputStreamWriter(outputStream);
             // CSVPrinter 생성 및 헤더 지정
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader("ID", "Name"))) { // header 지정

            // 데이터는 “원본 그대로” 남기기 위해 실제 라인 작성은 Writer를 직접 사용
            for (String line : rawLines) {
                writer.write(line);
                writer.write(System.lineSeparator()); // Add newline
            }
        }
    }
}