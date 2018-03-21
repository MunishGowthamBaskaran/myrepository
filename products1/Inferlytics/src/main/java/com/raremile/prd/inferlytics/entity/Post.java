package com.raremile.prd.inferlytics.entity;

import java.util.List;

public class Post {

	/**
	 * @param args
	 */
	private String postId;
	private boolean isPositive;
	private String content;
	private Product product;
	private String word;
	private int sentenceNo;
	private String clicks;
	private String date;
	private String title;
	private String permaLink;
	private String source;
	private int rating;
	private String reviewRating;
	private List<String> categories;
	private List<String> keywords;


	/**
	 * @return the categories
	 */
	public List<String> getCategories() {
		return categories;
	}

	public String getClicks() {
		return clicks;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	public String getDate() {
		return date;
	}

	/**
	 * @return the keywords
	 */
	public List<String> getKeywords() {
		return keywords;
	}

	/**
	 * @return the permaLink
	 */
	public String getPermaLink() {
		return permaLink;
	}

	/**getblogsForEvents
	 * @return the postId
	 */
	public String getPostId() {
		return postId;
	}

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * @return the rating
	 */
	public double getRating() {
		return rating;
	}

	/**
	 * @return the reviewRating
	 */
	public String getReviewRating() {
		return reviewRating;
	}

	/**
	 * @return the sentenceNo
	 */
	public int getSentenceNo() {
		return sentenceNo;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * @return the isPositive
	 */
	public boolean isPositive() {
		return isPositive;
	}

	/**
	 * @param categories the categories to set
	 */
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public void setClicks(String clicks) {
		this.clicks = clicks;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @param keywords the keywords to set
	 */
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	/**
	 * @param permaLink the permaLink to set
	 */
	public void setPermaLink(String permaLink) {
		this.permaLink = permaLink;
	}

	/**
	 * @param isPositive
	 *            the isPositive to set
	 */
	public void setPositive(boolean isPositive) {
		this.isPositive = isPositive;
	}

	/**
	 * @param postId
	 *            the postId to set
	 */
	public void setPostId(String postId) {
		this.postId = postId;
	}

	/**
	 * @param product
	 *            the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}

	/**
	 * @param rating the rating to set
	 */
	public void setRating(int rating) {
		this.rating = rating;
	}

	/**
	 * @param reviewRating the reviewRating to set
	 */
	public void setReviewRating(String reviewRating) {
		this.reviewRating = reviewRating;
	}

	/**
	 * @param sentenceNo
	 *            the sentenceNo to set
	 */
	public void setSentenceNo(int sentenceNo) {
		this.sentenceNo = sentenceNo;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param word
	 *            the word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}

}
