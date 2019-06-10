package com.ballistic.batch_report.util;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CreativeMessagesConstants {

    public String CREATIVE_CREATE_SUCCESS = "New creative created successfully";
    public String CREATIVE_CREATE_FAILURE = "Unable to create new creative";

    public String CREATIVE_RETURN_SUCCESS = "Creative retrieved successfully";
    public String CREATIVE_RETURN_FAILURE = "Unable to retrive creative";

    public String CREATIVE_RETURN_ALL_SUCCESS = "All creatives retrieved successfully";
    public String CREATIVE_RETURN_ALL_FAILURE = "Unable to retrive creatives";

    public String UPLOAD_FILE_SUCCESS = "File upload successfully";
    public String UPLOAD_FILE_FAILURE = "Unable to upload file";

    public String CREATE_FILE_FAILURE = "Create file failure";
    public String CREATE_FILE_SUCCESS = "Create file success";

    public String READ_FILE_FAILURE = "Read file failure";
    public String READ_FILE_SUCCESS = "Read file success";

    public String DOWNLOAD_URL_FILE_SUCCESS = "Download file from URL successfully";
    public String DOWNLOAD_URL_FILE_FAILURE = "Unable to download file from URL";

}
