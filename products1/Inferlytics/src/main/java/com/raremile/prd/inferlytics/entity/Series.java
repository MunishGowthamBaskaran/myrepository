package com.raremile.prd.inferlytics.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Series extends MainSeries implements java.io.Serializable {

	private static final long serialVersionUID = -7649248800899142893L;
	
	private Integer[] data1;
	
	private List<Integer> data;
	
	public void addToDataList(Integer data) {
		if(this.data == null) {
			this.data = new ArrayList<Integer>();
		}
		this.data.add(data);
	}
	
	public void replace(int index, int data) {
		if(this.data != null) {
			this.data.set(index, data);
		}
	}

	/**
	 * @return the data
	 */
	public Integer[] getData() {
		data1 = this.data.toArray(new Integer[0]);
		//return data;
		//return (Integer[]) this.dataList.toArray();
		return data1;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Integer[] data) {
		this.data1 = data;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Series other = (Series) obj;
		if (getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!getName().equals(other.getName())) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Series [name=" + getName() + ", data="
				+ Arrays.toString(getData()) + "]";
	}
	

}
