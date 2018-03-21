/**
 *  * Copyright (c) 2013 RareMile Technologies. 
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
 * @author Praty
 * @created Apr 23, 2013
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class IdWordList {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(IdWordList.class);

	private List<Integer> idList;
	private List<String> wordList;
	private String productName;

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	/**
	 * @param wordList the wordList to set
	 */
	public void setWordList(List<String> wordList) {
		this.wordList = wordList;
	}
	/**
	 * @return the wordList
	 */
	public List<String> getWordList() {
		return wordList;
	}
	/**
	 * @param idList the idList to set
	 */
	public void setIdList(List<Integer> idList) {
		this.idList = idList;
	}
	/**
	 * @return the idList
	 */
	public List<Integer> getIdList() {
		return idList;
	}

	public void addToId(int id) {
		if (null != idList) {
			idList.add(id);
		}
	}

	public void addToWord(String word) {
		if (null != wordList) {
			wordList.add(word);
		}
	}

	public int getIdByWord(String word) {
		int idToBeReturned = 0;
		if (null != idList && null != wordList) {
			int index = wordList.indexOf(word);
			if (index != -1) {
				idToBeReturned = idList.get(index);
			} else {
				idToBeReturned = -1;
			}
		}
		return idToBeReturned;
	}

	public boolean doesWordExist(String word) {
		boolean doesWordExist = false;
		if (null != wordList) {
			if (wordList.contains(word)) {
				doesWordExist = true;
			}
		}
		return doesWordExist;
	}

	public IdWordList() {
		idList = new ArrayList<Integer>();
		wordList = new ArrayList<String>();
	}

}
