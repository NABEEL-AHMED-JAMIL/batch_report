package com.ballistic.batch_report.async;

import com.ballistic.batch_report.manager.StockPriceManager;
import com.ballistic.batch_report.model.ApplicationDecorator;
import com.ballistic.batch_report.model.ResponseObject;
import com.ballistic.batch_report.util.CommonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by AdMaxim on 5/27/2019.
 */
@Component
@Scope("prototype")
public class AsyncStockPriceSaveTask implements Runnable {

    public static final Logger logger = LogManager.getLogger(AsyncStockPriceSaveTask.class);

    private @Autowired StockPriceManager stockPriceManager;
    private @Autowired CommonUtil commonUtil;

    private ApplicationDecorator applicationDecorator;

    public AsyncStockPriceSaveTask() {}

    public ApplicationDecorator getApplicationDecorator() { return applicationDecorator; }
    public void setApplicationDecorator(ApplicationDecorator applicationDecorator) { this.applicationDecorator = applicationDecorator; }

    @Override
    public void run() {
        try {
            logger.debug("Thread StockPrice Save Start");
            this.stockPriceManager.createStockPrice(this.getApplicationDecorator());
            ResponseObject responseObject = this.commonUtil.responseToClient(this.getApplicationDecorator());
            logger.info("Data Response :- " + responseObject);
            logger.debug("Thread StockPrice Save End");
        } catch (Exception ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
        }
    }

}
