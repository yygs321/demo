package com.example.service;

import com.example.entity.UploadHistory;
import com.example.mapper.UploadHistoryMapper;
import com.example.service.handler.EmployeeFileHandler;
import com.example.util.FileHashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private final EmployeeFileHandler employeeFileHandler;

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
        String fileHash = null;
        UploadHistory history = null;

        try {
            fileHash = FileHashUtil.calculateSha256(file.getAbsolutePath());
            Optional<UploadHistory> existingHistory = uploadHistoryMapper.findByFileNameAndFileHash(fileName, fileHash);

            // 완료된 파일이면 prossed 디렉토리로 이동
            if (existingHistory.isPresent() && "COMPLETED".equals(existingHistory.get().getStatus())) {
                log.info("File {} (hash: {}) already processed and completed. Moving to processed directory.", fileName, fileHash);
                moveFile(file, processedDir);
                return;
            }

            history = UploadHistory.builder()
                    .fileName(fileName)
                    .fileHash(fileHash)
                    .status("STAGED")
                    .processedAt(LocalDateTime.now())
                    .build();
            uploadHistoryMapper.save(history);

            // EmployeeFileHandler에 파일 처리 위임
            try (InputStream inputStream = new FileInputStream(file)) {
                employeeFileHandler.handleFile(inputStream, fileName);
            }

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