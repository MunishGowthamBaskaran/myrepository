package com.raremile.prd.inferlytics.entity;

import java.util.ArrayList;
import java.util.List;

public class ProductDetails {
	private String name;
	private String imgURL;
	private ArrayList<KeyWord> posKeywords = new ArrayList<>();
	private ArrayList<KeyWord> negKeywords = new ArrayList<>();
	private int totposCount;
	private int totnegCount;
	private double price;
	private String currency;
	private String productId;
	private String prodUrl;
	private List<Post> positivePosts;
	private List<Post> negativePosts;
	private List<KeyWord> categories;

	private double averageRating;

	private String chartData;

	public void addNegReview(ArrayList<KeyWord> negReviews) {
		negKeywords=negReviews;
	}

	public void addPosReview(ArrayList<KeyWord> posReviews) {
		posKeywords=posReviews;
	}

	/**
	 * @return the averageRating
	 */
	public double getAverageRating() {
		return averageRating;
	}

	/**
	 * @return the categories
	 */
	public List<KeyWord> getCategories() {
		return categories;
	}

	/**
	 * @return the chartData
	 */
	public String getChartData() {
		return chartData;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	public String getImgURL() {
		return imgURL;

	}

	public String getname() {
		return name;

	}

	/**
	 * @return the negativePosts
	 */
	public List<Post> getNegativePosts() {
		return negativePosts;
	}



	public ArrayList<KeyWord> getNegReviews() {
		return negKeywords;

	}

	/**
	 * @return the positivePosts
	 */
	public List<Post> getPositivePosts() {
		return positivePosts;
	}

	public ArrayList<KeyWord> getPosReviews() {
		return posKeywords;

	}

	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	public String getProductId() {
		return productId;
	}

	public String getProdUrl() {
		return prodUrl;
	}

	public int getTotnegCount() {
		return totnegCount;
	}

	public int getTotposCount() {
		return totposCount;
	}

	/**
	 * @param averageRating the averageRating to set
	 */
	public void setAverageRating(double averageRating) {
		this.averageRating = averageRating;
	}

	/**
	 * @param categories the categories to set
	 */
	public void setCategories(List<KeyWord> categories) {
		this.categories = categories;
	}



	/**
	 * @param chartData the chartData to set
	 */
	public void setChartData(String chartData) {
		this.chartData = chartData;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setImgURL(String imgURL) {
		this.imgURL = imgURL;

	}

	public void setName(String name) {
		this.name = name;

	}

	/**
	 * @param negativePosts the negativePosts to set
	 */
	public void setNegativePosts(List<Post> negativePosts) {
		this.negativePosts = negativePosts;
	}

	/**
	 * @param positivePosts2
	 *            the positivePosts to set
	 */
	public void setPositivePosts(List<Post> positivePosts2) {
		this.positivePosts = positivePosts2;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public void setProdUrl(String prodUrl) {
		this.prodUrl = prodUrl;
	}

	public void setTotnegCount(int totnegCount) {
		this.totnegCount = totnegCount;
	}

	public void setTotposCount(int totposCount) {
		this.totposCount = totposCount;
	}
}
