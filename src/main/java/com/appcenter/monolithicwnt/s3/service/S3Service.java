package com.appcenter.monolithicwnt.s3.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.appcenter.monolithicwnt.global.exception.BusinessException;
import com.appcenter.monolithicwnt.global.exception.ErrorCode;
import com.appcenter.monolithicwnt.s3.domain.ImgType;
import com.appcenter.monolithicwnt.s3.dto.UploadedFileUrlResponse;
import com.appcenter.monolithicwnt.user.infrastructure.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3Service {

    private static final Logger log = LoggerFactory.getLogger(S3Service.class);
    private static final long MAX_FILE_SIZE_MB = 1024 * 1024 * 5;

    private final AmazonS3Client amazonS3;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final ThreadPoolTaskExecutor asyncExecutor;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /*
     * 파일을 S3에 업로드한다.
     * - 파일이 존재하는지 검증한다.
     * - 파일 확장자가 허용된 확장자인지 검증한다.
     * - 파일에 대한 메타 데이터를 생성한다.
     * - 임의의 랜덤한 문자열로 파일 이름을 생성한다.
     * - S3에 파일을 업로드한다.
     * */

    public UploadedFileUrlResponse uploadFile(MultipartFile multipartFile, ImgType imageFile) {
        // 파일 검증
        validateImgFile(multipartFile);
        // 파일 이름 생성
        UUID randomUUID = UUID.randomUUID();
        String fileName = imageFile.getType() + "/" + randomUUID;
        // 파일업로드 비동기로 진행
        if (multipartFile.getSize() >= MAX_FILE_SIZE_MB) {
            asyncExecutor.submit(() -> {
                fileUploadService.uploadFile(bucket, "origin/" + fileName, multipartFile);
            });
        } else {
            asyncExecutor.submit(() -> {
                fileUploadService.uploadFile(bucket, fileName, multipartFile);
            });
        }
        return new UploadedFileUrlResponse(fileName);
    }

    public List<UploadedFileUrlResponse> uploadFiles(List<MultipartFile> multipartFile, ImgType imageFile) {

        List<UploadedFileUrlResponse> uploadedFileUrlResponseList = new ArrayList<>();
        for (MultipartFile file : multipartFile) {
            UploadedFileUrlResponse uploadedFileUrlResponse = uploadFile(file, imageFile);
            uploadedFileUrlResponseList.add(uploadedFileUrlResponse);
        }
        return uploadedFileUrlResponseList;
    }

    private void validateImgFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.S3_SERVICE_EXCEPTION);
        }

        String fileName = Objects.requireNonNull(file.getOriginalFilename());
        String fileExtension = getFileExtension(fileName).toLowerCase();

        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "webp", "pdf", "word", "docx");
        if (!allowedExtensions.contains(fileExtension)) {
            throw new BusinessException(ErrorCode.NOT_ALLOWED_FILE_EXTENSIONS);
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            throw new BusinessException(ErrorCode.INVALID_FILE_EXTENSIONS);
        }
        return fileName.substring(dotIndex + 1);
    }

    private void deleteFile(String fileName) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
        } catch (AmazonServiceException e) {
            log.error("파일 삭제 중 s3 서비스 예외 발생 : {}", e.getMessage());
            throw new BusinessException(ErrorCode.S3_SERVICE_EXCEPTION);
        } catch (SdkClientException e) {
            log.error("파일 삭제 중 s3 클라이언트 예외 발생 : {}", e.getMessage());
            throw new BusinessException(ErrorCode.S3_CLIENT_EXCEPTION);
        }
    }
}
