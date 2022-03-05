package com.amitthk.sbamqpconcurrency.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.amazonaws.services.s3.model.*;
import com.amitthk.sbamqpconcurrency.model.UserSessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;

import javax.annotation.Resource;

@Service
public class S3RepositoryImpl implements S3Repository {

    private static final int NUMBER_OF_THREADS = 50;
    private Logger logger = LoggerFactory.getLogger(S3RepositoryImpl.class);

    @Resource(name="getUserSessionStore")
    UserSessionStore userSessionStore;

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UserSessionStore getUserSessionStore(){
        return new UserSessionStore();
    }

    @Override
    public void setCredentials(String access_key_id, String secret_access_key, String region){
        userSessionStore.setCredentials(access_key_id,secret_access_key,region);
    }

    @Override
    public void saveSessionByEmail(String email) {

    }

    @Override
    public ByteArrayOutputStream readObject(String bucketName, String keyName) throws Exception {
        try {
            S3Object s3object = userSessionStore.getS3client().getObject(new GetObjectRequest(bucketName, keyName));

            InputStream is = s3object.getObjectContent();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[4096];
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, len);
            }

            return baos;
        } catch (IOException ioe) {
            logger.error(ioe.toString());
        } catch (AmazonServiceException ase) {
            logger.error(ase.toString());
            throw ase;
        } catch (AmazonClientException ace) {
            logger.error(ace.toString());
            throw ace;
        }

        return null;
    }

    @Override
    public void addObject(String bucketName, String keyName, MultipartFile file) throws Exception {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            userSessionStore.getS3client().putObject(bucketName, keyName, file.getInputStream(), metadata);
        } catch(IOException ioe) {
            logger.error(ioe.toString());
        } catch (AmazonServiceException ase) {
            logger.error(ase.toString());
            throw ase;
        } catch (AmazonClientException ace) {
            logger.error(ace.toString());
            throw ace;
        }
    }

    @Override
    public void deleteObject(String bucketName, String keyName) throws Exception {
        try {
            userSessionStore.getS3client().deleteObject(bucketName, keyName);
        } catch (AmazonServiceException ase) {
            logger.error(ase.toString());
            throw ase;
        } catch (AmazonClientException ace) {
            logger.error(ace.toString());
            throw ace;
        }
    }

    @Override
    public List<String> listObjects(String bucketName) throws Exception {
        ExecutorService exec = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        ListObjectsRequest listObjectsRequest =
                new ListObjectsRequest()
                        .withBucketName(bucketName);

        List<String> keys = new ArrayList<>();

        ObjectListing objects = userSessionStore.getS3client().listObjects(listObjectsRequest);

        while (true) {
            List<S3ObjectSummary> summaries = objects.getObjectSummaries();
            if (summaries.size() < 1) {
                break;
            }

            for (S3ObjectSummary item : summaries) {
                if (!item.getKey().endsWith("/"))
                    keys.add(item.getKey());
            }

            objects = userSessionStore.getS3client().listNextBatchOfObjects(objects);
        }
        return keys;
    }

}
