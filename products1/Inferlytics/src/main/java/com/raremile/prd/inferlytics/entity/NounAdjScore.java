package com.raremile.prd.inferlytics.entity;

import java.io.Serializable;

public class NounAdjScore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String noun;
	private String synonymNoun;
	private String adj;
	private long adjId;
	private Double score;
	/**
	 * @param noun the noun to set
	 */
	public void setNoun(String noun) {
		this.noun = noun;
	}
	/**
	 * @return the noun
	 */
	public String getNoun() {
		return noun;
	}
	/**
	 * @param adj the adj to set
	 */
	public void setAdj(String adj) {
		this.adj = adj;
	}
	/**
	 * @return the adj
	 */
	public String getAdj() {
		return adj;
	}
	/**
	 * @param score the score to set
	 */
	public void setScore(Double score) {
		this.score = score;
	}
	/**
	 * @return the score
	 */
	public Double getScore() {
		return score;
	}
	/**
	 * @param adjId the adjId to set
	 */
	public void setAdjId(long adjId) {
		this.adjId = adjId;
	}
	/**
	 * @return the adjId
	 */
	public long getAdjId() {
		return adjId;
	}
	/**
	 * @return the synonymNoun
	 */
	public String getSynonymNoun() {
		return synonymNoun;
	}
	/**
	 * @param synonymNoun the synonymNoun to set
	 */
	public void setSynonymNoun(String synonymNoun) {
		this.synonymNoun = synonymNoun;
	}

}
