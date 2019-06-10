package com.ballistic.batch_report.manager;

import com.ballistic.batch_report.coredel.pojo.StockPrice;
import com.ballistic.batch_report.model.ApplicationDecorator;
import com.ballistic.batch_report.model.databeans.StockByRange;
import com.ballistic.batch_report.model.databeans.StockPriceDatabean;
import com.ballistic.batch_report.model.vo.StockPriceVo;
import com.ballistic.batch_report.service.StockPriceService;
import com.ballistic.batch_report.util.BeanFactory;
import com.ballistic.batch_report.util.CreativeMessagesConstants;
import com.ballistic.batch_report.util.DateUtil;
import com.ballistic.batch_report.util.ReturnConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by AdMaxim on 5/24/2019.
 */
@Component
@Scope("prototype")
@SuppressWarnings("unchecked")
public class StockPriceManager {

    public static final Logger logger = LogManager.getLogger(StockPriceManager.class);

    private @Autowired ReturnConstants returnConstants;
    private @Autowired CreativeMessagesConstants creativeMessagesConstants;
    private @Autowired StockPriceService stockPriceService;
    private @Autowired BeanFactory beanFactory;
    private @Autowired DateUtil dateUtil;

    private StockPriceDatabean stockPriceDatabean;
    private StockPriceVo stockPriceVo;
    private List<StockPriceVo> stockPriceVoList;
    private List<StockPrice> stockPriceList;

    // test done with thread task
    public void createStockPrice(ApplicationDecorator decorator) {
        try {
            if(!hasDecorator(decorator)) {
                decorator.setResponseMessage(this.returnConstants.INVALID_INFO);
                decorator.setReturnCode(this.returnConstants.ReturnCodeFailure);
                return;
            } else {
                this.stockPriceDatabean = (StockPriceDatabean) decorator.getDataBean();
                this.stockPriceVo = new StockPriceVo();
                this.stockPriceBeanToVo(this.stockPriceDatabean, this.stockPriceVo);
                decorator.setDataBean(this.stockPriceVo);
                this.stockPriceService.createStockPrice(decorator);
                if(decorator.getInfo().size() > 0 && decorator.getInfo().containsKey("id")) { decorator.getInfo().put("id", decorator.getInfo().remove("uuId")); }
                decorator.setResponseMessage(this.creativeMessagesConstants.CREATIVE_CREATE_SUCCESS);
                decorator.setReturnCode(this.returnConstants.ReturnCodeInsert);
            }
        } catch(Exception ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
            if(decorator.getErrors().size() == 0) { decorator.getErrors().add(this.returnConstants.TECHNICAL_ISSUE); }
            decorator.setResponseMessage(this.creativeMessagesConstants.CREATIVE_CREATE_FAILURE);
            decorator.setReturnCode(this.returnConstants.ReturnCodeFailure);
        }
        this.clearStockGarbage();
    }

    public void getStockPriceById(ApplicationDecorator decorator) {
        try {
            this.stockPriceDatabean = (StockPriceDatabean) decorator.getDataBean();
            this.stockPriceVo = new StockPriceVo();
            this.stockPriceBeanToVo(this.stockPriceDatabean, this.stockPriceVo);
            decorator.setDataBean(this.stockPriceVo);
            if(this.stockPriceVo != null && this.stockPriceVo.getUuId() != null) {
                this.stockPriceService.getStockPriceById(decorator);
                decorator.setDataBean(this.beanFactory.getVOBean(decorator.getDataBean())); // change from pojo to vo
                decorator.setResponseMessage(this.creativeMessagesConstants.CREATIVE_RETURN_SUCCESS);
                decorator.setReturnCode(this.returnConstants.ReturnCodeGet);
            } else {
                throw new NullPointerException("Not Found UUID For Search...");
            }
        } catch (Exception ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
            if(decorator.getErrors().size() == 0) { decorator.getErrors().add(this.returnConstants.TECHNICAL_ISSUE); }
            decorator.setResponseMessage(this.creativeMessagesConstants.CREATIVE_CREATE_FAILURE);
            decorator.setReturnCode(this.returnConstants.ReturnCodeFailure);
        }
        this.clearStockGarbage();
    }

    public void getStockPriceByRange(ApplicationDecorator decorator) {
        try {
            this.stockPriceService.getStockPriceByRange(decorator);
            this.stockPriceList = (List<StockPrice>) decorator.getDataBean();
            if(this.stockPriceList != null && this.stockPriceList.size() > 0) {
                this.stockPriceVoList = this.stockPriceList.stream().map(stockPrice -> {
                    try { this.stockPriceVo = (StockPriceVo) this.beanFactory.getVOBean(stockPrice); }
                    catch (Exception e) { e.printStackTrace(); }
                    return this.stockPriceVo;
                }).collect(Collectors.toList());
                decorator.setDataBean(this.stockPriceVoList);
                decorator.setResponseMessage(this.creativeMessagesConstants.CREATIVE_RETURN_SUCCESS);
                decorator.setReturnCode(this.returnConstants.ReturnCodeGet);
            } else {
                decorator.setResponseMessage(this.creativeMessagesConstants.CREATIVE_RETURN_FAILURE);
                decorator.setReturnCode(this.returnConstants.ReturnCodeNoDataFound);
            }
        } catch (Exception ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
            if(decorator.getErrors().size() == 0) { decorator.getErrors().add(this.returnConstants.TECHNICAL_ISSUE); }
            decorator.setResponseMessage(this.creativeMessagesConstants.CREATIVE_CREATE_FAILURE);
            decorator.setReturnCode(this.returnConstants.ReturnCodeFailure);
        }
        this.clearStockGarbage();
    }

    public void getStockPriceIdByDateRange(ApplicationDecorator decorator) {
        try {
            StockByRange stockByRange = (StockByRange) decorator.getDataBean();
            stockByRange.setUserUuid(null); // no need this that y we use the stock-instance here
            decorator.setDataBean(stockByRange);
            this.stockPriceService.getStockPriceIdByDateRange(decorator);
            this.stockPriceList = (List<StockPrice>) decorator.getDataBean();
            if(this.stockPriceList != null && this.stockPriceList.size() > 0) {
                this.stockPriceVoList = this.stockPriceList.stream().map(stockPrice -> {
                    try { this.stockPriceVo = (StockPriceVo) this.beanFactory.getVOBean(stockPrice); }
                    catch (Exception e) { e.printStackTrace(); }
                    return this.stockPriceVo;
                }).collect(Collectors.toList());
                decorator.setDataBean(this.stockPriceVoList);
                decorator.setResponseMessage(this.creativeMessagesConstants.CREATIVE_RETURN_SUCCESS);
                decorator.setReturnCode(this.returnConstants.ReturnCodeGet);
            } else {
                decorator.setResponseMessage(this.creativeMessagesConstants.CREATIVE_RETURN_FAILURE);
                decorator.setReturnCode(this.returnConstants.ReturnCodeNoDataFound);
            }
        } catch (Exception ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
            if(decorator.getErrors().size() == 0) { decorator.getErrors().add(this.returnConstants.TECHNICAL_ISSUE); }
            decorator.setResponseMessage(this.creativeMessagesConstants.CREATIVE_CREATE_FAILURE);
            decorator.setReturnCode(this.returnConstants.ReturnCodeFailure);
        }
        this.clearStockGarbage();
    }

    private Boolean hasDecorator(ApplicationDecorator decorator) { return decorator != null; }

    private void stockPriceBeanToVo(StockPriceDatabean stockPriceDatabean, StockPriceVo stockPriceVo) {
         if(stockPriceDatabean.getDate() != null) {
            stockPriceVo.setDate(stockPriceDatabean.getDate());
        } else {
            Date currentDate = new Date();
            stockPriceVo.setDate(currentDate);
        }
        if(stockPriceDatabean.getUuId() != null) { stockPriceVo.setUuId(stockPriceDatabean.getUuId()); }
        if(stockPriceDatabean.getOpenPrice() != null) { stockPriceVo.setOpenPrice(stockPriceDatabean.getOpenPrice()); }
        if(stockPriceDatabean.getHighPrice() != null) { stockPriceVo.setHighPrice(stockPriceDatabean.getHighPrice()); }
        if(stockPriceDatabean.getLowPrice() != null) { stockPriceVo.setLowPrice(stockPriceDatabean.getLowPrice()); }
        if(stockPriceDatabean.getClosePrice() != null) { stockPriceVo.setClosePrice(stockPriceDatabean.getClosePrice()); }
        if(stockPriceDatabean.getWap() != null) { stockPriceVo.setWap(stockPriceDatabean.getWap()); }
        if(stockPriceDatabean.getNoOfShares() != null) { stockPriceVo.setNoOfShares(stockPriceDatabean.getNoOfShares()); }
        if(stockPriceDatabean.getNoOfTrades() != null) { stockPriceVo.setNoOfTrades(stockPriceDatabean.getNoOfTrades()); }
        if(stockPriceDatabean.getTotalTurnover() != null) { stockPriceVo.setTotalTurnover(stockPriceDatabean.getTotalTurnover()); }
        if(stockPriceDatabean.getDeliverableQuantity() != null) { stockPriceVo.setDeliverableQuantity(stockPriceDatabean.getDeliverableQuantity()); }
        if(stockPriceDatabean.getDeliQtyToTradedQty() != null) { stockPriceVo.setDeliQtyToTradedQty(stockPriceDatabean.getDeliQtyToTradedQty()); }
        if(stockPriceDatabean.getSpreadHighLow() != null) { stockPriceVo.setSpreadHighLow(stockPriceDatabean.getSpreadHighLow()); }
        if(stockPriceDatabean.getSpreadCloseOpen() != null) { stockPriceVo.setSpreadCloseOpen(stockPriceDatabean.getSpreadCloseOpen()); }
        logger.debug("Bean To Vo =====> " + stockPriceVo);
    }

    public void clearStockGarbage() {
        this.stockPriceDatabean = null;
        this.stockPriceVo = null;
        this.stockPriceVoList = null;
        this.stockPriceList = null;
    }


}
