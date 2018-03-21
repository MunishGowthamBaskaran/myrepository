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

import java.util.List;

/**
 * @author Praty
 * @created Apr 24, 2013
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class SunBurstData {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(SunBurstData.class);

	private String name;
	private String color;
	private List<SunBurstData> children;
	private int size;
	private transient String nameWithOutcount;
	private List<String> postIds;
	private String colour;
	private List<String> words;
	

	
	private int posCount;

	/**
	 * @return the posCount
	 */
	public int getPosCount() {
		return posCount;
	}

	/**
	 * @param posCount
	 *            the posCount to set
	 */
	public void setPosCount(int posCount) {
		this.posCount = posCount;
	}

	private int negCount;

	public List<String> getPostIds() {
		return postIds;
	}

	public void setPostIds(List<String> postIds) {
		this.postIds = postIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<SunBurstData> children) {
		this.children = children;
	}

	/**
	 * @return the children
	 */
	public List<SunBurstData> getChildren() {
		return children;
	}

	/**
	 * @param count
	 *            the count to set
	 */

	/**
	 * @param nameWithOutcount
	 *            the nameWithOutcount to set
	 */
	public void setNameWithOutcount(String nameWithOutcount) {
		this.nameWithOutcount = nameWithOutcount;
	}

	/**
	 * @return the nameWithOutcount
	 */
	public String getNameWithOutcount() {

		return nameWithOutcount;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(int size) {
		
		if (size != 0) {
			this.size += size;
		} else {
			this.size = posCount + negCount;
		}	
		
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param colour
	 *            the colour to set
	 */
	public void setColour(String colour) {
		this.colour = colour;
	}

	/**
	 * @return the colour
	 */
	public String getColour() {
		return colour;
	}

	/**
	 * @param words
	 *            the words to set
	 */
	public void setWords(List<String> words) {
		this.words = words;
	}

	/**
	 * @return the words
	 */
	public List<String> getWords() {
		return words;
	}

	/**
	 * @return the negCount
	 */
	public int getNegCount() {
		return negCount;
	}

	/**
	 * @param negCount
	 *            the negCount to set
	 */
	public void setNegCount(int negCount) {
		this.negCount = negCount;
	}

}
