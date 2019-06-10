package com.ballistic.batch_report.coredel.pojo;

import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.util.*;

/**
 * Created by AdMaxim on 5/24/2019.
 */
@Data
@Entity("ReportInfo")
public class ReportInfo {

    public static final String BUCKET_NAME = "bucket_name";
    public static final String KEY = "key";
    public static final String URL = "url";
    public static final String SIZE = "size";
    public static final String STOCK_ID = "stock_id";

    @Id
    @Property("_id")
    private String uuId;
    private Date createDate;
    private Date deleteDate;
    // raw data (bucket-name,key,url,size,stock-id)
    private Map<String, Object> rawData;
    private Boolean isDelete = false;

    public static List<ReportInfo> dummyDemo() {
        List<ReportInfo> reportInfos = new ArrayList<>();
        Map<String, Object> rawData = new HashMap<>();
        rawData.put(BUCKET_NAME, "Pakistan");
        rawData.put(KEY, "xyzsdiesxxsxsdfdzasd");
        rawData.put(URL,"http://google.com");
        rawData.put(SIZE, "126*120");
        rawData.put(STOCK_ID,UUID.randomUUID().toString());
        ReportInfo reportInfo = new ReportInfo();
        reportInfo.setUuId(UUID.randomUUID().toString());
        reportInfo.setCreateDate(new Date());
        reportInfo.setRawData(rawData);
        reportInfos.add(reportInfo);
        return reportInfos;
    }

    public static void main(String args[]) {
        System.out.println(dummyDemo());
    }

}
