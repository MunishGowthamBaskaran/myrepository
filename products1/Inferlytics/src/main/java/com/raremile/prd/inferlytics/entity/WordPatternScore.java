package com.raremile.prd.inferlytics.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WordPatternScore implements Serializable, Cloneable{

	private int id;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}
	/**
	 * @param word the word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}
	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}
	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	/**
	 * @return the score
	 */
	public Double getScore() {
		return score;
	}
	/**
	 * @param score the score to set
	 */
	public void setScore(Double score) {
		this.score = score;
	}
	private String word;
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		  String guid = getId()+getWord();
	        return guid.hashCode();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object other) {
        String thisGuid = getId()+getWord();
       
		if (other == null || !(other instanceof WordPatternScore)) {
			return false;
		}
        String thatGuid = ((WordPatternScore)other).getId()+((WordPatternScore)other).getWord();
        return thatGuid != null && thisGuid.equals(thatGuid);
	}
	private String pattern;
	Double score;
	
	 @Override
	public Object clone()
     throws CloneNotSupportedException
 {
     return super.clone();
 }
}
