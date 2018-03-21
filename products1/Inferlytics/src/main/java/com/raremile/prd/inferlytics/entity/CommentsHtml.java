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

import com.google.gson.Gson;

/**
 * @author pratyusha
 * @created 07-Aug-2013
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class CommentsHtml {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(CommentsHtml.class);

	/**
	 * @return the tagName
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * @param tagName
	 *            the tagName to set
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className
	 *            the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the textContent
	 */
	public String getTextContent() {
		return textContent;
	}

	/**
	 * @param textContent
	 *            the textContent to set
	 */
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	/**
	 * @return the childNodes
	 */
	public List<CommentsHtml> getChildNodes() {
		return childNodes;
	}

	/**
	 * @param childNodes
	 *            the childNodes to set
	 */
	public void setChildNodes(List<CommentsHtml> childNodes) {
		this.childNodes = childNodes;
	}
	
	public void addChildNode(CommentsHtml childNode) {
		if (this.childNodes == null) {
			this.childNodes = new ArrayList<>();
		}
		this.childNodes.add(childNode);

	}

	private String href;
	private String src;
	private String tagName;
	private String className;
	private String textContent;
	private String click;
	private String title;
	private String id;
    private String subDim;
    private String word;
	private List<CommentsHtml> childNodes;
	private String value;
	private Integer count;
	private Boolean isShowMessage;
	private String productId;
	private String scNo;
	
	



	public static void main(String[] args) {

		List<CommentsHtml> childs = new ArrayList<CommentsHtml>(2);

		CommentsHtml child = new CommentsHtml();
		child.setTextContent("Hello");
		childs.add(child);

		child = new CommentsHtml();
		child.setTextContent("World!");
		child.setTagName("span");
		childs.add(child);

		CommentsHtml parent = new CommentsHtml();
		parent.setTagName("div");
		parent.setClassName("foo");
		parent.setChildNodes(childs);

		List<CommentsHtml> parents = new ArrayList<CommentsHtml>(1);
		parents.add(parent);

		System.out.println(new Gson().toJson(parents));
	}


	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	/**
	 * @return the src
	 */
	public String getSrc() {
		return src;
	}

	/**
	 * @param src
	 *            the src to set
	 */
	public void setSrc(String src) {
		this.src = src;
	}

	/**
	 * @return the click
	 */
	public String getClick() {
		return click;
	}
/**
 * 
 * @param title
 *  			sets the title attribute
 */
	public void setTitle(String title){
		this.title=title;
		
	}
	
	public void setId(String id)
	{
		this.id=id;
	}
	
	public void setsubDimension(String subDim){
		this.subDim=subDim;
	}


	/**
	 * @param click the click to set
	 */
	public void setClick(String click) {
		this.click = click;
	}

	/**
	 * @return the href
	 */
	public String getHref() {
		return href;
	}

	/**
	 * @param href
	 *            the href to set
	 */
	public void setHref(String href) {
		this.href = href;
	}

	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * @param word the word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
	
	public Boolean getisShowMessage() {
		return isShowMessage;
	}

	public void setisShowMessage(Boolean isShowMessage) {
		this.isShowMessage = isShowMessage;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the scNo
	 */
	public String getScNo() {
		return scNo;
	}

	/**
	 * @param scNo the scNo to set
	 */
	public void setScNo(String scNo) {
		this.scNo = scNo;
	}

}
