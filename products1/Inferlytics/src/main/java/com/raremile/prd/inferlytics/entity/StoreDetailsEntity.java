package com.raremile.prd.inferlytics.entity;

import java.util.ArrayList;
import java.util.List;

public class StoreDetailsEntity {

	private String storeName;
	private String imageSrc;
	private String id;
	private double avgScore;
	private int storesCount;
	private int posCommentsCount;
	private int negCommentsCount;
	private List<KeyWord> keyWords=new ArrayList<>(); 
	private List<String> storenames = new ArrayList<>();

	/**
	 * @return the avgScore
	 */
	public double getAvgScore() {
		return avgScore;
	}

	public String getId() {
		return id;
	}

	public String getImageSrc() {
		return imageSrc;
	}

	public List<KeyWord> getKeyWords() {
		return keyWords;
	}

	/**
	 * @return the negCommentsCount
	 */
	public int getNegCommentsCount() {
		return negCommentsCount;
	}

	/**
	 * @return the posCommentsCount
	 */
	public int getPosCommentsCount() {
		return posCommentsCount;
	}

	public String getStoreName() {
		return storeName;
	}

	/**
	 * @return the storenames
	 */
	public List<String> getStorenames() {
		return storenames;
	}

	/**
	 * @return the storesCount
	 */
	public int getStoresCount() {
		return storesCount;
	}

	/**
	 * @param avgScore the avgScore to set
	 */
	public void setAvgScore(double avgScore) {
		this.avgScore = avgScore;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}

	public void setKeyWords(List<KeyWord> keyWords) {
		this.keyWords = keyWords;
	}

	/**
	 * @param negCommentsCount the negCommentsCount to set
	 */
	public void setNegCommentsCount(int negCommentsCount) {
		this.negCommentsCount = negCommentsCount;
	}

	/**
	 * @param posCommentsCount the posCommentsCount to set
	 */
	public void setPosCommentsCount(int posCommentsCount) {
		this.posCommentsCount = posCommentsCount;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public void setStorenames(ArrayList<String> storenames)  {
		this.storenames=storenames;
	}

	/**
	 * @param storesCount
	 *            the storesCount to set
	 */
	public void setStoresCount(int storesCount) {
		this.storesCount = storesCount;
	}

}
