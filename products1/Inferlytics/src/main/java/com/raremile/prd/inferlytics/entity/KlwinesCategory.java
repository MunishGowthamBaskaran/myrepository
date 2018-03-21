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
 * @created 05-May-2014
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class KlwinesCategory {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(KlwinesCategory.class);
	private int redWinesCount = 0;
	private int whiteWinesCount = 0;
	private int otherWinesCount = 0;

	/**
	 * @return the otherWinesCount
	 */
	public int getOtherWinesCount() {
		return otherWinesCount;
	}

	public int getRedWinesCount() {
		return redWinesCount;
	}

	public int getWhiteWinesCount() {
		return whiteWinesCount;
	}

	/**
	 * @param otherWinesCount
	 *            the otherWinesCount to set
	 */
	public void setOtherWinesCount(int otherWinesCount) {
		this.otherWinesCount = otherWinesCount;
	}

	public void setRedWinesCount(int redWinesCount) {
		this.redWinesCount = redWinesCount;
	}

	public void setWhiteWinesCount(int whiteWinesCount) {
		this.whiteWinesCount = whiteWinesCount;
	}
}
