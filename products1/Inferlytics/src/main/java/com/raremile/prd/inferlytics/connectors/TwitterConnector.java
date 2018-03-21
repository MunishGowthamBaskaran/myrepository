package com.raremile.prd.inferlytics.connectors;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.entity.Feed;
import com.raremile.prd.inferlytics.entity.Opinion;
import com.raremile.prd.inferlytics.entity.QueryLevelCache;
import com.raremile.prd.inferlytics.exception.CriticalException;
import com.raremile.prd.inferlytics.exception.DAOException;
import com.raremile.prd.inferlytics.preprocessing.CacheManager;
import com.raremile.prd.inferlytics.sentiment.SentimentAnalysis;
import com.raremile.prd.inferlytics.utils.Util;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 
 * @author Pratyusha
 */
public class TwitterConnector {

	private static final Logger LOG = Logger.getLogger(TwitterConnector.class);
	private static final String BASEURL = "http://search.twitter.com/search.json";
	private static final String EMPTYJSON = "[]";
	private static String nextPage = null;

	public static List<Feed> searchTweets(String searchText) {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		
		LOG.info("Querying Twitter for "+searchText);
		params.add("q", searchText);
		params.add("rpp", "75");//response per page
		params.add("lang", "en");
		params.add("result_type", "recent");//mixed,recent,popular
		ClientResponse response = null;
		String responseText = null;
		List<Feed> parsedList = null;

		do {

			String searchUrl = null;
			if (nextPage != null) {
				searchUrl = BASEURL + nextPage;
			} else {
				searchUrl = BASEURL;
			}
			try {
				response = fireURL(searchUrl, ApplicationConstants.GET_METHOD,
						params, null, null);
			} catch (Exception ex) {
				LOG.error("Exception while fetching search tweets for "
						+ searchText + " Reason: " + ex.getMessage());
				throw new CriticalException(
						"Exception while fetching tweets for " + searchText
								+ " Reason: " + ex.getMessage());
			}
			int status = response.getStatus();
			LOG.debug("response: " + status);
			if (status == 200) {
				responseText = response.getEntity(String.class);
				try {/*
					 * long timeStart = System.currentTimeMillis(); parsedList =
					 * parseJson(searchText, responseText); long timeEnd =
					 * System.currentTimeMillis();
					 * LOG.info("Time taken to parse the feed is " + (timeEnd -
					 * timeStart)); // TODO DB Operation Here.. InsertFeedThread
					 * thread = new InsertFeedThread();
					 * thread.setParsedList(parsedList);
					 * thread.setSearchText(searchText); thread.start(); //
					 * Operator.getInstance().storePostBatch(parsedList,
					 * searchText); DAOFactory .getInstance(
					 * ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					 * .getLexiconDAO() .storePostBatch(parsedList, searchText,
					 * false);
					 */
				}
				catch(DAOException de){
					LOG.error("", de);
					nextPage = null;
					throw new CriticalException("DAO exception");
				}

				/*
				 * catch (JSONException e) { LOG.error("", e); nextPage = null;
				 * }
				 */

			} else {
				LOG.error("Could not fetch tweets for " + searchText
						+ ". Reason: " + response.getEntity(String.class));
			}
		} while (false);// TODO change this to nextPage != null
		return parsedList;

	}

	// fire URL
	private static ClientResponse fireURL(String url, int httpMethod,
			MultivaluedMap<String, String> params,
			MultivaluedMap<String, String> body, String type) {
		Client client = Client.create();
		LOG.info("URL: " + url);
		WebResource resource = client.resource(url);
		ClientResponse response = null;
		if (httpMethod == ApplicationConstants.GET_METHOD) {
			if (params != null) {
				response = resource.queryParams(params).get(
						ClientResponse.class);
			} else {
				response = resource.get(ClientResponse.class);
			}
		} else if (httpMethod == ApplicationConstants.POST_METHOD) {
			if (type != null) {
				response = resource.queryParams(params).type(type)
						.post(ClientResponse.class, body);
			} else {
				response = resource.queryParams(params).post(
						ClientResponse.class, body);
			}
		}
		return response;
	}

	private static List<Feed> parseJson(String query, String jsonText)
			throws JSONException {

		LOG.info("jsonText: " + jsonText);

		if (jsonText.equals(EMPTYJSON)) {
			return null;
		}
		JSONObject req = new JSONObject(jsonText);
		JSONArray recs = req.getJSONArray("results");
		QueryLevelCache.idWordList = CacheManager.getDimensionMapForEntity(query);

		QueryLevelCache.productSynonyms = DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getSentimentDAO().getSynonymsBySubProductId(0);
		// Fill Product Entity Stopword list also in cache here..
		QueryLevelCache.productBrandStopwordList = DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getLexiconDAO().getStopWordsForProduct(0, query, null);

		List<Feed> feeds = new ArrayList<Feed>();
		LOG.info("Feeds size " + recs.length() + " for " + nextPage);
		for (int i = 0; i < recs.length(); i++) {
			Feed feed = getFeedFromJson(query, recs.getJSONObject(i));
			feeds.add(feed);

		}
		// nextPage = req.getString("next_page");
		return feeds;
	}

	/**
	 * @param query
	 * @param jsonObj
	 * @param i
	 * @return
	 * @throws JSONException
	 */
	private static Feed getFeedFromJson(String query, JSONObject obj)
			throws JSONException {
		Feed feed = new Feed();
		feed.setFeedId(obj.getString("id"));
		feed.setFeedData(Jsoup.parse(obj.getString("text")).text());
		feed.setLangId(obj.getString("iso_language_code"));
		Opinion opinion = new Opinion();
		opinion.setObject(query);
		feed.setOpinion(opinion);
		SentimentAnalysis.setOpinion(feed);
		opinion.setOpinionHolder("");

		try {
			feed.setFeedDate(Util.getTwitterDateFromString(obj
					.getString("created_at")));
		} catch (ParseException e) {
			LOG.error("Error in date format " + e.getMessage(), e);

		}
		return feed;
	}

	private static List<Feed> fetchJsonFromFiles(String query, String path) {
		LOG.info("processing path" + path);
		File file = new File(path);
		List<Feed> feeds = new ArrayList<Feed>();
		if (file.list() != null) {

			for (int i = 0; i < file.list().length; i++) {
				File f = new File(path + File.separator + file.list()[i]);
				if (f.isFile()) {

					try {
						JSONObject obj = new JSONObject(Util.readFile(f));
						Feed feed = getFeedFromJson(query, obj);
						feeds.add(feed);
					} catch (JSONException e) {

						LOG.error(
								"JSONException while performing operation in fetchJsonFromFiles",
								e);
					} catch (IOException e) {

						LOG.error(
								"IOException while performing operation in fetchJsonFromFiles",
								e);
					}

				} else if (f.isDirectory()) {
					// Not doing anything as of now.
				}

			}
		}
		return feeds;
	}
	public static void main(String[] s) {
		SentimentAnalysis.initialize();
		List<Feed> feeds = searchTweets("marriot");

		LOG.info("Total number of feeds extracted: " + feeds.size());
	}

}
