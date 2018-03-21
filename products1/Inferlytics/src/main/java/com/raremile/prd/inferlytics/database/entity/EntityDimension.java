/**
 *  * Copyright (c) 2013 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.database.entity;

/**
 * @author Praty
 * @created Apr 25, 2013
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class EntityDimension {


	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public String getSubDimension() {
		return subDimension;
	}

	public void setSubDimension(String subDimension) {
		this.subDimension = subDimension;
	}



	public int getDetailedSentiment() {
		return detailedSentiment;
	}

	public void setDetailedSentiment(int detailedSentiment) {
		this.detailedSentiment = detailedSentiment;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @param postId
	 *            the postId to set
	 */
	public void setPostId(String postId) {
		this.postId = postId;
	}

	/**
	 * @return the postId
	 */
	public String getPostId() {
		return postId;
	}

	/**
	 * @param word the word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * @return the senti
	 */
	public boolean isSenti() {
		return senti;
	}

	/**
	 * @param senti
	 *            the senti to set
	 */
	public void setSenti(boolean senti) {
		this.senti = senti;
	}

	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @param score
	 *            the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * @return the totalCount
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * @param totalCount the totalCount to set
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	private String dimension;
	private String subDimension;
	private int detailedSentiment;
	private double score;
	private int count;
	private int totalCount;
	private String postId;
	private String word;
	private boolean senti;
}
