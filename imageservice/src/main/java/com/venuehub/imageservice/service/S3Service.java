package com.venuehub.imageservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName = "venuehub-imagebucket";

    public S3Service() {
        this.s3Client = S3Client.builder()
                .region(Region.EU_NORTH_1) // Replace with your AWS region
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

    }

    public String uploadFile(MultipartFile file, String fileName) throws IOException {
        File convertedFile = convertMultiPartFileToFile(file);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        s3Client.putObject(putObjectRequest, convertedFile.toPath());
        convertedFile.delete();
        return fileName;
    }


    private File convertMultiPartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
