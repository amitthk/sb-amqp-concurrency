package com.amitthk.sbamqpconcurrency.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amitthk.sbamqpconcurrency.model.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class S3SessionService {

    private static final Logger logger = LoggerFactory.getLogger(S3SessionService.class);

    @Autowired
    AmazonS3 amazonS3;

//    @Autowired
//    RabbitMqService rabbitMqService;


    @Value("${app.config.env-no-proxy}")
    private String envNoProxy;

    public CompletableFuture<List<S3ObjectSummary>> getSize(String prefix, String bucket) {

        CompletableFuture<List<S3ObjectSummary>> completableFuture = CompletableFuture.supplyAsync(()->{
            List<S3ObjectSummary> lstReturn = new ArrayList<>();
try{
    ObjectListing listing4 = amazonS3.listObjects((new ListObjectsRequest()).withBucketName(bucket).withPrefix(prefix));

    while (listing4.isTruncated()) {
        listing4 = amazonS3.listNextBatchOfObjects(listing4);
        List<S3ObjectSummary> lstSummary = listing4.getObjectSummaries();
        lstReturn.addAll(lstSummary);
        //lstSummary.forEach(s->rabbitMqService.send(s));
    }

}catch (Exception exc){
    logger.error(Utils.printStrackTrace(exc));
}return lstReturn;
        });
        return completableFuture;
    }
}
