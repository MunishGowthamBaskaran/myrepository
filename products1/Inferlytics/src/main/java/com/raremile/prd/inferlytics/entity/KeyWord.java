package com.raremile.prd.inferlytics.entity;

import java.util.List;

public class KeyWord {
	private String keyWord;
	private int count;
	private int posCount;
	private int negativeCount;
	private int totalCount;
	private int secondCount;
	private int percentage;
	private List<String> postIds;

	public KeyWord() {
		posCount = 0;
		negativeCount = 0;
		totalCount = 0;
	}

	public int getcount() {
		return count;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public int getNegativeCount() {
		return negativeCount;
	}

	public int getPercentage() {
		return percentage;
	}

	public int getPosCount() {
		return posCount;
	}

	/**
	 * @return the postIds
	 */
	public List<String> getPostIds() {
		return postIds;
	}

	public int getSecondCount() {
		return secondCount;
	}



	public int getTotalCount() {
		return totalCount;
	}

	public void setcount(int count) {
		this.count = count;
	}

	public void setKeyWord(String word) {
		keyWord = word;
	}

	public void setNegativeCount(int negativeCount) {
		this.negativeCount = negativeCount;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public void setPosCount(int posCount) {
		this.posCount = posCount;
	}

	/**
	 * @param postIds the postIds to set
	 */
	public void setPostIds(List<String> postIds) {
		this.postIds = postIds;
	}

	public void setSecondCount(int secondCount) {
		this.secondCount = secondCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

}
