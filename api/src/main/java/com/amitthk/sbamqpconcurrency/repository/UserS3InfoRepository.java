package com.amitthk.sbamqpconcurrency.repository;

import com.amitthk.sbamqpconcurrency.model.UserS3Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserS3InfoRepository extends JpaRepository<UserS3Info,Long> {
    @Query("SELECT t FROM UserS3Info t WHERE t.bucketNumber = ?1")
    List<UserS3Info> findBybucketNumber(Long bucketNumber);

}
