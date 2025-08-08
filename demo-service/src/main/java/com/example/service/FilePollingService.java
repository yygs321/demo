package com.example.service;

import com.example.entity.Employee;
import com.example.entity.UploadHistory;
import com.example.mapper.EmployeeMapper;
import com.example.mapper.UploadHistoryMapper;
import com.example.util.FileHashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilePollingService {

    @Value("${file.upload.incoming-dir}")
    private String incomingDir;

    @Value("${file.upload.processed-dir}")
    private String processedDir;

    @Value("${file.upload.error-dir}")
    private String errorDir;

    private final UploadHistoryMapper uploadHistoryMapper;
    private final EmployeeMapper employeeMapper; // CSV 파싱 및 저장을 위해 EmployeeMapper 필요

    // 30초마다 자동 실행
    @Scheduled(cron = "*/30 * * * * *")
    public void pollIncomingDirectory() {
        log.info("Polling incoming directory: {}", incomingDir);
        File folder = new File(incomingDir);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    processFile(file);
                }
            }
        }
    }

    @Transactional
    public void processFile(File file) {
        String fileName = file.getName();
        String filePath = file.getAbsolutePath();
        String fileHash = null;
        UploadHistory history = null;

        try {
            fileHash = FileHashUtil.calculateSha256(filePath);
            Optional<UploadHistory> existingHistory = uploadHistoryMapper.findByFileNameAndFileHash(fileName, fileHash);

            if (existingHistory.isPresent() && "COMPLETED".equals(existingHistory.get().getStatus())) {
                log.info("File {} (hash: {}) already processed and completed. Moving to processed directory.", fileName, fileHash);
                moveFile(file, processedDir);
                return;
            }

            // 새 파일이거나 재처리 필요한 파일
            history = UploadHistory.builder()
                    .fileName(fileName)
                    .fileHash(fileHash)
                    .status("STAGED")
                    .processedAt(LocalDateTime.now())
                    .build();
            uploadHistoryMapper.save(history);

            // 파일 내용 파싱 및 DB 저장 (CSV 파일이라고 가정)
            parseAndSaveCsv(filePath);

            // 성공 시 상태 업데이트 및 파일 이동
            uploadHistoryMapper.updateStatus(history.getId(), "COMPLETED");
            moveFile(file, processedDir);
            log.info("File {} processed successfully. Moved to processed directory.", fileName);

        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("Error processing file {}: {}", fileName, e.getMessage());
            if (history != null) {
                uploadHistoryMapper.updateStatus(history.getId(), "FAILED");
            }
            moveFile(file, errorDir);
        } catch (Exception e) {
            log.error("Unexpected error during file processing {}: {}", fileName, e.getMessage());
            if (history != null) {
                uploadHistoryMapper.updateStatus(history.getId(), "FAILED");
            }
            moveFile(file, errorDir);
        }
    }

    private void parseAndSaveCsv(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data.length == 2) {
                    try {
                        Long id = Long.parseLong(data[0].trim());
                        String name = data[1].trim();
                        Employee employee = Employee.builder().id(id).name(name).build();
                        employeeMapper.save(employee);
                        log.debug("Saved employee: id={}, name={}", id, name);
                    } catch (NumberFormatException e) {
                        log.warn("Skipping invalid line (ID not a number) in {}: {}", filePath, line);
                    }
                } else {
                    log.warn("Skipping invalid line (incorrect format) in {}: {}", filePath, line);
                }
            }
        }
    }

    private void moveFile(File file, String destinationDir) {
        try {
            Path sourcePath = file.toPath();
            Path destinationPath = Paths.get(destinationDir, file.getName());
            Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Failed to move file {} to {}: {}", file.getName(), destinationDir, e.getMessage());
        }
    }
}