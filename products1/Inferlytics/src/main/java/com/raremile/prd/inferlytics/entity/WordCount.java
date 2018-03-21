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

/**
 * @author mallikarjuna
 * @created 06-Jun-2014
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class WordCount {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(WordCount.class);
	private String name;
	private int count;

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @return the word
	 */
	public String getWord() {
		return name;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @param word
	 *            the word to set
	 */
	public void setWord(String word) {
		this.name = word;
	}
}
