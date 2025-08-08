package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadHistory {
    private Long id;
    private String fileName;
    private String fileHash;
    private String status; // STAGED, COMPLETED, FAILED
    private LocalDateTime processedAt;
}