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

    /**
     * incoming 디렉토리를 주기적으로 확인하며 존재하면 파일을 하나씩 처리
     * 스케줄링 실행주기를 외부파일(yml)에서 불러오도록 설정
     * 실행주기: 30초
     */
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

    /**
     * 파일을 name, hash값으로 구분하여 db에 file_history 업로드
     *
     */
    public void processFile(File file) {
        String fileName = file.getName();
        String fileHash = null;
        UploadHistory history = null;

        try {
            // processedDir에 파일이 이미 존재하는지 확인
            Path processedFilePath = Paths.get(processedDir, fileName);
            if (Files.exists(processedFilePath)) {
                log.info("파일 {} 는 이미 처리 완료되었습니다.", fileName);
                moveFile(file, processedDir);
                return;
            }

            // 새로운 파일이면 저장
            // calculate256: 파일 구분을 위한 해시값 생성 메서드
            fileHash = FileHashUtil.calculateSha256(file.getAbsolutePath());

            history = UploadHistory.builder()
                    .fileName(fileName)
                    .fileHash(fileHash)
                    .status("STAGED")
                    .processedAt(LocalDateTime.now())
                    .build();
            uploadHistoryMapper.save(history);


            // EmployeeFileHandler에 파일 처리 위임 (DB 저장 및 오류 파일 생성)
            // handleFile: 오류 여부만 반환
            boolean hasErrors;
            try (InputStream inputStream = new FileInputStream(file)) {
                hasErrors = employeeFileHandler.handleFile(inputStream, fileName);
            }

            // 파일 디렉토리 이동
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

        } catch (Exception e) {
            if (history != null) {
                uploadHistoryMapper.updateStatus(history.getId(), "FAILED");
            }
            moveFile(file, errorDir);
        }
    }

    /**
     * 현재 디렉토리와 목표 디렉토리 설정해서 이동
     */
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