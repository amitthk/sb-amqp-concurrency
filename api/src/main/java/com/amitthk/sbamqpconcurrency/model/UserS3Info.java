package com.amitthk.sbamqpconcurrency.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class UserS3Info {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long bucketNumber;
    private String awsId;

    private String awsKey;

    private String region;

    private String currentS3Bucket;
}
