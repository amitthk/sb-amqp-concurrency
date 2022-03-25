package com.amitthk.sbamqpconcurrency.controller;

import com.amazonaws.services.devicefarm.model.ArgumentException;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amitthk.sbamqpconcurrency.service.S3SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController("/api")
public class S3Controller {

    @Autowired
    S3SessionService s3SessionService;

    @GetMapping("/api/traverse")
    public CompletableFuture<List<S3ObjectSummary>> traverse(@RequestParam(value = "bucketName", required = true) String bucketName, @RequestParam(value = "prefix", required = false) String prefix) throws Exception {
        if(bucketName==null){
            throw new ArgumentException("bucketName cannot be null!");
        }

        try{
           return s3SessionService.getSizeOld(prefix,bucketName);
        }catch (Exception exception){
            throw(exception);
        }
    }

    @GetMapping("/api/traversew")
    public CompletableFuture<List<List<S3ObjectSummary>>> traverse2(@RequestParam(value = "bucketName", required = true) String bucketName, @RequestParam(value = "prefix", required = false) String prefix) throws Exception {
        if(bucketName==null){
            throw new ArgumentException("bucketName cannot be null!");
        }

        try{
            return s3SessionService.getSizeWithExceptionLog(prefix,bucketName);
        }catch (Exception exception){
            throw(exception);
        }
    }

}
