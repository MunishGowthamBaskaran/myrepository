package com.raremile.prd.inferlytics.database;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.commons.FilePropertyManager;

public class MongoConnection {

	static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(MongoConnection.class);

	protected static MongoClient mongoClient = null;
	protected static DB db = null;
	static {
		List<ServerAddress> addrs = new ArrayList<>();
		try {

			String primaryHost = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_HOST);
			String primaryPort = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_PORT);
			// secondary
			String secondaryHost = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_HOST2);
			String secondaryPort = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_PORT2);
			// arbitary
			String arbitaryHost = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_HOST3);
			String arbitaryPort = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_PORT3);

			addrs.add(new ServerAddress(primaryHost, Integer
					.parseInt(primaryPort)));
			if (!secondaryHost.isEmpty()) {
				addrs.add(new ServerAddress(secondaryHost, Integer
						.parseInt(secondaryPort)));
			}
			if (!arbitaryHost.isEmpty()) {
				addrs.add(new ServerAddress(arbitaryHost, Integer
						.parseInt(arbitaryPort)));
			}
		} catch (NumberFormatException | UnknownHostException e) {
			LOG.error(
					"Error occured while adding server address to mongoclient",
					e);
		}

		int connectionsPerHost = Integer.parseInt(FilePropertyManager
				.getProperty(ApplicationConstants.MONGO_PROPERTIES_FILE,
						ApplicationConstants.MONGO_CONNECTION_POOL_SIZE));
		int socketTimeout = Integer.parseInt(FilePropertyManager.getProperty(
				ApplicationConstants.MONGO_PROPERTIES_FILE,
				ApplicationConstants.SOCKET_TIME_OUT));
		int maxWaitTime = Integer.parseInt(FilePropertyManager.getProperty(
				ApplicationConstants.MONGO_PROPERTIES_FILE,
				ApplicationConstants.MAX_WAIT_TIME));
		int connectTimeout = Integer.parseInt(FilePropertyManager
				.getProperty(ApplicationConstants.MONGO_PROPERTIES_FILE,
						ApplicationConstants.CONNECT_TIMEOUT));

		int maxConnectionIdleTime = Integer.parseInt(FilePropertyManager
				.getProperty(ApplicationConstants.MONGO_PROPERTIES_FILE,
						ApplicationConstants.MAX_CONNECTION_IDLE_TIME));
		int minConnectionsPerHost = Integer.parseInt(FilePropertyManager
				.getProperty(ApplicationConstants.MONGO_PROPERTIES_FILE,
						ApplicationConstants.MIN_CONNECTIONS_PER_HOST));
		MongoClientOptions options = new MongoClientOptions.Builder()
		.connectionsPerHost(connectionsPerHost)
		.minConnectionsPerHost(minConnectionsPerHost)
		.socketTimeout(socketTimeout).maxWaitTime(maxWaitTime)
		.connectTimeout(connectTimeout)
		.maxConnectionIdleTime(maxConnectionIdleTime).build();

		mongoClient = new MongoClient(addrs, options);

		mongoClient.setReadPreference(ReadPreference.nearest());

	}

	/**
	 * @param database2
	 * @return
	 * @throws UnknownHostException
	 */
	protected static DB getMongoDB(String database) throws UnknownHostException {
		/*
		 * if (db == null) { db = mongoClient.getDB(database); }
		 */

		return mongoClient.getDB(database);
	}

	public MongoConnection() {
		super();
	}

}