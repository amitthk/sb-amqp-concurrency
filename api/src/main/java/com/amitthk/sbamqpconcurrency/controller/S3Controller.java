package com.amitthk.sbamqpconcurrency.controller;

import com.amazonaws.services.devicefarm.model.ArgumentException;
import com.amitthk.sbamqpconcurrency.model.S3CredentialsModel;
import com.amitthk.sbamqpconcurrency.model.UserS3Info;
import com.amitthk.sbamqpconcurrency.model.UserSessionStore;
import com.amitthk.sbamqpconcurrency.repository.UserS3InfoRepository;
import com.amitthk.sbamqpconcurrency.service.S3Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController("/api")
public class S3Controller {

    @Resource(name="getUserSessionStore")
    UserSessionStore userSessionStore;

    @Autowired
    S3Repository s3Repository;

    @Autowired
    UserS3InfoRepository userS3InfoRepository;

    @GetMapping("/api/list")
    public List<String> listBucket(@RequestParam(value = "bucketName", required = true) String bucketName) throws Exception {
        if(bucketName==null){
            throw new ArgumentException("bucketName cannot be null!");
        }

        List<String> lstRtrn= s3Repository.listObjects(bucketName);
        return(lstRtrn);
    }

    @PostMapping("/api/s3/updatecredentials")
    public String updateCredentials(@RequestBody S3CredentialsModel s3CredentialsModel) {
        s3Repository.setCredentials(s3CredentialsModel.getAccess_key_id(),s3CredentialsModel.getSecret_access_key(),s3CredentialsModel.getRegion());
        return "Credentials successfully updated!";
    }

    @PostMapping("/api/s3/save_s3session")
    public String saveS3Session(Long bucketNumber) {
            UserS3Info userS3Info = new UserS3Info();
            userS3Info.setBucketNumber(bucketNumber);
            userS3Info.setAwsId(userSessionStore.getUserS3Info().getAwsId());
            userS3Info.setAwsKey(userSessionStore.getUserS3Info().getAwsKey());
            userS3Info.setRegion(userSessionStore.getUserS3Info().getRegion());
            userS3Info.setCurrentS3Bucket(userSessionStore.getUserS3Info().getCurrentS3Bucket());
            userS3InfoRepository.save(userS3Info);
            return "Credentials successfully saved!";
    }

    @PostMapping("/api/s3/load_last_s3session")
    public String loadLastS3Session(Long bucketNumber) {
            UserS3Info userS3Info = userS3InfoRepository.findBybucketNumber(bucketNumber).get(0);
            s3Repository.setCredentials(userS3Info.getAwsId(),userS3Info.getAwsKey(),userS3Info.getRegion());
            return "Credentials successfully loaded!";
    }

    @PostMapping("/api/file/upload")
    public String uploadMultipartFile(@RequestParam(value = "bucketName", required = true) String bucketName, @RequestParam("keyname") String keyName, @RequestParam("uploadfile") MultipartFile file) throws Exception {
        s3Repository.addObject(bucketName, keyName, file);
        return "Upload Successfully. -> KeyName = " + keyName;
    }

    @GetMapping("/api/file/download")
    public ResponseEntity<byte[]> downloadObject(@RequestParam(value = "bucketName", required = true) String bucketName, @RequestParam(value = "keyname",required = true) String keyname) throws Exception {
        ByteArrayOutputStream downloadInputStream = s3Repository.readObject(bucketName,keyname);

        return ResponseEntity.ok()
                .contentType(contentType(keyname))
                .header(HttpHeaders.CONTENT_DISPOSITION,String.format("attachment; filename=\"%s\"",keyname))
                .body(downloadInputStream.toByteArray());
    }

    @DeleteMapping("/api/file/delete")
    public String deleteObject(@RequestParam(value = "bucketName", required = true) String bucketName, @RequestParam(value = "keyname",required = true) String keyname) throws Exception {
        s3Repository.deleteObject(bucketName,keyname);

        return String.format("File %s deleted!",keyname);
    }

    private MediaType contentType(String keyname) {
        String[] arr = keyname.split("\\.");
        String type = arr[arr.length-1];
        switch(type) {
            case "txt": return MediaType.TEXT_PLAIN;
            case "png": return MediaType.IMAGE_PNG;
            case "jpg": return MediaType.IMAGE_JPEG;
            default: return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
