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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.raremile.prd.inferlytics.database.InsertThread;
import com.raremile.prd.inferlytics.database.MongoConnector;
import com.raremile.prd.inferlytics.entity.Feed;
import com.raremile.prd.inferlytics.entity.Product;
import com.raremile.prd.inferlytics.preprocessing.RedisCacheManager;
import com.raremile.prd.inferlytics.utils.Util;

/**
 * @author pratyusha
 * @created 06-Sep-2013
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class MoltonBrownConnector {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(MoltonBrownConnector.class);

	private static final String brandName = "moltonbrown";

	private static final String productName = "moltonbrown";

	private static final String sourceName = "moltonbrown";

	private static List<DBObject> posts;
	private static Map<String, Integer> productReviewCounter;
	private static int reviewcounter = 0;

	public static void main(String[] s) throws IOException {

		// SentimentAnalysis.initialize();
		try {
			posts = new ArrayList<>();
			productReviewCounter = new HashMap<>();
			processDir("/home/pratyusha/C Drive/Projects/Sentiment Analysis/Data/MoltonBrown/Reviews_live_uk/8057-en_gb/reviewsPage/product");

			 MongoConnector.InsertObjectList(posts, "moltonbrownposts");
			LOG.info("moltonbrownposts count "+posts.size());
			LOG.info("Total review count "+reviewcounter);
			LOG.info("Products count : "+productReviewCounter.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static Connection.Response openURL(String url) throws IOException {
		Connection.Response res = Jsoup
				.connect(url.trim())
				.userAgent(
						"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
				.timeout(100000).ignoreHttpErrors(true)
				.referrer("http://www.google.com").execute();
		return res;
	}

	public static void processDir(String path) throws Exception {

		LOG.info("processing path" + path);

		File file = new File(path);

		if (file.list() != null) {

			for (int i = 0; i < file.list().length; i++) {
				File f = new File(path + File.separatorChar + file.list()[i]);
				if (f.isFile()) {
					processFile(f);
				} else if (f.getCanonicalFile().isDirectory()) {
					// do this again

					processDir(path + File.separatorChar + file.list()[i]);
				}
			}

		}
	}

	/*
	 * public static void processReviewFromFiles(String path) { File file = new
	 * File(path); List<Feed> feeds = new ArrayList<Feed>(); if (file.list() !=
	 * null) { for (int i = 0; i < file.list().length; i++) { File f = new
	 * File(path + File.separator + file.list()[i]); if (f.isFile()) {
	 * 
	 * processFile(f);
	 * 
	 * } else if (f.isDirectory()) { LOG.info("Oops.. Found a directory in " +
	 * path); }
	 * 
	 * if (feeds.size() >= 25) { InsertThread thread = new InsertThread();
	 * thread.setFeeds(feeds);
	 * thread.setDbMethodToInvoke("storePostsIntoStaging"); thread.start();
	 * 
	 * feeds = new ArrayList<Feed>(); }
	 * 
	 * }
	 * 
	 * InsertThread thread = new InsertThread(); thread.setFeeds(feeds);
	 * thread.setDbMethodToInvoke("storePostsIntoStaging"); thread.start(); } }
	 */

	/**
	 * @param feeds
	 * @param f
	 */
	private static void processFile(File f) {
		try {
			String reviewHtml = Util.readFile(f);

			getFeedsFromHtml(f.getName().split("\\.")[0], reviewHtml);

		} catch (IOException e) {

			LOG.error(
					"IOException while performing operation in fetchJsonFromFiles",
					e);
		}
	}

	/**
	 * @param string
	 * @param obj
	 * @return
	 */
	private static void getFeedsFromHtml(String prodId, String reviewHtml) {
		LOG.trace("Method: getFeedFromHtml called.");

		if (null != reviewHtml) {

			Document doc = Jsoup.parse(reviewHtml);
			Elements elements = doc.select("span.description");
			Elements summaryEle = doc.select("span.summary");
			int counter = 0;
			Product pp = RedisCacheManager.getProductById(prodId, brandName);
			if (null == pp) {
				LOG.error("PRODUCT NOT FOUND " + prodId);
			}
			if (!productReviewCounter.containsKey(prodId)) {
				productReviewCounter.put(prodId, 0);
			}else{
				counter = productReviewCounter.get(prodId);
			}
int thisFilecounter = 0;
			for (Element element : elements) {
				
				
				String content = summaryEle.get(thisFilecounter).html() + ". "
						+ element.html();
				
				counter++;
				DBObject postObj = new BasicDBObject();
				postObj.put("_id", prodId + ":" + counter);
				postObj.put("content",content);
				postObj.put("subpoductId", 10);

				// Opinion opinion = new Opinion();
				// opinion.setObject(brandName);
				// feed.setOpinion(opinion);
				// SentimentAnalysis.setOpinion(feed);
				// opinion.setOpinionHolder("");

				// LOG.info("comment --" + feed.getFeedData());

				posts.add(postObj);
				thisFilecounter++;
				reviewcounter++;
			}

			{
				productReviewCounter.put(prodId, counter);				
			}
		}

		LOG.trace("Method: getFeedFromHtml finished.");
		//LOG.info("Total REVIEWCOUNT " + reviewcounter);

	}
}