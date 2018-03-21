package com.raremile.prd.inferlytics.commons;

public class Sentiword {

	private String pos;
	private long id;
	private  double posscore;
	private double negscore;
	private String word;
	private String glossary;
	private String pattern;
	/**
	 * @return the pOS
	 */
	public String getPOS() {
		return pos;
	}
	/**
	 * @param pOS the pOS to set
	 */
	public void setPOS(String pOS) {
		pos = pOS;
	}
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the posscore
	 */
	public double getPosscore() {
		return posscore;
	}
	/**
	 * @param posscore the posscore to set
	 */
	public void setPosscore(double posscore) {
		this.posscore = posscore;
	}
	/**
	 * @return the negscore
	 */
	public double getNegscore() {
		return negscore;
	}
	/**
	 * @param negscore the negscore to set
	 */
	public void setNegscore(double negscore) {
		this.negscore = negscore;
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
	 * @return the glossary
	 */
	public String getGlossary() {
		return glossary;
	}
	/**
	 * @param glossary the glossary to set
	 */
	public void setGlossary(String glossary) {
		this.glossary = glossary;
	}
	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}
	
}
