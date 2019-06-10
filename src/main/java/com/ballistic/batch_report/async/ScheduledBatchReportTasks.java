package com.ballistic.batch_report.async;

import com.ballistic.batch_report.manager.StockPriceManager;
import com.ballistic.batch_report.model.ApplicationDecorator;
import com.ballistic.batch_report.model.databeans.StockByRange;
import com.ballistic.batch_report.model.databeans.StockPriceDatabean;
import com.ballistic.batch_report.model.vo.StockPriceVo;
import com.ballistic.batch_report.util.DateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.text.ParseException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by AdMaxim on 5/24/2019.
 */
@Component
public class ScheduledBatchReportTasks implements ApplicationRunner {

    public static final Logger logger = LogManager.getLogger(ScheduledBatchReportTasks.class);

    private final String downloadUrl = "https://raw.githubusercontent.com/rrohitramsen/firehose/master/src/main/resources/data/stock_6_lac.csv";
    private final String FORMAT_DATE = "d-MMMM-yyyy";

    private @Autowired AsyncDALTaskExecutor asyncDALTaskExecutor;
    private @Autowired AsyncStockPriceSaveTask asyncStockPriceSaveTask;
    private @Autowired AsyncStockPriceReportTask asyncStockPriceReportTask;
    private @Autowired StockPriceManager stockPriceManager;
    private @Autowired DateUtil dateUtil;

    public void runEveryFiveMinuteScheduler() {
        logger.info("Every Five Minute Scheduler : {}", "active");
    }

    //second, minute, hour, day, month, weekday
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void runEveryOneHourScheduler() {
        logger.info("Every One Hour Scheduler : {}", "active");
        long startTime = System.currentTimeMillis();
        ApplicationDecorator decorator = null;
        StockByRange stockByRange = null;
        try {
            Date currentDateTime = new Date(); // current date+time
            Date previousDateTime = this.dateUtil.getDateTimeFromCurrentDateTime(); // previous date+time -1 HOUR_OF_DAY
            logger.debug("Current Date + Time :- " + currentDateTime);
            while (!this.dateUtil.isDateValidEqual(previousDateTime, currentDateTime)) {
                Date previousDateTimeIncrement = this.dateUtil.getDateTime(previousDateTime);
                logger.info("Previous Date " + previousDateTime + " <===Increment===> " + previousDateTimeIncrement);
                decorator = new ApplicationDecorator();
                stockByRange = new StockByRange(previousDateTime, previousDateTimeIncrement);
                decorator.setDataBean(stockByRange);
                this.stockPriceManager.getStockPriceIdByDateRange(decorator);
                decorator.setQueryTime((System.currentTimeMillis() - startTime) + "");
                if((decorator.getReturnCode() != null && decorator.getReturnCode() == 1005)) {
                    logger.error("Response :- " + decorator.getFailure() + "<=====>" + decorator.getReturnCode() + "<=====>" + decorator.getResponseMap() + "<=====>" + decorator.getResponseMessage() + "<=====>" + decorator.getQueryTime());
                } else {
                    // process the ==> next think
                    List<StockPriceVo> stockPriceList = (List<StockPriceVo>) decorator.getDataBean();
                    stockPriceList.stream().forEach(stockPriceVo -> {
                        try {
                            logger.debug("Sending Uuid for Report Process ");
                            this.asyncStockPriceReportTask.setUuid(stockPriceVo.getUuId());
                            this.asyncDALTaskExecutor.addTask(this.asyncStockPriceReportTask);
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                            logger.error("*********** Exception ***********", ex.getMessage());
                        }
                    });
                }
                previousDateTime = this.dateUtil.getDateTime(previousDateTime);
            }
        } catch (Exception ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
        }
    }

    public void intiMethod() {
        try {
            logger.debug("Sending Uuid for Report Process ");
            this.asyncStockPriceReportTask.setUuid("5ff7ad41d13943bea0a6838b45d3867b");
            this.asyncDALTaskExecutor.addTask(this.asyncStockPriceReportTask);
            Thread.sleep(2);
        } catch (InterruptedException ex) {
            logger.error("*********** Exception ***********", ex);
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        long startTime = System.currentTimeMillis();
        logger.info("Fetch Online date start");
        //this.openConnection();
        this.runEveryOneHourScheduler();
        logger.info("Fetch Online date end");
        logger.info("Start Time :- " + new Date(startTime) + ", Current Time :- " + new Date(System.currentTimeMillis()) + " Total Time :- " + (System.currentTimeMillis() - startTime) + ".ms");
    }

    private void openConnection() throws InterruptedException {
        try {
            long startTime = System.currentTimeMillis();
            URL myUrl = new URL(downloadUrl);
            HttpsURLConnection conn = (HttpsURLConnection) myUrl.openConnection();
            InputStream inputStream = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);

            String inputLine;
            Integer currentLine = 0;
            while ((inputLine = br.readLine()) != null) {
                long singTime = System.currentTimeMillis();
                logger.info("Current Line :- " + (currentLine) + " Value :- " + inputLine);
                if(currentLine != 0) {
                    final ApplicationDecorator decorator = new ApplicationDecorator();
                    StockPriceDatabean stockPriceDatabean = rawDataToStockDataBean(inputLine);
                    decorator.setDataBean(stockPriceDatabean);
                    this.asyncStockPriceSaveTask.setApplicationDecorator(decorator);
                    this.asyncDALTaskExecutor.addTask(this.asyncStockPriceSaveTask);
                    Thread.sleep(2); // m-second
                }
                logger.info("Start Time :- " + new Date(startTime) + ", Current Time :- " + new Date(System.currentTimeMillis()) + " Total Time :- " + (System.currentTimeMillis() - singTime) + ".ms");
                currentLine++;
            }
            br.close();
            logger.debug("Start Time :- " + new Date(startTime) + ", Current Time :- " + new Date(System.currentTimeMillis()) + " Total Time :- " + (System.currentTimeMillis() - startTime) + ".ms");
        } catch (IOException ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
        } catch (NullPointerException ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
        }
    }

    private StockPriceDatabean rawDataToStockDataBean(String inputLine) {
        long startTime = System.currentTimeMillis();
        StockPriceDatabean stockPriceDatabean = new StockPriceDatabean();
        try {
            String[] stockPrices = inputLine.split(",");
            stockPriceDatabean.setUuId(java.util.UUID.randomUUID().toString().replaceAll("-", ""));
            if(stockPrices[0] != null && !stockPrices[0].equals("")) { stockPriceDatabean.setDate(new SimpleDateFormat(FORMAT_DATE, Locale.ENGLISH).parse(stockPrices[0])); }
            if(stockPrices[1] != null && !stockPrices[1].equals("")) { stockPriceDatabean.setOpenPrice(Double.valueOf(stockPrices[1])); }
            if(stockPrices[2] != null && !stockPrices[2].equals("")) { stockPriceDatabean.setHighPrice(Double.valueOf(stockPrices[2])); }
            if(stockPrices[3] != null && !stockPrices[3].equals("")) { stockPriceDatabean.setLowPrice(Double.valueOf(stockPrices[3])); }
            if(stockPrices[4] != null && !stockPrices[4].equals("")) { stockPriceDatabean.setClosePrice(Double.valueOf(stockPrices[4])); }
            if(stockPrices[5] != null && !stockPrices[5].equals("")) { stockPriceDatabean.setWap(Double.valueOf(stockPrices[5])); }
            if(stockPrices[6] != null &&  !stockPrices[6].equals("")) { stockPriceDatabean.setNoOfShares(Integer.valueOf(stockPrices[6])); }
            if(stockPrices[7] != null && !stockPrices[7].equals("")) { stockPriceDatabean.setNoOfTrades(Integer.valueOf(stockPrices[7])); }
            if(stockPrices[8] != null && !stockPrices[8].equals("")) { stockPriceDatabean.setTotalTurnover(Double.valueOf(stockPrices[8])); }
            if(stockPrices[9] != null && !stockPrices[9].equals("")) { stockPriceDatabean.setDeliverableQuantity(Integer.valueOf(stockPrices[9])); }
            if(stockPrices[10] != null && !stockPrices[10].equals("")) { stockPriceDatabean.setDeliQtyToTradedQty(Double.valueOf(stockPrices[10])); }
            if(stockPrices[11] != null && !stockPrices[11].equals("")) { stockPriceDatabean.setSpreadHighLow(Double.valueOf(stockPrices[11])); }
            if(stockPrices[12] != null && !stockPrices[12].equals("")) { stockPriceDatabean.setSpreadCloseOpen(Double.valueOf(stockPrices[12])); }

        } catch (ParseException ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
        }  catch (NumberFormatException ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
        }
        logger.debug("Rwa Data   --> StockPriceBean  Convert Time :- " + (System.currentTimeMillis() - startTime) + ".ms");
        return stockPriceDatabean;
    }

}