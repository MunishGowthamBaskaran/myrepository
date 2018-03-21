/**
 *  * Copyright (c) 2013 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.preprocessing;

import java.util.ArrayList;
import java.util.List;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.entity.WordsPerSentence;


/**
 * @author Pratyusha
 * @created Jun 12, 2013
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class DataGenerationThread extends Thread {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(DataGenerationThread.class);
	private static List<WordsPerSentence> wordsPerSentenceList;



	/**
	 * @return the wordsPerSentenceList
	 */
	public List<WordsPerSentence> getWordsPerSentenceList() {
		return wordsPerSentenceList;
	}


	public static synchronized void addAllToWordSentList(
			List<WordsPerSentence> wpsList) {
		if (wordsPerSentenceList == null) {
			wordsPerSentenceList = new ArrayList<WordsPerSentence>();
		}
		wordsPerSentenceList.addAll(wpsList);
		if (wordsPerSentenceList.size() >= 50) {
			LOG.info("wordsPerSentenceList is greater than or equal to 50 now. "
					+ wordsPerSentenceList.size());
			List<WordsPerSentence> localwordsPerSentenceList = new ArrayList<WordsPerSentence>();
			localwordsPerSentenceList.addAll(wordsPerSentenceList);
			wordsPerSentenceList = null;
			DataInsertionThread dit = new DataInsertionThread();
			dit.setWordsPerSentenceList(localwordsPerSentenceList);
			dit.start();
		}
	}


}

class DataInsertionThread extends Thread {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(DataInsertionThread.class);
	private List<WordsPerSentence> wordsPerSentenceList;


	/**
	 * @param wordsPerSentenceList
	 *            the wordsPerSentenceList to set
	 */
	public void setWordsPerSentenceList(
			List<WordsPerSentence> wordsPerSentenceList) {
		this.wordsPerSentenceList = wordsPerSentenceList;
	}

	/**
	 * @return the wordsPerSentenceList
	 */
	public List<WordsPerSentence> getWordsPerSentenceList() {
		return wordsPerSentenceList;
	}


	@Override
	public void run() {
		LOG.debug("Method: run called.");

		if (wordsPerSentenceList != null) {
			DAOFactory
					.getInstance(
								ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
						.getLexiconDAO().storePatterns(wordsPerSentenceList);

				}
	}
}

