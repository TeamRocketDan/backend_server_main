package com.rocket.utils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class AwsS3Provider {

    private final AmazonS3Client amazonS3Client;
    private static final String PREFIX = "rocket/";

    @Value("${property.s3-base-url}")
    private String BASE_URL;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * user profile image path : users/1/
     * feed image paths : feed/1/
     * @param multipartFiles
     * @param path
     * @return
     */
    public List<String> uploadFile(List<MultipartFile> multipartFiles, String path) {
        List<String> files = new ArrayList<>();

        multipartFiles.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try(InputStream inputStream = file.getInputStream()) {
                PutObjectResult putObjectResult = amazonS3Client.putObject(
                        new PutObjectRequest(
                                bucket,
                                PREFIX + path + fileName,
                                inputStream,
                                objectMetadata
                        )
                                .withCannedAcl(CannedAccessControlList.PublicRead)
                );

                files.add(BASE_URL + PREFIX + path + fileName);
            } catch (AmazonS3Exception | IOException e) {
                throw new AmazonS3Exception(e.getMessage());
            }
        });

        return files;
    }

    public boolean deleteFile(List<String> paths) {

        List<KeyVersion> keyVersions = new ArrayList<>();

        List<String> keys = parseAwsUrlToKey(paths);

        for (String key : keys) {
            keyVersions.add(new KeyVersion(key));
        }

        DeleteObjectsRequest deleteObjectsRequest =
                new DeleteObjectsRequest(bucket).withKeys(keyVersions).withQuiet(false);

        try {
            amazonS3Client.deleteObjects(deleteObjectsRequest);
        } catch (MultiObjectDeleteException e){
            throw new AmazonS3Exception(e.getMessage());
        }

        return true;
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (Exception e) {
            throw new AmazonS3Exception(e.getMessage());
        }
    }

    public List<String> parseAwsUrlToKey(List<String> urlList){
        List<String> keys = new ArrayList<>();
        if(urlList != null && urlList.size() > 0 ) {
            for (int i = 0; i < urlList.size(); i++) {
                keys.add(urlList.get(0).split(".com/")[1]);
            }
        }

        return keys;
    }

    public String generatePath(String prefix, Long userId) {
        return prefix + "/" + userId + "/";
    }
}
