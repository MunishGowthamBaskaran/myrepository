package com.raremile.prd.inferlytics.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

public class ActivityLog {
	private static final Logger LOG_ACTIVITY = Logger.getLogger("activity");

	public static void addClientDetailstoLog(String brand, String subProduct,
			String category, String subDimension, String word, String prodId,
			String reviewtype, String postId, String userIP) {
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(Calendar.getInstance().getTime());
		LOG_ACTIVITY.info("<" + brand + "|" + subProduct + "|" + category + "|"
				+ subDimension + "|" + word + "|" + prodId + "|" + reviewtype
				+ "|" + postId + "|" + userIP + "|" + timeStamp + ">");
	}

}
