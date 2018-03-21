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
 * @created 22-Jul-2014
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class BeeGoodTopicData {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(BeeGoodTopicData.class);
	private String productName;
	private double posReviewspercentage;
	private int reviewsCount;

	public double getPosReviewspercentage() {
		return posReviewspercentage;
	}

	public String getProductName() {
		return productName;
	}

	public int getReviewsCount() {
		return reviewsCount;
	}

	public void setPosReviewspercentage(double posReviewspercentage) {
		this.posReviewspercentage = posReviewspercentage;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public void setReviewsCount(int reviewsCount) {
		this.reviewsCount = reviewsCount;
	}

}
