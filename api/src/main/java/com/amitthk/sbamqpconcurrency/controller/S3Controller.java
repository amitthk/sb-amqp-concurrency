package com.amitthk.sbamqpconcurrency.controller;

import com.amazonaws.services.devicefarm.model.ArgumentException;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amitthk.sbamqpconcurrency.model.S3CredentialsModel;
import com.amitthk.sbamqpconcurrency.model.UserS3Info;
import com.amitthk.sbamqpconcurrency.model.UserSessionStore;
import com.amitthk.sbamqpconcurrency.repository.UserS3InfoRepository;
import com.amitthk.sbamqpconcurrency.service.S3SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.Future;

@RestController("/api")
public class S3Controller {

    @Autowired
    S3SessionService s3SessionService;

    @GetMapping("/api/traverse")
    public Future<List<S3ObjectSummary>> traverse(@RequestParam(value = "bucketName", required = true) String bucketName,@RequestParam(value = "prefix", required = true) String prefix) throws Exception {
        if(bucketName==null){
            throw new ArgumentException("bucketName cannot be null!");
        }

        try{
           return s3SessionService.getSize(prefix,bucketName);
        }catch (Exception exception){
            throw(exception);
        }
    }

}
