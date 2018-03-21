/**
 *  * Copyright (c) 2013 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.database;

import java.util.List;
import java.util.Map;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.commons.Sentiword;
import com.raremile.prd.inferlytics.entity.Feed;
import com.raremile.prd.inferlytics.entity.WordPatternScore;



/**
 * @author pratyusha
 * @created 09-Jul-2013
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class InsertThread extends Thread {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(InsertThread.class);

	private String dbMethodToInvoke;

	private List<Feed> feeds;

	private String searchText;

	private List<WordPatternScore> wpsList;

	private List<Sentiword> sentiList;

	private Map<String, Double> lexiconMap;

	private boolean isPostStaging;

	/**
	 * @return the isPostStaging
	 */
	public boolean isPostStaging() {
		return isPostStaging;
	}

	/**
	 * @param isPostStaging the isPostStaging to set
	 */
	public void setPostStaging(boolean isPostStaging) {
		this.isPostStaging = isPostStaging;
	}

	@Override
	public void run() {
		if (dbMethodToInvoke.equals("storePostBatch")) {
		DAOFactory.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getLexiconDAO().storePostBatch(feeds, searchText, isPostStaging);
		} else if (dbMethodToInvoke.equals("storeSentiwordPatterns")) {
			DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getLexiconDAO().storeSentiwordPatterns(sentiList);
		} else if (dbMethodToInvoke.equals("storeWordPattern")) {
			DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getLexiconDAO().storeWordPattern(null, wpsList, null);

		} else if (dbMethodToInvoke.equals("storeLexicon")) {
			DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getLexiconGenerationDAO().storeLexicon(lexiconMap);
		} else if (dbMethodToInvoke.equals("storePostsIntoStaging")) {
			DAOFactory
			.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE).getSentimentDAO().storePostsIntoStaging(feeds, null, null, null);
		}
	}

	/**
	 * @param dbMethodToInvoke
	 *            the dbMethodToInvoke to set
	 */
	public void setDbMethodToInvoke(String dbMethodToInvoke) {
		this.dbMethodToInvoke = dbMethodToInvoke;
	}

	/**
	 * @param feeds
	 *            the feeds to set
	 */
	public void setFeeds(List<Feed> feeds) {
		this.feeds = feeds;
	}

	/**
	 * @param searchText
	 *            the searchText to set
	 */
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	/**
	 * @param wpsList
	 *            the wpsList to set
	 */
	public void setWpsList(List<WordPatternScore> wpsList) {
		this.wpsList = wpsList;
	}

	/**
	 * @param sentiList
	 *            the sentiList to set
	 */
	public void setSentiList(List<Sentiword> sentiList) {
		this.sentiList = sentiList;
	}

	/**
	 * @param lexiconMap
	 *            the lexiconMap to set
	 */
	public void setLexiconMap(Map<String, Double> lexiconMap) {
		this.lexiconMap = lexiconMap;
	}
}
