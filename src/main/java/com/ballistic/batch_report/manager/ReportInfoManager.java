package com.ballistic.batch_report.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by AdMaxim on 6/8/2019.
 */
@Component
@Scope("prototype")
@SuppressWarnings("unchecked")
public class ReportInfoManager {

    public static final Logger logger = LogManager.getLogger(StockPriceManager.class);
}
