package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.raremile.prd.inferlytics.cache.ApplicationCache;
import com.raremile.prd.inferlytics.database.MongoConnector;
import com.raremile.prd.inferlytics.entity.CommentsHtml;
import com.raremile.prd.inferlytics.entity.Post;
import com.raremile.prd.inferlytics.entity.Product;
import com.raremile.prd.inferlytics.entity.UsecaseDetails;
import com.raremile.prd.inferlytics.utils.ActivityLog;
import com.raremile.prd.inferlytics.utils.BusinessUtil;
import com.raremile.prd.inferlytics.utils.Util;

/**
 * Servlet implementation class GetPostsFromMongo
 */
public class GetPostsFromMongo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(GetPostsFromMongo.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetPostsFromMongo() {
		super();
		//
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		long startTime = System.currentTimeMillis();
		String brand = request.getParameter("entity");
		String subProduct = request.getParameter("subproduct");
		if (subProduct.equals("tescowinesnew")) {
			subProduct = "tescowines";
		}
		String word = request.getParameter("word");
		String skip = request.getParameter("skip");
		String reviewType = request.getParameter("reviewType");
		String showcomments = request.getParameter("showcomments");
		String limit = request.getParameter("limit");
		String productID = request.getParameter("productId");
		String subDimension = request.getParameter("subDimension");
		String category = request.getParameter("category");
		String mbProduct = request.getParameter("mbProduct");
		String priceRange = request.getParameter("price");
		String klwinesCategories = request.getParameter("klwinesCategory");
		String klwinesCountry= request.getParameter("selectedCountry");
		String klwinesSubRegion= request.getParameter("selectedSubRegion");
		ArrayList<String> klwinesCategoryList=new ArrayList<>();
		if (klwinesCategories != null && !klwinesCategories.isEmpty()) {
			String[] categories=klwinesCategories.split(",");
			int i = 0;
			while (i < categories.length) {
				klwinesCategoryList.add(categories[i]);
				i++;
			}
		}

		// get minMax Price by Brand
		Integer minPrice = null;
		Integer maxPrice = null;

		String currPrice =BusinessUtil.getCurrencyPriceByBrand(brand,subProduct);
		String priceString = currPrice.split(":")[1];
		if(!priceString.equals("null")){
			minPrice=0;
			maxPrice = Integer.parseInt(priceString);
		}
		if(null != priceRange && !"undefined".equals(priceRange)){
			String[] stringPriceArray =priceRange.split("-");
			minPrice =Integer.parseInt(stringPriceArray[0].trim());
			maxPrice =Integer.parseInt(stringPriceArray[1].trim());
		}
		UsecaseDetails ucb =  ApplicationCache.getUsecaseDetail(brand, subProduct);			 
		int entityId = ucb.getEntityId();
		int subProductId = ucb.getSubProductId();
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}

		LOG.debug(showcomments + ":showcomments");
		LOG.debug(brand + " " + subProduct + " " + word);
		PrintWriter writer = response.getWriter();
		String responseString = null;
		if (productID != null
				&& (subDimension != null && !subDimension.contains("undefined") && !subDimension
				.equals("null"))) {
			ActivityLog.addClientDetailstoLog(brand, subProduct, "feature",
					subDimension, word, productID, reviewType, null, ipAddress);

			Integer posPostsCount = 0;
			Integer negPostsCount = 0;
			List<Post> posts = new ArrayList<>();
			Map<String, String> feeds = new LinkedHashMap<>();
			if (Integer.parseInt(reviewType) == 2) {

				negPostsCount = 0;
				posts = MongoConnector.getPostIdsForFeatures(
						entityId,
						subProductId, subDimension, word,
						productID, Integer.parseInt(skip),
						Integer.parseInt(limit), true);
				posPostsCount = posts.size();
				MongoConnector.getFeedsByIds(posts);
			} else {
				posPostsCount = 0;

				posts = MongoConnector.getPostIdsForFeatures(
						entityId,
						subProductId, subDimension, word,
						productID, Integer.parseInt(skip),
						Integer.parseInt(limit), false);
				negPostsCount = posts.size();
				MongoConnector.getFeedsByIds(posts);

			}
			if (posPostsCount == 0 && negPostsCount == 0) {
				Util.appendSpanTag(feeds);
				responseString = new Gson().toJson(feeds);

			} else {
				// Generate json like structure with htmls here
				List<CommentsHtml> commentsHtml = BusinessUtil
						.getCommentsHtmlFromPosts(posts, word, subDimension,
								"-bw-");
				responseString = new Gson().toJson(commentsHtml);
			}


		}

		else if ((subDimension != null) && !subDimension.contains("undefined")
				&& !subDimension.equals("null")) {

			ActivityLog.addClientDetailstoLog(brand, subProduct, "feature",
					subDimension, word, null, null, null, ipAddress);

			List<Product> products = MongoConnector.getProductIdsForFeature(
					entityId,
					subProductId, word, subDimension,
					Integer.parseInt(skip), Integer.parseInt(limit), category,
					minPrice, maxPrice, klwinesCategoryList, klwinesCountry,
					klwinesSubRegion);
			// Generate json like structure with htmls here
			List<Product> productDetails = null;
			if ("nike".equals(brand)) {
				productDetails = MongoConnector.getProductDetailsById(products);
			} else {
				productDetails = MongoConnector.getProductDetails(products);
			}
			List<CommentsHtml> commentsHtml = BusinessUtil
					.getCommentsHtmlFromProdIds(productDetails, brand, word,
							subDimension, request.getParameter("subproduct"));
			responseString = new Gson().toJson(commentsHtml);

		}
		else if ((null == productID || "undefined".equals(productID) || productID
				.equals("null"))
				&& (null == showcomments || "undefined".equals(showcomments) || showcomments
				.equals("null")) && null != brand) {
			LOG.debug("Entered here -- Show Products For keywords");
			ActivityLog.addClientDetailstoLog(brand, subProduct, "keyword",
					null, word, null, null, null, ipAddress);
			List<Product> products = MongoConnector.getProductIdsForWord(
					entityId,
					subProductId, word,
					Integer.parseInt(skip), Integer.parseInt(limit), category);
			LOG.debug(" In block to show Products Ids Count=" + products.size());
			// Generate json like structure with htmls here
			List<Product> productDetails = MongoConnector
					.getProductDetails(products);

			Collections.reverse(productDetails);
			List<CommentsHtml> commentsHtml = BusinessUtil
					.getCommentsHtmlFromProdIds(productDetails, brand, word,
							null,null);
			responseString = new Gson().toJson(commentsHtml);

		}

		else {
			LOG.debug("ENTERED HERE -- to show comments for keywords");
			ActivityLog.addClientDetailstoLog(brand, subProduct, "keyword",
					subDimension, word, productID, reviewType, null, ipAddress);
			Integer posPostsCount = 0;
			Integer negPostsCount = 0;
			List<Post> posts = new ArrayList<>();
			Map<String, String> feeds = new LinkedHashMap<>();
			if (Integer.parseInt(reviewType) == 2) {
				negPostsCount = 0;
				posts = MongoConnector.getPostIdsForNouns(
						entityId,
						subProductId, word,
						Integer.parseInt(skip), productID, limit, true);
				posPostsCount = posts.size();
				MongoConnector.getFeedsByIds(posts);
			} else {
				posPostsCount = 0;
				posts = MongoConnector.getPostIdsForNouns(
						entityId,
						subProductId, word,
						Integer.parseInt(skip), productID, limit, false);
				negPostsCount = posts.size();
				MongoConnector.getFeedsByIds(posts);
			}
			if (posPostsCount == 0 && negPostsCount == 0) {
				Util.appendSpanTag(feeds);
				responseString = new Gson().toJson(feeds);
			} else {
				List<CommentsHtml> commentsHtml;
				// Generate json like structure with htmls here
				if (mbProduct != null && mbProduct.contains("true")) {
					commentsHtml = BusinessUtil.getCommentsHtmlFromPosts(posts,
							word, subDimension, "-mb-prd-");
				} else {
					commentsHtml = BusinessUtil.getCommentsHtmlFromPosts(posts,
							word, subDimension, "-pw-");
				}
				responseString = new Gson().toJson(commentsHtml);
			}
			LOG.debug(responseString);

		}

		writer.write(responseString);
		writer.flush();
		writer.close();
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		LOG.error(" Peformance message: Time Taken in nilliseconds is   "
				+ elapsedTime);
	}

}