package com.ballistic.batch_report.api;

import com.ballistic.batch_report.manager.StockPriceManager;
import com.ballistic.batch_report.model.ApplicationDecorator;
import com.ballistic.batch_report.model.databeans.StockByRange;
import com.ballistic.batch_report.model.databeans.StockPriceDatabean;
import com.ballistic.batch_report.util.CommonUtil;
import com.ballistic.batch_report.util.ReturnConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AdMaxim on 5/24/2019.
 */
@RestController
@Scope("prototype")
public class StockPriceRestApi {

    public static final Logger logger = LogManager.getLogger(StockPriceRestApi.class);

    private @Autowired CommonUtil commonUtil;
    private @Autowired StockPriceManager stockPriceManager;

    @RequestMapping(value = "/api/createStockPrice", method = RequestMethod.POST)
    public Object createStockPrice(@RequestBody String json) {
        long startTime = System.currentTimeMillis();
        ApplicationDecorator decorator = new ApplicationDecorator();
        String jsonRequest = json;
        StockPriceDatabean stockPriceDatabean = (StockPriceDatabean) this.commonUtil.populateDataBeanFromJSON(StockPriceDatabean.class, decorator, jsonRequest);
        if(decorator.getResponseMessage() == null) {
            decorator.setDataBean(stockPriceDatabean);
            this.stockPriceManager.createStockPrice(decorator);
            decorator.setApiName("/api/createStockPrice");
        }
        logger.debug("createStockPrice : " + json);
        decorator.setQueryTime((System.currentTimeMillis() - startTime) + "");
        logger.info("Api-Response Time :- " + decorator.getQueryTime());
        return this.commonUtil.responseToClient(decorator);
    }

    @RequestMapping(value = { "/api/getStockPriceById/{uuid}" }, method = RequestMethod.GET)
    public Object getStockPriceById(@PathVariable("uuid") String uuid) {
        long startTime = System.currentTimeMillis();
        ApplicationDecorator decorator = new ApplicationDecorator();
        StockPriceDatabean stockPriceDatabean = (StockPriceDatabean) this.commonUtil.populateDataBeanFromJSON(StockPriceDatabean.class, decorator, "");
        if(decorator.getResponseMessage() == null && (StringUtils.isNotEmpty(uuid) && !uuid.equalsIgnoreCase("null"))) {
            stockPriceDatabean.setUuId(uuid);
            decorator.setDataBean(stockPriceDatabean);
            this.stockPriceManager.getStockPriceById(decorator);
            decorator.setApiName("/api/getStockPriceById/"+uuid);
        } else {
            decorator.setResponseMessage("Invalid Request");
            decorator.getErrors().add("Wrong Uuid");
            decorator.setReturnCode(new ReturnConstants().ReturnCodeFailure);
            logger.error("createUserInfoCreative :- Invalid Request");
        }
        logger.debug("getStockPriceById : " + uuid);
        decorator.setQueryTime((System.currentTimeMillis() - startTime) + "");
        logger.info("Api-Response Time :- " + decorator.getQueryTime());
        return this.commonUtil.responseToClient(decorator);
    }

    @RequestMapping(value = { "/api/getStockPriceByRange" }, method = RequestMethod.GET)
    public Object getStockPriceIdByRange(@RequestBody String json) {
        long startTime = System.currentTimeMillis();
        ApplicationDecorator decorator = new ApplicationDecorator();
        StockByRange stockByRange = (StockByRange) this.commonUtil.populateDataBeanFromJSON(StockByRange.class, decorator, json);
        if(decorator.getResponseMessage() == null) {
            decorator.setDataBean(stockByRange);
            this.stockPriceManager.getStockPriceByRange(decorator);
            decorator.setApiName("/api/getStockPriceIdByRange");
        }
        logger.debug("getStockPriceByRange : " + json);
        decorator.setQueryTime((System.currentTimeMillis() - startTime) + "");
        logger.info("Api-Response Time :- " + decorator.getQueryTime());
        return this.commonUtil.responseToClient(decorator);
    }

    @RequestMapping(value = { "/api/getStockPriceIdByDateRange" }, method = RequestMethod.GET)
    public Object getStockPriceIdByDateRange(@RequestBody String json) {
        long startTime = System.currentTimeMillis();
        ApplicationDecorator decorator = new ApplicationDecorator();
        StockByRange stockByRange = (StockByRange) this.commonUtil.populateDataBeanFromJSON(StockByRange.class, decorator, json);
        if(decorator.getResponseMessage() == null) {
            decorator.setDataBean(stockByRange);
            this.stockPriceManager.getStockPriceIdByDateRange(decorator); // mean live rquest
            decorator.setApiName("/api/getStockPriceIdByDateRange");
        }
        logger.debug("getStockPriceIdByDateRange : " + json);
        decorator.setQueryTime((System.currentTimeMillis() - startTime) + "");
        logger.info("Api-Response Time :- " + decorator.getQueryTime());
        return this.commonUtil.responseToClient(decorator);
    }

}
