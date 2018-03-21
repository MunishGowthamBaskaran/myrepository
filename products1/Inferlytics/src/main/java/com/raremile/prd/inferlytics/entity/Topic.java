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
 * @created 04-Aug-2014
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class Topic {
	private String name;
	private List<WordCount> traits;
	private int count;
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(Topic.class);

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the traits
	 */
	public List<WordCount> getTraits() {
		return traits;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param traits the traits to set
	 */
	public void setTraits(List<WordCount> traits) {
		this.traits = traits;
	}
}
