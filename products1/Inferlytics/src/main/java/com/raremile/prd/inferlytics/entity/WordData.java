/**
 *  * Copyright (c) 2014 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mallikarjuna
 * @created 22-Jul-2014
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class WordData {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(WordData.class);
	private String word;
	private List<BeeGoodTopicData> wordStats = new ArrayList<>();

	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * @return the wordStats
	 */
	public List<BeeGoodTopicData> getWordStats() {
		return wordStats;
	}

	/**
	 * @param word
	 *            the word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * @param wordStats
	 *            the wordStats to set
	 */
	public void setWordStats(List<BeeGoodTopicData> wordStats) {
		this.wordStats = wordStats;
	}

}
