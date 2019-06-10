package com.ballistic.batch_report.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Created by AdMaxim on 6/8/2019.
 */
@Service
@Scope("prototype")
@SuppressWarnings("unchecked")
public class ReportInfoService {

    public static final Logger logger = LogManager.getLogger(ReportInfoService.class);
}
