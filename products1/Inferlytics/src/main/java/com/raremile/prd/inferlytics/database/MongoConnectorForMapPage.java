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

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.commons.FilePropertyManager;
import com.raremile.prd.inferlytics.entity.IdMap;
import com.raremile.prd.inferlytics.entity.KeyWord;
import com.raremile.prd.inferlytics.entity.Post;
import com.raremile.prd.inferlytics.entity.StoreDetailsEntity;
import com.raremile.prd.inferlytics.utils.ChartData;

/**
 * @author mallikarjuna
 * @created 22-May-2014
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class MongoConnectorForMapPage extends MongoConnector {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(MongoConnectorForMapPage.class);

	/**
	 * @param entity
	 * @param subProduct
	 * @return
	 */
	public static String getAvgScoreForStates(String entity, String subProduct) {
		DB db = null;
		ArrayList<StoreDetailsEntity> states = new ArrayList<>();
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
			matchQuery.put("SubProductId", IdMap.getSubproductId(subProduct));
			matchQuery.put("EntityId", IdMap.getEntityId(entity));

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
					@SuppressWarnings("unchecked")
					ArrayList<String> stores = (ArrayList<String>) object
					.get("Stores");
					state.setStorenames(getStoreNames(stores));
					state.setStoresCount(stores.size());
					states.add(state);
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
		return new Gson().toJson(states);

	}

	public static List<KeyWord> getEnterpriseCarDataForPieChart(
			Integer entityId, Integer subProductId, String storeId,
			String stateCode) {
		DB db = null;
		List<KeyWord> keyWords = new ArrayList<>();
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
			matchQuery.append("EntityId", entityId);
			matchQuery.append("SubProductId", subProductId);
			if (storeId != null && !storeId.contains("all")
					&& !storeId.contains("null")) {
				matchQuery.put("ProductId", storeId);
			}
			matchQuery.append("StateCode", stateCode);
			match.put("$match", matchQuery);

			DBObject unwind1 = new BasicDBObject("$unwind", "$SubDimension");
			DBObject groupFields1 = new BasicDBObject("_id", "$SubDimension");
			groupFields1.put("Count", new BasicDBObject("$sum", 1));
			groupFields1.put("SentenceScore", new BasicDBObject("$push",
					"$SentenceScore"));
			BasicDBObject group = new BasicDBObject("$group", groupFields1);

			DBObject unWind2 = new BasicDBObject("$unwind", "$SentenceScore");

			BasicDBObject matchQuery2 = new BasicDBObject();
			matchQuery2.append("SentenceScore", new BasicDBObject("$gt", 0));
			BasicDBObject match2 = new BasicDBObject("$match", matchQuery2);

			BasicDBObject groupfields2 = new BasicDBObject();
			groupfields2.append("_id", "$_id");
			groupfields2
			.append("CountOfPositive", new BasicDBObject("$sum", 1));
			groupfields2.append("TotalCount", new BasicDBObject("$first",
					"$Count"));
			BasicDBObject group2 = new BasicDBObject("$group", groupfields2);

			AggregationOutput output = collections.aggregate(match, unwind1,
					group, unWind2, match2, group2);
			LOG.info(match + "," + unwind1 + "," + group + "," + unWind2 + ","
					+ match2 + "," + group2);
			Iterable<DBObject> result = output.results();
			if (result != null) {
				for (DBObject object : result) {
					KeyWord keyword = new KeyWord();
					keyword.setKeyWord(object.get("_id").toString());
					keyword.setPosCount((Integer) object.get("CountOfPositive"));
					keyword.setTotalCount((Integer) object.get("TotalCount"));
					keyWords.add(keyword);
				}
			}
		} catch (IOException ex) {
			LOG.error("", ex);
		} catch (MongoException ex) {

			LOG.error("MongoException occured during batch insert.", ex);
		} catch (Exception ex) {
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return keyWords;
	}

	/**
	 * @param entity
	 * @param subProduct
	 * @param state
	 * @return
	 */
	public static String getPosNegCommentsCountForMapView(String entity,
			String subProduct, String statecode) {
		DB db = null;
		StoreDetailsEntity storeDetails = new StoreDetailsEntity();
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
			matchQuery.append("EntityId", IdMap.getEntityId(entity));
			matchQuery
			.append("SubProductId", IdMap.getSubproductId(subProduct));
			matchQuery.append("StateCode", statecode);
			matchQuery.put("SentenceScore", new BasicDBObject("$gt", 0));
			match.put("$match", matchQuery);

			BasicDBObject groupfields1 = new BasicDBObject();
			groupfields1.put("_id", "$PostId");

			BasicDBObject group1 = new BasicDBObject("$group", groupfields1);

			BasicDBObject groupfields2 = new BasicDBObject();
			groupfields2.put("_id", null);
			groupfields2.put("Count", new BasicDBObject("$sum", 1));

			BasicDBObject group2 = new BasicDBObject("$group", groupfields2);
			List<DBObject> pipeline = new ArrayList<>();

			pipeline.add(match);
			pipeline.add(group1);
			pipeline.add(group2);
			AggregationOutput ids = collections.aggregate(pipeline);
			Iterable<DBObject> results = ids.results();
			if (results != null) {

				for (DBObject dbObject : results) {
					storeDetails.setPosCommentsCount((int) dbObject
							.get("Count"));
				}
			}
			// Change matchQuery to get thw negative Comments
			matchQuery.put("SentenceScore", new BasicDBObject("$lt", 0));
			ids = collections.aggregate(pipeline);
			results = ids.results();
			if (results != null) {
				for (DBObject dbObject : results) {
					storeDetails.setNegCommentsCount((int) dbObject
							.get("Count"));
				}
			}

		} catch (IOException ex) {
			LOG.error("", ex);
		} catch (MongoException ex) {

			LOG.error("MongoException occured during batch insert.", ex);
		} catch (Exception ex) {
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return new Gson().toJson(storeDetails);

	}

	/**
	 * 
	 * @param entityId
	 * @param subProductId
	 * @param subDimension
	 * @param word
	 * @param state
	 * @param skip
	 * @param limit
	 * @param ispositive
	 * @return
	 */

	public static List<Post> getPostIdsForEnterPriseCar(int entityId,
			int subProductId, String subDimension, String word,
			String stateCode, int skip, int limit, boolean ispositive,
			String storeId) {
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
			if (!subDimension.equals("null")) {
				matchQuery.put("SubDimension", subDimension);
			}
			if (storeId != null && !storeId.contains("all")
					&& !storeId.contains("null")) {
				matchQuery.put("ProductId", storeId);
			}
			matchQuery.put("StateCode", stateCode);

			if (!word.equals("null")) {
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
			LOG.info(matchByEntitySubProduct + "," + group + "," + sortOp + ","
					+ projection + "," + skipposts + "," + limits);
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
	 * @param stores
	 * @return
	 */
	private static ArrayList<String> getStoreNames(ArrayList<String> stores) {
		ArrayList<String> storeNames = new ArrayList<>();
		DB db = null;
		new StoreDetailsEntity();
		new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);

			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("products");
			DBObject findQuery = new BasicDBObject("ProductId",
					new BasicDBObject("$in", stores));
			DBCursor cursor = collections.find(findQuery);
			while (cursor.hasNext()) {
				DBObject object = cursor.next();
				String productName = object.get("ProductName").toString() + "|"
						+ object.get("ProductId").toString();
				storeNames.add(productName);
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
		return storeNames;

	}

	/**
	 * @param entityId
	 * @param subproductId
	 * @param startDate1
	 * @param endDate1
	 * @param isPositive
	 * @param mapOfDates
	 * @param stateCode
	 * @param store
	 * @return
	 */
	public static List<ChartData> getTimelineData(int entityId,
			int subProductId, Date startDate, Date endDate, boolean isPositive,
			Map<String, ChartData> mapOfDates, String stateCode, String storeId) {
		DB db = null;
		List<ChartData> chartdatas = new ArrayList<>();
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
			matchQuery.append("EntityId", entityId);
			matchQuery.append("SubProductId", subProductId);
			matchQuery.append("StateCode", stateCode);
			if (isPositive) {
				matchQuery.append("OverallScore", new BasicDBObject("$gt", 0));
			} else {
				matchQuery.append("OverallScore", new BasicDBObject("$lt", 0));
			}
			if (startDate != null) {
				matchQuery.append(
						"CreatedDate",
						BasicDBObjectBuilder.start("$gte", startDate)
						.add("$lte", endDate).get());
			}

			if (storeId != null && !storeId.contains("all")
					&& !storeId.contains("null")) {
				matchQuery.put("ProductId", storeId);
			}

			match.put("$match", matchQuery);
			BasicDBObject grp1Fields = new BasicDBObject();
			grp1Fields.put("_id", "$PostId");
			grp1Fields.put("Date", new BasicDBObject("$addToSet",
					"$CreatedDate"));
			BasicDBObject group1 = new BasicDBObject("$group", grp1Fields);

			BasicDBObject unwind = new BasicDBObject("$unwind", "$Date");

			BasicDBObject grpFieldsYearandMonth = new BasicDBObject();
			grpFieldsYearandMonth.append("Year", new BasicDBObject("$year",
					"$Date"));
			grpFieldsYearandMonth.append("Month", new BasicDBObject("$month",
					"$Date"));

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
			List<DBObject> pipeline = new ArrayList<>();
			pipeline.add(match);
			pipeline.add(group1);
			pipeline.add(unwind);
			pipeline.add(group);
			pipeline.add(project);
			pipeline.add(sort);
			LOG.info(pipeline);
			AggregationOutput output = collections.aggregate(pipeline);

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

	/**
	 * @param state
	 * @param entityId
	 * @param subproductId
	 * @return
	 */
	public static List<KeyWord> getTopWords(String stateCode, int entityId,
			int subproductId) {
		DB db = null;
		ArrayList<KeyWord> keywords = new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");

			BasicDBObject matchByEntitySubProduct = new BasicDBObject();
			List<DBObject> pipeline = new ArrayList<>();


			DBObject matchQuery = new BasicDBObject("EntityId", entityId);
			matchQuery.put("SubProductId", subproductId);
			if (stateCode != null) {
				matchQuery.put("StateCode", stateCode);
			}
			matchQuery.put("SentenceScore", new BasicDBObject("$ne", 0));
			matchByEntitySubProduct.put("$match", matchQuery);
			pipeline.add(matchByEntitySubProduct);
			DBObject groupdata = new BasicDBObject();
			groupdata.put("SynonymWord", "$SynonymWord");
			groupdata.put("PostId", "$PostId");
			DBObject groupFields = new BasicDBObject("_id", groupdata);
			groupFields.put("Score", new BasicDBObject("$addToSet",
					"$SentenceScore"));
			DBObject group = new BasicDBObject("$group", groupFields);
			pipeline.add(group);
			BasicDBObject unwind = new BasicDBObject("$unwind", "$Score");
			pipeline.add(unwind);

			DBObject groupFields2 = new BasicDBObject("_id", "$_id.SynonymWord");
			groupFields2.put("Count", new BasicDBObject("$sum", 1));
			groupFields2.put("Score", new BasicDBObject("$push", "$Score"));

			DBObject group2 = new BasicDBObject("$group", groupFields2);
			pipeline.add(group2);
			pipeline.add(unwind);

			DBObject matchfields2 = new BasicDBObject("Score",
					new BasicDBObject("$gt", 0));

			DBObject match2 = new BasicDBObject("$match", matchfields2);
			pipeline.add(match2);

			DBObject groupfields3 = new BasicDBObject("_id", "$_id");
			groupfields3.put("PosCount", new BasicDBObject("$sum", 1));
			groupfields3.put("Count", new BasicDBObject("$first", "$Count"));

			DBObject group3 = new BasicDBObject("$group", groupfields3);
			pipeline.add(group3);

			DBObject sortOp = new BasicDBObject("$sort", new BasicDBObject(
					"Count", -1));
			pipeline.add(sortOp);

			DBObject limit = new BasicDBObject("$limit", 10);
			pipeline.add(limit);

			ArrayList<String> arrayValues=new ArrayList<>();
			arrayValues.add("$PosCount");
			arrayValues.add("$Count");
			DBObject projectvalues = new BasicDBObject("percentage",
					new BasicDBObject("$divide", arrayValues));
			projectvalues.put("PosCount", 1);
			projectvalues.put("Count", 1);
			DBObject project = new BasicDBObject("$project", projectvalues);
			pipeline.add(project);
			BasicDBObject sort2 = new BasicDBObject("$sort", new BasicDBObject(
					"percentage", -1));
			pipeline.add(sort2);
			AggregationOutput ids = null;
			ids = collections.aggregate(pipeline);


			Iterable<DBObject> results = ids.results();

			if (results != null) {
				for (DBObject dbObject : results) {
					KeyWord keyword = new KeyWord();
					keyword.setKeyWord((String) dbObject.get("_id"));
					keyword.setcount((Integer) dbObject.get("Count"));
					keyword.setPosCount((Integer) dbObject.get("PosCount"));
					keywords.add(keyword);
				}

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

		return keywords;

	}

	public static void main(String[] args) throws ParseException, IOException {
		getTopWords(null, 427, 34);
	}
}
