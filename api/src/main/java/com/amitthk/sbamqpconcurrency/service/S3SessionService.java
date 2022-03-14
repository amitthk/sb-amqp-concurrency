package com.amitthk.sbamqpconcurrency.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class S3SessionService {

    private static final Logger logger = LoggerFactory.getLogger(S3SessionService.class);

    @Autowired
    AmazonS3 amazonS3;

    @Autowired
    RabbitMqService rabbitMqService;


    @Value("${app.config.env-no-proxy}")
    private String envNoProxy;

    public Future<List<S3ObjectSummary>> getSize(String prefix, String bucket) {

        CompletableFuture<List<S3ObjectSummary>> completableFuture = new CompletableFuture<>();
        Executors.newCachedThreadPool().submit(() -> {
            ObjectListing listing4 = amazonS3.listObjects((new ListObjectsRequest()).withBucketName(bucket).withPrefix(prefix));
            while (listing4.isTruncated()) {
                listing4 = amazonS3.listNextBatchOfObjects(listing4);
                List<S3ObjectSummary> lstSummary = listing4.getObjectSummaries();
                completableFuture.complete(lstSummary);
                //lstSummary.forEach(s->rabbitMqService.send(s));
            }
            return null;
        });

        completableFuture.exceptionally(throwable-> {
            if(throwable instanceof RuntimeException){
                return null;
            }if(throwable instanceof Error) throw (Error)throwable;
            throw new AssertionError(throwable);
        });
        return completableFuture;
    }
}
