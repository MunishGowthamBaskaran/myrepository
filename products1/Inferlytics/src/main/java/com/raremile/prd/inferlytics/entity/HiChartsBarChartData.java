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

import java.util.List;

/**
 * @author mallikarjuna
 * @created 16-Jul-2014
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class HiChartsBarChartData {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(HiChartsBarChartData.class);
	private List<String> words;
	private List<HiChartSeries> series;

	public List<HiChartSeries> getSeries() {
		return series;
	}

	public List<String> getWords() {
		return words;
	}

	public void setSeries(List<HiChartSeries> series) {
		this.series = series;
	}

	public void setWords(List<String> words) {
		this.words = words;
	}

}
