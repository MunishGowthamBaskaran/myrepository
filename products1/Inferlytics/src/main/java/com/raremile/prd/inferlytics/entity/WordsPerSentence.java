package com.raremile.prd.inferlytics.entity;

import java.util.List;

public class WordsPerSentence {
	private List<List<String>> wordsAroundList;
	private List<String> wordsNotPresent;
	private List<String> wordsPresent;
	private double sentenceScore;
	private double overallScore;

	public List<List<String>> getWordsAroundList() {
		return wordsAroundList;
	}

	public void setWordsAroundList(List<List<String>> wordsAroundList) {
		this.wordsAroundList = wordsAroundList;
	}

	public List<String> getWordsNotPresent() {
		return wordsNotPresent;
	}

	public void setWordsNotPresent(List<String> wordsNotPresent) {
		this.wordsNotPresent = wordsNotPresent;
	}

	/**
	 * @param sentenceScore
	 *            the sentenceScore to set
	 */
	public void setSentenceScore(double sentenceScore) {
		this.sentenceScore = sentenceScore;
	}

	/**
	 * @return the sentenceScore
	 */
	public double getSentenceScore() {
		return sentenceScore;
	}

	/**
	 * @param overallScore
	 *            the overallScore to set
	 */
	public void setOverallScore(double overallScore) {
		this.overallScore = overallScore;
	}

	/**
	 * @return the overallScore
	 */
	public double getOverallScore() {
		return overallScore;
	}

	/**
	 * @param wordsPresent the wordsPresent to set
	 */
	public void setWordsPresent(List<String> wordsPresent) {
		this.wordsPresent = wordsPresent;
	}

	/**
	 * @return the wordsPresent
	 */
	public List<String> getWordsPresent() {
		return wordsPresent;
	}

}
