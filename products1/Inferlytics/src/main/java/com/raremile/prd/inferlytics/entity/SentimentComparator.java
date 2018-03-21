package com.raremile.prd.inferlytics.entity;

import java.util.Comparator;

public class SentimentComparator implements Comparator<MainSeries>{

	@Override
	public int compare(MainSeries o1, MainSeries o2) {
		return DetailedSentiment.valueOf(o1.getName()).compareTo(
				DetailedSentiment.valueOf(o2.getName()));
	}

}
