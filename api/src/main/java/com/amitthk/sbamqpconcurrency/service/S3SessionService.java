package com.amitthk.sbamqpconcurrency.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amitthk.sbamqpconcurrency.model.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class S3SessionService {

    private static final Logger logger = LoggerFactory.getLogger(S3SessionService.class);

    @Autowired
    AmazonS3 amazonS3;

    @Autowired
    RabbitMqService rabbitMqService;
    private int objectCount=0;

//    @Autowired
//    private SimpMessageSendingOperations messagingTemplate;

//    @Value("${app.config.env-no-proxy}")
//    private String envNoProxy;
//
//    @Value("${stomp.broker.relay}")
//    private String stompBrokerRelay;


    public CompletableFuture<List<S3ObjectSummary>> getSize(String prefix, String bucketName) {

        prefix=prefix.isEmpty()?"":prefix;
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucketName)
                .withPrefix(prefix);
        ObjectListing objectListing;
        CompletableFuture<List<S3ObjectSummary>> completableFuture = new CompletableFuture<>();


            do{
                objectListing = amazonS3.listObjects(listObjectsRequest);
                completableFuture.complete();
                listObjectsRequest.setMarker(objectListing.getNextMarker());
                //completableFuture.join();
            }while(objectListing.isTruncated());

        completableFuture.exceptionally(ex->{
            if(ex!=null) {
                logger.error(Utils.getStrackTraceAsString((Exception) ex));
            }
            return null;
        });
        return completableFuture;
    }

    @Async
    public CompletableFuture<List> getSizeWithExceptionLog(String prefix, String bucketName) {

        prefix=prefix.isEmpty()?"":prefix;
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucketName)
                .withPrefix(prefix);
        ObjectListing objectListing;
        List<CompletableFuture<List<S3ObjectSummary>>> futureList = new ArrayList<>();
        do{
            objectListing = amazonS3.listObjects(listObjectsRequest);
            futureList.add(extractObjectSummary(objectListing));
            listObjectsRequest.setMarker(objectListing.getNextMarker());
            //completableFuture.join();
        }while(objectListing.isTruncated());
        return Utils.allOf(futureList).joina();
    }

    private CompletableFuture<List<S3ObjectSummary>> extractObjectSummary(ObjectListing objectListing) {
        return CompletableFuture.supplyAsync(()->{
            List<S3ObjectSummary> lstSummary= objectListing.getObjectSummaries();
            lstSummary.forEach(s->rabbitMqService.send(s));
            return objectListing.getObjectSummaries();
        }).exceptionally(ex->{
            if(ex!=null) {
                logger.error(Utils.getStrackTraceAsString((Exception) ex));
            }
            return null;
        });
    }

    @Async
    public void backgroundProcessWithExceptionLog(String prefix, String bucketName) {
        prefix=prefix.isEmpty()?"":prefix;
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucketName)
                .withPrefix(prefix);
        ObjectListing listObjects = null;

        List<CompletableFuture> futureList = new ArrayList<>();
        do{
            listObjects = amazonS3.listObjects(listObjectsRequest);
            futureList.add(processObjectSummary(listObjects));
            listObjects.setMarker(listObjects.getNextMarker());
        }while (listObjects.isTruncated());
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
    }

    private CompletableFuture processObjectSummary(ObjectListing objectListing) {
        return CompletableFuture.runAsync(()->{
            List<S3ObjectSummary> lstSummary= objectListing.getObjectSummaries();
            lstSummary.forEach(s-> {
                rabbitMqService.send(s);
//                messagingTemplate.convertAndSend(stompBrokerRelay,s);
            });

        }).exceptionally(ex->{
            if(ex!=null) {
                logger.error(Utils.getStrackTraceAsString((Exception) ex));
            }
            return null;
        });
    }

    private static <T> CompletableFuture<List<T>> joina(List<CompletableFuture<T>> executionPromises) {
        CompletableFuture<Void> joinedPromise = CompletableFuture.allOf(executionPromises.toArray(CompletableFuture[]::new));
        return joinedPromise.thenApply(voit -> executionPromises.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }

}
