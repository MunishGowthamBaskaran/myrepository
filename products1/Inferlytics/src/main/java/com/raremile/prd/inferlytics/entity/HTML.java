package com.raremile.prd.inferlytics.entity;

import com.google.gson.annotations.SerializedName;

/**
 * @author Pratyusha
 * @created Apr 12, 2013
 * 
 *          This Class is used in the generation of TagCloud.
 * 
 */
public class HTML {

	/**
	 * This is used to indicate whether the tag is positive or negative.
	 */
	@SerializedName("class")
	private String className;

	private String title;
	private String onclick;

	/**
	 * @return the className
	 */

	public String getClassName() {
		return className;
	}

	public String getOnclick() {
		return onclick;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HTML [className=" + className + "]";
	}

}
