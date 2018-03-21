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
 * @created 09-Jun-2014
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class FeatureWords {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(FeatureWords.class);

	private String feature;
	private List<WordCount> words = new ArrayList<>();
	private String firstWord;
	private int count;

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}



	/**
	 * @return the feature
	 */
	public String getFeature() {
		return feature;
	}



	/**
	 * @return the firstWord
	 */
	public String getFirstWord() {
		return firstWord;
	}

	/**
	 * @return the words
	 */
	public List<WordCount> getWords() {
		return words;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @param feature the feature to set
	 */
	public void setFeature(String feature) {
		this.feature = feature;
	}

	/**
	 * @param firstWord
	 *            the firstWord to set
	 */
	public void setFirstWord(String firstWord) {
		this.firstWord = firstWord;
	}

	public void setWords(List<WordCount> words) {
		this.words = words;
	}
}
