package com.raremile.prd.inferlytics.entity;

import java.io.Serializable;

/**
 *
 * @author Pratyusha
 */
public class Sentiment implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SENTIMENTENUM sentiment;
    private DetailedSentiment detailedSentiment;
    private double polarity;
	/**
	 * @param sentiment the sentiment to set
	 */
	public void setSentiment(SENTIMENTENUM sentiment) {
		this.sentiment = sentiment;
	}
	/**
	 * @return the sentiment
	 */
	public SENTIMENTENUM getSentiment() {
		return sentiment;
	}
	/**
	 * @param polarity the polarity to set
	 */
	public void setPolarity(double polarity) {
		this.polarity = polarity;
	}
	/**
	 * @return the polarity
	 */
	public double getPolarity() {
		return polarity;
	}
	public void setDetailedSentiment(DetailedSentiment detailedSentiment) {
		this.detailedSentiment = detailedSentiment;
	}
	public DetailedSentiment getDetailedSentiment() {
		return detailedSentiment;
	}
	
    
}
