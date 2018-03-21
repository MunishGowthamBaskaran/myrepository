package com.raremile.prd.inferlytics.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.commons.FilePropertyManager;
import com.raremile.prd.inferlytics.exception.DAOConfigurationException;



/**
 * This class represents a DAO factory for a SQL database. You can use
 * {@link #getInstance(String)} to obtain a new instance for the given database
 * name. The specific instance returned depends on the properties file
 * configuration. You can obtain DAO's for the DAO factory instance using the
 * DAO getters.
 * <p>
 * This class requires a properties file named 'dao.properties' in the classpath
 * with among others the following properties:
 * 
 * <pre>
 * name.url *
 * name.driver
 * name.username
 * name.password
 * </pre>
 * 
 * Those marked with * are required, others are optional and can be left away or
 * empty. Only the username is required when any password is specified.
 * <ul>
 * <li>The 'name' must represent the database name in
 * {@link #getInstance(String)}.</li>
 * <li>The 'name.url' must represent either the JDBC URL or JNDI name of the
 * database.</li>
 * <li>The 'name.driver' must represent the full qualified class name of the
 * JDBC driver.</li>
 * <li>The 'name.username' must represent the username of the database login.</li>
 * <li>The 'name.password' must represent the password of the database login.</li>
 * </ul>
 * If you specify the driver property, then the url property will be assumed as
 * JDBC URL. If you omit the driver property, then the url property will be
 * assumed as JNDI name. When using JNDI with username/password preconfigured,
 * you can omit the username and password properties as well.
 * <p>
 * Here are basic examples of valid properties for a database with the name
 * 'rm_lexicondb':
 * 
 * <pre>
 * javabase.jdbc.url = jdbc:mysql://localhost:3306/rm_lexicondb
 * javabase.jdbc.driver = com.mysql.jdbc.Driver
 * javabase.jdbc.username = root
 * javabase.jdbc.password = root
 * </pre>
 * 
 * <pre>
 * javabase.jndi.url = jdbc / rm_lexicondb
 * </pre>
 * 
 * Here is a basic use example:
 * 
 * <pre>
 * DAOFactory lexicon = DAOFactory.getInstance(&quot;rm_lexicondb.jdbc&quot;);
 * UserDAO userDAO = lexicon.getUserDAO();
 * </pre>
 * 
 * @author Pratyusha
 * 
 */
public abstract class DAOFactory {

	// Constants ----------------------------------------------------------------------------------


	// Actions ------------------------------------------------------------------------------------

	/**
	 * Returns a new DAOFactory instance for the given database name.
	 * @param name The database name to return a new DAOFactory instance for.
	 * @return A new DAOFactory instance for the given database name.
	 * @throws DAOConfigurationException If the database name is null, or if the properties file is
	 * missing in the classpath or cannot be loaded, or if a required property is missing in the
	 * properties file, or if either the driver cannot be loaded or the datasource cannot be found.
	 */
	public static DAOFactory getInstance(String name) throws DAOConfigurationException {
		if (name == null) {
			throw new DAOConfigurationException("Database name is null.");
		}


		String url = FilePropertyManager.getProperty(name, ApplicationConstants.DB_URL);
		String driverClassName = FilePropertyManager.getProperty(name, ApplicationConstants.DRIVER_CLASS_NAME);
		String password = FilePropertyManager.getProperty(name, ApplicationConstants.DB_PASSWORD);
		String username = FilePropertyManager.getProperty(name, ApplicationConstants.DB_USERNAME);
		DAOFactory instance;

		// If driver is specified, then load it to let it register itself with DriverManager.
		if (driverClassName != null) {
			try {
				Class.forName(driverClassName);
			} catch (ClassNotFoundException e) {
				throw new DAOConfigurationException(
						"Driver class '" + driverClassName + "' is missing in classpath.", e);
			}
			instance = new DriverManagerDAOFactory(url, username, password);
		}

		// Else assume URL as DataSource URL and lookup it in the JNDI.
		else {
			DataSource dataSource;
			try {
				dataSource = (DataSource) new InitialContext().lookup(url);
			} catch (NamingException e) {
				throw new DAOConfigurationException(
						"DataSource '" + url + "' is missing in JNDI.", e);
			}
			if (username != null) {
				instance = new DataSourceWithLoginDAOFactory(dataSource, username, password);
			} else {
				instance = new DataSourceDAOFactory(dataSource);
			}
		}

		return instance;
	}

	/*
	 * public static DAOFactory getMongoInstance(String name) throws
	 * DAOConfigurationException, UnknownHostException { if (name == null) {
	 * throw new DAOConfigurationException("Database name is null."); }
	 * 
	 * DAOFactory instance = null;
	 * 
	 * MongoClient mongoClient = new MongoClient("localhost", 27017); DB db =
	 * mongoClient.getDB("test"); // boolean auth = db.authenticate("username",
	 * "password".toCharArray()); DBCollection table = db.getCollection("user");
	 * BasicDBObject document = new BasicDBObject(); document.put("name",
	 * "mkyong"); document.put("age", 30); document.put("createdDate", new
	 * Date()); table.insert(document); return instance; }
	 */

	/**
	 * Returns a connection to the database. Package private so that it can be used inside the DAO
	 * package only.
	 * @return A connection to the database.
	 * @throws SQLException If acquiring the connection fails.
	 */
	abstract Connection getConnection() throws SQLException;

	// DAO implementation getters -----------------------------------------------------------------

	public DAOAnalyticsData getDAOAnalyticsData(){
		return new DAOAnalyticsData(this);
	}

	public DaoConnector getDaoConnector() {
		return new DaoConnector(this);
	}
	public DAOUtility getDaoUtility(){
		return new DAOUtility(this);
	}
	/**
	 * Returns the Lexicon DAO associated with the current DAOFactory.
	 * 
	 * @return The Lexicon DAO associated with the current DAOFactory.
	 */
	public LexiconCorrectionDAOInterface getLexiconCorrectionDAO() {
		return new LexiconDAOCorrectionImpl(this);
	}

	/**
	 * Returns the Lexicon DAO associated with the current DAOFactory.
	 * 
	 * @return The Lexicon DAO associated with the current DAOFactory.
	 */
	public LexiconDAOInterface getLexiconDAO() {
		return new LexiconDAOImpl(this);
	}

	/**
	 * Returns the Lexicon Generation DAO associated with the current
	 * DAOFactory.
	 * 
	 * @return The Lexicon DAO associated with the current DAOFactory.
	 */
	public LexiconDAOGenerationInterface getLexiconGenerationDAO() {
		return new LexiconDAOGenerationImpl(this);
	}

	/**
	 * Returns the Sentiment DAO associated with the current DAOFactory.
	 * 
	 * @return The Sentiment DAO associated with the current DAOFactory.
	 */
	public SentimentDAOInterface getSentimentDAO() {
		return new SentimentDAOImpl(this);
	}
	// You can add more DAO implementation getters here.
	/**
	 * Returns the SentimentWidget DAO associated with the current DAOFactory.
	 * 
	 * @return The SentimentWidget DAO associated with the current DAOFactory.
	 */
	public SentimentDAOWidgetImpl getSentimentWidgetDAO() {
		return new SentimentDAOWidgetImpl(this);
	}
	// You can add more DAO implementation getters here.

	public Validator getValidator(){
		return new Validator(this);
	}
}

//Default DAOFactory implementations -------------------------------------------------------------


/**
 * The DataSource based DAOFactory.
 */
class DataSourceDAOFactory extends DAOFactory {
	private final DataSource dataSource;

	DataSourceDAOFactory(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}

/**
 * The DataSource-with-Login based DAOFactory.
 */
class DataSourceWithLoginDAOFactory extends DAOFactory {
	private final DataSource dataSource;
	private final String username;
	private final String password;

	DataSourceWithLoginDAOFactory(DataSource dataSource, String username, String password) {
		this.dataSource = dataSource;
		this.username = username;
		this.password = password;
	}

	@Override
	Connection getConnection() throws SQLException {
		return dataSource.getConnection(username, password);
	}

}

/**
 * The DriverManager based DAOFactory.
 */
class DriverManagerDAOFactory extends DAOFactory {
	private final String url;
	private final String username;
	private final String password;

	DriverManagerDAOFactory(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	@Override
	Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}

}