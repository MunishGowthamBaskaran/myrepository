package com.raremile.prd.inferlytics.entity;

/**
 * @author Pratyusha
 * 
 */
public class Serie extends MainSeries implements java.io.Serializable {
	
	private static final long serialVersionUID = -355316555608386049L;

	private Integer data;

	public Serie(String name, Integer data) {
		super(name);
		this.data = data;
	}

	/**
	 * @return the data
	 */
	public Integer getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Integer data) {
		this.data = data;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Serie [name=" + super.getName() + ", data=" +data + "]";
	}

}
