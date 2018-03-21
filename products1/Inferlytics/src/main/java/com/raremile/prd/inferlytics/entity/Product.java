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
import java.util.List;

/**
 * @author pratyusha
 * @created 11-Sep-2013
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class Product {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(Product.class);

	private String productId;
	private String productName;
	private String imageUrl;
	private String productUrl;
	private int posCommentsCount;
	private int negCommentsCount;
	private int totalCount;
	private int clickCount;/* Used in analytics to keep track of the no of clicks on the product*/	
	private String postId; /* Used in analytics to keep track of the no of clicks on the product*/	
	private String price;
	private double similarityScore;
	private double sentimentScore;
	private double averageScore;
	private List<FeatureWords> featureWords = new ArrayList<>();
	private List<String> posTraitslist = new ArrayList<>();
	private List<String> negTraitslist = new ArrayList<>();

	/**
	 * @return the productId
	 */

	public Product(){
		posCommentsCount=0;
		negCommentsCount=0;
		totalCount=0;
	}

	/**
	 * @return the averageScore
	 */
	public double getAverageScore() {
		return averageScore;
	}

	public int getClickCount() {
		return clickCount;
	}

	/**
	 * @return the featureWords
	 */
	public List<FeatureWords> getFeatureWords() {
		return featureWords;
	}

	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	public int getNegCommentsCount(){
		return negCommentsCount;

	}

	/**
	 * @return the negTraitslist
	 */
	public List<String> getNegTraitslist() {
		return negTraitslist;
	}

	public int getPosCommentsCount(){
		return posCommentsCount;

	}

	public String getPostId() {
		return postId;
	}

	/**
	 * @return the posTraitslist
	 */
	public List<String> getPosTraitslist() {
		return posTraitslist;
	}

	/**
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}

	public String getProductId() {
		return productId;
	}

	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * @return the productUrl
	 */
	public String getProductUrl() {
		return productUrl;
	}

	/**
	 * @return the sentimentScore
	 */
	public double getSentimentScore() {
		return sentimentScore;
	}

	/**
	 * @return the similarityScore
	 */
	public double getSimilarityScore() {
		return similarityScore;
	}

	public int getTotalCount(){
		return totalCount;

	}

	/**
	 * @param averageScore the averageScore to set
	 */
	public void setAverageScore(double averageScore) {
		this.averageScore = averageScore;
	}

	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}

	/**
	 * @param featureWords the featureWords to set
	 */
	public void setFeatureWords(List<FeatureWords> featureWords) {
		this.featureWords = featureWords;
	}

	/**
	 * @param imageUrl
	 *            the imageUrl to set
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setNegCommmentsCount(int negCount)
	{
		this.negCommentsCount=negCount;

	}

	/**
	 * @param negTraitslist the negTraitslist to set
	 */
	public void setNegTraitslist(List<String> negTraitslist) {
		this.negTraitslist = negTraitslist;
	}

	public void setPosCommmentsCount(int PosCount)
	{
		this.posCommentsCount=PosCount;

	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	/**
	 * @param posTraitslist the posTraitslist to set
	 */
	public void setPosTraitslist(List<String> posTraitslist) {
		this.posTraitslist = posTraitslist;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(String price) {
		this.price = price;
	}

	/**
	 * @param productId
	 *            the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}

	/**
	 * @param productName
	 *            the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}

	/**
	 * @param productUrl
	 *            the productUrl to set
	 */
	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}

	/**
	 * @param sentimentScore the sentimentScore to set
	 */
	public void setSentimentScore(double sentimentScore) {
		this.sentimentScore = sentimentScore;
	}

	/**
	 * @param similarityScore
	 *            the similarityScore to set
	 */
	public void setSimilarityScore(double similarityScore) {
		this.similarityScore = similarityScore;
	}

	public void setTotalCount(int totCount){
		this.totalCount=totCount;
	}
}
