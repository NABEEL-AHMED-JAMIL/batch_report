package com.ballistic.batch_report.coredel;

import com.mongodb.ReadPreference;
import com.mongodb.event.ClusterClosedEvent;
import com.mongodb.event.ClusterDescriptionChangedEvent;
import com.mongodb.event.ClusterListener;
import com.mongodb.event.ClusterOpeningEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MongoClusterListener  implements ClusterListener {

    public static final Logger logger = LogManager.getLogger(MongoClusterListener.class);

    private final ReadPreference readPreference;
    private boolean isWritable;
    private boolean isReadable;

    public MongoClusterListener(final ReadPreference readPreference) {
        this.readPreference = readPreference;
    }

    @Override
    public void clusterOpening(final ClusterOpeningEvent clusterOpeningEvent) {
        logger.debug(String.format("Cluster with unique client identifier %s opening", clusterOpeningEvent.getClusterId()));
    }

    @Override
    public void clusterClosed(final ClusterClosedEvent clusterClosedEvent) {
        logger.debug(String.format("Cluster with unique client identifier %s closed", clusterClosedEvent.getClusterId()));
    }

    @Override
    public void clusterDescriptionChanged(final ClusterDescriptionChangedEvent event) {
        if (!isWritable) {
            if (event.getNewDescription().hasWritableServer()) {
                isWritable = true;
                logger.debug("Writable server available!");
            }
        } else {
            if (!event.getNewDescription().hasWritableServer()) {
                isWritable = false;
                logger.error("No writable server available!");
            }
        }

        if (!isReadable) {
            if (event.getNewDescription().hasReadableServer(readPreference)) {
                isReadable = true;
                logger.debug("Readable server available!");
            }
        } else {
            if (!event.getNewDescription().hasReadableServer(readPreference)) {
                isReadable = false;
                logger.error("No readable server available!");
            }
        }
    }
}
