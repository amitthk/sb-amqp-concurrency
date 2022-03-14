package com.amitthk.sbamqpconcurrency.service;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amitthk.sbamqpconcurrency.model.BucketType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class S3Client {

    @Value("${app.config.bucket-type}")
    private BucketType bucketTypeToUse;

    @Value("${app.config.custom-bucket-access-key}")
    private String customBucketAccessKey;

    @Value("${app.config.custom-bucket-key-secret}")
    private String customBucketKeySecret;


    @Value("${app.config.custom-bucket-endpoint}")
    private String customBucketEndpoint;

    AmazonS3 client;

    @Bean
    public AmazonS3 amazonS3() {

        Regions clientRegion = Regions.AP_SOUTHEAST_1; //this is fixed for now we can parameterise
        System.setProperty(SDKGlobalConfiguration.DISABLE_CERT_CHECKING_SYSTEM_PROPERTY,"true");
        if (this.bucketTypeToUse== BucketType.KeyAccess){
            BasicAWSCredentials s3creds = new BasicAWSCredentials(this.customBucketAccessKey,this.customBucketKeySecret);
            System.out.println(this.customBucketAccessKey + this.customBucketKeySecret);
            ClientConfiguration cc = new ClientConfiguration();
            //cc.setNonProxyHosts("*");//String.join("|",envNoProxy.split(",")));
            //cc.setProtocol(Protocol.HTTPS);

            //AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(this.customBucketEndpoint, clientRegion.getName());
            AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(s3creds);
            client = AmazonS3ClientBuilder.standard().withClientConfiguration(cc)
//                    .withEndpointConfiguration(endpointConfiguration)
                    .withCredentials(provider)
                    .build();
        }else{
            client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .build();
        }
        return client;
    }
}
