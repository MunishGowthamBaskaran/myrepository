package com.raremile.prd.inferlytics.entity;

public class MainSeries implements java.io.Serializable {

	private static final long serialVersionUID = -5974449883254092470L;
	
	private String name;

	public MainSeries(String name) {
		super();
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public MainSeries() {
		super();
	}

}
