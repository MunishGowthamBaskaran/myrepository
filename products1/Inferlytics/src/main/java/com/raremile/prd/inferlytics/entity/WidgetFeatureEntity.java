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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pratyusha
 * @created 26-Jul-2013
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class WidgetFeatureEntity {

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		if (count == 0) {
			return positiveCount + negativeCount;
		} else {
			return count;
		}
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the positiveCount
	 */
	public int getPositiveCount() {
		return positiveCount;
	}

	/**
	 * @param positiveCount
	 *            the positiveCount to set
	 */
	public void setPositiveCount(int positiveCount) {
		this.positiveCount = positiveCount;
	}

	public void incPosCount() {
		this.positiveCount++;
	}

	public void incPosByCount(int count) {
		this.positiveCount += count;
	}

	/**
	 * @return the negativeCount
	 */
	public int getNegativeCount() {
		return negativeCount;
	}

	/**
	 * @param negativeCount
	 *            the negativeCount to set
	 */
	public void setNegativeCount(int negativeCount) {
		this.negativeCount = negativeCount;
	}

	public void incNegCount() {
		this.negativeCount++;
	}

	public void incNegByCount(int count) {
		this.negativeCount += count;
	}

	/**
	 * @return the posPostIds
	 */
	public Set<String> getPosPostIds() {
		return posPostIds;
	}

	/**
	 * @param posPostIds
	 *            the posPostIds to set
	 */
	public void setPosPostIds(Set<String> posPostIds) {
		this.posPostIds = posPostIds;
	}

	public void addPosPostIdToList(String postId) {
		if (this.posPostIds == null) {
			this.posPostIds = new HashSet<String>();
		}
		
			this.posPostIds.add(postId);
		
	}

	/**
	 * @return the negPostIds
	 */
	public Set<String> getNegPostIds() {
		return negPostIds;
	}

	/**
	 * @param negPostIds
	 *            the negPostIds to set
	 */
	public void setNegPostIds(Set<String> negPostIds) {
		this.negPostIds = negPostIds;
	}

	public void addNegPostIdToList(String postId) {
		if (this.negPostIds == null) {
			this.negPostIds = new HashSet<String>();
		}
		if (!this.negPostIds.contains(postId)) {
			this.negPostIds.add(postId);
		}
	}

	/**
	 * @return the colour
	 */
	public String getColour() {
		return colour;
	}

	/**
	 * @param colour
	 *            the colour to set
	 */
	public void setColour(String colour) {
		this.colour = colour;
	}

	/**
	 * @return the words
	 */
	public Set<String> getWords() {
		return words;
	}

	/**
	 * @param words
	 *            the words to set
	 */
	public void setWords(Set<String> words) {
		this.words = words;
	}

	public void addWordToList(String word) {
		if (this.words == null) {
			this.words = new HashSet<String>();
		}
		if (!this.words.contains(word)) {
			this.words.add(word);
		}
	}

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(WidgetFeatureEntity.class);

	private String name;
	private String color;
	private int count;
	private int positiveCount;
	private int negativeCount;
	private Set<String> posPostIds;
	private Set<String> negPostIds;
	private String colour;
	private Set<String> words;
	private Map<String, Integer[]> wordCount;

	private double posCountPercent;

	/**
	 * @return the posCountPercent
	 */
	public double getPosCountPercent() {
		return posCountPercent;
	}

	/**
	 * @param posCountPercent
	 *            the posCountPercent to set
	 */
	public void setPosCountPercent(double posCountPercent) {
		this.posCountPercent = posCountPercent;
	}

	/**
	 * @return the negCountPercent
	 */
	public double getNegCountPercent() {
		return negCountPercent;
	}

	/**
	 * @param negCountPercent
	 *            the negCountPercent to set
	 */
	public void setNegCountPercent(double negCountPercent) {
		this.negCountPercent = negCountPercent;
	}

	/**
	 * @return the totCountPercent
	 */
	public double getTotCountPercent() {
		return totCountPercent;
	}

	/**
	 * @param totCountPercent
	 *            the totCountPercent to set
	 */
	public void setTotCountPercent(double totCountPercent) {
		this.totCountPercent = totCountPercent;
	}

	private double negCountPercent;
	private double totCountPercent;

	@Override
	public boolean equals(final Object obj) {
		String name = getName();
		if (this == obj) {
			return true;
		}
		if ((obj == null) || !(obj instanceof WidgetFeatureEntity)) {
			return false;
		}
		return name.equals(((WidgetFeatureEntity) obj).getName());
	}

	@Override
	public int hashCode() {
		String name = getName();
		if (!name.isEmpty()) {
			return name.hashCode();
		} else {
			return super.hashCode();
		}
	}

	/**
	 * @return the wordCount
	 */
	public Map<String, Integer[]> getWordCount() {
		return wordCount;
	}

	/**
	 * @param wordCount
	 *            the wordCount to set
	 */
	public void setWordCount(Map<String, Integer[]> wordCount) {
		this.wordCount = wordCount;
	}

}
