package com.ballistic.batch_report.coredel;

import com.mongodb.event.CommandFailedEvent;
import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;
import com.mongodb.event.CommandSucceededEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MongoCmListener implements CommandListener {

    public static final Logger logger = LogManager.getLogger(MongoClusterListener.class);

    @Override
    public void commandStarted(CommandStartedEvent event) {
        logger.debug(String.format("Sent command '%s:%s' with id %s to database '%s' " + "on connection '%s' to server '%s'", event.getCommandName(), event.getCommand().get(event.getCommandName()), event.getRequestId(), event.getDatabaseName(), event.getConnectionDescription().getConnectionId(), event.getConnectionDescription().getServerAddress()));
    }

    @Override
    public void commandSucceeded(CommandSucceededEvent event) {
        logger.debug(String.format("Successfully executed command '%s' with id %s " + "on connection '%s' to server '%s'", event.getCommandName(), event.getRequestId(), event.getConnectionDescription().getConnectionId(), event.getConnectionDescription().getServerAddress()));
    }

    @Override
    public void commandFailed(CommandFailedEvent event) {
        logger.debug(String.format("Failed execution of command '%s' with id %s " + "on connection '%s' to server '%s' with exception '%s'", event.getCommandName(), event.getRequestId(), event.getConnectionDescription().getConnectionId(), event.getConnectionDescription().getServerAddress(), event.getThrowable()));
    }
}
