/**
 *  * Copyright (c) 2014 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.commons.FilePropertyManager;
import com.raremile.prd.inferlytics.entity.Product;
import com.raremile.prd.inferlytics.entity.StoreDetailsEntity;
import com.raremile.prd.inferlytics.utils.SimilarWinesFilter;

/**
 * @author mallikarjuna
 * @created 12-May-2014
 * 
 *          This class is used to update the mongo collections with the extra
 *          data that are necessary for displaying on the dashboard.
 * 
 * 
 */
public class MongoUpdater extends MongoConnection {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(MongoUpdater.class);

	/**
	 * @param string
	 * @return
	 */
	private static List<String> getAllProductIds(String string, String dbName) {
		List<String> productsList = new ArrayList<>();
		try {
			db = getMongoDB(dbName);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection table = db.getCollection("products");

			DBCursor cursor = table.find(new BasicDBObject("Product",
					"macysClothing"));
			while (cursor.hasNext()) {
				DBObject object = cursor.next();
				productsList.add(object.get("ProductId").toString());
			}
			// System.out.println(productsList);
		} catch (UnknownHostException e) {

			LOG.error(
					"UnknownHostException while performing operation in getAllProductIds",
					e);
		}

		return productsList;

	}

	/**
	 * @param productId
	 * @param subProductId
	 * @param entityId
	 * @param dbname
	 * @return
	 */
	private static Product getAvgScoreandTotalCommentsCount(String productId,
			int subProductId, int entityId, String dbname) {
		Product product = new Product();

		double avgScore = 0;
		try {
			FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(dbname);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("posts");

			DBCursor cursor = collections.find(new BasicDBObject("_id",
					new BasicDBObject("$regex", java.util.regex.Pattern
							.compile("^" + productId + ":"))));
			double score = 0.0;
			while (cursor.hasNext()) {
				DBObject object = cursor.next();
				if (object.get("rating") != null) {
					score += Double.valueOf(object.get("rating").toString());
				}
			}
			if (cursor.size() != 0) {
				avgScore = score / cursor.size();
			}
			product.setAverageScore(avgScore);
			product.setTotalCount(cursor.size());
		} catch (UnknownHostException e) {

			LOG.error(
					"UnknownHostException while performing operation in getAvgScore",
					e);
		}

		return product;

	}

	private static List<String> getProductIdsForSimilarWines(String productId,
			String category, int subproductId) {
		DB db = null;
		ArrayList<String> subDimensionsToMatch = new ArrayList<>();
		subDimensionsToMatch.add("wine by finish");
		subDimensionsToMatch.add("wines by aromas");
		subDimensionsToMatch.add("wine by style");
		subDimensionsToMatch.add("wine by body");
		subDimensionsToMatch.add("pairing suggestions");
		List<String> productIds = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();

			DBCollection collections = db.getCollection("features");

			DBObject unwind = new BasicDBObject("$unwind", "$SubDimension");
			BasicDBObject matchParameters = new BasicDBObject();
			matchParameters.put("ProductId", productId);
			matchParameters.put("SubDimension", new BasicDBObject("$in",
					subDimensionsToMatch));
			BasicDBObject match = new BasicDBObject();
			match.put("$match", matchParameters);
			DBObject groupfileds = new BasicDBObject("_id", "$SubDimension");
			groupfileds.put("SynonymWord", new BasicDBObject("$addToSet",
					"$SynonymWord"));

			DBObject group = new BasicDBObject("$group", groupfileds);
			LOG.info(unwind + "," + match + "," + group);

			Map<String, ArrayList<String>> dimensionList = new HashMap<>();
			Map<String, ArrayList<String>> dimensionProductIdsList = new HashMap<>();

			@SuppressWarnings("deprecation")
			AggregationOutput output = collections.aggregate(unwind, match,
					group);
			Iterable<DBObject> results = output.results();

			if (results != null) {
				for (DBObject dbObject : results) {
					String subDimension = dbObject.get("_id").toString();
					@SuppressWarnings("unchecked")
					ArrayList<String> wordsList = (ArrayList<String>) dbObject
					.get("SynonymWord");
					dimensionList.put(subDimension, wordsList);
				}
			}
			if (dimensionList.get("wine by finish") != null
					&& dimensionList.get("wines by aromas") != null) {
				BasicDBObject matchParametersForFinish = new BasicDBObject();
				matchParametersForFinish.put("SubDimension", "wine by finish");
				matchParametersForFinish.put("SubProductId", subproductId);
				matchParametersForFinish.put("SynonymWord", new BasicDBObject(
						"$in", dimensionList.get("wine by finish")));
				// matchParametersForFinish.put("Varietal", category);

				BasicDBObject matchForFinish = new BasicDBObject();
				matchForFinish.put("$match", matchParametersForFinish);
				DBObject groupfieldsForFinish = new BasicDBObject("_id", "null");
				groupfieldsForFinish.put("ProductIds", new BasicDBObject(
						"$addToSet", "$ProductId"));
				DBObject groupForFinish = new BasicDBObject("$group",
						groupfieldsForFinish);
				output = collections.aggregate(matchForFinish, groupForFinish);
				results = output.results();

				if (results != null) {
					for (DBObject dbObject : results) {
						dimensionProductIdsList.put("wine by finish",
								(ArrayList<String>) dbObject.get("ProductIds"));
					}
				}

				if (dimensionProductIdsList.get("wine by finish") != null) {
					dimensionProductIdsList.get("wine by finish").remove(
							productId);
					dimensionProductIdsList.put(
							"wines by aromas",
							getSimilarWinesProductIdsForSubDimension(
									"wines by aromas", dimensionProductIdsList
									.get("wine by finish"),
									dimensionList.get("wines by aromas")));
				}
				if (dimensionProductIdsList.get("wines by aromas") != null
						&& dimensionList.get("wine by style") != null) {
					dimensionProductIdsList.put(
							"wine by style",
							getSimilarWinesProductIdsForSubDimension(
									"wine by style", dimensionProductIdsList
									.get("wines by aromas"),
									dimensionList.get("wine by style")));
				}
				if (dimensionProductIdsList.get("wine by style") != null
						&& dimensionList.get("wine by body") != null) {
					dimensionProductIdsList.put(
							"wine by body",
							getSimilarWinesProductIdsForSubDimension(
									"wine by body", dimensionProductIdsList
									.get("wine by style"),
									dimensionList.get("wine by body")));
				}

				if (dimensionProductIdsList.get("wine by body") != null
						&& dimensionList.get("pairing suggestions") != null) {
					dimensionProductIdsList
					.put("pairing suggestions",
							getSimilarWinesProductIdsForSubDimension(
									"pairing suggestions",
									dimensionProductIdsList
									.get("wine by body"),
									dimensionList
									.get("pairing suggestions")));
				}

				productIds = SimilarWinesFilter
						.sortProductIds(dimensionProductIdsList);

			} else {
				return null;
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

	private static ArrayList<String> getSimilarWinesProductIdsForSubDimension(
			String subDimension, List<String> productIds,
			ArrayList<String> words) {
		DB db = null;
		ArrayList<String> productIdsForDimension = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");

			BasicDBObject matchParametersForFinish = new BasicDBObject();
			matchParametersForFinish.put("SubDimension", subDimension);
			matchParametersForFinish.put("SynonymWord", new BasicDBObject(
					"$in", words));
			matchParametersForFinish.put("ProductId", new BasicDBObject("$in",
					productIds));

			BasicDBObject matchForFinish = new BasicDBObject();
			matchForFinish.put("$match", matchParametersForFinish);
			DBObject groupfieldsForFinish = new BasicDBObject("_id", "null");
			groupfieldsForFinish.put("ProductIds", new BasicDBObject(
					"$addToSet", "$ProductId"));
			DBObject groupForFinish = new BasicDBObject("$group",
					groupfieldsForFinish);
			LOG.info(matchForFinish + "," + groupForFinish);
			ArrayList<DBObject> aggregationParams = new ArrayList<>();
			aggregationParams.add(matchForFinish);
			aggregationParams.add(groupForFinish);
			AggregationOutput output = collections.aggregate(aggregationParams);
			Iterable<DBObject> results = output.results();
			if (results != null) {
				for (DBObject dbObject : results) {
					productIdsForDimension = (ArrayList<String>) dbObject
							.get("ProductIds");
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
		return productIdsForDimension;
	}

	/**
	 * @return
	 */
	private static List<BasicDBObject> getTopics(String productId,
			int entityId, int subProductId, String database) {

		DB db = null;

		List<BasicDBObject> topics = new ArrayList<>();
		try {
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");

			List<DBObject> pipeline = new ArrayList<>();
			BasicDBObject matchParameters = new BasicDBObject();
			matchParameters.put("SubProductId", subProductId);
			matchParameters.put("EntityId", entityId);
			matchParameters.put("ProductId", productId);
			matchParameters.put("SentenceScore", new BasicDBObject("$ne", 0));
			BasicDBObject match = new BasicDBObject("$match", matchParameters);

			pipeline.add(match);

			BasicDBObject unwind = new BasicDBObject("$unwind", "$SubDimension");
			pipeline.add(unwind);

			BasicDBObject groupParameters1 = new BasicDBObject();
			groupParameters1.append("SubDimension", "$SubDimension");
			groupParameters1.append("SynonymWord", "$SynonymWord");
			groupParameters1.append("PostId", "$PostId");

			BasicDBObject group1 = new BasicDBObject("$group",
					new BasicDBObject("_id", groupParameters1).append(
							"SentenceScore",
							new BasicDBObject("$first", "$SentenceScore"))
							.append("Word",
									new BasicDBObject("$first", "$Word")));
			pipeline.add(group1);
			BasicDBObject groupParameters2 = new BasicDBObject();
			groupParameters2.append("SubDimension", "$_id.SubDimension");
			groupParameters2.append("SynonymWord", "$_id.SynonymWord");

			BasicDBObject group2 = new BasicDBObject(
					"$group",
					new BasicDBObject("_id", groupParameters2)
					.append("SentenceScores",
							new BasicDBObject("$push", "$SentenceScore"))
							.append("Count", new BasicDBObject("$sum", 1))
							.append("Words",
									new BasicDBObject("$addToSet", "$Word")));
			pipeline.add(group2);

			BasicDBObject unwind2 = new BasicDBObject();
			unwind2.append("$unwind", "$SentenceScores");
			pipeline.add(unwind2);

			BasicDBObject match2 = new BasicDBObject();
			match2.append("$match", new BasicDBObject("SentenceScores",
					new BasicDBObject("$gt", 0)));
			pipeline.add(match2);
			BasicDBObject groupParameters3 = new BasicDBObject();
			groupParameters3.append("SubDimension", "$_id.SubDimension");
			groupParameters3.append("SynonymWord", "$_id.SynonymWord");

			BasicDBObject group3 = new BasicDBObject("$group",
					new BasicDBObject("_id", groupParameters2)
			.append("PosCount", new BasicDBObject("$sum", 1))
			.append("Count",
					new BasicDBObject("$first", "$Count"))
					.append("Words",
							new BasicDBObject("$first", "$Words")));
			pipeline.add(group3);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject(
					"_id.SubDimension", 1).append("Count", -1));
			pipeline.add(sort);
			BasicDBObject groupParameters4 = new BasicDBObject();
			groupParameters4.append("_id", "$_id.SubDimension");
			groupParameters4.append(
					"traits",
					new BasicDBObject("$push", new BasicDBObject("name",
							"$_id.SynonymWord").append("count", "$Count")
							.append("posCount", "$PosCount")
							.append("Words", "$Words")));
			groupParameters4.append("count", new BasicDBObject("$sum", 1));

			BasicDBObject group4 = new BasicDBObject("$group", groupParameters4);
			pipeline.add(group4);
			// System.out.println(pipeline);
			AggregationOutput output = collections.aggregate(pipeline);
			System.out.println(pipeline);
			Iterable<DBObject> result = output.results();
			for (DBObject object : result) {
				BasicDBObject topicObject = new BasicDBObject();
				topicObject.append("name", object.get("_id").toString());
				List<BasicDBObject> traits = (List<BasicDBObject>) object
						.get("traits");

				for (BasicDBObject trait : traits) {
					trait.put("SubDimenison", object.get("_id").toString());
					topics.add(trait);
				}

			}

			System.out.println(new Gson().toJson(topics));
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);

		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured while fetching fields. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("Exception", ex);
			System.out.println(ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

		return topics;

	}

	/**
	 * @return
	 */
	private static List<BasicDBObject> getTraits(String productId,
			int entityId, int subProductId, String database,
			Map<String, Integer> traitsScores) {

		DB db = null;

		List<BasicDBObject> topics = new ArrayList<>();
		try {
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");

			List<DBObject> pipeline = new ArrayList<>();
			BasicDBObject matchParameters = new BasicDBObject();
			matchParameters.put("SubProductId", subProductId);
			matchParameters.put("EntityId", entityId);
			matchParameters.put("ProductId", productId);
			matchParameters.put("SentenceScore", new BasicDBObject("$ne", 0));

			BasicDBObject match = new BasicDBObject("$match", matchParameters);

			pipeline.add(match);

			BasicDBObject groupParameters1 = new BasicDBObject();
			groupParameters1.append("SynonymWord", "$SynonymWord");
			groupParameters1.append("PostId", "$PostId");
			BasicDBList list = new BasicDBList();
			list.add("$SentenceScore");
			list.add(0);
			BasicDBObject group1 = new BasicDBObject("$group",
					new BasicDBObject("_id", groupParameters1).append(
							"Values",
							new BasicDBObject("$addToSet", new BasicDBObject(
									"$gt", list))).append("Word",
											new BasicDBObject("$first", "$Word")));

			pipeline.add(group1);
			BasicDBObject unwind1 = new BasicDBObject("$unwind", "$Values");
			pipeline.add(unwind1);

			BasicDBObject groupParameters2 = new BasicDBObject();

			groupParameters2.append("SynonymWord", "$_id.SynonymWord");
			groupParameters2.append("isPositive", "$Values");

			BasicDBObject group2 = new BasicDBObject("$group",
					new BasicDBObject("_id", groupParameters2).append("Count",
							new BasicDBObject("$sum", 1)).append("Words",
									new BasicDBObject("$addToSet", "$Word")));
			pipeline.add(group2);

			BasicDBObject group3 = new BasicDBObject("$group",
					new BasicDBObject("_id", "$_id.SynonymWord")
			.append("TotalCount",
					new BasicDBObject("$sum", "$Count"))
					.append("Values",
							new BasicDBObject("$push",
									new BasicDBObject("isPositive",
											"$_id.isPositive").append(
													"Count", "$Count")))
													.append("Words",
															new BasicDBObject("$push", "$Words")));
			pipeline.add(group3);
			BasicDBObject unwind2 = new BasicDBObject("$unwind", "$Words");
			pipeline.add(unwind2);
			pipeline.add(unwind2);

			BasicDBObject groupParameters4 = new BasicDBObject();
			groupParameters4.append("_id", "$_id");
			groupParameters4
			.append("TotalCount",
					new BasicDBObject("$first", "$TotalCount"))
					.append("Values", new BasicDBObject("$first", "$Values"))
					.append("Words", new BasicDBObject("$addToSet", "$Words"));
			groupParameters4.append("count", new BasicDBObject("$sum", 1));

			BasicDBObject group4 = new BasicDBObject("$group", groupParameters4);
			pipeline.add(group4);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject(
					"TotalCount", -1));
			pipeline.add(sort);
			System.out.println(pipeline);

			AggregationOutput output = collections.aggregate(pipeline);

			Iterable<DBObject> result = output.results();
			for (DBObject object : result) {
				BasicDBObject topicObject = new BasicDBObject();
				topicObject.append("name", object.get("_id").toString());
				topicObject.append("count", (int) object.get("TotalCount"));
				topicObject.append("Words", object.get("Words"));

				BasicDBObject posorNegCount = (BasicDBObject) ((List<DBObject>) object
						.get("Values")).get(0);
				if (posorNegCount.getBoolean("isPositive")) {
					// System.out.println(object.get("_id").toString());

					topicObject.append("posCount",
							(int) posorNegCount.get("Count"));

				} else {
					int posCount = (int) object.get("TotalCount")
							- (int) posorNegCount.get("Count");

					topicObject.append("posCount", posCount);
				}

				topics.add(topicObject);
			}

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
			System.out.println(ex);
		} catch (MongoException ex) {
			// Exception never thrown
			System.out.println(ex);
			LOG.error("MongoException occured while fetching fields. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("Exception", ex);
			System.out.println(ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

		return topics;

	}

	private static Map<String, Integer> getTraitScoreMap() {

		Map<String, Integer> traitScoreMap = new HashMap<>();
		BufferedReader br;
		List<String> traitsList = new ArrayList<>();
		try {
			br = new BufferedReader(new FileReader(
					"/home/mallikarjuna/data/destinationxl/traits/traits.txt"));
			String line = "";
			while ((line = br.readLine()) != null) {
				traitScoreMap.put(line.split("\t")[0].toLowerCase(),
						Integer.valueOf(line.split("\t")[1]));
				traitsList.add(line.split("\t")[0].toLowerCase());
			}
			br.close();
			// System.out.println(new Gson().toJson(traitsList));
		} catch (Exception e) {

			LOG.error(
					"FileNotFoundException while performing operation in getTraitScoreMap",
					e);
		}

		return traitScoreMap;

	}

	public static void main(String[] args) throws ParseException, IOException {
		// setOverallScoreAndCompetitorScoreForCar(424, 29, "enterpriseCar");
		// updateAvgScoreForStores(34, 427);
		/*
		 * Scanner in = new Scanner(System.in);
		 * 
		 * String s = in.next(); String b = in.next(); System.out.println(s +
		 * b);
		 */
		updateProductsCollection("macysClothing", 417, 42, "activewear");
		// updateProductsCollection("bhphotovideo", 433, 43, "Inferlytics");
		// getTopics("36282", 430, 37, "Inferlytics");

		// updateReviewsDataForReviewCapture(37);
		// updateproductsForReviewCapture("destinationxl");
	}

	/* This method updates the products */

	private static void setOverallCompetitorScoreForCar(int entityId,
			int subProductId, String productName) {

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
			BasicDBObject match = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();
			ArrayList<String> competitorIds = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDaoUtility().getCompetitorIds(424);
			for (String id : competitorIds) {
				int compEntityId = Integer.parseInt(id.split(":")[0]);
				int compSubProductId = Integer.parseInt(id.split(":")[1]);

				matchQuery.put("StateCode", new BasicDBObject("$ne", null));
				matchQuery.put("EntityId", compEntityId);
				matchQuery.put("SubProductId", compSubProductId);
				match.append("$match", matchQuery);
				BasicDBObject groupQuery = new BasicDBObject();
				groupQuery.append("_id", "$StateCode");
				groupQuery.append("AvgScore", new BasicDBObject("$avg",
						"$SentenceScore"));
				groupQuery.append("Stores", new BasicDBObject("$addToSet",
						"$ProductId"));
				BasicDBObject group = new BasicDBObject("$group", groupQuery);

				BasicDBObject sort = new BasicDBObject("$sort",
						new BasicDBObject("_id", 1));
				LOG.info(match + "," + group + "," + sort);
				AggregationOutput output = collections.aggregate(match, group,
						sort);

				Iterable<DBObject> result = output.results();
				if (result != null) {
					for (DBObject object : result) {
						StoreDetailsEntity state = new StoreDetailsEntity();
						state.setAvgScore((Double) object.get("AvgScore"));
						state.setId(object.get("_id").toString());
						updateScore((Double) object.get("AvgScore"),
								productName, state.getId());
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

	}

	private static void setOverallScoreAndCompetitorScoreForCar(int entityId,
			int subProductId, String productName) {

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
			BasicDBObject match = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.put("SubProductId", subProductId);
			matchQuery.put("EntityId", entityId);

			matchQuery.put("StateCode", new BasicDBObject("$ne", null));
			match.append("$match", matchQuery);
			BasicDBObject groupQuery = new BasicDBObject();
			groupQuery.append("_id", "$StateCode");
			groupQuery.append("AvgScore", new BasicDBObject("$avg",
					"$SentenceScore"));
			groupQuery.append("Stores", new BasicDBObject("$addToSet",
					"$ProductId"));
			BasicDBObject group = new BasicDBObject("$group", groupQuery);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject(
					"_id", 1));
			LOG.info(match + "," + group + "," + sort);
			AggregationOutput output = collections
					.aggregate(match, group, sort);

			Iterable<DBObject> result = output.results();
			if (result != null) {
				for (DBObject object : result) {
					StoreDetailsEntity state = new StoreDetailsEntity();
					state.setAvgScore((Double) object.get("AvgScore"));
					state.setId(object.get("_id").toString());
					updateScore((Double) object.get("AvgScore"), productName,
							state.getId());
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

	}

	private static void updateAvgScoreForStores(int subProductId, int entityId) {
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

			BasicDBObject unwind = new BasicDBObject("$unwind", "$SubDimension");
			BasicDBObject match = new BasicDBObject();
			match.put("$match", matchParameters);

			BasicDBObject groupfields = new BasicDBObject("_id", "$ProductId");
			groupfields.append("OverallScore", new BasicDBObject("$avg",
					"$SentenceScore"));
			BasicDBObject group = new BasicDBObject("$group", groupfields);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject(
					"OverallScore", 1));

			List<DBObject> aggregateParams = new ArrayList<>();
			aggregateParams.add(match);
			aggregateParams.add(unwind);
			aggregateParams.add(group);
			aggregateParams.add(sort);

			AggregationOutput output = collections.aggregate(aggregateParams);
			collections = db.getCollection("products");
			Iterable<DBObject> results = output.results();
			for (DBObject dbObject : results) {
				String productId = dbObject.get("_id").toString();
				double score = (double) dbObject.get("OverallScore");
				productId = productId.substring(0, 6);
				DBObject object = new BasicDBObject("CategoryId", productId);
				collections.update(object, new BasicDBObject("$set",
						new BasicDBObject("AvgScore", score)), false, true);
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

	private static void updateProductsCollection(String productName,
			int entityId, int subProductId, String dbname) {
		List<String> productIdList = getAllProductIds(productName, dbname);
		for (String productId : productIdList) {
			List<BasicDBObject> topics = getTopics(productId, entityId,
					subProductId, dbname);

			Product product = getAvgScoreandTotalCommentsCount(productId,
					subProductId, entityId, dbname);
			System.out.println(productId);
			try {
				db = getMongoDB(dbname);
				db.requestStart();
				db.requestEnsureConnection();
				DBCollection table = db.getCollection("products");
				DBObject object = table.findOne(new BasicDBObject("ProductId",
						productId));
				table.update(
						object,
						new BasicDBObject("$set", new BasicDBObject("traits",
								topics).append("averageRating",
										product.getAverageScore()).append(
												"reviewCount", product.getTotalCount())));
			} catch (UnknownHostException e) {

				LOG.error(
						"UnknownHostException while performing operation in updateProductsCollection",
						e);
			}

		}

	}

	private static void updateproductsForReviewCapture(String productName) {

		List<DBObject> objects = new ArrayList<>();

		try {
			FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB("Inferlytics");
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("products");

			DBCursor cursor = collections.find(new BasicDBObject("Product",
					productName));

			while (cursor.hasNext()) {
				DBObject object = cursor.next();
				DBObject newProductObject = new BasicDBObject();
				newProductObject.put("_id", object.get("_id"));
				newProductObject.put("productDescription",
						object.get("Description"));
				newProductObject.put("productUrl", object.get("ProductUrl"));
				newProductObject.put("brandId", object.get("content"));
				newProductObject.put("productPrice", object.get("Price"));
				newProductObject.put("productName", object.get("ProductName"));
				newProductObject.put("productImageUrl",
						object.get("ProdImgUrl"));
				int reviewsCount = (int) object.get("reviewCount");
				newProductObject.put("sumOfRatings", 0);
				if (object.get("Rating") != null) {
					newProductObject.put("averageRating",
							Double.valueOf(object.get("Rating").toString()));
				} else {
					newProductObject.put("averageRating", 0);
				}
				newProductObject.put("noOfReviews", reviewsCount);
				newProductObject.put(
						"detailedRatings",
						new BasicDBObject("1starRatings", 0)
						.append("2starRatings", 0)
						.append("3starRatings", 0)
						.append("4starRatings", 0)
						.append("5starRatings", 0));

				objects.add(newProductObject);
			}
			db = getMongoDB("destinationXL");
			collections = db.getCollection("products");
			collections.insert(objects);
		} catch (Exception e) {
			LOG.info(
					"UnknownHostException while performing operation in getAvgScore",
					e);
		}
	}

	public static void updateProductsWithSimilarWinesData(int skip)
			throws UnknownHostException {
		String database = FilePropertyManager.getProperty(
				ApplicationConstants.MONGO_PROPERTIES_FILE,
				ApplicationConstants.MONGO_DB);
		db = getMongoDB(database);
		db.requestStart();
		db.requestEnsureConnection();
		DBCollection table = db.getCollection("products");
		DBCursor cursor = table
				.find(new BasicDBObject("Product", "tescowines"))
				.sort(new BasicDBObject("ProductId", -1)).skip(skip).limit(500);
		while (cursor.hasNext()) {
			DBObject objoutput = cursor.next();
			String productId = objoutput.get("ProductId").toString();
			// String category= objoutput.get("Category").toString();
			List<String> productIds = getProductIdsForSimilarWines(productId,
					null, 26);
			DBObject object = table
					.findOne(new BasicDBObject("_id", productId));
			table.update(object, new BasicDBObject("$set", new BasicDBObject(
					"SimilarWines", productIds)));
		}

	}

	/**
	 * @param subProductId
	 */
	private static void updateReviewsDataForReviewCapture(int subProductId) {

		List<DBObject> objects = new ArrayList<>();

		try {
			FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB("Inferlytics");
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("posts");

			DBCursor cursor = collections.find(new BasicDBObject("subpoductId",
					subProductId));

			while (cursor.hasNext()) {
				DBObject object = cursor.next();
				DBObject newReviewObject = new BasicDBObject();
				newReviewObject.put("_id", object.get("_id"));
				newReviewObject.put("INFRR-reviewTitle", "");
				newReviewObject.put("productId", object.get("ProductId"));
				newReviewObject.put("INFRR-reviewContent",
						object.get("content"));
				newReviewObject
				.put("INFRR-overallRating", object.get("rating"));
				newReviewObject.put("Date", object.get("createdDate"));
				newReviewObject.put("helpfulCount", 0);
				newReviewObject.put("inappropriate", 0);
				newReviewObject.put("nonHelpfulCount", 0);
				newReviewObject.put("status", "Approved");
				newReviewObject.put("userDetails",
						new BasicDBObject("userId", "").append("email", "")
						.append("username", "").append("source", "")
						.append("imgUrl", "undefined"));
				objects.add(newReviewObject);
			}
			db = getMongoDB("destinationXL");
			collections = db.getCollection("reviews");
			collections.insert(objects);
		} catch (Exception e) {
			LOG.info(
					"UnknownHostException while performing operation in getAvgScore",
					e);
		}
	}

	/**
	 * @param state
	 * @param productName
	 */
	private static void updateScore(Double score, String productName,
			String stateCode) {
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("products");
			new ArrayList<>();

			DBObject object = new BasicDBObject("StateCode", stateCode).append(
					"Product", productName);
			collections.update(object, new BasicDBObject("$set",
					new BasicDBObject("OverallScoreForState", score)), false,
					true);

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

}
