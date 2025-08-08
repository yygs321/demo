package com.example.mapper;

import com.example.entity.UploadHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UploadHistoryMapper {
    void save(UploadHistory uploadHistory);
    Optional<UploadHistory> findByFileNameAndFileHash(@Param("fileName") String fileName, @Param("fileHash") String fileHash);
    void updateStatus(@Param("id") Long id, @Param("status") String status);
}