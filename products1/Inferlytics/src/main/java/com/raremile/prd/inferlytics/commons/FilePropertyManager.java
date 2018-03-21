package com.raremile.prd.inferlytics.commons;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.raremile.prd.inferlytics.exception.CriticalException;



/**
 * Utility class to read property files.
 * @author
 */
public final class FilePropertyManager {

	private FilePropertyManager() {

	}

	private static final Logger LOG = Logger
			.getLogger(FilePropertyManager.class);
	private static Map<String, Properties> propertyFilemap = new HashMap<String, Properties>();

	/**
	 * Returns a property from a properties file.
	 * @param filename
	 * @param key
	 * @return Value of the key in the file. If the key is not present then
	 *         <code>null</code> is returned
	 */
	public static String getProperty(final String fileName, final String key) {
		try {
			if (propertyFilemap.get(fileName) == null) {
				// load the property file
				loadPropertyFile(fileName);
			}
			Properties prop = propertyFilemap.get(fileName);
			if (prop.getProperty(key) != null) {
				return prop.getProperty(key);
			} else {
				LOG.error("Property " + key + " not present in " + fileName);
				throw new CriticalException("Property " + key
						+ " not present in " + fileName);
			}
		} catch (CriticalException nce) {
			LOG.error("Exception while reading property: " + key + ": "
					+ nce.getMessage());
			return null;
		}
	}

	private static void loadPropertyFile(final String fileName)
			throws CriticalException {
		try {
			Properties prop = new Properties();
			prop.load(FilePropertyManager.class.getClassLoader()
					.getResourceAsStream(fileName));
			propertyFilemap.put(fileName, prop);
		} catch (IOException ioe) {
			LOG.error(ioe.getMessage());
			throw new CriticalException(ioe.getMessage());
		}
	}

	public static Properties fetchPropertyFile(final String fileName)
			throws CriticalException {
		try {
			Properties prop = new Properties();
			prop.load(FilePropertyManager.class.getClassLoader()
					.getResourceAsStream(fileName));
			return prop;
		} catch (IOException ioe) {
			LOG.error(ioe.getMessage());
			throw new CriticalException(ioe.getMessage());
		}
	}
}