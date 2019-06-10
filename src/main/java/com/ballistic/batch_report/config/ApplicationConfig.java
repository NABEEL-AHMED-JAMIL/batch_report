package com.ballistic.batch_report.config;

import com.ballistic.batch_report.async.AsyncDALTaskExecutor;
import com.ballistic.batch_report.coredel.MongoDBContext;
import com.ballistic.batch_report.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ApplicationConfig {

    public static final Logger logger = LogManager.getLogger(ApplicationConfig.class);

    private @Autowired Constants constants;

    @Bean @Scope("singleton")
    public MongoDBContext getMongoDBContext() throws Exception {
        MongoDBContext mongoDBContext = null;
        logger.debug("===============Application-DAO-INIT===============");
        mongoDBContext = new MongoDBContext(this.constants.getDB_NAME(),this.constants.getHOST());
        logger.debug("===============Application-DAO-END===============");
        return mongoDBContext;
    }

    @Bean @Scope("singleton")
    public AsyncDALTaskExecutor getAsyncTaskExecutor() throws Exception {
        AsyncDALTaskExecutor taskExecutor = null;
        logger.debug("===============Application-DAO-INIT===============");
        taskExecutor = new AsyncDALTaskExecutor(this.constants.getMinThreads(), this.constants.getMaxThreads(), this.constants.getIdleThreadLife());
        logger.debug("===============Application-DAO-END===============");
        return taskExecutor;
    }

}