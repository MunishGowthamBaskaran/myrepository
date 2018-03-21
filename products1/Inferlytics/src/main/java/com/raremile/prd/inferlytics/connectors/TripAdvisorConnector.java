/**
 *  * Copyright (c) 2013 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.connectors;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.raremile.prd.inferlytics.database.InsertThread;
import com.raremile.prd.inferlytics.database.MongoConnector;
import com.raremile.prd.inferlytics.entity.Feed;
import com.raremile.prd.inferlytics.entity.Opinion;
import com.raremile.prd.inferlytics.sentiment.SentimentAnalysis;

/**
 * @author Praty
 * @created Apr 8, 2013
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class TripAdvisorConnector {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(TripAdvisorConnector.class);

	public static Connection.Response openURL(String url) throws IOException {
		Connection.Response res = Jsoup
				.connect(url.trim())
				.userAgent(
						"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
				.timeout(100000).ignoreHttpErrors(true)
				.referrer("http://www.google.com").execute();
		return res;
	}

	/**
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public static List<Feed> searchTripAdvisor(String url, String searchText)
			throws IOException, ParseException {
		List<Feed> feeds = new ArrayList<Feed>();

		int reviewsCount = -1;
		int reviewsCounter = 0;
		do {
			Connection.Response response = openURL(url.trim());
			if (response.statusCode() == HttpURLConnection.HTTP_OK
					&& response.contentType().contains("text/html")) {

				Document doc = Jsoup.parse(response.parse().toString());

				Elements number = doc.select("div.srtTools");
				if (number.size() > 0 && number.get(0).select("label") != null) {
					String reviewCount = number.get(0).select("label").html();
					reviewsCount = Integer.parseInt(reviewCount
							.substring(0,
									reviewCount.indexOf("reviews sorted by"))
							.trim().replaceAll(",", ""));
				}
				Elements ele = doc.select("div.reviewSelector");
				for (Element element : ele) {
					Feed feed = new Feed();

					// Get Author

					// Get Time
					Elements dates = element.select("span.ratingDate");

					if (null != dates && dates.size() > 0) {
						Element date = dates.get(0);
						String string = date.attr("title");
						if (string == "") {
							string = date
									.html()
									.substring(
											date.html().indexOf("Reviewed") + 9)
									.trim();
							System.out.println(string + "Formatted");
							feed.setFeedDate(new SimpleDateFormat(
									"d MMMM yyyy", Locale.ENGLISH)
									.parse(string));
						}

						else {
							feed.setFeedDate(new SimpleDateFormat(
									"d MMMM yyyy", Locale.ENGLISH)
									.parse(string));
						}

					}
					// Get Content
					StringBuffer content = new StringBuffer("");
					Elements quotes = element.select("div.quote");
					if (quotes.size() > 0 && quotes.get(0).select("a") != null) {

						// Get ID
						String Id = element.id().replace("review_", "");
						feed.setFeedId(Id);

						content.append(quotes.get(0).select("a").get(0).text());
						content.append(". ");
					}
					Elements realContents = element.select("p.partial_entry");
					if (realContents.size() > 0) {
						content.append(realContents.get(0).text());

					}
					feed.setEntityId(25);
					feed.setSubProductId(9);
					feed.setSourceId(2);
					feed.setFeedData(content.toString());
					System.out.println("Review is -- >" + content.toString());

				}

				if (feeds.size() >= 25) {
					System.out.println("Going to insert");

					MongoConnector.insertPostsForTripAdvisor((feeds));
					feeds = new ArrayList<Feed>();
				}
			}
			reviewsCounter += 10;
			if (reviewsCounter < reviewsCount) {
				url = url.replace("Reviews-", "Reviews-or" + reviewsCounter
						+ "-");
			}
		} while (reviewsCount != -1 && reviewsCounter != 0);
		return feeds;
	}

	public static void main(String[] args) throws IOException, ParseException {
		// Usuage:
		SentimentAnalysis.initialize();
		String url = "http://www.tripadvisor.com/Hotel_Review-g154998-d182928-Reviews-Marriott_Niagara_Falls_Fallsview_Hotel_Spa-Niagara_Falls_Ontario.htm";
		// String url =
		// "http://www.tripadvisor.com/Hotel_Review-g154998-d254839-Reviews-Niagara_Plaza_Hotel_Conference_Centre-Niagara_Falls_Ontario.html";
		searchTripAdvisor(url, "marriott");
	}

}
