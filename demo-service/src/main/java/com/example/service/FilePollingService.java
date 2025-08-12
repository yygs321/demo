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
    @Scheduled(cron = "${polling.cron}")
    public void pollIncomingDirectory() {
        log.info("폴링 시작: {}", incomingDir);
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

    public void processFile(File file) {
        String fileName = file.getName();
        String fileHash = null;
        UploadHistory history = null;

        try {
            fileHash = FileHashUtil.calculateSha256(file.getAbsolutePath());
            Optional<UploadHistory> existingHistory = uploadHistoryMapper.findByFileNameAndFileHash(fileName, fileHash);

            // 이미 완료된 파일이면 prossed 디렉토리로 이동
            if (existingHistory.isPresent() && "COMPLETED".equals(existingHistory.get().getStatus())) {
                log.info("파일 {} 는 이미 처리 완료되었습니다.", fileName);
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

            boolean hasErrors;
            // EmployeeFileHandler에 파일 처리 위임 (DB 저장 및 오류 파일 생성 포함)
            try (InputStream inputStream = new FileInputStream(file)) {
                hasErrors = employeeFileHandler.handleFile(inputStream, fileName);
            }

            if (hasErrors) {
                // 오류가 있었으면 FAILED 처리하고 errorDir로 이동
                uploadHistoryMapper.updateStatus(history.getId(), "FAILED");
                moveFile(file, errorDir);
            } else {
                // 오류가 없었으면 COMPLETED 처리하고 processedDir로 이동
                uploadHistoryMapper.updateStatus(history.getId(), "COMPLETED");
                moveFile(file, processedDir);
                log.info("파일 {} 처리가 성공적으로 완료되었습니다.", fileName);
            }

        } catch (IOException | NoSuchAlgorithmException e) {
            if (history != null) {
                uploadHistoryMapper.updateStatus(history.getId(), "FAILED");
            }
            moveFile(file, errorDir);
        } catch (Exception e) {
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
            log.error("파일 {}을 {}로 이동하는 데 실패했습니다: {}", file.getName(), destinationDir, e.getMessage());
        }
    }
}