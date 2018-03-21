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
 * @created 16-Jul-2014
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class BrandData {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(BrandData.class);
	private String entity;
	private int posReviewsCount;
	private int negReviewsCount;
	private int totalReviewsCount;
	private int socialMediaReviewsCount;
	private int socialMediaPosReviewsCount;
	private int socialMediaNegReviewsCount;
	private double overAllRating;
	private double overallScore;
	private String subProduct;


	/**
	 * @return the entity
	 */
	public String getEntity() {
		return entity;
	}

	public int getNegReviewsCount() {
		return negReviewsCount;
	}

	public double getOverAllRating() {
		return overAllRating;
	}

	public double getOverallScore() {
		return overallScore;
	}

	public int getPosReviewsCount() {
		return posReviewsCount;
	}

	public int getSocialMediaNegReviewsCount() {
		return socialMediaNegReviewsCount;
	}

	/**
	 * @return
	 */
	public int getSocialMediaPosReviewsCount() {
		return socialMediaPosReviewsCount;
	}

	public int getSocialMediaReviewsCount() {
		return socialMediaReviewsCount;
	}


	/**
	 * @return the subProduct
	 */
	public String getSubProduct() {
		return subProduct;
	}

	public int getTotalReviewsCount() {
		return totalReviewsCount;
	}

	/**
	 * @param entity the entity to set
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	public void setNegReviewsCount(int negReviewsCount) {
		this.negReviewsCount = negReviewsCount;
	}

	public void setOverAllRating(double overAllRating) {
		this.overAllRating = overAllRating;
	}
	public void setOverallScore(double overallScore) {
		this.overallScore = overallScore;
	}
	public void setPosReviewsCount(int posReviewsCount) {
		this.posReviewsCount = posReviewsCount;
	}

	public void setSocialMediaNegReviewsCount(int socialMediaNegReviewsCount) {
		this.socialMediaNegReviewsCount = socialMediaNegReviewsCount;
	}

	public void setSocialMediaPosReviewsCount(int socialMediaPosReviewsCount) {
		this.socialMediaPosReviewsCount = socialMediaPosReviewsCount;
	}

	public void setSocialMediaReviewsCount(int socialMediaReviewsCount) {
		this.socialMediaReviewsCount = socialMediaReviewsCount;
	}

	/**
	 * @param subProduct the subProduct to set
	 */
	public void setSubProduct(String subProduct) {
		this.subProduct = subProduct;
	}

	public void setTotalReviewsCount(int totalReviewsCount) {
		this.totalReviewsCount = totalReviewsCount;
	}
}
