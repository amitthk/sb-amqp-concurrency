package com.amitthk.sbamqpconcurrency.model;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.apache.logging.log4j.util.Strings;

import javax.annotation.PostConstruct;

public class UserSessionStore {

    private UserS3Info userS3Info;
    private AmazonS3 s3Client;

    public UserS3Info getUserS3Info() {
        return userS3Info;
    }

    public void setUserS3Info(UserS3Info userS3Info) {
        this.userS3Info = userS3Info;
    }

    public UserSessionStore() {
        this.userS3Info = new UserS3Info();
    }

    public String getCurrentS3Bucket() {
        return this.userS3Info.getCurrentS3Bucket();
    }

    public void setCurrentS3Bucket(String currentS3Bucket) {
        this.userS3Info.setCurrentS3Bucket(currentS3Bucket);
    }

    public void setCredentials(String awsId, String awsKey, String region) {
        this.userS3Info.setAwsId(awsId);
        this.userS3Info.setAwsKey(awsKey);
        this.userS3Info.setRegion(region);
        builds3Client();
    }

    public AmazonS3 getS3client() throws Exception {
        if(Strings.isEmpty(this.userS3Info.getAwsId()) || Strings.isEmpty(this.userS3Info.getAwsKey()) || Strings.isEmpty(this.userS3Info.getRegion())){
            throw new Exception("AWS Credentials not set in session");
        }
        if(this.s3Client==null){
            builds3Client();
        }
        return s3Client;
    }

    private void builds3Client() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(this.userS3Info.getAwsId(), this.userS3Info.getAwsKey());
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(this.userS3Info.getRegion()))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
        this.s3Client=s3Client;
    }
}
