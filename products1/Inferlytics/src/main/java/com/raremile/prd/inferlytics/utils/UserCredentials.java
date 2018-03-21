package com.raremile.prd.inferlytics.utils;

public class UserCredentials {

	public static void main(String[] args) {


	}
	/**
	 * @param args
	 */

	private String userName;
	private String userId;
	private String entityName;
	private String productName;
	private String subProduct;
	private boolean success;

	private Integer userType;

	public String getEntityName() {
		return entityName;
	}

	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}

	public String getSubProduct() {
		return subProduct;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	/**
	 * @return the userType
	 */
	public Integer getUserType() {
		return userType;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}


	public void setSubProduct(String subProduct) {
		this.subProduct = subProduct;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @param userType
	 *            the userType to set
	 */
	public void setUserType(Integer userType) {
		this.userType = userType;
	}

}
