package com.raremile.prd.inferlytics.entity;

public class Features {

	private String text;
	
	private int weight;
	
	private String link;
	
	private HTML html;

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * @return the html
	 */
	public HTML getHtml() {
		return html;
	}

	/**
	 * @param html the html to set
	 */
	public void setHtml(HTML html) {
		this.html = html;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Features [text=" + text + ", weight=" + weight + ", link="
				+ link + ", html=" + html + "]";
	}
	
	
}
