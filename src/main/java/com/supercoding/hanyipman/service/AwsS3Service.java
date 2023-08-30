package com.supercoding.hanyipman.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsS3Service {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // MultipartFile을 전달받아 File로 전환한 후 S3에 업로드
    public String memoryUpload(MultipartFile uploadFile, String fileName) throws IOException {
        try (InputStream inputStream = uploadFile.getInputStream()) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(uploadFile.getSize());
            objectMetadata.setContentType(uploadFile.getContentType());
            amazonS3Client.putObject(
                    new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)	// PublicRead 권한으로 업로드 됨
            );
            return amazonS3Client.getUrl(bucket, fileName).toString();
        }
    }

//    private void removeFile(String imageUrl) {
//        String filePath = FilePathUtils.convertImageUrlToFilePath(imageUrl);
//        if(!amazonS3Client.doesObjectExist(bucket, filePath)){
//            throw new CustomException(UtilErrorCode.NOT_FOUND_FILE);
//        }
//
//        amazonS3Client.deleteObject(bucket, filePath);
//    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)	// PublicRead 권한으로 업로드 됨
        );
        uploadFile.delete();
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }


}
