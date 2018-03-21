package com.raremile.prd.inferlytics.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



@SuppressWarnings("serial")
public class Feed implements Serializable, Cloneable{
	/*
	 * private String content; private String author; private Date date;
	 */
	private String feedId;
	private String langId;
	private long entityId;
	private long subProductId;
	private String productId;
	private String userName;
	private String userLocation;
	private String gender;
	private String age;
	private Date feedDate;
	private Date createdDate;
	private String feedRating;
	private int sourceId;
	private String feedData;
	private Opinion opinion;
	private ArrayList<WordPatternScore> wpsList;
	private ArrayList<String> hashTags;
    private int brandId;
	private String brandName;
	private String itemId;
	private Opinion output;
	private int relationId;
	private String sha1;
	private List<String> categories;

	/**
	 * @return the sha1
	 */
	public String getSha1() {
		return sha1;
	}

	/**
	 * @param sha1 the sha1 to set
	 */
	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}

	public int getRelationId() {
		return relationId;
	}

	public void setRelationId(int relationId) {
		this.relationId = relationId;
	}

	public String getFeedId() {
		return feedId;
	}

	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	public long getSubProductId() {
		return subProductId;
	}

	public void setSubProductId(long subProductId) {
		this.subProductId = subProductId;
	}

	public Date getFeedDate() {
		return feedDate;
	}

	public void setFeedDate(Date feedDate) {
		this.feedDate = feedDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}


	public int getBrandId() {
		return brandId;
	}

	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Opinion getOutput() {
		return output;
	}

	public void setOutput(Opinion output) {
		this.output = output;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(String userLocation) {
		this.userLocation = userLocation;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}



	public String getFeedRating() {
		return feedRating;
	}

	public void setFeedRating(String feedRating) {
		this.feedRating = feedRating;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public String getFeedData() {
		return feedData;
	}

	public void setFeedData(String feedData) {
		this.feedData = feedData;
	}





	/*
	 * public void setContent(String content) { this.content = content; }
	 */

	/**
	 * @return the content
	 */
	/*
	 * public String getContent() { return content; }
	 */

	/**
	 * @param author
	 *            the author to set
	 */
	/*
	 * public void setAuthor(String author) { this.author = author; }
	 */

	/**
	 * @return the author
	 */
	/*
	 * public String getAuthor() { return author; }
	 */

	/**
	 * @param date
	 *            the date to set
	 */
	/*
	 * public void setDate(Date date) { this.date = date; }
	 */

	/**
	 * @return the date
	 */
	/*
	 * public Date getDate() { return date; }
	 */

	/**
	 * @param langId
	 *            the langId to set
	 */
	public void setLangId(String langId) {
		this.langId = langId;
	}

	/**
	 * @return the langId
	 */
	public String getLangId() {
		return langId;
	}

	/**
	 * @param opinion
	 *            the opinion to set
	 */
	public void setOpinion(Opinion opinion) {
		this.opinion = opinion;
	}

	/**
	 * @return the opinion
	 */
	public Opinion getOpinion() {
		return opinion;
	}

	public Long getTime() {
		if (null != getFeedDate()) {
			return getFeedDate().getTime();
		}
		return  0L;
	}
	public String getStringDate() {
		if (null != getFeedDate()) {
			return getFeedDate().toString();
		}
		return  null;
	}


	public void setWpsList(ArrayList<WordPatternScore> wpsList) {
		this.wpsList = wpsList;
	}

	public ArrayList<WordPatternScore> getWpsList() {
		return wpsList;
	}

	@Override
	public Object clone()
     throws CloneNotSupportedException
 {
     return super.clone();
 }

	/*
	 * @Override public boolean equals(Object other) { String thisGuid =
	 * getFeedId(); if (null != thisGuid && thisGuid.isEmpty()) { return this ==
	 * other; } if (other == null ||
	 * !getClass().getName().equals(other.getClass().getName())) { return false;
	 * } String thatGuid = ((Feed) other).getFeedId(); return
	 * !thatGuid.isEmpty() && thisGuid == thatGuid; }
	 */
	@Override
	public boolean equals(final Object obj) {
		String thisGuid = getFeedId();
		if (this == obj) {
			return true;
		}
		if ((obj == null) || !(obj instanceof Feed)) {
			return false;
		}
		return thisGuid.equals(((Feed) obj).getFeedId());
	}

    @Override
	public int hashCode() {
		String guid = getFeedId();
		if (!guid.isEmpty()) {
			return guid.hashCode();
		} else {
			return super.hashCode();
		}
	}

		public void setHashTags(ArrayList<String> hashTags) {
			this.hashTags = hashTags;
		}

		public ArrayList<String> getHashTags() {
			return hashTags;
		}

		/**
		 * @return the categories
		 */
		public List<String> getCategories() {
			return categories;
		}

		/**
		 * @param categories the categories to set
		 */
		public void setCategories(List<String> categories) {
			this.categories = categories;
		}

		/**
		 * @return the productId
		 */
		public String getProductId() {
			return productId;
		}

		/**
		 * @param productId the productId to set
		 */
		public void setProductId(String productId) {
			this.productId = productId;
		}


}
