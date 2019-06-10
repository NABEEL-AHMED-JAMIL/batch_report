package com.ballistic.batch_report.service;

import com.ballistic.batch_report.coredel.dao.ApplicationDAO;
import com.ballistic.batch_report.coredel.pojo.StockPrice;
import com.ballistic.batch_report.coredel.query.FieldQuery;
import com.ballistic.batch_report.coredel.query.LocalQuery;
import com.ballistic.batch_report.model.ApplicationDecorator;
import com.ballistic.batch_report.model.databeans.StockByRange;
import com.ballistic.batch_report.util.BeanFactory;
import com.ballistic.batch_report.util.CommonUtil;
import com.ballistic.batch_report.util.DateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Scope("prototype")
@SuppressWarnings("unchecked")
public class StockPriceService {

    public static final Logger logger = LogManager.getLogger(StockPriceService.class);

    private final static String _ID = "_id";
    private final static String ID = "id";
    private final static String CREATE_DATE = "createDate";

    private @Autowired ApplicationDAO applicationDAO;
    private @Autowired BeanFactory beanFactory;
    private @Autowired DateUtil dateUtil;
    private @Autowired CommonUtil commonUtil;

    private StockPrice stockPrice;
    private StockByRange stockByRange;
    private List<StockPrice> stockPricesList;

    public void createStockPrice(ApplicationDecorator decorator) throws Exception {
        this.stockPrice = (StockPrice) this.beanFactory.getPojoBean(decorator.getDataBean());
        this.stockPrice.setCreateDate(new Date());
        this.stockPrice.setUpdateDate(new Date());
        this.applicationDAO.save(this.stockPrice);
        decorator.setDataBean(this.stockPrice);
        decorator.getInfo().put(ID, this.commonUtil.getId(this.stockPrice) + "");
        logger.debug("save successful.");
    }

    public void getStockPriceById(ApplicationDecorator decorator) throws Exception {
        this.stockPrice = (StockPrice) this.beanFactory.getPojoBean(decorator.getDataBean());
        this.stockPrice = (StockPrice) this.applicationDAO.findById(this.stockPrice.getClass(), this.stockPrice.getUuId());
        if(this.stockPrice != null) {
            decorator.setDataBean(this.stockPrice);
            decorator.getInfo().put(ID, this.commonUtil.getId(this.stockPrice) + "");
            logger.debug("found successful.");
        } else {
            throw new Exception("Data Not Found Exception.");
        }
    }

    public void getStockPriceByRange(ApplicationDecorator decorator) throws Exception {
        this.stockByRange = (StockByRange) decorator.getDataBean();
        this.stockPricesList = null; // make null before process
        if(this.dateUtil.isDateValid(this.stockByRange.getStartDate(), this.stockByRange.getEndDate())) {
            LocalQuery localQuery = new LocalQuery();
            this.fillLocaleQueryForStockPriceRange(localQuery, 1);
            this.stockPricesList = this.applicationDAO.findByMQL(StockPrice.class, localQuery);
            decorator.setDataBean(this.stockPricesList);
            logger.debug("found successful :- " + this.stockPricesList.size());
        } else {
            throw new Exception("Invalid Request.");
        }
    }

    public void getStockPriceIdByDateRange(ApplicationDecorator decorator) throws Exception {
        this.stockByRange = (StockByRange) decorator.getDataBean();
        if(this.dateUtil.isDateValid(this.stockByRange.getStartDate(), this.stockByRange.getEndDate())) {
            LocalQuery localQuery = new LocalQuery();
            this.fillLocaleQueryForStockPriceRange(localQuery, 2);
            this.stockPricesList = this.applicationDAO.findByMQL(StockPrice.class, localQuery);
            decorator.setDataBean(this.stockPricesList);
            logger.info("found successful :- " + this.stockPricesList.size());
        } else {
            throw new Exception("Invalid Request.");
        }
    }

    private void fillLocaleQueryForStockPriceRange(LocalQuery localQuery, Integer queryType) {
        Set<FieldQuery> fieldQueries = new HashSet<>();
        FieldQuery startDateField = new FieldQuery(CREATE_DATE, 4, this.stockByRange.getStartDate());
        FieldQuery endDateDateField = new FieldQuery(CREATE_DATE, 3, this.stockByRange.getEndDate());

        if(queryType == 1) {
            if(this.stockByRange.getUserUuid() != null && this.stockByRange.getUserUuid().size() > 0) {
                FieldQuery stockUidField = new FieldQuery(_ID, 6, this.stockByRange.getUserUuid());
                fieldQueries.add(stockUidField);
            }
        } else if (queryType == 2) {
            Set<String> projection = new HashSet<>();
            projection.add(_ID);
            projection.add(CREATE_DATE);
            localQuery.setProjection(projection);
            localQuery.setFilterApply(false);
        }

        fieldQueries.add(startDateField);
        fieldQueries.add(endDateDateField);
        localQuery.setFilter(fieldQueries);
    }
}
