package com.amitthk.sbamqpconcurrency.service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface S3Repository {
    public List<String> listObjects(String bucketName) throws Exception;
    public ByteArrayOutputStream readObject(String bucketName, String keyName) throws Exception;
    public void addObject(String bucketName, String keyName, MultipartFile file) throws Exception;
    public void deleteObject(String bucketName, String keyName) throws Exception;
    public void setCredentials(String access_key_id, String secret_access_key, String region);
    public void saveSessionByEmail(String email);
}

