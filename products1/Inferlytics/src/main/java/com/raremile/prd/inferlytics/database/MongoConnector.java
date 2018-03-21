/**
w *  * Copyright (c) 2013 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.commons.FilePropertyManager;
import com.raremile.prd.inferlytics.database.entity.EntityDimension;
import com.raremile.prd.inferlytics.entity.FeatureWords;
import com.raremile.prd.inferlytics.entity.Feed;
import com.raremile.prd.inferlytics.entity.KeyWord;
import com.raremile.prd.inferlytics.entity.KlwinesCategory;
import com.raremile.prd.inferlytics.entity.Opinion;
import com.raremile.prd.inferlytics.entity.Post;
import com.raremile.prd.inferlytics.entity.Product;
import com.raremile.prd.inferlytics.entity.ProductDetails;
import com.raremile.prd.inferlytics.entity.StoreProductDetails;
import com.raremile.prd.inferlytics.entity.SunBurstData;
import com.raremile.prd.inferlytics.entity.WordCount;
import com.raremile.prd.inferlytics.sentiment.SentimentAnalysis;
import com.raremile.prd.inferlytics.utils.BusinessUtil;
import com.raremile.prd.inferlytics.utils.ChartData;

/**
 * @author Pratyusha
 * @created Apr 10, 2013
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class MongoConnector extends MongoConnection {

	static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(MongoConnector.class);

	public static final Comparator<Product> PRODUCTS_ORDER = new Comparator<Product>() {

		@Override
		public int compare(Product o1, Product o2) {

			int retValue = 0;
			if (o1.getTotalCount() < o2.getTotalCount()) {
				retValue = -1;
			} else if (o1.getTotalCount() > o2.getTotalCount()) {
				retValue = 1;
			} /*
			 * else { // check for the productid if (o1.getProductId() != null
			 * && o2.getProductId() != null) { retValue =
			 * o1.getProductId().compareTo(o2.getProductId()); } }
			 */
			return retValue;
		}
	};

	public static final Comparator<Product> SIMILAR_WINES_ORDER = new Comparator<Product>() {

		@Override
		public int compare(Product o1, Product o2) {

			int retValue = 0;
			if (o1.getSimilarityScore() < o2.getSimilarityScore()) {
				retValue = -1;
			} else if (o1.getSimilarityScore() > o2.getSimilarityScore()) {
				retValue = 1;
			} /*
			 * else { // check for the productid if (o1.getProductId() != null
			 * && o2.getProductId() != null) { retValue =
			 * o1.getProductId().compareTo(o2.getProductId()); } }
			 */
			return retValue;
		}
	};

	/**
	 * @param featureWords
	 * @param product
	 */
	private static Product calculateScoreForEachProduct(
			List<FeatureWords> featureWordslist, Product product) {
		List<FeatureWords> productFeatureWords = product.getFeatureWords();
		Map<String, FeatureWords> categoryFeatureWordMap = new HashMap<>();
		for (FeatureWords featureWords : productFeatureWords) {
			categoryFeatureWordMap.put(featureWords.getFeature(), featureWords);
		}
		double totalScore = 0;
		List<FeatureWords> filteredFeatureWordListForProduct = new ArrayList<>();
		for (FeatureWords featureWords : featureWordslist) {
			String category = featureWords.getFeature();
			FeatureWords productCategoryWords = categoryFeatureWordMap
					.get(category);
			if (productCategoryWords != null) {

				List<WordCount> wordCountlist = featureWords.getWords();
				Map<String, WordCount> productWordCountMap = new HashMap<>();
				for (WordCount wordCount : productCategoryWords.getWords()) {
					productWordCountMap.put(wordCount.getWord(), wordCount);
				}
				double score = 0;
				List<WordCount> filteredwordCountList = new ArrayList<>();
				for (WordCount wordCount : wordCountlist) {
					int countOfWord = wordCount.getCount();
					WordCount productwordCount = productWordCountMap
							.get(wordCount.getWord());
					if (productwordCount != null) {
						int countOfWordProduct = productwordCount.getCount();
						int absDifference = Math.abs(countOfWordProduct
								- countOfWord);
						absDifference = (absDifference == 0 ? 1 : absDifference);
						score += countOfWordProduct
								* ((double) 1 / absDifference);
						filteredwordCountList.add(productwordCount);
					}
				}
				if (filteredwordCountList.size() != 0) {
					productCategoryWords.setWords(filteredwordCountList);
					filteredFeatureWordListForProduct.add(productCategoryWords);
				}
				int weightage = 1;
				if (category.contains("flavor") || category.contains("aroma")
						|| category.contains("body")) {
					weightage = 2;
				}
				totalScore += (score * weightage);
			}

		}

		product.setFeatureWords(filteredFeatureWordListForProduct);
		product.setSimilarityScore(totalScore);
		return product;
	}

	public static void getAllNounWords() throws IOException {

		DB db = null;
		File tsvFileForProductsFile = new File(
				"/home/mallikarjuna/data/klwines/adjectivewords.txt");
		if (!tsvFileForProductsFile.exists()) {
			tsvFileForProductsFile.createNewFile();
		}
		FileWriter tsvForProducts = new FileWriter(
				tsvFileForProductsFile.getAbsoluteFile(), true);
		BufferedWriter tsvProductsBW = new BufferedWriter(tsvForProducts);
		try {

			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection table = db.getCollection("klwinesNewWords");
			DBCursor cursor = table.find().sort(new BasicDBObject("count", -1));
			while (cursor.hasNext()) {
				DBObject objoutput = cursor.next();
				String nounword = objoutput.get("word").toString();
				int count = (Integer) objoutput.get("count");
				if (count > 2) {
					tsvProductsBW.write(nounword + " : " + count + "\n");
				}
				tsvProductsBW.flush();
			}

			// mongoClient.close();

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
				tsvProductsBW.close();
			}

		}

	}

	public static void getBlogsByIds(List<Post> posts) {

		List<ObjectId> ids = new ArrayList<>();
		for (Post post : posts) {
			ids.add(new ObjectId(post.getPostId()));
		}
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection table = db.getCollection("blog2");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("_id", new BasicDBObject("$in", ids));
			DBCursor cursor = table.find(whereQuery);
			while (cursor.hasNext()) {
				DBObject objoutput = cursor.next();

				for (Post post : posts) {
					if (post.getPostId()
							.equals(objoutput.get("_id").toString())) {

						post.setContent(objoutput.get("reviewContent")
								.toString());
						post.setTitle((String) objoutput.get("title"));
						post.setPermaLink((String) objoutput.get("url"));
						// fill date here
						break;
					}
				}
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

	}

	public static List<String> getCategoriesForProduct(String productId) {

		List<String> categories = null;
		DB db = null;
		try {

			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection table = db.getCollection("products");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("ProductId", productId);
			DBCursor cursor = table.find(whereQuery);
			while (cursor.hasNext()) {
				DBObject objoutput = cursor.next();
				categories = (List<String>) objoutput.get("Categories");

			}

			// mongoClient.close();

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();

			}

		}

		return categories;

	}

	public static String getCategoryForKlwines(String productId) {
		String prodName = "";
		DB db = null;
		try {

			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("products");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("ProductId", productId);
			DBObject obj = collections.findOne(whereQuery);

			if (obj != null) {

				prodName = (String) obj.get("Category");

			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);

		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return prodName;
	}

	/**
	 * Method returns the count of the Recent reviews(Used for pagination).
	 * 
	 * @param entityId
	 *            Entity Id
	 * @param subProductID
	 *            Sub Product Id
	 * @param fromDate
	 *            Start date
	 * @param toDate
	 *            End date
	 * @param isPositive
	 *            Indicates positive reviews or negative.
	 * @return
	 */
	public static int getcountRecentReviews(int entityId, int subProductID,
			Date fromDate, Date toDate, String isPositive) {
		DB db = null;
		int i = 0;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("posts");
			BasicDBObject match = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.append("EntityId", entityId);
			matchQuery.append("subpoductId", subProductID);
			if (isPositive.equals("true")) {
				matchQuery.append("Score", new BasicDBObject("$gt", 0));
			} else {
				matchQuery.append("Score", new BasicDBObject("$lt", 0));
			}
			if (fromDate != null) {
				matchQuery.append(
						"createdDate",
						BasicDBObjectBuilder.start("$gte", fromDate)
						.add("$lte", toDate).get());
			}
			match.put("$match", matchQuery);
			BasicDBObject groupQuery = new BasicDBObject();
			groupQuery.append("_id", null);
			groupQuery.append("reviewCount", new BasicDBObject("$sum", 1));
			BasicDBObject group = new BasicDBObject("$group", groupQuery);
			AggregationOutput output = collections.aggregate(match, group);
			Iterable<DBObject> result = output.results();
			if (result != null) {
				for (DBObject object : result) {
					i = (Integer) object.get("reviewCount");
				}
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured  ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return i;
	}

	/**
	 * @param subDimension
	 * @param synonymWord
	 * @return
	 */
	public static String getCountriesForKlWines(String subDimension,
			String synonymWord) {
		DB db = null;
		ArrayList<String> countrylist = new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");
			BasicDBObject match = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.put("SubProductId", 25);
			matchQuery.put("SubDimension", subDimension);
			if (!synonymWord.equals("null") && !synonymWord.equals("undefined")) {
				matchQuery.put("SynonymWord", synonymWord);
			}
			match.append("$match", matchQuery);
			BasicDBObject groupQuery1 = new BasicDBObject();
			groupQuery1.append("_id", "$Country");
			BasicDBObject group1 = new BasicDBObject("$group", groupQuery1);

			BasicDBObject groupQuery2 = new BasicDBObject();
			groupQuery2.append("_id", null);
			groupQuery2.append("Countries", new BasicDBObject("$push", "$_id"));
			BasicDBObject group2 = new BasicDBObject("$group", groupQuery2);
			LOG.info(match + "," + group1 + "," + group2);
			AggregationOutput output = collections.aggregate(match, group1,
					group2);

			Iterable<DBObject> result = output.results();
			if (result != null) {
				for (DBObject object : result) {
					countrylist = (ArrayList<String>) object.get("Countries");
					if (countrylist.contains(null)) {
						countrylist.remove(null);
						if (!countrylist.contains("Other International")) {
							countrylist.add("Other International");
						}
					}
				}
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured  ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

		return new Gson().toJson(countrylist);

	}

	/**
	 * @param postId
	 * @return
	 */
	private static List<String> getDimensionsForPost(String postId) {
		LOG.trace("Method: getDimensionsForPost called.");

		List<String> subDimList = null;

		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");
			// {$match:{PostId:"250562002:3"}},{$unwind:"$SubDimension"},{$group:{_id:"$SubDimension"}}
			List<DBObject> aggInput = new ArrayList<>();

			BasicDBObject matchquery = new BasicDBObject("PostId", postId);

			aggInput.add(new BasicDBObject("$match", matchquery));

			DBObject unwind = new BasicDBObject("$unwind", "$SubDimension");
			aggInput.add(unwind);

			DBObject group = new BasicDBObject("$group", new BasicDBObject(
					"_id", "$SubDimension"));
			aggInput.add(group);
			AggregationOutput output = collections.aggregate(aggInput);
			Iterable<DBObject> results = output.results();
			if (results != null) {
				subDimList = new ArrayList<>();
				for (DBObject dbObject : results) {
					String word = dbObject.get("_id").toString();
					subDimList.add(word);
				}

			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);

		} catch (MongoException ex) {
			// Exception never thrown

			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {

			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		LOG.trace("Method: getDimensionsForPost finished.");
		return subDimList;
	}

	private static List<EntityDimension> getEntityDimListFromMongoResult(
			AggregationOutput output, boolean isPositive, boolean forKeyword) {
		List<EntityDimension> entityDimList = null;
		Iterable<DBObject> results = output.results();
		if (results != null) {
			entityDimList = new ArrayList<>();
			EntityDimension entityDim = null;
			if (forKeyword) {
				for (DBObject dbObject : results) {
					entityDim = new EntityDimension();
					entityDim.setCount((int) dbObject
							.get("PosNegNounWeightage"));
					entityDim.setSenti(isPositive);
					entityDim.setWord((String) dbObject.get("_id"));
					entityDimList.add(entityDim);
				}
			} else {
				for (DBObject dbObject : results) {
					entityDim = new EntityDimension();
					entityDim.setCount((int) dbObject.get("PostCount"));
					entityDim.setTotalCount((int) dbObject.get("ProductCount"));
					entityDim.setSenti(isPositive);
					entityDim.setWord((String) dbObject.get("Word"));
					entityDim.setSubDimension((String) dbObject
							.get("SubDimension"));

					entityDimList.add(entityDim);
				}
			}
		}
		return entityDimList;
	}

	public static List<SunBurstData> getFeatureCommentCountForEntitySubProduct(
			int entityId, int subProductId, String category, Integer minPrice,
			Integer maxPrice, boolean ispositive) {
		List<SunBurstData> featureList = null;
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			/**
			 * db.features.aggregate({ "$match" : { "EntityId" : 417 ,
			 * "SubProductId" : 18}}, { "$unwind" : "$SubDimension"}, { "$group"
			 * : { "_id" : { "SubDimension" : "$SubDimension" , "Word" :
			 * "$SynonymWord"} , "PostIdScNo" : { "$addToSet" : { "PostId" :
			 * "$PostId" , "Sentence" : "$SentenceNo"}}}}, { "$project" : {
			 * "_id" : "$_id" , "PostIdScNo" : "$PostIdScNo"}}, { "$unwind" :
			 * "$PostIdScNo"}, { "$group" : { "_id" : "$_id" , "PostScCount" : {
			 * "$sum" : 1}}}, { "$project" : { "SubDimension" :
			 * "$_id.SubDimension" , "Word" : "$_id.Word" , "PostCount" :
			 * "$PostScCount"}}, { "$sort" : { "SubDimension" : 1 , "Word" :
			 * 1}})
			 */
			DBCollection collection = db.getCollection("features");
			BasicDBObject matchParameters = new BasicDBObject();
			matchParameters.put("EntityId", entityId);
			matchParameters.put("SubProductId", subProductId);

			if (category != null && !category.equals("null")
					&& !category.equals("undefined")) {
				matchParameters.put("Categories", category);
			}

			if (null != minPrice && maxPrice != null) {
				matchParameters.put("Price", new BasicDBObject("$gt", minPrice)
				.append("$lt", maxPrice));
			}
			if (ispositive) {
				matchParameters
				.put("OverallScore", new BasicDBObject("$gt", 0));
			} else {
				matchParameters
				.put("OverallScore", new BasicDBObject("$lt", 0));
			}

			BasicDBObject match = new BasicDBObject();
			match.put("$match", matchParameters);
			/**
			 * Unwind based on subdimension as it is an array
			 */
			DBObject unwind = new BasicDBObject("$unwind", "$SubDimension");
			/**
			 * Group based on subDim, Word, so that we can group those in java
			 * code later. Group based on PostId-sentenceNo as we are treating
			 * sentence as different comment here.
			 */
			DBObject groupfileds = new BasicDBObject("_id", new BasicDBObject(
					"SubDimension", "$SubDimension").append("Word",
							"$SynonymWord"));
			groupfileds
			.put("PostIdScNo",
					new BasicDBObject("$addToSet", new BasicDBObject(
							"PostId", "$PostId").append("OverallScore",
									"$OverallScore")));
			DBObject group = new BasicDBObject("$group", groupfileds);
			// Projection
			DBObject projection1 = new BasicDBObject("$project",
					new BasicDBObject("_id", "$_id").append("PostIdScNo",
							"$PostIdScNo"));
			/**
			 * unwind on postId-score, and again group it for counting
			 */
			// unwind with PostIdScore
			DBObject unwindByPostIdScore = new BasicDBObject("$unwind",
					"$PostIdScNo");
			// group again for counting PostId-score
			DBObject groupfileds2 = new BasicDBObject("_id", "$_id");
			groupfileds2.put("PostScCount", new BasicDBObject("$sum", 1));

			DBObject group2 = new BasicDBObject("$group", groupfileds2);
			// Projection
			DBObject projection2 = new BasicDBObject("$project",
					new BasicDBObject("SubDimension", "$_id.SubDimension")
			.append("Word", "$_id.Word").append("PostScCount",
					"$PostScCount"));

			DBObject sort = new BasicDBObject("$sort", new BasicDBObject(
					"SubDimension", 1).append("Word", 1));
			AggregationOutput output = collection.aggregate(match, unwind,
					group, projection1, unwindByPostIdScore, group2,
					projection2, sort);

			Iterable<DBObject> results = output.results();

			if (results != null) {
				featureList = new ArrayList<>();
			}
			SunBurstData subDim = null;
			int commentCount = 0;
			List<SunBurstData> WordList = null;

			for (DBObject dbObject : results) {

				if (null == subDim) {
					subDim = new SunBurstData();
					subDim.setName((String) dbObject.get("SubDimension"));
					WordList = new ArrayList<>();
				} else if (!subDim.getName().equals(
						dbObject.get("SubDimension"))) {
					subDim.setChildren(WordList);
					subDim.setSize(commentCount);
					WordList = new ArrayList<>();
					featureList.add(subDim);
					subDim = new SunBurstData();
					subDim.setName((String) dbObject.get("SubDimension"));
					commentCount = 0;
				}
				SunBurstData word = new SunBurstData();
				word.setName((String) dbObject.get("Word"));
				word.setSize((int) dbObject.get("PostScCount"));
				commentCount += word.getSize();
				WordList.add(word);
			}
			subDim.setChildren(WordList);
			subDim.setSize(commentCount);
			featureList.add(subDim);
			// Collections.sort(featureList, BusinessUtil.FEATURE_ORDER);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured while fetching fields. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return featureList;
	}

	/**
	 * This method calculates features- Word and ProductCount only . No
	 * negativePositive commentCounts
	 * 
	 * @param subProductId
	 * @param maxPrice
	 * @param minPrice
	 */
	public static JsonElement getFeatureProductCountForSubProduct(int entityId,
			int subProductId, String category, Integer minPrice,
			Integer maxPrice) {

		/**
		 * db.features.aggregate({ "$match" : { "EntityId" : 412 ,
		 * "SubProductId" : 10 }},{ "$unwind" : "$SubDimension"},{ "$group" : {
		 * "_id" : { "SubDimension" : "$SubDimension" , "Word" : "$SynonymWord"}
		 * , "ProductId": { "$addToSet" : "$ProductId"}}}, { "$unwind":
		 * "$ProductId"}, { "$group": { "_id":{ "SubDimension" :
		 * "$_id.SubDimension", "Word" : "$_id.Word"}, "ProductCount": { "$sum":
		 * 1}}}, { "$project" : { "SubDimension" : "$_id.SubDimension" , "Word"
		 * : "$_id.Word" , "ProductCount" : "$ProductCount"}},{ "$sort"
		 * :{"SubDimension":1, "ProductCount": -1}},{"$limit": 10})
		 */
		LOG.info("Entered getFeatureProductCountForSubProduct ");
		List<SunBurstData> featureList = null;
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");

			BasicDBObject matchParameters = new BasicDBObject();
			matchParameters.put("EntityId", entityId);
			matchParameters.put("SubProductId", subProductId);

			if (category != null && !category.equals("null")
					&& !category.equals("undefined")) {
				matchParameters.put("Categories", category);
			}

			if (null != minPrice && maxPrice != null) {
				matchParameters.put("Price", new BasicDBObject("$gt", minPrice)
				.append("$lt", maxPrice));
			}
			BasicDBObject match = new BasicDBObject();
			match.put("$match", matchParameters);
			DBObject unwind = new BasicDBObject("$unwind", "$SubDimension");

			DBObject groupfileds = new BasicDBObject("_id", new BasicDBObject(
					"SubDimension", "$SubDimension").append("Word",
							"$SynonymWord"));

			// groupfileds.put("TotalCount", new BasicDBObject("$sum", 1));
			groupfileds.put("ProductId", new BasicDBObject("$addToSet",
					"$ProductId"));

			DBObject group = new BasicDBObject("$group", groupfileds);

			DBObject unwindByProductId = new BasicDBObject("$unwind",
					"$ProductId");

			// Group2

			DBObject groupfileds2 = new BasicDBObject("_id", new BasicDBObject(
					"SubDimension", "$_id.SubDimension").append("Word",
							"$_id.Word"));
			groupfileds2.put("ProductCount", new BasicDBObject("$sum", 1));

			DBObject group2 = new BasicDBObject("$group", groupfileds2);

			// Projection
			DBObject projection = new BasicDBObject("$project",
					new BasicDBObject("SubDimension", "$_id.SubDimension")
			.append("Word", "$_id.Word").append("ProductCount",
					"$ProductCount"));
			DBObject sort = new BasicDBObject("$sort", new BasicDBObject(
					"SubDimension", 1).append("ProductCount", -1));
			LOG.info("Calling db here");
			AggregationOutput output = collections.aggregate(match, unwind,
					group, unwindByProductId, group2, projection, sort);
			Iterable<DBObject> results = output.results();

			if (results != null) {
				featureList = new ArrayList<>();
			}
			SunBurstData subDim = null;
			// int prodCount = 0;
			List<SunBurstData> WordList = null;
			Map<String, Integer> subDimenProductCount = getSubDimProductCount(
					entityId, subProductId, category, minPrice, maxPrice);
			for (DBObject dbObject : results) {

				if (null == subDim) {
					subDim = new SunBurstData();
					subDim.setName((String) dbObject.get("SubDimension"));
					subDim.setSize(subDimenProductCount.get(subDim.getName()));
					WordList = new ArrayList<>();
				} else if (!subDim.getName().equals(
						dbObject.get("SubDimension"))) {
					subDim.setChildren(WordList);
					WordList = new ArrayList<>();
					// subDim.setSize(prodCount);
					// prodCount = 0;
					featureList.add(subDim);
					subDim = new SunBurstData();
					subDim.setName((String) dbObject.get("SubDimension"));
					subDim.setSize(subDimenProductCount.get(subDim.getName()));
				}
				SunBurstData word = new SunBurstData();
				word.setName((String) dbObject.get("Word"));
				word.setSize((int) dbObject.get("ProductCount"));
				// prodCount += word.getSize();
				WordList.add(word);
			}
			subDim.setChildren(WordList);
			// subDim.setSize(prodCount);
			featureList.add(subDim);

			Collections.sort(featureList, BusinessUtil.FEATURE_ORDER);
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured while fetching fields. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return new Gson().toJsonTree(featureList);// .toJson(featureList);
	}

	public static List<EntityDimension> getFeaturesForEntitySubproducts(
			int entityId, int subProductId, String category, Integer minPrice,
			Integer maxPrice) {

		List<EntityDimension> entityDimList = new ArrayList<EntityDimension>();
		/**
		 * db.features.aggregate({ "$match" : { "EntityId" : 412 ,
		 * "SubProductId" : 10 }},{$unwind : "$SubDimension" },{ "$group": {
		 * _id: {"SubDimension" : "$SubDimension" , "Word" :
		 * "$SynonymWord"},"ProductId": { "$addToSet" : "$ProductId"},
		 * "PostIdScore"
		 * :{"$addToSet":{"PostId":"$PostId","Score":"$SentenceScore"
		 * }}}},{$unwind:"$ProductId"}, { "$group": { "_id":{ "SubDimension"
		 * :"$_id.SubDimension", "Word" : "$_id.Word"}, "ProductCount": {
		 * "$sum":1}, "PostIdScore": {$first:
		 * "$PostIdScore"}}},{"$unwind":"$PostIdScore"},{
		 * "$project":{"_id":"$_id"
		 * ,"ProductCount":"$ProductCount","PostIdScore":
		 * "$PostIdScore"}},{"$match": { "PostIdScore.Score" :
		 * {"$gt":0}}},{"$group":{ _id:"$_id","PostIds":{ "$addToSet":
		 * "$PostIdScore.PostId"
		 * },"ProductCount":{"$first":"$ProductCount"}}},{"$unwind"
		 * :"$PostIds"},{
		 * "$group":{"_id":"$_id","ProductCount":{"$first":"$ProductCount"
		 * },"PostCount"
		 * :{"$sum":1}}},{"$project":{"SubDimension":"$_id.SubDimension"
		 * ,"Word":"$_id.Word"
		 * ,"ProductCount":"$ProductCount","PostCount":"$PostCount"} },{
		 * "$sort": { SubDimension:1,Word:1}})
		 */
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");
			BasicDBObject matchParameters = new BasicDBObject();
			matchParameters.put("EntityId", entityId);
			matchParameters.put("SubProductId", subProductId);

			if (category != null && !category.equals("null")
					&& !category.equals("undefined")) {
				matchParameters.put("Categories", category);
			}
			if (null != minPrice && maxPrice != null) {
				matchParameters.put("Price", new BasicDBObject("$gt", minPrice)
				.append("$lt", maxPrice));
			}

			BasicDBObject matchByEntitySubProduct = new BasicDBObject();
			matchByEntitySubProduct.put("$match", matchParameters);

			DBObject unwind = new BasicDBObject("$unwind", "$SubDimension");

			DBObject groupfileds = new BasicDBObject("_id", new BasicDBObject(
					"SubDimension", "$SubDimension").append("Word",
							"$SynonymWord"));
			groupfileds.put("ProductId", new BasicDBObject("$addToSet",
					"$ProductId"));
			groupfileds.put("PostIdScore",
					new BasicDBObject("$addToSet", new BasicDBObject("PostId",
							"$PostId").append("Score", "$SentenceScore")));
			DBObject group = new BasicDBObject("$group", groupfileds);

			// unwind with ProductId

			DBObject unwindByProductId = new BasicDBObject("$unwind",
					"$ProductId");

			// Group2

			DBObject groupfileds2 = new BasicDBObject("_id", new BasicDBObject(
					"SubDimension", "$_id.SubDimension").append("Word",
							"$_id.Word"));
			groupfileds2.put("ProductCount", new BasicDBObject("$sum", 1));
			groupfileds2.put("PostIdScore", new BasicDBObject("$first",
					"$PostIdScore"));
			DBObject group2 = new BasicDBObject("$group", groupfileds2);

			// unwind with PostIdScore
			DBObject unwindByPostIdScore = new BasicDBObject("$unwind",
					"$PostIdScore");
			// Projection
			DBObject projection = new BasicDBObject("$project",
					new BasicDBObject("_id", "$_id").append("ProductCount",
							"$ProductCount").append("PostIdScore",
									"$PostIdScore"));
			// match

			BasicDBObject matchByScoregt = new BasicDBObject();
			matchByScoregt.put("$match", new BasicDBObject("PostIdScore.Score",
					new BasicDBObject("$gt", 0)));
			BasicDBObject matchByScorelt = new BasicDBObject();
			matchByScorelt.put("$match", new BasicDBObject("PostIdScore.Score",
					new BasicDBObject("$lt", 0)));
			// group 3
			DBObject groupfileds3 = new BasicDBObject("_id", "$_id");
			groupfileds3.put("PostIds", new BasicDBObject("$addToSet",
					"$PostIdScore.PostId"));
			groupfileds3.put("ProductCount", new BasicDBObject("$first",
					"$ProductCount"));
			DBObject group3 = new BasicDBObject("$group", groupfileds3);

			// unwind With PostId

			DBObject unwindByPostId = new BasicDBObject("$unwind", "$PostIds");

			// group 4
			DBObject groupfileds4 = new BasicDBObject("_id", "$_id");
			groupfileds4.put("PostCount", new BasicDBObject("$sum", 1));
			groupfileds4.put("ProductCount", new BasicDBObject("$first",
					"$ProductCount"));
			DBObject group4 = new BasicDBObject("$group", groupfileds4);

			// Projection
			DBObject projection2 = new BasicDBObject("$project",
					new BasicDBObject("SubDimension", "$_id.SubDimension")
			.append("Word", "$_id.Word")
			.append("ProductCount", "$ProductCount")
			.append("PostCount", "$PostCount"));

			DBObject sort = new BasicDBObject("$sort", new BasicDBObject(
					"SubDimension", 1).append("Word", 1));

			AggregationOutput posOutput = collections.aggregate(
					matchByEntitySubProduct, unwind, group, unwindByProductId,
					group2, unwindByPostIdScore, projection, matchByScoregt,
					group3, unwindByPostId, group4, projection2, sort);
			AggregationOutput negOutput = collections.aggregate(
					matchByEntitySubProduct, unwind, group, unwindByProductId,
					group2, unwindByPostIdScore, projection, matchByScorelt,
					group3, unwindByPostId, group4, projection2, sort);

			entityDimList = getEntityDimListFromMongoResult(posOutput, true,
					false);
			entityDimList.addAll(getEntityDimListFromMongoResult(negOutput,
					false, false));

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured while fetching fields. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

		return entityDimList;
	}

	public static String getFeatureWordsForKlwines(int entityId,
			int subProductId, String productId) {
		DB db = null;
		List<FeatureWords> featuresWordsList = new ArrayList<>();

		try {
			new HashMap<>();
			new LinkedHashSet<>();
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");

			List<DBObject> pipeLine = new ArrayList<>();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.put("EntityId", entityId);
			matchQuery.put("SubProductId", subProductId);
			matchQuery.put("ProductId", productId);

			BasicDBObject matchByEntitySubProduct = new BasicDBObject();
			matchByEntitySubProduct.put("$match", matchQuery);

			pipeLine.add(matchByEntitySubProduct);

			BasicDBObject unwind = new BasicDBObject("$unwind", "$SubDimension");
			pipeLine.add(unwind);
			DBObject groupFields1 = new BasicDBObject("_id", new BasicDBObject(
					"SubDimension", "$SubDimension").append("Word",
							"$SynonymWord").append("PostId", "$PostId"));
			DBObject group1 = new BasicDBObject("$group", groupFields1);
			pipeLine.add(group1);
			DBObject groupFields2 = new BasicDBObject("_id", new BasicDBObject(
					"SubDimension", "$_id.SubDimension").append("Word",
							"$_id.Word"));
			groupFields2.put("Count", new BasicDBObject("$sum", 1));
			DBObject group2 = new BasicDBObject("$group", groupFields2);
			pipeLine.add(group2);

			DBObject sort = new BasicDBObject();
			sort.put("$sort",
					new BasicDBObject("_id.SubDimension", 1).append("Count", 1));
			pipeLine.add(sort);

			DBObject groupFields3 = new BasicDBObject("_id",
					"$_id.SubDimension");
			groupFields3.put("WordCount",
					new BasicDBObject("$push", new BasicDBObject("word",
							"$_id.Word").append("count", "$Count")));
			DBObject group3 = new BasicDBObject("$group", groupFields3);
			pipeLine.add(group3);

			AggregationOutput output = collections.aggregate(pipeLine);
			Iterable<DBObject> results = output.results();

			if (results != null) {
				for (DBObject dbObject : results) {
					String category = dbObject.get("_id").toString();
					category = category.replace("wines for", "")
							.replace("wines by", "").replace("wine by", "");
					@SuppressWarnings("unchecked")
					List<BasicDBObject> wordswithCount = (List<BasicDBObject>) dbObject
					.get("WordCount");

					List<WordCount> wordswithCountList = new ArrayList<>();
					for (BasicDBObject word : wordswithCount) {
						WordCount wordCount = new WordCount();
						wordCount.setWord(word.getString("word"));
						wordCount.setCount(word.getInt("count"));
						wordswithCountList.add(wordCount);

					}
					FeatureWords featureWord = new FeatureWords();
					featureWord.setFeature(category);
					featureWord.setWords(wordswithCountList);
					featuresWordsList.add(featureWord);
				}
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured while fetching fields. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return new Gson().toJson(featuresWordsList);
	}

	public static DBObject getFeedsById(String id) {
		DBObject obj = null;
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection table = db.getCollection("posts");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("_id", id);
			obj = table.findOne(whereQuery);
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

		return obj;
	}

	public static List<Feed> getFeedsByIdRange(long idStart, long idEnd) {
		List<Feed> feeds = new ArrayList<Feed>();
		DB db = null;
		try {
			String query = "ZapposNIKE";
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection table = db.getCollection("posts");
			BasicDBObject gtQuery = new BasicDBObject();
			gtQuery.put("_id",
					new BasicDBObject("$gt", idStart).append("$lt", idEnd));
			DBCursor cursor = table.find(gtQuery);

			while (cursor.hasNext()) {
				Feed feed = new Feed();
				DBObject objoutput = cursor.next();
				// JSONObject obj = recs.getJSONObject(i);
				feed.setFeedId((String) objoutput.get("_id"));
				feed.setFeedData((String) objoutput.get("content"));

				Opinion opinion = new Opinion();
				opinion.setObject(query);
				feed.setOpinion(opinion);
				SentimentAnalysis.setOpinion(feed);
				opinion.setOpinionHolder("");

				feeds.add(feed);
			}

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

		return feeds;
	}

	public static void getFeedsByIds(List<Post> posts) {
		List<String> ids = new ArrayList<>();
		for (Post post : posts) {
			ids.add(post.getPostId());
		}
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection table = db.getCollection("posts");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("_id", new BasicDBObject("$in", ids));
			DBCursor cursor = table.find(whereQuery);
			while (cursor.hasNext()) {
				DBObject objoutput = cursor.next();

				for (Post post : posts) {
					if (post.getPostId().equals(objoutput.get("_id"))) {
						post.setContent((String) objoutput.get("content"));
						break;
					}
				}
			}

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

	}

	public static LinkedHashMap<String, String> getFeedsByIds(Set<String> ids) {
		LinkedHashMap<String, String> feeds = new LinkedHashMap<>();
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection table = db.getCollection("posts");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("_id", new BasicDBObject("$in", ids));
			DBCursor cursor = table.find(whereQuery);
			while (cursor.hasNext()) {
				DBObject objoutput = cursor.next();
				// feeds.add((String) objoutput.get("content"));
				feeds.put((String) objoutput.get("_id"),
						(String) objoutput.get("content"));
			}

		} catch (IOException ex) {

			LOG.error("", ex);
		} catch (MongoException ex) {

			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {

			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

		return feeds;
	}

	/**
	 * method returns the keywords for widgetservlet along with the positive and
	 * negatve count of the words occuring in the posts.
	 * 
	 * @param entityId
	 * @param subProductId
	 * @return EntityDimension contains the word along with its count.
	 */
	public static List<EntityDimension> getKewordsForEntitySubproduct(
			int entityId, int subProductId) {
		List<EntityDimension> entityDimList = null;
		DB db = null;
		try {

			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collection = db.getCollection("noundata");
			/**
			 * db.noundata.aggregate({$match: { EntityId:334,SubProductId:1}},
			 * {$group:{_id: "$NounWord", NounWeightage : { $sum : 1}, AdjScore
			 * :{ $push:"$AdjScore"}}}, { $sort :{ NounWeightage : -1}},{$unwind
			 * :"$AdjScore"}, { $match:{ AdjScore:{ $gt:0}}},{ $group :{_id: {
			 * NounWord:"$_id"},TotalCount:{ $first : "$NounWeightage"},
			 * PosNounWeightage : { $sum : 1}}},{$project :{NounWord
			 * :"$_id.NounWord", TotalCount : "$TotalCount", PosNounWeightage
			 * :"$PosNounWeightage"}},{ $sort :{ PosNounWeightage :
			 * -1}},{$limit:10})
			 */
			// create our pipeline operations, first with the $match
			BasicDBObject matchByEntitySubProduct = new BasicDBObject();
			matchByEntitySubProduct.put("$match", BasicDBObjectBuilder.start()
					.add("EntityId", entityId)
					.add("SubProductId", subProductId).get());

			// Now the $group operation
			DBObject groupFields = new BasicDBObject("_id", "$NounWord");
			groupFields.put("NounWeightage", new BasicDBObject("$sum", 1));
			groupFields
			.put("AdjScore", new BasicDBObject("$push", "$AdjScore"));
			DBObject group = new BasicDBObject("$group", groupFields);

			// Now sort operation
			DBObject sortOp = new BasicDBObject("$sort", new BasicDBObject(
					"NounWeightage", -1));

			// Now unwind operation
			DBObject unwindOp = new BasicDBObject("$unwind", "$AdjScore");

			// Greater condition match for AdjScore"
			DBObject adjScoreMatchgt = new BasicDBObject("$match",
					new BasicDBObject("AdjScore", new BasicDBObject("$gt", 0)));
			DBObject adjScoreMatchlt = new BasicDBObject("$match",
					new BasicDBObject("AdjScore", new BasicDBObject("$lt", 0)));

			// lessthan condition match for AdjScore"

			// Now the $group operation
			DBObject groupTwoFields = new BasicDBObject("_id", "$_id");
			groupTwoFields.put("NounWeightage", new BasicDBObject("$first",
					"$NounWeightage"));
			groupTwoFields.put("PosNegNounWeightage", new BasicDBObject("$sum",
					1));

			DBObject groupTwo = new BasicDBObject("$group", groupTwoFields);

			/*
			 * // build the $projection operation DBObject fields = new
			 * BasicDBObject("NounWord", "$_id.NounWord");
			 * fields.put("NounWeightage", "$NounWeightage");
			 * fields.put("PosNegNounWeightage", "$PosNegNounWeightage");
			 * DBObject project = new BasicDBObject("$project", fields); // Now
			 * sort operation for positive descending DBObject sortOpPos = new
			 * BasicDBObject("$sort", new BasicDBObject( "PosNegNounWeightage",
			 * -1)); // Now sort operation for positive descending DBObject
			 * sortOpNeg = new BasicDBObject("$sort", new BasicDBObject(
			 * "PosNegNounWeightage", -1));
			 */

			// Limit for testing
			DBObject limit = new BasicDBObject("$limit", 100);

			// run aggregation
			AggregationOutput posOutput = collection.aggregate(
					matchByEntitySubProduct, group, sortOp, limit, unwindOp,
					adjScoreMatchgt, groupTwo, sortOp);
			AggregationOutput negOutput = collection.aggregate(
					matchByEntitySubProduct, group, sortOp, limit, unwindOp,
					adjScoreMatchlt, groupTwo, sortOp);

			entityDimList = getEntityDimListFromMongoResult(posOutput, true,
					true);
			entityDimList.addAll(getEntityDimListFromMongoResult(negOutput,
					false, true));

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return entityDimList;

	}

	/**
	 * Returns the keywords for subProduct along with count of the number of
	 * products that contain the word in its posts.
	 * 
	 * @param entityId
	 * @param subProductId
	 * @return entitydimension list containing the words and their count.
	 */

	public static List<EntityDimension> getKeywordsForEntitySubproducts(
			int entityId, int subProductId, String category) {

		List<EntityDimension> entityDimList = new ArrayList<EntityDimension>();
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("noundata");
			BasicDBObject match = new BasicDBObject();
			match.put("EntityId", entityId);
			match.put("SubProductId", subProductId);
			if (category != null && !category.equals("null")
					&& !category.equals("undefined")) {
				match.put("Categories", category);
			}
			BasicDBObject matchByEntitySubProduct = new BasicDBObject();
			matchByEntitySubProduct.put("$match", match);

			DBObject groupFields = new BasicDBObject("_id", "$SynonymWord");
			groupFields.put("NounWeightage", new BasicDBObject("$sum", 1));
			groupFields.put("ProductId", new BasicDBObject("$addToSet",
					"$ProductId"));
			DBObject group = new BasicDBObject("$group", groupFields);

			DBObject unwind = new BasicDBObject("$unwind", "$ProductId");

			DBObject groupTwoFields = new BasicDBObject("_id", "$_id");
			groupTwoFields.put("TotalCount", new BasicDBObject("$sum", 1));
			DBObject sortOp2 = new BasicDBObject("$sort", new BasicDBObject(
					"TotalCount", -1));
			DBObject limit = new BasicDBObject("$limit", 100);

			DBObject groupTwo = new BasicDBObject("$group", groupTwoFields);
			AggregationOutput output = collections.aggregate(
					matchByEntitySubProduct, group, unwind, groupTwo, sortOp2,
					limit);
			Iterable<DBObject> results = output.results();
			if (results != null) {
				for (DBObject result : results) {

					EntityDimension entity = new EntityDimension();
					entity.setCount((int) result.get("TotalCount"));
					entity.setWord((String) result.get("_id"));
					entityDimList.add(entity);

				}
			}

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

		return entityDimList;
	}

	/**
	 * @param postId
	 * @return
	 */
	private static List<String> getKeywordsForPost(String postId) {
		LOG.trace("Method: getKeywordsForPost called.");
		List<String> keywords = null;

		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("PostId", postId);
			DBCursor cursor = collections.find(whereQuery, new BasicDBObject(
					"SynonymWord", 1));
			if (cursor != null) {
				keywords = new ArrayList<>();
				while (cursor.hasNext()) {
					DBObject objoutput = cursor.next();
					String word = objoutput.get("SynonymWord").toString();
					if (!keywords.contains(word)) {
						keywords.add(word);
					}

				}
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);

		} catch (MongoException ex) {
			// Exception never thrown

			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {

			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

		LOG.trace("Method: getKeywordsForPost finished.");
		return keywords;
	}

	public static KlwinesCategory getklwinesCategoryCount(String subDimension,
			String synonymWord, String klwinesCountry, String klwinessubRegion) {
		DB db = null;
		KlwinesCategory klwinesCategory = new KlwinesCategory();

		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");
			BasicDBObject matchParameters = new BasicDBObject();
			matchParameters.put("SubProductId", 25);
			matchParameters.put("SubDimension", subDimension);
			if (!synonymWord.equals("null") && !synonymWord.equals("undefined")) {
				matchParameters.put("SynonymWord", synonymWord);
			}
			if (!klwinesCountry.equals("All")) {
				if (klwinesCountry.equals("Other International")) {
					matchParameters.put("Country", null);
				} else {
					matchParameters.put("Country", klwinesCountry);
				}
			}
			if (!klwinessubRegion.equals("All")) {
				if (klwinessubRegion.equals("Others")) {
					matchParameters.put("SubRegion", null);
				} else {
					matchParameters.put("SubRegion", klwinessubRegion);
				}
			}
			BasicDBObject match = new BasicDBObject();
			match.put("$match", matchParameters);
			DBObject groupfields1 = new BasicDBObject("_id", new BasicDBObject(
					"ProductId", "$ProductId").append("Varietal", "$Varietal"));
			DBObject group1 = new BasicDBObject("$group", groupfields1);
			DBObject groupfields2 = new BasicDBObject("_id", "$_id.Varietal")
			.append("Count", new BasicDBObject("$sum", 1));
			DBObject group2 = new BasicDBObject("$group", groupfields2);
			AggregationOutput output = collections.aggregate(match, group1,
					group2);
			LOG.info(match + "," + group1 + "," + group2);

			Iterable<DBObject> results = output.results();

			for (DBObject dbObject : results) {
				if (dbObject.get("_id").toString().equals("red")) {
					klwinesCategory.setRedWinesCount((int) dbObject
							.get("Count"));
				} else if (dbObject.get("_id").toString().equals("white")) {
					klwinesCategory.setWhiteWinesCount((int) dbObject
							.get("Count"));
				} else {
					klwinesCategory.setOtherWinesCount((int) dbObject
							.get("Count"));
				}

			}

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured while fetching fields. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

		return klwinesCategory;
	}

	/**
	 * Returns the Name and Image url for a given Product and inserts into the
	 * product that is passed.
	 * 
	 * @param productId
	 *            ProductId of the product.
	 * @param product
	 *            Product object where name and url will be inserted into.
	 * @param isNike
	 *            Indicates if it is Nike Brand.
	 */
	public static void getNameUrl(String productId, ProductDetails product,
			Boolean isNike) {
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("products");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("ProductId",
					java.util.regex.Pattern.compile(productId));
			DBObject obj = collections.findOne(whereQuery);
			if (obj != null) {
				product.setName((String) obj.get("ProductName"));
				product.setPrice(Double.valueOf(obj.get("Price").toString()));
				String imgURL;
				if (isNike) {
					imgURL = (String) obj.get("ProductImgLargeUrl");
				} else {
					imgURL = (String) obj.get("ProdImgUrl");
				}

				/* To get the larger size images */
				imgURL = imgURL.replace("M.", "L.");

				// For klwines.
				imgURL = imgURL.replace("m.jpg", "l.jpg");

				// For tescowines.
				imgURL = imgURL.replace("IDShot_126x150.jpg",
						"IDShot_150x300.jpg");
				product.setImgURL(imgURL);
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);

		} catch (MongoException ex) {
			// Exception never thrown

			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {

			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
	}

	public static ArrayList<KeyWord> getNegativeKeyWords(String productId,
			String negCount, int skip, ProductDetails product, int entityId,
			int subProductId) {
		DB db = null;
		ArrayList<KeyWord> negKeywords = new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");
			BasicDBObject matchByEntitySubProduct = new BasicDBObject();
			DBObject matchQuery = new BasicDBObject("ProductId", productId);
			matchQuery.put("SentenceScore", new BasicDBObject("$lt", 0));
			matchQuery.put("SubProductId", subProductId);
			matchQuery.put("EntityId", entityId);
			matchByEntitySubProduct.put("$match", matchQuery);
			DBObject groupdata = new BasicDBObject();
			groupdata.put("SynonymWord", "$SynonymWord");
			groupdata.put("PostId", "$PostId");
			DBObject groupFields = new BasicDBObject("_id", groupdata);
			DBObject groupFields2 = new BasicDBObject("_id", "$_id.SynonymWord");
			groupFields2.put("Count", new BasicDBObject("$sum", 1));
			DBObject group = new BasicDBObject("$group", groupFields);
			DBObject group2 = new BasicDBObject("$group", groupFields2);
			DBObject skipField = new BasicDBObject("$skip", skip);
			DBObject sortOp = new BasicDBObject("$sort", new BasicDBObject(
					"Count", -1).append("_id", 1));
			AggregationOutput ids = null;
			if (skip == 0) {
				ids = collections.aggregate(matchByEntitySubProduct, group,
						group2, sortOp, skipField);
				LOG.info(matchByEntitySubProduct + "," + group + "," + group2
						+ "," + sortOp + "," + skipField);
			} else {
				DBObject limit = new BasicDBObject("$limit",
						Integer.parseInt(negCount));
				ids = collections.aggregate(matchByEntitySubProduct, group,
						group2, sortOp, skipField, limit);

			}
			Iterable<DBObject> results = ids.results();

			if (results != null) {
				int totnegCount = 0;
				for (DBObject dbObject : results) {

					KeyWord keyword = new KeyWord();
					keyword.setKeyWord((String) dbObject.get("_id"));
					keyword.setcount((Integer) dbObject.get("Count"));
					negKeywords.add(keyword);

					totnegCount++;
				}
				product.setTotnegCount(totnegCount);
			}

			/*
			 * db.noundata.aggregate({$match: { EntityId:334,SubProductId:1,
			 * NounWord:'foot',AdjScore:{$gt:0}}}, { $sort :{ AdjScore : -1}},
			 * {$project :{PostIds:"$PostId"}},{ $skip : 200}, { $limit : 10})
			 */

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);

		} catch (MongoException ex) {
			// Exception never thrown

			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {

			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return negKeywords;
	}

	public static void getNounCountForSubProduct(int subProductId) {
		StringBuilder csv = null;
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collection = db.getCollection("noundata");
			/**
			 * db.noundata.aggregate({$match: { subproductId:13}}, {$group:{_id:
			 * "$NounWord", NounWeightage : { $sum : 1}}}, { $sort :{
			 * NounWeightage : -1}});
			 */
			// create our pipeline operations, first with the $match
			BasicDBObject matchBySubProduct = new BasicDBObject();
			matchBySubProduct.put(
					"$match",
					BasicDBObjectBuilder.start()
					.add("SubProductId", subProductId).get());

			// Now the $group operation
			DBObject groupFields = new BasicDBObject("_id", "$NounWord");
			groupFields.put("NounWeightage", new BasicDBObject("$sum", 1));

			DBObject group = new BasicDBObject("$group", groupFields);

			// Now sort operation
			DBObject sortOp = new BasicDBObject("$sort", new BasicDBObject(
					"NounWeightage", -1));

			// Limit for testing
			// DBObject limit = new BasicDBObject("$limit", 100);

			// run aggregation
			AggregationOutput output = collection.aggregate(matchBySubProduct,
					group, sortOp);

			Iterable<DBObject> results = output.results();
			csv = new StringBuilder();
			if (results != null) {
				csv.append("Noun,Count").append("\n");

				for (DBObject dbObject : results) {
					csv.append(dbObject.get("_id")).append(",")
					.append(dbObject.get("NounWeightage"));
					csv.append("\n");
				}
			}
			try {

				File file = new File(
						"/home/pratyusha/C Drive/Projects/Sentiment Analysis/Data/nounResult_nikemenshoes.csv");

				if (file.createNewFile()) {
					System.out.println("File is created!");
				} else {
					System.out.println("File already exists.");
				}
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(csv.toString());
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

	}

	public static ArrayList<KeyWord> getPositiveKeyWords(String productId,
			String posCount, int skip, ProductDetails product, int entityId,
			int subProductId) {
		DB db = null;
		ArrayList<KeyWord> posKeywords = new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");
			BasicDBObject matchByEntitySubProduct = new BasicDBObject();
			DBObject matchQuery = new BasicDBObject("EntityId", entityId);
			matchQuery.put("SubProductId", subProductId);
			matchQuery.put("ProductId", productId);
			matchQuery.put("SentenceScore", new BasicDBObject("$gt", 0));
			matchByEntitySubProduct.put("$match", matchQuery);
			DBObject groupdata = new BasicDBObject();
			groupdata.put("SynonymWord", "$SynonymWord");
			groupdata.put("PostId", "$PostId");
			DBObject groupFields = new BasicDBObject("_id", groupdata);
			DBObject groupFields2 = new BasicDBObject("_id", "$_id.SynonymWord");
			groupFields2.put("Count", new BasicDBObject("$sum", 1));
			DBObject group = new BasicDBObject("$group", groupFields);
			DBObject group2 = new BasicDBObject("$group", groupFields2);

			DBObject sortOp = new BasicDBObject("$sort", new BasicDBObject(
					"Count", -1).append("_id", 1));
			DBObject skipField = new BasicDBObject("$skip", skip);
			AggregationOutput ids = null;

			if (skip == 0) {
				ids = collections.aggregate(matchByEntitySubProduct, group,
						group2, sortOp, skipField);
				LOG.info(matchByEntitySubProduct + "," + group + "," + group2
						+ "," + sortOp + "," + skipField);
			} else {
				DBObject limit = new BasicDBObject("$limit",
						Integer.parseInt(posCount));
				ids = collections.aggregate(matchByEntitySubProduct, group,
						group2, sortOp, skipField, limit);
				LOG.info(matchByEntitySubProduct + "," + group + "," + group2
						+ "," + sortOp + "," + skipField + "," + limit);

			}

			Iterable<DBObject> results = ids.results();

			if (results != null) {
				int totposCount = 0;
				for (DBObject dbObject : results) {

					KeyWord keyword = new KeyWord();
					keyword.setKeyWord((String) dbObject.get("_id"));
					keyword.setcount((Integer) dbObject.get("Count"));
					posKeywords.add(keyword);

					totposCount++;
				}
				product.setTotposCount(totposCount);
			}

			/*
			 * db.noundata.aggregate({$match: { EntityId:334,SubProductId:1,
			 * NounWord:'foot',AdjScore:{$gt:0}}}, { $sort :{ AdjScore : -1}},
			 * {$project :{PostIds:"$PostId"}},{ $skip : 200}, { $limit : 10})
			 */

			// mongoClient.close();

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);

		} catch (MongoException ex) {
			// Exception never thrown

			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {

			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return posKeywords;
	}

	/**
	 * Fetches the post ids for the particular product and particular
	 * subDimension and the word that is passed.
	 * 
	 * @param entityId
	 * @param subProductId
	 * @param subDimension
	 * @param word
	 * @param productId
	 *            Specific Product
	 * @param skip
	 * @param limit
	 * @param ispositive
	 *            indicates to fetch positive or negative comment.
	 * @return returns the map containing the postid as key and corresponding
	 *         productId as value.
	 */
	public static List<Post> getPostIdsForFeatures(int entityId,
			int subProductId, String subDimension, String word,
			String productId, int skip, int limit, boolean ispositive) {
		List<Post> posts = new ArrayList<>();
		DB db = null;
		try {

			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");

			BasicDBObject matchByEntitySubProduct = new BasicDBObject();

			DBObject matchQuery = new BasicDBObject("EntityId", entityId);
			matchQuery.put("SubProductId", subProductId);
			if (subDimension != null && !subDimension.equals("null")) {
				matchQuery.put("SubDimension", subDimension);
			}
			if (productId != null) {
				matchQuery.put("ProductId", productId);
			}
			if (word != null && !word.equals("null")) {
				matchQuery.put("SynonymWord", word);
			}
			if (ispositive) {
				matchQuery.put("SentenceScore", new BasicDBObject("$gt", 0));

			} else {
				matchQuery.put("SentenceScore", new BasicDBObject("$lt", 0));
			}

			matchByEntitySubProduct.put("$match", matchQuery);

			DBObject groupFields = new BasicDBObject("_id", new BasicDBObject(
					"PostId", "$PostId"));
			groupFields.put("Word", new BasicDBObject("$first", "$Word"));
			groupFields.put("SentenceNo", new BasicDBObject("$first",
					"$SentenceNo"));
			groupFields.put("SentenceScore", new BasicDBObject("$first",
					"$SentenceScore"));

			DBObject group = new BasicDBObject("$group", groupFields);

			DBObject sortOrder;
			if (ispositive) {
				sortOrder = new BasicDBObject("SentenceScore", -1);

			} else {
				sortOrder = new BasicDBObject("SentenceScore", 1);

			}

			DBObject sortOp = new BasicDBObject("$sort", sortOrder);
			DBObject projection;

			projection = new BasicDBObject("$project", new BasicDBObject(
					"PostId", "$_id.PostId").append("Word", "$Word").append(
							"SentenceNo", "$SentenceNo"));

			// .append("OverallScore","$OverallScore"));
			DBObject skipposts = new BasicDBObject("$skip", skip);
			DBObject limits = new BasicDBObject("$limit", limit);

			LOG.info(matchByEntitySubProduct + "," + group + ","
					+ sortOp + "," + projection + "," + skipposts + ","
					+ limits);
			AggregationOutput ids = collections.aggregate(
					matchByEntitySubProduct, group, sortOp, projection,
					skipposts, limits);

			Iterable<DBObject> results = ids.results();
			if (results != null) {

				for (DBObject dbObject : results) {
					Post post = new Post();
					post.setPostId((String) dbObject.get("PostId"));
					post.setWord((String) dbObject.get("Word"));
					if (null != dbObject.get("SentenceNo")) {
						post.setSentenceNo((int) dbObject.get("SentenceNo"));
					}
					post.setPositive(ispositive);
					posts.add(post);
				}
			}

			/*
			 * db.noundata.aggregate({$match: { EntityId:334,SubProductId:1,
			 * NounWord:'foot',AdjScore:{$gt:0}}}, { $sort :{ AdjScore : -1}},
			 * {$project :{PostIds:"$PostId"}},{ $skip : 200}, { $limit : 10})
			 */

		} catch (IOException ex) {
			// Unreachable code
			System.out.println(ex);
		} catch (MongoException ex) {
			// Exception never thrown
			System.out.println("MongoException occured during batch insert. "
					+ ex);
		} catch (Exception ex) {
			// Handle exception
			System.out.println(ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

		return posts;
	}

	/**
	 * Fetches the comment Ids for the specific noun word.
	 * 
	 * @param entityId
	 * @param subProductId
	 * @param word
	 * @param skip
	 * @param productId
	 * @param limit
	 * @param ispositive
	 *            Indicates positive or negative reviews.
	 * @return Returns the set of postIds.
	 */
	public static List<Post> getPostIdsForNouns(int entityId, int subProductId,
			String word, int skip, String productId, String limit,
			boolean ispositive) {
		List<Post> posts = new ArrayList<>();
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");

			BasicDBObject matchByEntitySubProduct = new BasicDBObject();

			DBObject matchQuery = new BasicDBObject("EntityId", entityId);
			matchQuery.put("SubProductId", subProductId);
			matchQuery.put("SynonymWord", word);

			if (null != productId) {

				matchQuery.put("ProductId", productId);
			}
			if (ispositive) {
				matchQuery.put("SentenceScore", new BasicDBObject("$gt", 0));
			} else {
				matchQuery.put("SentenceScore", new BasicDBObject("$lt", 0));
			}

			matchByEntitySubProduct.put("$match", matchQuery);

			DBObject groupFields = new BasicDBObject("_id", new BasicDBObject(
					"PostId", "$PostId"));
			groupFields.put("Word", new BasicDBObject("$first", "$Word"));
			DBObject group = new BasicDBObject("$group", groupFields);
			DBObject sortOp = new BasicDBObject("$sort", new BasicDBObject(
					"SentenceScore", -1));
			DBObject projection = new BasicDBObject("$project",
					new BasicDBObject("PostId", "$_id.PostId").append("Word",
							"$Word"));
			DBObject skipposts = new BasicDBObject("$skip", skip);
			DBObject limits = new BasicDBObject("$limit",
					Integer.parseInt(limit));

			AggregationOutput ids = collections.aggregate(
					matchByEntitySubProduct, group, sortOp, projection,
					skipposts, limits);

			Iterable<DBObject> results = ids.results();

			if (results != null) {

				for (DBObject dbObject : results) {
					Post post = new Post();
					post.setPostId((String) dbObject.get("PostId"));
					post.setWord((String) dbObject.get("Word"));
					post.setPositive(ispositive);
					posts.add(post);

				}
			}

			/*
			 * db.noundata.aggregate({$match: { EntityId:334,SubProductId:1,
			 * NounWord:'foot',AdjScore:{$gt:0}}}, { $sort :{ AdjScore : -1}},
			 * {$project :{PostIds:"$PostId"}},{ $skip : 200}, { $limit : 10})
			 */

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

		return posts;
	}

	/**
	 * @param productId
	 * @return
	 */
	private static double getPriceForProduct(String productId) {
		DB db = null;
		double price = 0;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection table = db.getCollection("products");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("ProductId", productId);
			DBObject object = table.findOne(whereQuery);
			if (object != null) {
				price = Double.valueOf(object.get("Price").toString());
			}

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return price;

	}

	/**
	 * Returns the product details stored in mongo.
	 * 
	 * @param ids
	 *            Product ids for identifying products .
	 * @return list of products containing details like url,name.
	 */
	public static List<Product> getProductDetails(List<Product> products) {

		List<String> list = new ArrayList<String>();

		for (Product product : products) {
			list.add(product.getProductId());
		}
		String ids[] = list.toArray(new String[list.size()]);
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection table = db.getCollection("products");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("ProductId", new BasicDBObject("$in", ids));
			DBCursor cursor = table.find(whereQuery);
			while (cursor.hasNext()) {
				DBObject objoutput = cursor.next();
				// feeds.add((String) objoutput.get("content"));

				for (Product product : products) {
					if (product.getProductId().equals(
							objoutput.get("ProductId"))) {
						product.setImageUrl((String) objoutput
								.get("ProdImgUrl"));
						product.setProductName((String) objoutput
								.get("ProductName"));
						product.setProductUrl((String) objoutput
								.get("ProductUrl"));
					}
				}
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return products;
	}

	public static List<Product> getProductDetailsById(List<Product> products) {
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection table = db.getCollection("products");

			for (Product product : products) {
				BasicDBObject whereQuery = new BasicDBObject();
				whereQuery
				.put("ProductId", java.util.regex.Pattern
						.compile(product.getProductId()));
				DBObject objoutput = table.findOne(whereQuery);
				product.setImageUrl((String) objoutput.get("ProductImgUrl"));
				product.setProductName((String) objoutput.get("ProductName"));
				product.setProductUrl((String) objoutput.get("ProductUrl"));
			}
			/*
			 * DBCursor cursor = table.find(whereQuery); while
			 * (cursor.hasNext()) { DBObject objoutput = cursor.next(); //
			 * feeds.add((String) objoutput.get("content")); for (Product
			 * product : products) { if (product.getProductId().equals( (String)
			 * objoutput.get("ProductId"))) { product.setImageUrl((String)
			 * objoutput .get("ProdImgUrl")); product.setProductName((String)
			 * objoutput .get("ProductName")); product.setProductUrl((String)
			 * objoutput .get("ProductUrl")); } } }
			 */
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return products;
	}

	public static List<Product> getProductDetailsByIdForKlwines(
			List<Product> products) {
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection table = db.getCollection("products");
			for (Product product : products) {
				BasicDBObject whereQuery = new BasicDBObject();
				whereQuery
				.put("ProductId", java.util.regex.Pattern
						.compile(product.getProductId()));
				DBObject objoutput = table.findOne(whereQuery);
				product.setImageUrl((String) objoutput.get("ProdImgUrl"));
				product.setProductName((String) objoutput.get("ProductName"));
				product.setProductUrl((String) objoutput.get("ProductUrl"));
				if (objoutput.get("Price") != null) {
					product.setPrice(objoutput.get("Price").toString());
				}

			}
			/*
			 * DBCursor cursor = table.find(whereQuery); while
			 * (cursor.hasNext()) { DBObject objoutput = cursor.next(); //
			 * feeds.add((String) objoutput.get("content")); for (Product
			 * product : products) { if (product.getProductId().equals( (String)
			 * objoutput.get("ProductId"))) { product.setImageUrl((String)
			 * objoutput .get("ProdImgUrl")); product.setProductName((String)
			 * objoutput .get("ProductName")); product.setProductUrl((String)
			 * objoutput .get("ProductUrl")); } } }
			 */
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return products;
	}

	public static StoreProductDetails getProductDetailsForStore(String productId) {
		StoreProductDetails product = new StoreProductDetails();
		DB db = null;
		try {

			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("products");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("ProductId", productId);
			DBObject obj = collections.findOne(whereQuery);

			if (obj != null) {
				product.setProductName((String) obj.get("ProductName"));
				String imgURL;
				imgURL = (String) obj.get("ProdImgUrl");
				product.setImageUrl(imgURL);
				product.setAddress((String) obj.get("Address"));
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);

		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return product;
	}

	/**
	 * Fetches the productId for the given word from the features collections of
	 * mongo.
	 * 
	 * @param entityId
	 * @param subProductId
	 * @param word
	 * @param skip
	 * @param maxPrice
	 * @param minPrice
	 * @param klwinesCategoryList
	 * @return returns an array of productIds.
	 */
	public static ArrayList<Product> getProductIdsForFeature(int entityId,
			int subProductId, String word, String subDimension, int skip,
			int limits, String category, Integer minPrice, Integer maxPrice,
			ArrayList<String> klwinesCategoryList, String klwinesCountry,
			String klwinessubRegion) {

		ArrayList<Product> products = new ArrayList<Product>();
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");
			BasicDBObject matchByEntitySubProduct = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.put("EntityId", entityId);
			matchQuery.put("SubProductId", subProductId);
			if (subDimension != null && !subDimension.equals("null")
					&& !subDimension.equals("undefined")) {
				matchQuery.put("SubDimension", subDimension);
			}
			if (klwinesCategoryList.size() != 0) {
				matchQuery.put("Varietal", new BasicDBObject("$in",
						klwinesCategoryList));

			}
			if (!word.equals("null")) {
				matchQuery.put("SynonymWord", word);
			}
			if (klwinesCountry != null && !klwinesCountry.equals("undefined")
					&& !klwinesCountry.equals("All")
					&& !klwinesCountry.equals("null")) {
				if (klwinesCountry.equals("Other International")) {
					matchQuery.put("Country", null);
				} else {
					matchQuery.put("Country", klwinesCountry);
				}
			}
			if (klwinessubRegion != null
					&& !klwinessubRegion.equals("undefined")
					&& !klwinessubRegion.equals("All")
					&& !klwinessubRegion.equals("null")) {
				if (klwinessubRegion.equals("Others")) {
					matchQuery.put("SubRegion", null);
				} else {
					matchQuery.put("SubRegion", klwinessubRegion);
				}
			}
			if (!category.equals("undefined") && !(category.equals("null"))) {
				matchQuery.put("Categories", category);

			}
			if (null != minPrice && maxPrice != null) {
				matchQuery.put("Price", new BasicDBObject("$gt", minPrice)
				.append("$lt", maxPrice));
			}
			matchByEntitySubProduct.put("$match", matchQuery);

			DBObject groupFields1 = new BasicDBObject("_id", new BasicDBObject(
					"ProductId", "$ProductId").append("PostId", "$PostId"));
			groupFields1.put("adjscore", new BasicDBObject("$push",
					"$SentenceScore"));

			DBObject group1 = new BasicDBObject("$group", groupFields1);

			DBObject groupFields2 = new BasicDBObject("_id", "$_id.ProductId");
			groupFields2.put("Count", new BasicDBObject("$sum", 1));
			groupFields2.put("adjscore2", new BasicDBObject("$push",
					"$adjscore"));
			DBObject group2 = new BasicDBObject("$group", groupFields2);
			DBObject sortOp = new BasicDBObject("$sort", new BasicDBObject(
					"Count", -1).append("_id", 1));

			DBObject skipposts = new BasicDBObject("$skip", skip);
			DBObject limit = new BasicDBObject("$limit", limits);

			/*
			 * db.noundata.aggregate({$match:{SubProductId:10,NounWord:"compliment"
			 * }},
			 * {$group:{"_id":{"ProductId":"$ProductId","PostId":"$PostId"},adjscore
			 * :{"$addToSet":"$AdjScore"}}},
			 * {$group:{_id:"$_id.ProductId","Count"
			 * :{$sum:1},adjscore2:{"$push":"$adjscore"}}},
			 * {$sort:{"Count":-1}},{$limit:10},
			 * 
			 * {$unwind:"$adjscore2"},{$unwind:"$adjscore2"}, this for Second
			 * Querys {$match:{"adjscore2":{$gt:0}}},
			 * {$group:{_id:"$_id","PosCount":{$sum:1}}})
			 */

			DBObject unwind = new BasicDBObject("$unwind", "$adjscore2");

			DBObject match2 = new BasicDBObject("$match", new BasicDBObject(
					"adjscore2", new BasicDBObject("$gt", 0)));
			DBObject groupfields3 = new BasicDBObject("_id", "$_id");
			groupfields3.put("thisCount", new BasicDBObject("$sum", 1));
			groupfields3.put("totCount", new BasicDBObject("$first", "$Count"));
			DBObject group3 = new BasicDBObject("$group", groupfields3);
			DBObject sortOp2 = new BasicDBObject("$sort", new BasicDBObject(
					"totCount", -1).append("_id", 1));
			AggregationOutput ids = collections.aggregate(
					matchByEntitySubProduct, group1, group2, sortOp, skipposts,
					limit, unwind, match2, group3, sortOp2);
			LOG.info(matchByEntitySubProduct + "," + group1 + "," + group2
					+ "," + sortOp + "," + skipposts + "," + limit + ","
					+ unwind + "," + match2 + "," + group3 + "," + sortOp2);
			Iterable<DBObject> results = ids.results();
			if (results != null) {
				for (DBObject dbObject : results) {
					Product product = new Product();
					product.setProductId((String) dbObject.get("_id"));
					product.setPosCommmentsCount(((Integer) dbObject
							.get("thisCount")));
					product.setTotalCount(((Integer) dbObject.get("totCount")));
					products.add(product);
				}
			}
			match2 = new BasicDBObject("$match", new BasicDBObject("adjscore2",
					new BasicDBObject("$lt", 0)));
			ids = collections.aggregate(matchByEntitySubProduct, group1,
					group2, sortOp, skipposts, limit, unwind, match2, group3,
					sortOp2);
			LOG.info(matchByEntitySubProduct + "," + group1 + "," + group2
					+ "," + sortOp + "," + skipposts + "," + limit + ","
					+ unwind + "," + match2 + "," + group3 + "," + sortOp2);
			Iterable<DBObject> results2 = ids.results();

			if (results2 != null) {
				for (DBObject dbObject : results2) {
					String productId = (String) dbObject.get("_id");
					int negCount = (Integer) dbObject.get("thisCount");
					int flag = 0;
					for (Product product : products) {
						if (product.getProductId().equals(productId)) {
							flag = 1;
							product.setNegCommmentsCount(negCount);
						}

					}
					if (flag == 0) {
						Product product1 = new Product();
						product1.setProductId(productId);
						product1.setNegCommmentsCount(negCount);
						product1.setTotalCount(negCount);
						products.add(product1);
					}
				}

			}

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		Comparator<Product> reverseComparator = Collections
				.reverseOrder(PRODUCTS_ORDER);
		Collections.sort(products, reverseComparator);
		return products;
	}

	/**
	 * Fetches the productId for the given word from the noundata collections of
	 * mongo.
	 * 
	 * @param entityId
	 * @param subProductId
	 * @param word
	 * @param skip
	 * @return returns an array of productIds.
	 */
	public static ArrayList<Product> getProductIdsForWord(int entityId,
			int subProductId, String word, int skip, int limits, String category) {

		ArrayList<Product> products = new ArrayList<Product>();
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("noundata");
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.put("EntityId", entityId);
			matchQuery.put("SubProductId", subProductId);
			matchQuery.put("SynonymWord", word);
			if (!category.equals("undefined")) {
				matchQuery.put("Categories", category);
			}
			BasicDBObject matchByEntitySubProduct = new BasicDBObject();
			matchByEntitySubProduct.put("$match", matchQuery);
			DBObject groupFields1 = new BasicDBObject("_id", new BasicDBObject(
					"ProductId", "$ProductId").append("PostId", "$PostId"));
			groupFields1.put("adjscore",
					new BasicDBObject("$push", "$AdjScore"));

			DBObject group1 = new BasicDBObject("$group", groupFields1);

			DBObject groupFields2 = new BasicDBObject("_id", "$_id.ProductId");
			groupFields2.put("Count", new BasicDBObject("$sum", 1));
			groupFields2.put("adjscore2", new BasicDBObject("$push",
					"$adjscore"));
			DBObject group2 = new BasicDBObject("$group", groupFields2);
			DBObject sortOp = new BasicDBObject("$sort", new BasicDBObject(
					"Count", -1).append("_id", 1));
			DBObject skipposts = new BasicDBObject("$skip", skip);
			DBObject limit = new BasicDBObject("$limit", limits);

			/*
			 * db.noundata.aggregate({$match:{SubProductId:10,NounWord:"compliment"
			 * }},
			 * {$group:{"_id":{"ProductId":"$ProductId","PostId":"$PostId"},adjscore
			 * :{"$addToSet":"$AdjScore"}}},
			 * {$group:{_id:"$_id.ProductId","Count"
			 * :{$sum:1},adjscore2:{"$push":"$adjscore"}}},
			 * {$sort:{"Count":-1}},{$limit:10},
			 * 
			 * {$unwind:"$adjscore2"},{$unwind:"$adjscore2"}, this for Second
			 * Querys {$match:{"adjscore2":{$gt:0}}},
			 * {$group:{_id:"$_id","PosCount":{$sum:1}}})
			 */

			DBObject unwind = new BasicDBObject("$unwind", "$adjscore2");

			DBObject match2 = new BasicDBObject("$match", new BasicDBObject(
					"adjscore2", new BasicDBObject("$gt", 0)));
			DBObject groupfields3 = new BasicDBObject("_id", "$_id");
			groupfields3.put("TotCount", new BasicDBObject("$sum", 1));
			DBObject group3 = new BasicDBObject("$group", groupfields3);

			AggregationOutput ids = collections.aggregate(
					matchByEntitySubProduct, group1, group2, sortOp, skipposts,
					limit, unwind, match2, group3);

			Iterable<DBObject> results = ids.results();
			if (results != null) {

				for (DBObject dbObject : results) {

					Product product = new Product();
					product.setProductId((String) dbObject.get("_id"));
					product.setPosCommmentsCount(((Integer) dbObject
							.get("TotCount")));
					product.setTotalCount(((Integer) dbObject.get("TotCount")));
					products.add(product);

				}
			}

			match2 = new BasicDBObject("$match", new BasicDBObject("adjscore2",
					new BasicDBObject("$lt", 0)));

			ids = collections.aggregate(matchByEntitySubProduct, group1,
					group2, sortOp, skipposts, limit, unwind, match2, group3);
			Iterable<DBObject> results2 = ids.results();

			if (results2 != null) {

				for (DBObject dbObject : results2) {
					String productId = (String) dbObject.get("_id");
					int negCount = (Integer) dbObject.get("TotCount");
					int flag = 0;
					for (Product product : products) {

						if (product.getProductId().equals(productId)) {
							flag = 1;
							product.setNegCommmentsCount(negCount);
							product.setTotalCount(product.getPosCommentsCount()
									+ negCount);
						}

					}
					if (flag == 0) {
						Product product1 = new Product();
						product1.setProductId(productId);
						product1.setNegCommmentsCount(negCount);
						product1.setTotalCount(negCount);
						products.add(product1);
					}
				}

			}

			// {$group:{_id:"$_id","PosCount":{$sum:1}}})

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		// Comparator<Product> reverseComparator =
		// Collections.reverseOrder(PRODUCTS_ORDER);
		Collections.sort(products, PRODUCTS_ORDER);
		return products;
	}

	@SuppressWarnings("unchecked")
	public static List<ProductDetails> getProductsForYahoo(int skip, int limit,
			String productName) {
		DB db = null;
		List<ProductDetails> products = new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("products");

			DBCursor cursor = collections
					.find(new BasicDBObject("Product", productName))
					.sort(new BasicDBObject("TotalWordsCount", -1)).skip(skip)
					.limit(limit);
			while (cursor.hasNext()) {
				ProductDetails product = new ProductDetails();
				DBObject objoutput = cursor.next();
				product.setImgURL(objoutput.get("ProdImgUrl").toString());
				product.setName(objoutput.get("ProductName").toString());
				product.setProductId(objoutput.get("ProductId").toString());
				product.setPrice(Double.valueOf(objoutput.get("Price")
						.toString()));
				product.setProdUrl(objoutput.get("ProductUrl").toString());
				ArrayList<KeyWord> posKeyWords = new ArrayList<>();
				ArrayList<KeyWord> negKeyWords = new ArrayList<>();
				List<BasicDBObject> poswords = (List<BasicDBObject>) objoutput
						.get("PosKeywords");
				List<BasicDBObject> negwords = (List<BasicDBObject>) objoutput
						.get("NegKeyWords");

				if (poswords != null) {
					int i = 0;
					for (BasicDBObject object : poswords) {
						KeyWord keyword = new KeyWord();
						keyword.setKeyWord(object.getString("word"));
						keyword.setcount(object.getInt("count"));
						posKeyWords.add(keyword);
						if (i++ == 2) {
							break;
						}
					}

				}
				product.addPosReview(posKeyWords);

				if (negwords != null) {
					int i = 0;
					for (BasicDBObject object : negwords) {
						KeyWord keyword = new KeyWord();
						keyword.setKeyWord(object.getString("word"));
						keyword.setcount(object.getInt("count"));
						negKeyWords.add(keyword);
						if (i++ == 2) {
							break;
						}
					}
				}
				product.addNegReview(negKeyWords);
				products.add(product);
			}

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

		return products;
	}

	/**
	 * This method fetches the recent reviews,that is ordered by date. Reviews
	 * are fetched in the given date range if fromDate or toDate is not null.
	 * 
	 * @param entityId
	 *            EntityID
	 * @param subProductID
	 *            SubProductID
	 * @param skipValue
	 *            No of reviews to skip at once(Used for pagination).
	 * @param limit
	 *            No of reviews to be fetched at once.
	 * @param fromDate
	 *            Start date for the date range.
	 * @param toDate
	 *            End date for the date range.
	 * @param isPositive
	 *            true if positive revies need to be fetched,false for negative
	 *            reviews.
	 * @return List of Type Post.
	 * @throws ParseException
	 */
	public static List<Post> getRecentReviews(int entityId, int subProductID,
			int skipValue, int limit, Date fromDate, Date toDate,
			Boolean isPositive, String productId) throws ParseException {

		/*
		 * SimpleDateFormat mongoDBFormat = new
		 * SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); Date
		 * newfromDate=mongoDBFormat.parse(fromDate); Date
		 * newtoDate=mongoDBFormat.parse(toDate);
		 */
		DB db = null;
		List<Post> posts = new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("posts");
			BasicDBObject match = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.append("EntityId", entityId);
			matchQuery.append("subpoductId", subProductID);
			if (isPositive) {
				matchQuery.append("Score", new BasicDBObject("$gt", 0));
			} else {
				matchQuery.append("Score", new BasicDBObject("$lt", 0));
			}
			if (fromDate != null) {
				matchQuery.append(
						"createdDate",
						BasicDBObjectBuilder.start("$gte", fromDate)
						.add("$lte", toDate).get());
			}
			if (productId != null) {
				matchQuery.append("_id",
						java.util.regex.Pattern.compile(productId));
			}
			match.put("$match", matchQuery);
			BasicDBObject sortQuery = new BasicDBObject();
			sortQuery.append("createdDate", -1);
			BasicDBObject sort = new BasicDBObject("$sort", sortQuery);
			DBObject skip = new BasicDBObject("$skip", skipValue);
			DBObject limitquery = new BasicDBObject("$limit", limit);

			AggregationOutput output = null;
			if (limit != 0) {
				output = collections.aggregate(match, sort, skip, limitquery);
			} else {
				output = collections.aggregate(match, sort, skip);
			}

			Iterable<DBObject> result = output.results();

			if (result != null) {
				for (DBObject object : result) {

					Post post = new Post();
					post.setPostId(object.get("_id").toString());

					post.setContent(object.get("content").toString());
					Date date = (Date) object.get("createdDate");
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					String formatedDate = cal.get(Calendar.DATE) + "/"
							+ (cal.get(Calendar.MONTH) + 1) + "/"
							+ cal.get(Calendar.YEAR);
					post.setDate(formatedDate);
					int rating = (Integer) object.get("rating");
					if (rating != 0) {
						post.setRating(rating);
					}

					// get call categories for this post
					/*
					 * List<String> categories =
					 * getDimensionsForPost(post.getPostId());
					 * post.setCategories(categories); List<String> keywords =
					 * getKeywordsForPost(post.getPostId());
					 * post.setKeywords(keywords);
					 */posts.add(post);
				}
			} else {
				System.out.println("Null");
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {

			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return posts;
	}

	/**
	 * @param entityId
	 * @param subProductId
	 * @param maxPrice
	 * @param minPrice
	 * @param dimensions
	 * @return
	 */
	private static Map<String, Product> getSimilarProductIdsForProduct(
			int entityId, int subProductId, String productId,
			Map<String, List<String>> subDimensionWords,
			Map<String, Product> productIds, double minPrice, double maxPrice) {
		DB db = null;
		List<String> words = new ArrayList<>();
		for (String key : subDimensionWords.keySet()) {
			words.addAll(subDimensionWords.get(key));
		}
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");
			String category = getCategoryForKlwines(productId);
			List<DBObject> pipeLine = new ArrayList<>();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.put("EntityId", entityId);
			matchQuery.put("SubProductId", subProductId);
			matchQuery.put("ProductId", new BasicDBObject("$ne", productId));
			if (category != null) {
				matchQuery.put("Varietal", category);
			}
			matchQuery.put("Price", new BasicDBObject("$gte", minPrice).append(
					"$lte", maxPrice));
			BasicDBObject matchByEntitySubProduct = new BasicDBObject();
			matchByEntitySubProduct.put("$match", matchQuery);

			pipeLine.add(matchByEntitySubProduct);

			BasicDBObject unwind = new BasicDBObject("$unwind", "$SubDimension");
			pipeLine.add(unwind);

			BasicDBObject matchQuery2 = new BasicDBObject();
			matchQuery2.put("SubDimension", new BasicDBObject("$in",
					subDimensionWords.keySet()));
			matchQuery2.put("SynonymWord", new BasicDBObject("$in", words));

			BasicDBObject matchByEntitySubProduct2 = new BasicDBObject();
			matchByEntitySubProduct2.put("$match", matchQuery2);
			pipeLine.add(matchByEntitySubProduct2);

			DBObject groupFields1 = new BasicDBObject("_id", new BasicDBObject(
					"ProductId", "$ProductId").append("SubDimension",
							"$SubDimension").append("Word", "$SynonymWord")).append(
									"Count", new BasicDBObject("$sum", 1));

			DBObject group1 = new BasicDBObject("$group", groupFields1);
			pipeLine.add(group1);
			DBObject groupFields2 = new BasicDBObject("_id", new BasicDBObject(
					"ProductId", "$_id.ProductId").append("SubDimension",
							"$_id.SubDimension"));
			groupFields2.put("WordCount",
					new BasicDBObject("$push", new BasicDBObject("Word",
							"$_id.Word").append("Count", "$Count")));

			DBObject group2 = new BasicDBObject("$group", groupFields2);
			pipeLine.add(group2);

			DBObject sort = new BasicDBObject("$sort", new BasicDBObject(
					"_id.ProductId", 1).append("_id.SubDimension", 1));
			pipeLine.add(sort);
			DBObject groupFields3 = new BasicDBObject("_id", "$_id.ProductId");
			groupFields3.put("Count", new BasicDBObject("$sum", 1));
			groupFields3.put(
					"CategoryWord",
					new BasicDBObject("$push", new BasicDBObject("Category",
							"$_id.SubDimension").append("WordCount",
									"$WordCount")));
			DBObject group3 = new BasicDBObject("$group", groupFields3);
			pipeLine.add(group3);

			DBObject match3 = new BasicDBObject();
			match3.put("$match", new BasicDBObject("Count", new BasicDBObject(
					"$gte", 3)));
			pipeLine.add(match3);

			AggregationOutput output = collections.aggregate(pipeLine);
			Iterable<DBObject> results = output.results();

			if (results != null) {
				for (DBObject dbObject : results) {

					if (productIds.get(dbObject.get("_id").toString()) == null) {
						boolean isfinishSubdimensionPresent = false;
						boolean isaromasSubdimensionPresent = false;
						Product product = new Product();
						@SuppressWarnings("unchecked")
						List<BasicDBObject> featureWordsObjects = (List<BasicDBObject>) dbObject
						.get("CategoryWord");
						List<FeatureWords> featureWordList = new ArrayList<>();
						for (BasicDBObject object : featureWordsObjects) {
							FeatureWords categoryWords = new FeatureWords();
							categoryWords.setFeature(object
									.getString("Category"));
							if (categoryWords.getFeature().equals(
									"wine by finish")) {
								isfinishSubdimensionPresent = true;
							}
							if (categoryWords.getFeature().equals(
									"wines by aromas")) {
								isaromasSubdimensionPresent = true;
							}
							@SuppressWarnings("unchecked")
							List<BasicDBObject> wordCountObjects = (List<BasicDBObject>) object
							.get("WordCount");
							List<WordCount> wordsCountList = new ArrayList<>();
							for (BasicDBObject wordobject : wordCountObjects) {
								WordCount wordCount = new WordCount();
								wordCount.setWord(wordobject.getString("Word"));
								wordCount.setCount(wordobject.getInt("Count"));
								wordsCountList.add(wordCount);
							}
							categoryWords.setWords(wordsCountList);
							featureWordList.add(categoryWords);
						}

						if (isaromasSubdimensionPresent
								&& isfinishSubdimensionPresent) {
							product.setProductId(dbObject.get("_id").toString());
							product.setFeatureWords(featureWordList);
							productIds.put(product.getProductId(), product);
						}
					}

				}
			}

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured while fetching fields. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

		return productIds;

	}

	public static Map<String, Integer> getSimilarProductIdsForSubDimension(
			int entityId, int subProductId, String subDimension,
			List<String> words, Map<String, Integer> prodCountMap) {
		DB db = null;
		new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");

			List<DBObject> pipeLine = new ArrayList<>();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.put("EntityId", entityId);
			matchQuery.put("SubProductId", subProductId);
			BasicDBObject matchByEntitySubProduct = new BasicDBObject();
			matchByEntitySubProduct.put("$match", matchQuery);

			pipeLine.add(matchByEntitySubProduct);

			BasicDBObject unwind = new BasicDBObject("$unwind", "$SubDimension");
			pipeLine.add(unwind);

			BasicDBObject match2 = new BasicDBObject("$match",
					new BasicDBObject("SubDimension", subDimension).append(
							"SynonymWord", new BasicDBObject("$in", words)));
			pipeLine.add(match2);
			DBObject groupFields1 = new BasicDBObject("_id", new BasicDBObject(
					"ProductId", "$ProductId").append("SubDimension",
							"$SubDimension").append("Word", "$SynonymWord"));
			groupFields1.put("Count", new BasicDBObject("$sum", 1));

			DBObject group1 = new BasicDBObject("$group", groupFields1);
			pipeLine.add(group1);

			DBObject groupFields2 = new BasicDBObject("_id", new BasicDBObject(
					"ProductId", "$ProductId").append("SubDimension",
							"$SubDimension"));
			groupFields2.put("Count", new BasicDBObject("$sum", 1));

			DBObject group2 = new BasicDBObject("$group", groupFields2);
			pipeLine.add(group2);
			DBObject match3 = new BasicDBObject("$match", new BasicDBObject(
					"Count", new BasicDBObject("$gte", 2)));
			pipeLine.add(match3);
			LOG.info(pipeLine);
			AggregationOutput output = collections.aggregate(pipeLine);
			Iterable<DBObject> results = output.results();

			if (results != null) {

				for (DBObject dbObject : results) {
					System.out.println("Here");
				}
			}

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured while fetching fields. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return prodCountMap;
	}

	@SuppressWarnings("unchecked")
	public static List<Product> getSimilarProducts(String productId) {
		DB db = null;
		List<Product> similarProducts = new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("products");
			DBObject object = collections.findOne(new BasicDBObject("_id",
					productId));
			/*
			 * String category = object.get("Category").toString(); List<String>
			 * productIds = getProductIdsForSimilarWines(productId, category);
			 * if (productIds != null) { for (String id : productIds) { Product
			 * product = new Product(); product.setProductId(id);
			 * similarProducts.add(product); } }
			 */
			if (object.get("SimilarWines") != null) {
				ArrayList<String> productIds = (ArrayList<String>) object
						.get("SimilarWines");
				for (String id : productIds) {
					Product product = new Product();
					product.setProductId(id);
					similarProducts.add(product);
				}
			}

			similarProducts = getProductDetailsByIdForKlwines(similarProducts);
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured while fetching fields. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return similarProducts;
	}

	@SuppressWarnings("unchecked")
	public static List<Product> getSimilarProductsKlwines(int entityId,
			int subProductId, String productId) {
		DB db = null;
		List<Product> similarProducts = new ArrayList<>();
		try {
			new HashMap<>();
			Map<String, Product> productIdsMap = new HashMap<>();
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");
			List<FeatureWords> featureWords = new ArrayList<>();
			List<String> categoriesToMatch = new ArrayList<>();
			boolean isfinishSubdimensionPresent = false;
			boolean isaromasSubdimensionPresent = false;
			double price = getPriceForProduct(productId);
			double maxPrice = price * 1.3;
			double minPrice = price * 0.7;
			Map<String, List<String>> categoryWordsMap = new HashMap<>();
			categoriesToMatch.add("wine by flavors");
			categoriesToMatch.add("wine by body");
			categoriesToMatch.add("wine by finish");
			categoriesToMatch.add("wine by style");
			categoriesToMatch.add("pairing suggestions");
			categoriesToMatch.add("wines by aromas");

			ArrayList<String> wordsToSkipForMatch = new ArrayList<>();
			wordsToSkipForMatch.add("berries");
			wordsToSkipForMatch.add("cherry");
			wordsToSkipForMatch.add("spicy");

			List<DBObject> pipeLine = new ArrayList<>();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.put("EntityId", entityId);
			matchQuery.put("SubProductId", subProductId);
			matchQuery.put("ProductId", productId);
			matchQuery.put("SubDimension", new BasicDBObject("$in",
					categoriesToMatch));
			matchQuery.put("SynonymWord", new BasicDBObject("$nin",
					wordsToSkipForMatch));
			BasicDBObject matchByEntitySubProduct = new BasicDBObject();
			matchByEntitySubProduct.put("$match", matchQuery);

			pipeLine.add(matchByEntitySubProduct);

			BasicDBObject unwind = new BasicDBObject("$unwind", "$SubDimension");
			pipeLine.add(unwind);
			DBObject groupFields1 = new BasicDBObject("_id", new BasicDBObject(
					"SubDimension", "$SubDimension").append("Word",
							"$SynonymWord"));
			groupFields1.put("Count", new BasicDBObject("$sum", 1));
			DBObject group1 = new BasicDBObject("$group", groupFields1);
			pipeLine.add(group1);

			DBObject groupFields2 = new BasicDBObject("_id",
					"$_id.SubDimension");
			groupFields2.put("WordCount",
					new BasicDBObject("$push", new BasicDBObject("Word",
							"$_id.Word").append("Count", "$Count")));
			DBObject group2 = new BasicDBObject("$group", groupFields2);
			pipeLine.add(group2);
			LOG.info(pipeLine);
			AggregationOutput output = collections.aggregate(pipeLine);
			Iterable<DBObject> results = output.results();

			if (results != null) {
				for (DBObject dbObject : results) {
					FeatureWords featureWord = new FeatureWords();
					String subDimension = dbObject.get("_id").toString();
					if (subDimension.equals("wine by finish")) {
						isfinishSubdimensionPresent = true;
					}
					if (subDimension.equals("wines by aromas")) {
						isaromasSubdimensionPresent = true;
					}
					featureWord.setFeature(subDimension);
					List<BasicDBObject> wordCountObjects = (List<BasicDBObject>) dbObject
							.get("WordCount");
					List<WordCount> wordCountList = new ArrayList<>();
					List<String> words = new ArrayList<>();
					for (BasicDBObject object : wordCountObjects) {
						WordCount wordCount = new WordCount();
						wordCount.setWord(object.getString("Word"));
						wordCount.setCount(object.getInt("Count"));
						wordCountList.add(wordCount);
						words.add(wordCount.getWord());
					}
					categoryWordsMap.put(subDimension, words);
					featureWord.setWords(wordCountList);
					featureWords.add(featureWord);
				}
			}
			if (isaromasSubdimensionPresent && isfinishSubdimensionPresent) {

				productIdsMap = getSimilarProductIdsForProduct(entityId,
						subProductId, productId, categoryWordsMap,
						productIdsMap, minPrice, maxPrice);
				for (String id : productIdsMap.keySet()) {
					Product productWithScore = calculateScoreForEachProduct(
							featureWords, productIdsMap.get(id));
					if (productWithScore.getFeatureWords().size() >= 3) {
						similarProducts.add(productWithScore);
					}
				}

				Comparator<Product> reverseComparator = Collections
						.reverseOrder(SIMILAR_WINES_ORDER);
				Collections.sort(similarProducts, reverseComparator);

				similarProducts = getProductDetailsByIdForKlwines(similarProducts);
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured while fetching fields. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return similarProducts;
	}

	public static Map<String, Integer> getSubDimProductCount(int entityId,
			int subProductId, String category, Integer minPrice,
			Integer maxPrice) {
		/*
		 * db.features.aggregate({ "$match" : { "EntityId" : 412 ,"SubProductId"
		 * : 15 }},{ "$unwind" : "$SubDimension"}, { "$group" : {"_id" : {
		 * "SubDimension" : "$SubDimension"} , "ProductId": { "$addToSet" :
		 * "$ProductId"}}},{ "$unwind": "$ProductId"}, { "$group": { "_id":{
		 * "SubDimension" : "$_id.SubDimension"}, "ProductCount": { "$sum":1}}})
		 */

		Map<String, Integer> subDimProductCount = null;
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");

			BasicDBObject matchParameters = new BasicDBObject();
			matchParameters.put("EntityId", entityId);
			matchParameters.put("SubProductId", subProductId);

			if (category != null && !category.equals("null")
					&& !category.equals("undefined")) {
				matchParameters.put("Categories", category);
			}
			if (null != minPrice && maxPrice != null) {
				matchParameters.put("Price", new BasicDBObject("$gt", minPrice)
				.append("$lt", maxPrice));
			}
			BasicDBObject match = new BasicDBObject();
			match.put("$match", matchParameters);

			DBObject unwind = new BasicDBObject("$unwind", "$SubDimension");

			DBObject groupfileds = new BasicDBObject("_id", new BasicDBObject(
					"SubDimension", "$SubDimension"));

			// groupfileds.put("TotalCount", new BasicDBObject("$sum", 1));
			groupfileds.put("ProductId", new BasicDBObject("$addToSet",
					"$ProductId"));

			DBObject group = new BasicDBObject("$group", groupfileds);

			DBObject unwindByProductId = new BasicDBObject("$unwind",
					"$ProductId");
			// Group2
			DBObject groupfileds2 = new BasicDBObject("_id", new BasicDBObject(
					"SubDimension", "$_id.SubDimension"));
			groupfileds2.put("ProductCount", new BasicDBObject("$sum", 1));
			DBObject group2 = new BasicDBObject("$group", groupfileds2);

			// Projection
			DBObject projection = new BasicDBObject("$project",
					new BasicDBObject("SubDimension", "$_id.SubDimension")
			.append("ProductCount", "$ProductCount"));

			AggregationOutput output = collections.aggregate(match, unwind,
					group, unwindByProductId, group2, projection);
			Iterable<DBObject> results = output.results();

			if (results != null) {
				subDimProductCount = new HashMap<>();
			}
			for (DBObject dbObject : results) {
				subDimProductCount.put((String) dbObject.get("SubDimension"),
						(Integer) dbObject.get("ProductCount"));
			}

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured while fetching fields. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

		return subDimProductCount;

	}

	/**
	 * @param subDimension
	 * @param synonymWord
	 * @param country
	 * @return
	 */
	public static String getsubRegionForKlWines(String subDimension,
			String synonymWord, String country) {
		DB db = null;
		ArrayList<String> subRegionlist = new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");
			BasicDBObject match = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.put("SubProductId", 25);
			matchQuery.put("SubDimension", subDimension);
			if (!synonymWord.equals("null") && !synonymWord.equals("undefined")) {
				matchQuery.put("SynonymWord", synonymWord);
			}
			matchQuery.put("Country", country);
			match.append("$match", matchQuery);
			BasicDBObject groupQuery1 = new BasicDBObject();
			groupQuery1.append("_id", "$SubRegion");
			BasicDBObject group1 = new BasicDBObject("$group", groupQuery1);

			BasicDBObject groupQuery2 = new BasicDBObject();
			groupQuery2.append("_id", null);
			groupQuery2
			.append("SubRegions", new BasicDBObject("$push", "$_id"));
			BasicDBObject group2 = new BasicDBObject("$group", groupQuery2);
			LOG.info(match + "," + group1 + "," + group2);
			AggregationOutput output = collections.aggregate(match, group1,
					group2);

			Iterable<DBObject> result = output.results();
			if (result != null) {
				for (DBObject object : result) {
					subRegionlist = (ArrayList<String>) object
							.get("SubRegions");
					if (subRegionlist.contains(null)) {
						subRegionlist.remove(null);
						if (subRegionlist.size() != 0) {
							subRegionlist.add("Others");
						}
					}
				}
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured  ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

		return new Gson().toJson(subRegionlist);

	}

	public static List<ChartData> getTimelineData(int entityId,
			int subProductID, Date fromDate, Date toDate, Boolean isPositive,
			Map<String, ChartData> mapOfDates, String storeId)
					throws ParseException {

		/*
		 * SimpleDateFormat mongoDBFormat = new
		 * SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); Date
		 * newfromDate=mongoDBFormat.parse(fromDate); Date
		 * newtoDate=mongoDBFormat.parse(toDate);
		 */

		DB db = null;
		List<ChartData> chartdatas = new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("posts");
			BasicDBObject match = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.append("EntityId", entityId);
			matchQuery.append("subpoductId", subProductID);

			if (isPositive) {
				matchQuery.append("Score", new BasicDBObject("$gt", 0));
			} else {
				matchQuery.append("Score", new BasicDBObject("$lt", 0));
			}
			if (fromDate != null) {
				matchQuery.append(
						"createdDate",
						BasicDBObjectBuilder.start("$gte", fromDate)
						.add("$lte", toDate).get());
			}
			if (storeId != null) {
				matchQuery.append("_id",
						java.util.regex.Pattern.compile(storeId));
			}
			match.put("$match", matchQuery);

			BasicDBObject grpFieldsYearandMonth = new BasicDBObject();
			grpFieldsYearandMonth.append("Year", new BasicDBObject("$year",
					"$createdDate"));
			grpFieldsYearandMonth.append("Month", new BasicDBObject("$month",
					"$createdDate"));

			BasicDBObject groupFields = new BasicDBObject();
			groupFields.append("_id", grpFieldsYearandMonth);
			groupFields.append("Count", new BasicDBObject("$sum", 1));
			BasicDBObject group = new BasicDBObject();
			group.append("$group", groupFields);

			BasicDBObject projectValues = new BasicDBObject();
			projectValues.append("Year", "$_id.Year");
			projectValues.append("Month", "$_id.Month");
			projectValues.append("Count", "$Count");
			BasicDBObject project = new BasicDBObject("$project", projectValues);
			BasicDBObject sortQuery = new BasicDBObject();
			sortQuery.append("_id", -1);

			BasicDBObject sort = new BasicDBObject("$sort", sortQuery);
			AggregationOutput output = collections.aggregate(match, group,
					project, sort);
			LOG.info(match + "," + group + "," + project + "," + sort);
			Iterable<DBObject> result = output.results();

			if (result != null) {
				for (DBObject object : result) {
					String monthYear = object.get("Month").toString() + "-"
							+ object.get("Year").toString();
					String value = object.get("Count").toString();
					ChartData chData = mapOfDates.get(monthYear);
					chData.setValue(value);
					mapOfDates.put(monthYear, chData);
				}

			} else {
				LOG.info("No posts found in the range");
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		chartdatas = new ArrayList<ChartData>(mapOfDates.values());
		return chartdatas;
	}

	public static List<ChartData> getTimelineDataByWeek(int entityId,
			int subProductID, Date fromDate, Date toDate, Boolean isPositive,
			Map<String, ChartData> mapOfDates, String storeId)
					throws ParseException {

		/*
		 * SimpleDateFormat mongoDBFormat = new
		 * SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); Date
		 * newfromDate=mongoDBFormat.parse(fromDate); Date
		 * newtoDate=mongoDBFormat.parse(toDate);
		 */

		DB db = null;
		List<ChartData> chartdatas = new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("posts");
			BasicDBObject match = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.append("EntityId", entityId);
			matchQuery.append("subpoductId", subProductID);

			if (isPositive) {
				matchQuery.append("Score", new BasicDBObject("$gt", 0));
			} else {
				matchQuery.append("Score", new BasicDBObject("$lt", 0));
			}
			if (fromDate != null) {
				matchQuery.append(
						"createdDate",
						BasicDBObjectBuilder.start("$gte", fromDate)
						.add("$lte", toDate).get());
			}
			if (storeId != null) {
				matchQuery.append("_id",
						java.util.regex.Pattern.compile(storeId));
			}
			match.put("$match", matchQuery);

			BasicDBObject grpFieldsYearandMonth = new BasicDBObject();
			grpFieldsYearandMonth.append("Year", new BasicDBObject("$year",
					"$createdDate"));
			grpFieldsYearandMonth.append("Week", new BasicDBObject("$week",
					"$createdDate"));

			BasicDBObject groupFields = new BasicDBObject();
			groupFields.append("_id", grpFieldsYearandMonth);
			groupFields.append("Count", new BasicDBObject("$sum", 1));
			BasicDBObject group = new BasicDBObject();
			group.append("$group", groupFields);

			BasicDBObject projectValues = new BasicDBObject();
			projectValues.append("Year", "$_id.Year");
			projectValues.append("Week", "$_id.Week");
			projectValues.append("Count", "$Count");
			BasicDBObject project = new BasicDBObject("$project", projectValues);
			BasicDBObject sortQuery = new BasicDBObject();
			sortQuery.append("_id", -1);

			BasicDBObject sort = new BasicDBObject("$sort", sortQuery);
			AggregationOutput output = collections.aggregate(match, group,
					project, sort);
			LOG.info(match + "," + group + "," + project + "," + sort);
			Iterable<DBObject> result = output.results();

			if (result != null) {
				for (DBObject object : result) {
					String monthYear = object.get("Week").toString() + "-"
							+ object.get("Year").toString();
					String value = object.get("Count").toString();
					ChartData chData = mapOfDates.get(monthYear);
					chData.setValue(value);
					mapOfDates.put(monthYear, chData);
				}

			} else {
				LOG.info("No posts found in the range");
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		chartdatas = new ArrayList<ChartData>(mapOfDates.values());
		return chartdatas;
	}

	/**
	 * This method fetches the no of positive and negative Comments for the
	 * given products.
	 * 
	 * @param productids
	 *            Product Ids of the products for which the count of comments is
	 *            required.
	 * @return
	 */
	public static ArrayList<Product> getTopProductCommentsCount(
			String[] productids) {

		ArrayList<Product> products = new ArrayList<Product>();
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");
			BasicDBObject matchByEntitySubProduct = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery
			.append("ProductId", new BasicDBObject("$in", productids));
			matchByEntitySubProduct.put("$match", matchQuery);

			DBObject groupFields1 = new BasicDBObject("_id", new BasicDBObject(
					"ProductId", "$ProductId").append("PostId", "$PostId"));
			groupFields1.put("adjscore", new BasicDBObject("$push",
					"$OverallScore"));

			DBObject group1 = new BasicDBObject("$group", groupFields1);

			DBObject groupFields2 = new BasicDBObject("_id", "$_id.ProductId");
			groupFields2.put("Count", new BasicDBObject("$sum", 1));
			groupFields2.put("adjscore2", new BasicDBObject("$push",
					"$adjscore"));
			DBObject group2 = new BasicDBObject("$group", groupFields2);
			DBObject sortOp = new BasicDBObject("$sort", new BasicDBObject(
					"Count", -1).append("_id", 1));

			/*
			 * db.noundata.aggregate({$match:{SubProductId:10,NounWord:"compliment"
			 * }},
			 * {$group:{"_id":{"ProductId":"$ProductId","PostId":"$PostId"},adjscore
			 * :{"$addToSet":"$AdjScore"}}},
			 * {$group:{_id:"$_id.ProductId","Count"
			 * :{$sum:1},adjscore2:{"$push":"$adjscore"}}},
			 * {$sort:{"Count":-1}},{$limit:10},
			 * 
			 * {$unwind:"$adjscore2"},{$unwind:"$adjscore2"}, this for Second
			 * Querys {$match:{"adjscore2":{$gt:0}}},
			 * {$group:{_id:"$_id","PosCount":{$sum:1}}})
			 */

			DBObject unwind = new BasicDBObject("$unwind", "$adjscore2");

			DBObject match2 = new BasicDBObject("$match", new BasicDBObject(
					"adjscore2", new BasicDBObject("$gt", 0)));
			DBObject groupfields3 = new BasicDBObject("_id", "$_id");
			groupfields3.put("thisCount", new BasicDBObject("$sum", 1));
			groupfields3.put("totCount", new BasicDBObject("$first", "$Count"));
			DBObject group3 = new BasicDBObject("$group", groupfields3);
			DBObject sortOp2 = new BasicDBObject("$sort", new BasicDBObject(
					"totCount", -1).append("_id", 1));

			AggregationOutput ids = collections.aggregate(
					matchByEntitySubProduct, group1, group2, sortOp, unwind,
					match2, group3, sortOp2);

			Iterable<DBObject> results = ids.results();
			if (results != null) {
				for (DBObject dbObject : results) {
					Product product = new Product();
					product.setProductId((String) dbObject.get("_id"));
					product.setPosCommmentsCount(((Integer) dbObject
							.get("thisCount")));
					product.setTotalCount(((Integer) dbObject.get("totCount")));
					products.add(product);
				}
			}
			match2 = new BasicDBObject("$match", new BasicDBObject("adjscore2",
					new BasicDBObject("$lt", 0)));
			ids = collections.aggregate(matchByEntitySubProduct, group1,
					group2, sortOp, unwind, match2, group3, sortOp2);
			Iterable<DBObject> results2 = ids.results();

			if (results2 != null) {
				for (DBObject dbObject : results2) {
					String productId = (String) dbObject.get("_id");
					int negCount = (Integer) dbObject.get("thisCount");
					int flag = 0;
					for (Product product : products) {

						if (product.getProductId().equals(productId)) {
							flag = 1;
							product.setNegCommmentsCount(negCount);
						}

					}
					if (flag == 0) {
						Product product1 = new Product();
						product1.setProductId(productId);
						product1.setNegCommmentsCount(negCount);
						product1.setTotalCount(negCount);
						products.add(product1);
					}
				}

			}

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return products;
	}

	public static String getTopTraitsForSimilarWines(int entityId,
			int subProductId, Map<String, Product> productIdProductMap) {

		DB db = null;
		new ArrayList<>();

		try {
			new HashMap<>();
			new LinkedHashSet<>();
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");

			List<DBObject> pipeLine = new ArrayList<>();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.put("EntityId", entityId);
			matchQuery.put("SubProductId", subProductId);
			matchQuery.put("ProductId", new BasicDBObject("$in",
					productIdProductMap.keySet()));

			BasicDBObject matchByEntitySubProduct = new BasicDBObject();
			matchByEntitySubProduct.put("$match", matchQuery);

			pipeLine.add(matchByEntitySubProduct);

			BasicDBObject unwind = new BasicDBObject("$unwind", "$SubDimension");
			pipeLine.add(unwind);
			DBObject groupFields1 = new BasicDBObject("_id", new BasicDBObject(
					"ProductId", "$ProductId")
			.append("SubDimension", "$SubDimension")
			.append("Word", "$SynonymWord").append("PostId", "$PostId"));
			DBObject group1 = new BasicDBObject("$group", groupFields1);
			pipeLine.add(group1);
			DBObject groupFields2 = new BasicDBObject("_id", new BasicDBObject(
					"ProductId", "$_id.ProductId").append("SubDimension",
							"$_id.SubDimension").append("Word", "$_id.Word"));
			groupFields2.put("Count", new BasicDBObject("$sum", 1));
			DBObject group2 = new BasicDBObject("$group", groupFields2);
			pipeLine.add(group2);

			DBObject sort = new BasicDBObject();
			sort.put(
					"$sort",
					new BasicDBObject("_id.ProductId", 1).append(
							"_id.SubDimension", 1).append("Count", 1));
			pipeLine.add(sort);

			DBObject groupFields3 = new BasicDBObject("_id", new BasicDBObject(
					"ProductId", "$_id.ProductId").append("SubDimension",
							"$_id.SubDimension"));
			groupFields3.put("Word", new BasicDBObject("$first", "$_id.Word"));
			DBObject group3 = new BasicDBObject("$group", groupFields3);
			pipeLine.add(group3);

			DBObject groupfields4 = new BasicDBObject("_id", "$_id.ProductId");
			groupfields4.put("FeatureWord",
					new BasicDBObject("$push", new BasicDBObject("Feature",
							"$_id.SubDimension").append("Word", "$Word")));
			DBObject group4 = new BasicDBObject("$group", groupfields4);
			pipeLine.add(group4);
			AggregationOutput output = collections.aggregate(pipeLine);
			Iterable<DBObject> results = output.results();

			if (results != null) {
				for (DBObject dbObject : results) {
					System.out.println("Here");
				}
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured while fetching fields. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return null;
	}

	public static void InsertObjectList(List<DBObject> list,
			String collectionName) {
		DB db = null;
		try {
			if (list != null) {
				System.out.println("count of objects to insert in mongo "
						+ list.size() + " into collection " + collectionName);
			}
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			// boolean auth = db.authenticate("username",
			// "password".toCharArray());
			DBCollection table = db.getCollection(collectionName);
			WriteConcern concern = new WriteConcern();
			concern.continueOnErrorForInsert(true);
			table.insert(list, concern);

		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
	}

	public static void insertPostsForTripAdvisor(List<Feed> feeds) {
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("tripadvisorposts");
			for (Feed feed : feeds) {
				BasicDBObject feedtoInsert = new BasicDBObject();
				feedtoInsert.put("_id", feed.getFeedId());
				feedtoInsert.put("subpoductId", feed.getSubProductId());
				feedtoInsert.put("EntityId", feed.getEntityId());
				feedtoInsert.put("content", feed.getFeedData());
				feedtoInsert.put("createdDate", feed.getFeedDate());
				feedtoInsert.put("SourceID", feed.getSourceId());
				collections.insert(feedtoInsert);
			}
		} catch (Exception e) {

		}
	}

	public static boolean isStopword(String word) {
		DB db = null;
		boolean isStopword = false;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("stopwords");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("_id", word);
			DBObject obj = collections.findOne(whereQuery);
			if (obj != null) {
				isStopword = true;
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);

		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return isStopword;
	}

	public static void main(String[] args) throws ParseException, IOException {
		// getFeatureProductCountForSubProduct(10);
		// getPositiveKeyWords("BT014", "5");
		// getNameUrl("KBT007");

		// getNounCountForSubProduct(1);
		// LOG.info(getFeatureCommentCountForEntitySubProduct(417, 18, null,
		// null,
		// null, true));

		/*
		 * This cod would update the products collection by inserting positive
		 * and negative keyword
		 */
		// updateProductsWithkeyWords();
		// getProductsForYahoo(0, 4, "nordstromboots");
		// getAllNounWords();
		// getSimilarProducts("new1026977");
		// getklwinesCategoryCount("find wine by flavors", "spicy");
		// getsubRegionForKlWines("wine by style", "balanced", "France");
		getSimilarProductsKlwines(420, 25, "new1022679");
		// getFeatureWordsForKlwines(420, 25, "new1147758");
		// List<String> list = new ArrayList<>();
		// getSimilarProductIdsForProduct(420, 25, "", list);
		/*
		 * 
		 * getSimilarProductIdsForSubDimension(420, 25, "wine by flavors", list,
		 * null);
		 */}

	/**
	 * This method is used for the storing the details about the individual
	 * review, Used in case of (is This Review Helpful?) Increments the positive
	 * count or negative count based on the users choice.
	 * 
	 * @param postId
	 *            PostId of the review that needs to be rated
	 * @param isPositive
	 *            Indicates whether the rating is positive that is helpful or
	 *            negative.
	 */
	public static void setReviewCount(String postId, String isPositive) {
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("reviewCount");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("_id", postId);
			DBCursor cursor = collections.find(whereQuery);
			if (cursor.count() == 0) {
				BasicDBObject insertObject = new BasicDBObject();
				insertObject.put("_id", postId);
				if (isPositive.equals("true")) {
					insertObject.put("posCount", 1);
					insertObject.put("negCount", 0);
				} else {
					insertObject.put("posCount", 0);
					insertObject.put("negCount", 1);
				}
				collections.insert(insertObject);

			} else {
				BasicDBObject query = new BasicDBObject().append("_id", postId);
				BasicDBObject inc = new BasicDBObject();
				if (isPositive.equals("true")) {
					inc.append("$inc",
							new BasicDBObject().append("posCount", 1));
				} else {
					inc.append("$inc",
							new BasicDBObject().append("negCount", 1));
				}
				collections.update(query, inc);

			}

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

	}

	/**
	 * 
	 */
	private static void upadateData(ArrayList<KeyWord> words, String productId,
			String fieldName, int totCount) {
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("products");
			List<BasicDBObject> keywords = new ArrayList<>();
			for (KeyWord word : words) {
				BasicDBObject keywordObject = new BasicDBObject("word",
						word.getKeyWord());
				keywordObject.append("count", word.getcount());
				keywords.add(keywordObject);
			}
			DBObject object = collections.findOne(new BasicDBObject("_id",
					productId));
			collections.update(object,
					new BasicDBObject("$set", new BasicDBObject(fieldName,
							keywords).append("TotalWordsCount", totCount)
							.append(fieldName + "Count", keywords.size())));

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
	}

	/**
	 * 
	 */
	private static void updateProductsWithkeyWords() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(
					"/home/mallikarjuna/data/nordstromboots/prodIds.txt"));
		} catch (FileNotFoundException e) {
			LOG.error(
					"FileNotFoundException while performing operation in main",
					e);
		}
		try {
			String sline = "";
			while ((sline = br.readLine()) != null) {
				ArrayList<KeyWord> poswords = getPositiveKeyWords(sline, "20",
						0, new ProductDetails(), 421, 2);
				int totalCount = poswords.size();
				upadateData(poswords, sline, "PosKeywords", totalCount);
				ArrayList<KeyWord> negwords = getNegativeKeyWords(sline, "20",
						0, new ProductDetails(), 421, 2);
				totalCount += negwords.size();
				upadateData(negwords, sline, "NegKeyWords", totalCount);
			}
		} catch (IOException e) {

		}

	}

}