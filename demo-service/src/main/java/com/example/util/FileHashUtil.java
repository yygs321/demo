package com.example.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 파일의 해시값을 계산하는 유틸리티 클래스.
 * SHA-256 알고리즘을 사용하여 파일 내용을 고유한 16진수 문자열로 변환
 */
public class FileHashUtil {

    public static String calculateSha256(String filePath) throws IOException, NoSuchAlgorithmException {
        // SHA-256 해시 알고리즘 인스턴스 생성
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // 파일 내용을 1KB 단위로 읽어 해시 계산
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] byteArray = new byte[1024]; // 읽기 버퍼
            int bytesCount = 0;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount); // 읽은 만큼만 해시 업데이트
            }
        }

        // 최종 해시 바이트 배열 생성 및 문자열 변환하여 반환
        byte[] hashedBytes = digest.digest();
        return bytesToHex(hashedBytes);
    }

    /**
     * 바이트 배열을 16진수 문자열로 변환
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}