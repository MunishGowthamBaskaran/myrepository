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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Praty
 * @created Apr 18, 2013
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class FeatureAdjectiveEntity implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Integer> wordIdList;
	private int detailedSentiment;
	private double score;
	private List<FeatureWordEntity> featureWordEntity;
	

	public List<Integer> getWordIdList() {
		return wordIdList;
	}

	public void setWordIdList(List<Integer> wordIdList) {
		this.wordIdList = wordIdList;
	}

	public int getDetailedSentiment() {
		return detailedSentiment;
	}

	public void setDetailedSentiment(int detailedSentiment) {
		this.detailedSentiment = detailedSentiment;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * @return the featureWordEntity
	 */
	public List<FeatureWordEntity> getFeatureWordEntity() {
		return featureWordEntity;
	}

	/**
	 * @param featureWordEntity the featureWordEntity to set
	 */
	public void setFeatureWordEntity(List<FeatureWordEntity> featureWordEntity) {
		this.featureWordEntity = featureWordEntity;
	}





}
