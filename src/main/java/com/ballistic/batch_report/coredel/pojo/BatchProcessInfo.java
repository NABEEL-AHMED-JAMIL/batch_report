package com.ballistic.batch_report.coredel.pojo;

import lombok.Data;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;

import java.util.*;

/**
 * Created by AdMaxim on 5/24/2019.
 */
@Data
@Entity("BatchProcessInfo")
public class BatchProcessInfo {

    public static final String TOTAL = "total";
    public static final String PROCESS = "process";
    public static final String FAIL = "fail";
    public static final String PROCESS_ID = "process_id";
    public static final String FAIL_ID = "fail_id";

    @Id
    @Property("_id")
    private String uuId;
    private Date startDate;
    private Date endDate;
    private BatchType batchType;
    // total=50,process=25,fail=25,process_id=[1,2,3],fail_id=[1,2,3]
    private Map<String, Object> rawData;

    private static List<BatchProcessInfo> dummyData() {
        List<BatchProcessInfo> batchProcessInfos = new ArrayList<>();
        Map<String, Object> rawData = new HashMap<>();
        rawData.put(TOTAL, 50);
        rawData.put(PROCESS, 25);
        rawData.put(FAIL,25);
        String[] processId = { UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()};
        rawData.put(PROCESS_ID, processId);
        String[] failId = { UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()};
        rawData.put(FAIL_ID, failId);

        BatchProcessInfo batchProcessInfo = new BatchProcessInfo();
        batchProcessInfo.setUuId(UUID.randomUUID().toString());
        batchProcessInfo.setRawData(rawData);
        batchProcessInfo.setStartDate(new Date());
        batchProcessInfo.setEndDate(new Date());
        batchProcessInfo.setBatchType(BatchType.EVERY_ONE_HOUR);

        batchProcessInfos.add(batchProcessInfo);

        return batchProcessInfos;
    }

    public static void main(String args[]) {
        System.out.println(dummyData());
    }

}
