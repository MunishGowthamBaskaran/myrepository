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

import java.util.ArrayList;
import java.util.List;

/**
 * @author mallikarjuna
 * @created 16-Jul-2014
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class HiChartSeries {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(HiChartSeries.class);

	private String name;
	private List<Integer> data = new ArrayList<>();
	private String stack;

	public List<Integer> getData() {
		return data;
	}

	public String getName() {
		return name;
	}

	public String getStack() {
		return stack;
	}

	public void setData(List<Integer> data) {
		this.data = data;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStack(String stack) {
		this.stack = stack;
	}
}
