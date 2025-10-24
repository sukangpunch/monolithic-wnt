package com.appcenter.monolithicwnt.s3.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.appcenter.monolithicwnt.global.exception.BusinessException;
import com.appcenter.monolithicwnt.global.exception.ErrorCode;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@EnableAsync
@Slf4j
@RequiredArgsConstructor
public class FileUploadService {
    private final AmazonS3Client s3Client;

    @Async
    public void uploadFile(String bucket, String fileName, MultipartFile file) {
        // 메타데이터 생성
        String contentType = file.getContentType();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(file.getSize());

        try{
            s3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata)
                                       .withCannedAcl(CannedAccessControlList.PublicRead));
            log.info("이미지 업로드 정상적 완료 thread: {}", Thread.currentThread().getName());
        }catch (AmazonServiceException e){
            log.error("이미지 업로드 중 s3 서비스 예외 발생 : {}", e.getMessage());
            throw new BusinessException(ErrorCode.S3_SERVICE_EXCEPTION);
        }catch (SdkClientException | IOException e){
            log.error("이미지 업로드 중 s3 클라이언트 예외 발생 : {}", e.getMessage());
            throw new BusinessException(ErrorCode.S3_CLIENT_EXCEPTION);
        }
    }
}
