package com.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    // AWS S3 서비스와 상호작용하기 위한 클라이언트
    private final S3Client s3Client;

    @Value("${BUCKET_NAME}")
    private String bucketName;

    @Value("${REGION}")
    private String region;

    // 파일 저장 경로(폴더)
    private final String FILE_PATH_PREFIX = "articles/";

    // S3 파일 업로드 처리
    // 파일(file)을 articleService에서 받은 후, S3 업로드 -> imageUrl과 객체 키를 반환하는 메서드
    public Map<String, String> uploadFile(MultipartFile file) {
        // s3Key 생성
        String s3Key = FILE_PATH_PREFIX + UUID.randomUUID() + "_" + file.getOriginalFilename();

        // s3 버킷에 파일을 업로드
        // 업로드할 file과 s3 객체 키(s3Key)를 전달
        uploadFileToS3(s3Key, file);

        String IMAGE_URL_FORMAT = "https://%s.s3.%s.amazonaws.com/%s";
        // 템플릿 리터럴 `${변수}`
        // https://버킷명.s3.리전.amazonaws.com/객체키
        // https://sesac-imdla.s3.ap-northeast-2.amazonaws.com/~.jpg
        String imageUrl = String.format(IMAGE_URL_FORMAT, bucketName, region, s3Key);

        return Map.of(
                "imageUrl", imageUrl,
                "s3Key", s3Key
        );
    }

    // 실질적으로 S3 버킷에 파일(객체)을 업로드하는 메서드
    private void uploadFileToS3(String s3Key, MultipartFile file){
        try {// S3에 요청할 객체
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key).contentType((file.getContentType()))
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // 파일 삭제
    public void deleteFile(String s3Key){
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("파일 삭제 실패: " + e.getMessage());
        }
    }
}
