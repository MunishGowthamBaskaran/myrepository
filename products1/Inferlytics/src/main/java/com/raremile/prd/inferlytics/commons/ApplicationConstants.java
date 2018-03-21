package com.raremile.prd.inferlytics.commons;

public final class ApplicationConstants {

	/**
	 * HTTP Methods
	 */
	public static final int GET_METHOD = 1;

	public static final int POST_METHOD = 2;
	/**
	 * Property FileNames
	 */
	public static final String LEXICONDB_PROPERTIES_FILE = "lexicondb.properties";
	public static final String APPLICATION_PROPERTIES_FILE = "application.properties";
	public static final String MONGO_PROPERTIES_FILE="mongo.properties";
	public static final String USERDETAILS_PROPERTIES_FILE="userCredentialsDB.properties";
	/**
	 * DB COnstants
	 */

	public static final String DB_USERNAME = "DB_USERNAME";

	public static final String DB_PASSWORD = "DB_PASSWORD";
	public static final String DRIVER_CLASS_NAME = "DRIVER_CLASS_NAME";
	public static final String DB_DOMAIN = "DB_DOMAIN";
	public static final String DB_URL = "DB_URL";
	public static final String DB_JNDINAME = "DB_JNDINAME";
	public static final String DB_MAXACTIVE = "DB_MAXACTIVE";
	public static final String DB_MAXIDLE = "DB_MAXIDLE";
	public static final String DB_INITIALSIZE = "DB_INITIALSIZE";
	public static final String DB_MAXWAIT = "DB_MAXWAIT";
	public static final String DB_REMOVEABANDONEDTIMEOUT = "DB_REMOVEABANDONEDTIMEOUT";
	public static final String DB_MINEVICTABLEIDLETIMEMILLIS = "DB_MINEVICTABLEIDLETIMEMILLIS";
	public static final String DB_MINIDLE = "DB_MINIDLE";
	public static final String INITIAL_NUM_CONNECTIONS="INITIAL_NUM_CONNECTIONS";
	public static final String MAXIMUM_NUM_CONNECTIONS="MAXIMUM_NUM_CONNECTIONS";
	/**
	 * lua scripts paths
	 */

	public static final String LUA_GETMULTIWORDSOFUNIGRAM="DBScripts/getmultiwordsOfUnigram.lua";

	public static final String LUA_GETMULTIWORDSCOREBYPOSITION="DBScripts/getMultiwordScorebyPosition.lua";
	public static final String LUA_GETUNIGRAMSCORE="DBScripts/getUnigramScore.lua";
	public static final String LUA_GETUNIGRAMSCOREWOPATTERN="DBScripts/getUnigramScoreWoPattern.lua";
	public static final String LUA_GETPRODUCTDETAILS="DBScripts/getProductDetails.lua";
	/**
	 * mongo related constants
	 */

	public static final String MONGO_DB="MONGO_DB";

	public static final String MONGO_HOST="MONGO_HOST";
	public static final String MONGO_PORT="MONGO_PORT";	
	public static final String MONGO_HOST2="MONGO_HOST2";

	public static final String MONGO_PORT2="MONGO_PORT2";
	public static final String MONGO_HOST3="MONGO_HOST3";

	public static final String MONGO_PORT3="MONGO_PORT3";
	public static final String MONGO_CONNECTION_POOL_SIZE = "CONNECTION_POOL_SIZE";
	public static final String MAX_WAIT_TIME = "MAX_WAIT_TIME";
	public static final String SOCKET_TIME_OUT = "SOCKET_TIME_OUT";
	public static final String CONNECT_TIMEOUT = "CONNECT_TIMEOUT";
	public static final String MAX_CONNECTION_IDLE_TIME = "MAX_CONNECTION_IDLE_TIME";
	public static final String MIN_CONNECTIONS_PER_HOST = "MIN_CONNECTIONS_PER_HOST";


	/**
	 * misc constants
	 */
	public static final String BATCH = "BATCH";
	public static final String APPLICATION = "APPLICATION";
	/**
	 * AnalyseSentiment Thread Constants
	 */
	public static final String FEED_THRESHOLD = "FEED_THRESHOLD";

	/**
	 * Cron Scheduler regex
	 */
	public static final String CRON_REGEX="SCHEDULER_REGEX";

	/**
	 * Application related
	 */
	public static final String PRODUCTION_DOMAIN = "PRODUCTION_DOMAIN";




	private ApplicationConstants() {

	}
}
