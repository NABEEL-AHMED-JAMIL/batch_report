package com.ballistic.batch_report.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Scope("prototype")
public class Constants {

    @Value("${db.host}")
    private String HOST;
    //= "localhost:27017";
    @Value("${db.name}")
    private String DB_NAME;
    //= "cm_db";
    @Value("${file.upload.dir}")
    private Set<String> FILE_PATHs;

    @Value("${shareddal.asyncTaskExecutor.minThreads}")
    private Integer minThreads;

    @Value("${shareddal.asyncTaskExecutor.maxThreads}")
    private Integer maxThreads;

    @Value("${shareddal.asyncTaskExecutor.idleThreadLife}")
    private Integer idleThreadLife;

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Value("${amazonProperties.accessKey}")
    private String accessKey;

    @Value("${amazonProperties.secretKey}")
    private String secretKey;

    @Value("${amazonProperties.region}")
    private String region;


    public Constants() { }

    public String getHOST() { return HOST; }

    public String getDB_NAME() { return DB_NAME; }

    public Set<String> getFILE_PATH() { return FILE_PATHs; }

    public Integer getMinThreads() { return minThreads; }

    public Integer getMaxThreads() { return maxThreads; }

    public Integer getIdleThreadLife() { return idleThreadLife; }

    public String getEndpointUrl() { return endpointUrl; }

    public String getBucketName() { return bucketName; }  // default-bucket name

    public void setBucketName(String bucketName) { this.bucketName = bucketName; }

    public String getAccessKey() { return accessKey; }

    public String getSecretKey() { return secretKey; }

    public String getRegion() { return region; }

    public void setRegion(String region) { this.region = region; }

}