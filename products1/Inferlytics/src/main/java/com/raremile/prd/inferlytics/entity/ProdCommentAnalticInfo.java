package com.raremile.prd.inferlytics.entity;

public class ProdCommentAnalticInfo {
	private String entity;
	private String subProduct;
	private String category;
	private String subDimension;
	private String word;
	private String productId;
	private String postId;
	private String userIP;
	private String reviewType;
	private String date;

	public String getReviewType() {
		return reviewType;
	}

	public void setReviewType(String reviewType) {
		this.reviewType = reviewType;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the subDimension
	 */
	public String getSubDimension() {
		return subDimension;
	}

	/**
	 * @param subDimension
	 *            the subDimension to set
	 */
	public void setSubDimension(String subDimension) {
		this.subDimension = subDimension;
	}

	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * @param word
	 *            the word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * @param productId
	 *            the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}

	/**
	 * @return the postId
	 */
	public String getPostId() {
		return postId;
	}

	/**
	 * @param postId
	 *            the postId to set
	 */
	public void setPostId(String postId) {
		this.postId = postId;
	}

	/**
	 * @return the userIP
	 */
	public String getUserIP() {
		return userIP;
	}

	/**
	 * @param userIP
	 *            the userIP to set
	 */
	public void setUserIP(String userIP) {
		this.userIP = userIP;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getSubProduct() {
		return subProduct;
	}

	public void setSubProduct(String subProduct) {
		this.subProduct = subProduct;
	}

}
