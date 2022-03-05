package com.amitthk.sbamqpconcurrency.model;

public class S3CredentialsModel {
    private String access_key_id;
    private String secret_access_key;
    private String region;

    public S3CredentialsModel() {
    }

    public S3CredentialsModel(String access_key_id, String secret_access_key, String region) {
        this.access_key_id = access_key_id;
        this.secret_access_key = secret_access_key;
        this.region = region;
    }

    public String getAccess_key_id() {
        return access_key_id;
    }

    public void setAccess_key_id(String access_key_id) {
        this.access_key_id = access_key_id;
    }

    public String getSecret_access_key() {
        return secret_access_key;
    }

    public void setSecret_access_key(String secret_access_key) {
        this.secret_access_key = secret_access_key;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}