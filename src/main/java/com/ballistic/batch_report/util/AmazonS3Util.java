package com.ballistic.batch_report.util;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by AdMaxim on 6/3/2019.
 */
@Component
@Scope("prototype")
public class AmazonS3Util {

    public static final Logger logger = LogManager.getLogger(AmazonS3Util.class);

    private HashMap<String, Object> s3Response;
    private @Autowired Constants constants;

    private static volatile boolean isS3Initialized = false;

    public AmazonS3Util() {
        // for local test use this
        //this.constants = new Constants();
        //this.constants.setBucketName("stockreport1");
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Create a new S3 bucket - Amazon S3 bucket names are globally unique, so once a bucket name has been taken by any user, you
     * can't create another bucket with that same name.
     * You can optionally specify a location for your bucket if you want to keep your data closer to your applications or users.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public boolean createBucket(String bucketName) {
        boolean retVal = true;
        try {
            AmazonS3 s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(this.getCredentials())).withRegion(Regions.fromName(this.constants.getRegion())).build();
            logger.debug("Amazon info : bucketName=" + bucketName);
            if(s3client.doesBucketExistV2(bucketName)) {
                logger.error("Bucket name is available. " + " Try again with a different Bucket name.");
                return false;
            }
            logger.debug("Creating bucket " + bucketName);
            s3client.createBucket(bucketName);
        } catch (AmazonServiceException ase) {
            retVal = false;
            logger.error("Caught an AmazonServiceException, which means your request made it " + "to Amazon S3, but was rejected with an error response for some reason.");
            logger.error("Error Message :- " + ase.getMessage());
            logger.error("HTTP Status Code :- " + ase.getStatusCode());
            logger.error("AWS Error Code :- " + ase.getErrorCode());
            logger.error("Error Type :- " + ase.getErrorType());
            logger.error("Request ID :- " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            retVal = false;
            logger.error("Caught an AmazonClientException, which means the client encountered " + "a serious internal problem while trying to communicate with S3, " + "such as not being able to access the network.");
            logger.error("Error Message: " + ace.getMessage());
        } catch (Exception e) {
            retVal = false;
            e.printStackTrace();
        }
        return retVal;
    }

    /* * * * * * * * * * * * * * * * * *
	* List the buckets in your account *
	* * * * * * * * * * * * * * * * * */
    public boolean listBuckets() {
        boolean retVal = true;
        try {
            AmazonS3 s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(this.getCredentials())).withRegion(Regions.fromName(this.constants.getRegion())).build();
            logger.debug("Listing buckets");
            for (Bucket bucket : s3client.listBuckets()) { logger.debug(" - " + bucket.getName()); }
        } catch (AmazonServiceException ase) {
            retVal = false;
            logger.error("Caught an AmazonServiceException, which means your request made it " + "to Amazon S3, but was rejected with an error response for some reason.");
            logger.error("Error Message :- " + ase.getMessage());
            logger.error("HTTP Status Code :- " + ase.getStatusCode());
            logger.error("AWS Error Code :- " + ase.getErrorCode());
            logger.error("Error Type :- " + ase.getErrorType());
            logger.error("Request ID :- " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            retVal = false;
            logger.error("Caught an AmazonClientException, which means the client encountered " + "a serious internal problem while trying to communicate with S3, " + "such as not being able to access the network.");
            logger.error("Error Message: " + ace.getMessage());
        } catch (Exception e) {
            retVal = false;
            e.printStackTrace();
        }
        return retVal;
    }

    // no we create object key/url
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Upload an object to your bucket - You can easily upload a file to *
     * S3, or upload directly an InputStream if you know the length of   *
     * the data in the stream. You can also specify your own metadata    *
     * when uploading to S3, which allows you set a variety of options   *
     * like content-type and content-encoding, plus additional metadata  *
     * specific to your applications.                                    *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public Map<String, Object> uploadToBucket(File file) {
        try {
            AmazonS3 s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(this.getCredentials())).withRegion(Regions.fromName(this.constants.getRegion())).build();
            String objKey = this.generateFileName(file);
            logger.debug("Uploading a new object to S3 from a file > " + objKey);
            PutObjectRequest putObjectRequest = new PutObjectRequest(this.constants.getBucketName(), objKey, file);
            s3client.putObject(putObjectRequest);
            /* get signed URL (valid for 7 day) */
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(this.constants.getBucketName(), objKey);
            generatePresignedUrlRequest.setMethod(HttpMethod.GET);
            generatePresignedUrlRequest.setExpiration(DateTime.now().plusDays(7).toDate());
            URL signedUrl = s3client.generatePresignedUrl(generatePresignedUrlRequest);
            // raw data (bucket-name,key,url,size,stock-id)
            this.s3Response = new HashMap<>();
            this.s3Response.put("bucketName", this.constants.getBucketName());
            this.s3Response.put("objKey", objKey);
            this.s3Response.put("signedUrl", signedUrl);
            logger.info("Detail :- " + this.s3Response);
        } catch (AmazonServiceException ase) {
            logger.error("Caught an AmazonServiceException, which means your request made it " + "to Amazon S3, but was rejected with an error response for some reason.");
            logger.error("Error Message :- " + ase.getMessage());
            logger.error("HTTP Status Code :- " + ase.getStatusCode());
            logger.error("AWS Error Code :- " + ase.getErrorCode());
            logger.error("Error Type :- " + ase.getErrorType());
            logger.error("Request ID :- " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            logger.error("Caught an AmazonClientException, which means the client encountered " + "a serious internal problem while trying to communicate with S3, " + "such as not being able to access the network.");
            logger.error("Error Message :- " + ace.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.s3Response;
    }

    /*
     * Upload an object to your bucket - You can easily upload a file to
     * S3, or upload directly an InputStream if you know the length of
     * the data in the stream. You can also specify your own metadata
     * when uploading to S3, which allows you set a variety of options
     * like content-type and content-encoding, plus additional metadata
     * specific to your applications.
     */
    public Map<String, Object> uploadToBucket(File file, String bucket) {
        try {
            AmazonS3 s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(this.getCredentials())).withRegion(Regions.fromName(this.constants.getRegion())).build();
            String objKey = generateFileName(file);
            logger.debug("Amazon info : bucket=" + bucket);
            logger.debug("Uploading a new object to S3 from a file > " + objKey);
            PutObjectRequest putObjectRequest;
            putObjectRequest = new PutObjectRequest(this.constants.getBucketName(), objKey, file);
            s3client.putObject(putObjectRequest);
            /* get signed URL (valid for one year) */
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, objKey);
            generatePresignedUrlRequest.setMethod(HttpMethod.GET);
            generatePresignedUrlRequest.setExpiration(DateTime.now().plusDays(7).toDate());
            URL signedUrl = s3client.generatePresignedUrl(generatePresignedUrlRequest);
            this.s3Response = new HashMap<>();
            this.s3Response.put("bucketName", bucket);
            this.s3Response.put("objKey", objKey);
            this.s3Response.put("signedUrl", signedUrl);
            logger.info("Detail :- " + this.s3Response);
        } catch (AmazonServiceException ase) {
            logger.error("Caught an AmazonServiceException, which means your request made it " + "to Amazon S3, but was rejected with an error response for some reason.");
            logger.error("Error Message :- " + ase.getMessage());
            logger.error("HTTP Status Code :- " + ase.getStatusCode());
            logger.error("AWS Error Code :- " + ase.getErrorCode());
            logger.error("Error Type :- " + ase.getErrorType());
            logger.error("Request ID :- " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            logger.error("Caught an AmazonClientException, which means the client encountered " + "a serious internal problem while trying to communicate with S3, " + "such as not being able to access the network.");
            logger.error("Error Message :- " + ace.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s3Response;
    }

    /*
     * List objects in your bucket by prefix - There are many options
     * for listing the objects in your bucket. Keep in mind that buckets
     * with many objects might truncate their results when listing their
     * objects, so be sure to check if the returned object listing is
     * truncated, and use the AmazonS3.listNextBatchOfObjects(...)
     * operation to retrieve additional results.
     */
    public void listBucketObjects(String bucketName) {
        try {
            AmazonS3 s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(this.getCredentials())).withRegion(Regions.fromName(this.constants.getRegion())).build();
            logger.debug("Listing objects");
            ObjectListing objectListing = s3client.listObjects(new ListObjectsRequest().withBucketName(bucketName).withPrefix("My"));
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                logger.info(" - " + objectSummary.getKey() + "  " + "(size = " + objectSummary.getSize() + ")");
            }

        } catch (AmazonServiceException ase) {
            logger.error("Caught an AmazonServiceException, which means your request made it " + "to Amazon S3, but was rejected with an error response for some reason.");
            logger.error("Error Message :- " + ase.getMessage());
            logger.error("HTTP Status Code :- " + ase.getStatusCode());
            logger.error("AWS Error Code :- " + ase.getErrorCode());
            logger.error("Error Type :- " + ase.getErrorType());
            logger.error("Request ID :- " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            logger.error("Caught an AmazonClientException, which means the client encountered " + "a serious internal problem while trying to communicate with S3, " + "such as not being able to access the network.");
            logger.error("Error Message :- " + ace.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Delete an object - Unless versioning has been turned on for your
     * bucket, there is no way to undelete an object, so use caution
     * when deleting objects.
     */
    public boolean deleteBucketObject(String objKey) {
        boolean retVal = true;
        try {
            AmazonS3 s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(this.getCredentials())).withRegion(Regions.fromName(this.constants.getRegion())).build();
            logger.error("Deleting an object");
            s3client.deleteObject(this.constants.getBucketName(), objKey);
        } catch (AmazonServiceException ase) {
            retVal = false;
            logger.error("Caught an AmazonServiceException, which means your request made it " + "to Amazon S3, but was rejected with an error response for some reason.");
            logger.error("Error Message :- " + ase.getMessage());
            logger.error("HTTP Status Code :- " + ase.getStatusCode());
            logger.error("AWS Error Code :- " + ase.getErrorCode());
            logger.error("Error Type :- " + ase.getErrorType());
            logger.error("Request ID :- " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            retVal = false;
            logger.error("Caught an AmazonClientException, which means the client encountered " + "a serious internal problem while trying to communicate with S3, " + "such as not being able to access the network.");
            logger.error("Error Message :- " + ace.getMessage());
        } catch (Exception e) {
            retVal = false;
            e.printStackTrace();
        }
        return retVal;
    }

    /*
     * Delete a bucket - A bucket must be completely empty before it can
     * be deleted, so remember to delete any objects from your buckets
     * before you try to delete them.
     */
    public boolean deleteBucket(String bucketName) {
        boolean retVal = true;
        try {
            AmazonS3 s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(this.getCredentials())).withRegion(Regions.fromName(this.constants.getRegion())).build();
            logger.error("Deleting bucket " + bucketName + "\n");
            s3client.deleteBucket(bucketName);
        } catch (AmazonServiceException ase) {
            retVal = false;
            logger.error("Caught an AmazonServiceException, which means your request made it " + "to Amazon S3, but was rejected with an error response for some reason.");
            logger.error("Error Message :- " + ase.getMessage());
            logger.error("HTTP Status Code :- " + ase.getStatusCode());
            logger.error("AWS Error Code :- " + ase.getErrorCode());
            logger.error("Error Type :- " + ase.getErrorType());
            logger.error("Request ID :- " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            retVal = false;
            logger.error("Caught an AmazonClientException, which means the client encountered " + "a serious internal problem while trying to communicate with S3, " + "such as not being able to access the network.");
            logger.error("Error Message :- " + ace.getMessage());
        } catch (Exception e) {
            retVal = false;
            e.printStackTrace();
        }
        return retVal;
    }

    /*
     * Download an object - When you download an object, you get all of
     * the object's metadata and a stream from which to read the contents.
     * It's important to read the contents of the stream as quickly as
     * possibly since the data is streamed directly from Amazon S3 and your
     * network connection will remain open until you read all the data or
     * close the input stream.
     *
     * GetObjectRequest also supports several other options, including
     * conditional downloading of objects based on modification times,
     * ETags, and selectively downloading a range of an object.
     */
    public String getObjectMetadata(String objKey) {
        String retVal = null;
        try {
            AmazonS3 s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(this.getCredentials())).withRegion(Regions.fromName(this.constants.getRegion())).build();
            logger.info("Downloading an object");
            S3Object object = s3client.getObject(new GetObjectRequest(this.constants.getBucketName(), objKey));
            logger.info("Content-Type: "  + object.getObjectMetadata().getContentType());
            Map<String, String> userMetadataMap = object.getObjectMetadata().getUserMetadata();
            retVal = userMetadataMap.get("size");
        } catch (AmazonServiceException ase) {
            retVal = null;
            logger.error("Caught an AmazonServiceException, which means your request made it " + "to Amazon S3, but was rejected with an error response for some reason.");
            logger.error("Error Message :- " + ase.getMessage());
            logger.error("HTTP Status Code :- " + ase.getStatusCode());
            logger.error("AWS Error Code :- " + ase.getErrorCode());
            logger.error("Error Type :- " + ase.getErrorType());
            logger.error("Request ID :- " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            retVal = null;
            logger.error("Caught an AmazonClientException, which means the client encountered " + "a serious internal problem while trying to communicate with S3, " + "such as not being able to access the network.");
            logger.error("Error Message :- " + ace.getMessage());
        } catch(Exception e) {
            retVal = null;
            e.printStackTrace();
        }
        return retVal;
    }

    private String generateFileName(File file) { return UUID.randomUUID() + "-" + new Date().getTime() + "-" + file.getName(); }

    public AWSCredentials getCredentials() { return new BasicAWSCredentials(this.constants.getAccessKey(), this.constants.getSecretKey()); }

    public AWSCredentials getCredentialsTest() { return new BasicAWSCredentials("AKIAIRXEQCRT4NKGA2RQ", "AOa0uyteMC2K0LItFuOpQ+7YPUn+gSaUWzUdBYjQ"); }

    public static void main(String args[]) {
        AmazonS3Util amazonS3Util = new AmazonS3Util();
        File file = new File("C:\\Users\\AdMaxim\\Desktop\\62457490_2540493155984778_3485278854472269824_n.jpg");
        amazonS3Util.listBucketObjects("stockreport1");
    }
}