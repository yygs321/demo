package com.example.util;

import com.example.entity.Employee;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

public class CsvUtil {

    /**
     * 직원(Employee) 데이터를 CSV 형식으로 내보내는 메서드
     */
    public static void writeEmployeesToCsv(List<Employee> employees, OutputStream outputStream) throws IOException {
        try (Writer writer = new OutputStreamWriter(outputStream);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader("ID", "Name"))) { // header 지정

            for (Employee employee : employees) {
                csvPrinter.printRecord(employee.getId(), employee.getName());
            }
        }
    }

    /**
     * 가공되지 않은(raw) 문자열 데이터를 CSV 파일에 그대로 기록하는 메서드
     * - 오류 발생한 데이터 원본 라인 저장
     */
    public static void writeRawLinesToCsv(List<String> rawLines, OutputStream outputStream) throws IOException {
        try (Writer writer = new OutputStreamWriter(outputStream);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader("ID", "Name"))) { // header 지정

            for (String line : rawLines) {
                writer.write(line);
                writer.write(System.lineSeparator()); // Add newline
            }
        }
    }
}