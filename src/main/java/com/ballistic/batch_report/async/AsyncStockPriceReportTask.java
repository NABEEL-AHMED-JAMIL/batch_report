package com.ballistic.batch_report.async;

import com.ballistic.batch_report.manager.StockPriceManager;
import com.ballistic.batch_report.model.ResponseObject;
import com.ballistic.batch_report.model.databeans.StockPriceDatabean;
import com.ballistic.batch_report.model.vo.StockPriceVo;
import com.ballistic.batch_report.util.AmazonS3Util;
import com.ballistic.batch_report.util.CommonUtil;
import com.ballistic.batch_report.util.ReportUtil;
import com.ballistic.batch_report.util.ReturnConstants;
import com.google.zxing.WriterException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.ballistic.batch_report.model.ApplicationDecorator;

import java.io.File;
import java.io.IOException;
import java.util.Map;


@Component
@Scope("prototype")
public class AsyncStockPriceReportTask implements Runnable {

    public static final Logger logger = LogManager.getLogger(AsyncStockPriceReportTask.class);

    private final String SUCCESS = "SUCCESS";

    private @Autowired StockPriceManager stockPriceManager;
    private @Autowired CommonUtil commonUtil;
    private @Autowired ReturnConstants returnConstants;
    private @Autowired ReportUtil reportUtil;
    private @Autowired AmazonS3Util s3Util;

    private String uuid;
    private ApplicationDecorator decorator;
    private ResponseObject responseObject;

    public AsyncStockPriceReportTask() {}

    public AsyncStockPriceReportTask(String uuid) { this.uuid = uuid; }

    @Override
    public void run() {
        logger.debug("Thread Report Start");
        try {
            this.findRecordProcess();
        } catch (IOException ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
        } catch (WriterException ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
        }
        logger.debug("Thread Report End");
    }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public ApplicationDecorator getDecorator() { return decorator; }
    public void setDecorator(ApplicationDecorator decorator) { this.decorator = decorator; }

    private void findRecordProcess() throws IOException, WriterException {
        logger.debug("Uuid Report :- " + this.getUuid());
        this.decorator = new ApplicationDecorator();
        StockPriceDatabean stockPriceDatabean = new StockPriceDatabean();
        stockPriceDatabean.setUuId(this.getUuid());
        this.decorator.setDataBean(stockPriceDatabean);
        this.stockPriceManager.getStockPriceById(this.getDecorator());
        this.responseObject = this.commonUtil.responseToClient(this.getDecorator());
        if(this.responseObject.getReturnType().equals(SUCCESS)) {
            logger.debug("====>  Report-Process Start.");
            if(this.getDecorator().getDataBean() != null) {
                File file = this.reportUtil.generateStockPriceReportDailyExcl((StockPriceVo) decorator.getDataBean());
                if(file != null) {
                    Map<String, Object> resourceSaveInfo = this.s3Util.uploadToBucket(file);
                    logger.info("Resource ===========> " + " Save-To-Bucket " + " <=========== " + resourceSaveInfo);
                } else {
                    logger.error("Process Report Fail For Id :- " + this.getUuid() + " Report File Note Create");
                }
            } else {
                logger.error("Process Report Fail For Id :- " + this.getUuid());
            }
        }
        this.clearStockGarbage();
    }


    private void clearStockGarbage() {
        this.decorator = null;
        this.responseObject = null;
        this.uuid = null;
    }

}