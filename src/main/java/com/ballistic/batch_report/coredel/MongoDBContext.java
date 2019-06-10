package com.ballistic.batch_report.coredel;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class MongoDBContext {

    public static final Logger logger = LogManager.getLogger(MongoDBContext.class);

    private Datastore datastore;
    private MongoClient mongoClient;

    public MongoDBContext(String dbNames, String hosts) throws Exception {

        logger.info(">============Start DB Connection============<");
        try {
            MongoClientOptions options = MongoClientOptions.builder()
                    .connectionsPerHost(10).connectTimeout(60000)
                    .socketTimeout(60000).maxWaitTime(1000).maxConnectionIdleTime(60000)
                    .addCommandListener(new MongoCmListener()).addClusterListener (new MongoClusterListener(ReadPreference.secondary()))
                    .readPreference(ReadPreference.secondaryPreferred()).build();
            this.mongoClient = new MongoClient(hosts, options);
            Morphia morphia= new Morphia();
            this.datastore = morphia.createDatastore(this.mongoClient, dbNames);
            this.datastore.ensureIndexes();
            logger.info(">============SDatabase connection Successful============<");
        } catch (Exception ex) {
            logger.error(">============Database connection Fail============<");
            logger.error("*********** Exception ***********", ex.getMessage());
            throw ex;
        }
    }

    public Datastore getDatastore() { return datastore; }


}
