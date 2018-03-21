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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
import com.raremile.prd.inferlytics.entity.BrandData;
import com.raremile.prd.inferlytics.entity.IdMap;
import com.raremile.prd.inferlytics.entity.KeyWord;
import com.raremile.prd.inferlytics.entity.Post;
import com.raremile.prd.inferlytics.entity.StoreDetailsEntity;
import com.raremile.prd.inferlytics.utils.Util;

/**
 * @author mallikarjuna
 * @created 10-Jul-2014
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class BeeGoodDBConnector extends MongoConnector {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(BeeGoodDBConnector.class);

	public static final Comparator<KeyWord> KEYWORDS_ORDER = new Comparator<KeyWord>() {

		@Override
		public int compare(KeyWord o1, KeyWord o2) {

			int retValue = 0;
			if (o1.getTotalCount() < o2.getTotalCount()) {
				retValue = -1;
			} else if (o1.getTotalCount() > o2.getTotalCount()) {
				retValue = 1;
			}
			return retValue;
		}
	};

	/**
	 * @param entityId
	 * @param subproductId
	 */
	public static double getAvgScore(int entityId, int subproductId) {
		DB db = null;
		double score = 0;
		new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("posts");
			BasicDBObject matchByEntitySubProduct = new BasicDBObject();
			List<DBObject> pipeline = new ArrayList<>();

			DBObject matchQuery = new BasicDBObject("EntityId", entityId);
			matchQuery.put("subpoductId", subproductId);
			matchByEntitySubProduct.put("$match", matchQuery);
			pipeline.add(matchByEntitySubProduct);
			DBObject groupFields = new BasicDBObject("_id", null);
			groupFields.put("Score", new BasicDBObject("$avg", "$Score"));
			DBObject group = new BasicDBObject("$group", groupFields);
			pipeline.add(group);
			AggregationOutput ids = null;
			ids = collections.aggregate(pipeline);
			Iterable<DBObject> results = ids.results();

			if (results != null) {
				for (DBObject dbObject : results) {
					score = (double) dbObject.get("Score");
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
		return score;
	}

	/**
	 * @param state
	 * @param entityId
	 * @param subproductId
	 * @return
	 */
	public static List<KeyWord> getBarChartDataForSubTopicsTopics(int entityId,
			int subproductId, ArrayList<String> list, String subDimension,
			String source) {
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
			ArrayList<BasicDBObject> orQueryList = new ArrayList<BasicDBObject>();
			BasicDBObject query1 = new BasicDBObject("SentenceScore",
					new BasicDBObject("$gt", 0));
			BasicDBObject query2 = new BasicDBObject("SentenceScore",
					new BasicDBObject("$lt", -0.2));
			orQueryList.add(query1);
			orQueryList.add(query2);


			DBObject matchQuery = new BasicDBObject("EntityId", entityId);
			matchQuery.put("SubProductId", subproductId);
			matchQuery.put("$or", orQueryList);
			if (list != null) {
				matchQuery.put("SynonymWord", new BasicDBObject("$in", list));
			}
			if (!source.equals("null")) {
				if (source.equals("product")) {
					matchQuery.put("Source", "productSite");
				} else {
					matchQuery.put("Source", new BasicDBObject("$ne",
							"productSite"));
				}
			}
			matchQuery.put("SubDimension", subDimension);
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
					"_id", 1));
			pipeline.add(sortOp);
			ArrayList<String> arrayValues = new ArrayList<>();
			arrayValues.add("$PosCount");
			arrayValues.add("$Count");
			DBObject projectvalues = new BasicDBObject("percentage",
					new BasicDBObject("$divide", arrayValues));
			projectvalues.put("PosCount", 1);
			projectvalues.put("Count", 1);
			DBObject project = new BasicDBObject("$project", projectvalues);
			pipeline.add(project);
			AggregationOutput ids = null;
			LOG.info(pipeline);
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
		// System.out.println(new Gson().toJson(keywords));
		return keywords;

	}

	public static List<KeyWord> getBarChartDataForTopics(Integer entityId,
			Integer subProductId, List<String> subdimensions, String source) {
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

			ArrayList<BasicDBObject> orQueryList = new ArrayList<BasicDBObject>();
			BasicDBObject query1 = new BasicDBObject("SentenceScore",
					new BasicDBObject("$gt", 0));
			BasicDBObject query2 = new BasicDBObject("SentenceScore",
					new BasicDBObject("$lt", -0.2));
			orQueryList.add(query1);
			orQueryList.add(query2);

			BasicDBObject match = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.append("EntityId", entityId);
			matchQuery.append("SubProductId", subProductId);

			matchQuery.append("$or", orQueryList);
			if (subdimensions != null) {
				matchQuery.append("SubDimension", new BasicDBObject("$in",
						subdimensions));
			}
			if (!source.equals("null")) {
				if (source.equals("product")) {
					matchQuery.put("Source", "productSite");
				} else {
					matchQuery.put("Source", new BasicDBObject("$ne",
							"productSite"));
				}
			}
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
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject(
					"_id", 1));
			AggregationOutput output = collections.aggregate(match, unwind1,
					group, unWind2, match2, group2, sort);
			LOG.info(match + "," + unwind1 + "," + group + ","
					+ unWind2 + ","
					+ match2 + "," + group2);
			Iterable<DBObject> result = output.results();
			if (result != null) {
				for (DBObject object : result) {
					KeyWord keyword = new KeyWord();
					keyword.setKeyWord(object.get("_id").toString());
					keyword.setPosCount((Integer) object.get("CountOfPositive"));
					keyword.setcount((Integer) object.get("TotalCount"));
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
	 * @param entityId
	 * @param subproductId
	 * @return
	 */
	public static BrandData getBrandData(int entityId, int subproductId) {
		BrandData data = new BrandData();
		DB db = null;

		new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);

			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("posts");
			List<DBObject> pipeline = new ArrayList<>();
			BasicDBObject match = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.append("EntityId", entityId);
			matchQuery.append("subpoductId", subproductId);
			match.put("$match", matchQuery);
			pipeline.add(match);
			BasicDBObject groupfields1 = new BasicDBObject();
			groupfields1.put("_id", "$Source");
			groupfields1.put("totCount", new BasicDBObject("$sum", 1));
			groupfields1.put("Score", new BasicDBObject("$push", "$Score"));
			BasicDBObject group1 = new BasicDBObject("$group", groupfields1);
			pipeline.add(group1);
			BasicDBObject unwind = new BasicDBObject("$unwind", "$Score");
			pipeline.add(unwind);
			BasicDBObject match2 = new BasicDBObject("$match",
					new BasicDBObject("Score", new BasicDBObject("$gt", 0)));
			pipeline.add(match2);
			BasicDBObject groupfields2 = new BasicDBObject();
			groupfields2.put("_id", "$_id");
			groupfields2.put("PosCount", new BasicDBObject("$sum", 1));
			groupfields2.put("Count", new BasicDBObject("$first", "$totCount"));

			BasicDBObject group2 = new BasicDBObject("$group", groupfields2);
			pipeline.add(group2);
			LOG.info(pipeline);
			AggregationOutput ids = collections.aggregate(pipeline);
			Iterable<DBObject> results = ids.results();

			if (results != null) {

				for (DBObject dbObject : results) {
					String source = (String) dbObject.get("_id");
					if (source.equals("productSite")
							|| source.equals("website")) {
						data.setPosReviewsCount((Integer) dbObject
								.get("PosCount") + data.getPosReviewsCount());
						data.setTotalReviewsCount((Integer) dbObject
								.get("Count") + data.getTotalReviewsCount());
						data.setNegReviewsCount(data.getTotalReviewsCount()
								- data.getPosReviewsCount());
					} else {
						data.setSocialMediaPosReviewsCount((Integer) dbObject
								.get("PosCount")
								+ data.getSocialMediaPosReviewsCount());
						data.setSocialMediaReviewsCount((Integer) dbObject
								.get("Count")
								+ data.getSocialMediaReviewsCount());
						data.setSocialMediaNegReviewsCount(data
								.getSocialMediaReviewsCount()
								- data.getSocialMediaPosReviewsCount());
					}
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
		return data;

	}

	public static String getComments(String entity, String subProduct,
			String word, String subDimension, int limit, String source) {
		List<Post> posts = new ArrayList<>();

		posts = getPostIdsForFeatures(IdMap.getEntityId(entity),
				IdMap.getSubproductId(subProduct), subDimension, word, null, 0,
				limit, true, source);

		getFeedsByIds(posts);
		String resultJson = "{\"PosComments\":" + new Gson().toJson(posts)
				+ ",\"NegativeComments\":";
		posts = getPostIdsForFeatures(IdMap.getEntityId(entity),
				IdMap.getSubproductId(subProduct), subDimension, word, null, 0,
				limit, false, source);

		getFeedsByIds(posts);
		resultJson += new Gson().toJson(posts) + "}";
		return resultJson;
	}
	public static List<KeyWord> getDataForPieChart(Integer entityId,
			Integer subProductId, String storeId) {
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
			DBObject lessThan = new BasicDBObject("$lt", -0.2);
			DBObject greaterThan = new BasicDBObject("$gt", 0);

			BasicDBList or = new BasicDBList();
			or.add(new BasicDBObject("SentenceScore", lessThan));
			or.add(new BasicDBObject("SentenceScore", greaterThan));

			matchQuery.append("$or", or);
			/*
			 * matchQuery.append("$or", new BasicDBObject("SentenceScore", new
			 * BasicDBObject("$gt", 0.2).append("$lt", -0.2)));
			 */// new
			// BasicDBObject("$ne",
			// 0));
			if (storeId != null) {
				matchQuery.append("ProductId", storeId);
			}
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
						post.setContent(Util.appendEmTagForComment(
								(String) objoutput.get("content"),
								post.getSentenceNo()));
						if (objoutput.get("Source") != null) {
							post.setSource((String) objoutput.get("Source"));
						}
						Date date = (Date) objoutput.get("createdDate");
						if (date != null) {
							post.setDate(Util.formatDate(date.toString()));
						}
						post.setReviewRating(String.valueOf(objoutput
								.get("rating")));
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

	public static ArrayList<KeyWord> getKeyWordsForSubDimension(
			int entityId, int subProductId, String subDimension,
			String storeId, int limits) {

		ArrayList<KeyWord> keywords = new ArrayList<>();
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
			if (storeId != null) {
				matchQuery.put("ProductId", storeId);
			}
			if (!subDimension.equals("null")) {
				matchQuery.put("SubDimension", subDimension);
			}
			DBObject lessThan = new BasicDBObject("$lt", -0.2);
			DBObject greaterThan = new BasicDBObject("$gt", 0);

			BasicDBList or = new BasicDBList();
			or.add(new BasicDBObject("SentenceScore", lessThan));
			or.add(new BasicDBObject("SentenceScore", greaterThan));

			matchQuery.append("$or", or);
			matchByEntitySubProduct.put("$match", matchQuery);

			DBObject groupFields1 = new BasicDBObject("_id", new BasicDBObject(
					"SynonymWord", "$SynonymWord").append("PostId", "$PostId"));
			groupFields1.put("adjscore", new BasicDBObject("$push",
					"$SentenceScore"));

			DBObject group1 = new BasicDBObject("$group", groupFields1);

			DBObject groupFields2 = new BasicDBObject("_id", "$_id.SynonymWord");
			groupFields2.put("Count", new BasicDBObject("$sum", 1));
			groupFields2.put("adjscore2", new BasicDBObject("$push",
					"$adjscore"));
			DBObject group2 = new BasicDBObject("$group", groupFields2);
			DBObject sortOp = new BasicDBObject("$sort", new BasicDBObject(
					"Count", -1).append("_id", 1));

			DBObject limit = new BasicDBObject("$limit", limits);


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
					matchByEntitySubProduct, group1, group2, sortOp, limit,
					unwind, match2, group3, sortOp2);

			Iterable<DBObject> results = ids.results();
			if (results != null) {
				for (DBObject dbObject : results) {
					KeyWord keyword = new KeyWord();
					keyword.setKeyWord((String) dbObject.get("_id"));
					keyword.setPosCount(((Integer) dbObject.get("thisCount")));
					keyword.setTotalCount(((Integer) dbObject.get("thisCount")));
					keywords.add(keyword);
				}
			}
			match2 = new BasicDBObject("$match", new BasicDBObject("adjscore2",
					new BasicDBObject("$lt", -0.2)));
			ids = collections.aggregate(matchByEntitySubProduct, group1,
					group2, sortOp, limit, unwind, match2, group3, sortOp2);
			Iterable<DBObject> results2 = ids.results();

			if (results2 != null) {
				for (DBObject dbObject : results2) {
					String word = (String) dbObject.get("_id");
					int negCount = (Integer) dbObject.get("thisCount");
					int flag = 0;
					for (KeyWord keyword : keywords) {

						if (keyword.getKeyWord().equals(word)) {
							flag = 1;
							keyword.setNegativeCount(negCount);
							keyword.setTotalCount(keyword.getTotalCount()
									+ negCount);
						}

					}
					if (flag == 0) {
						KeyWord keyword = new KeyWord();
						keyword.setKeyWord(word);
						keyword.setNegativeCount(negCount);
						keyword.setTotalCount(negCount);
						keywords.add(keyword);
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
		Comparator<KeyWord> reverseComparator = Collections
				.reverseOrder(KEYWORDS_ORDER);
		Collections.sort(keywords, reverseComparator);

		return keywords;
	}
	public static String getPosNegCommentsCountFor(String entity,
			String subProduct) {
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
			matchQuery.put("SentenceScore", new BasicDBObject("$lt", -0.2));
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

	public static List<Post> getPostIdsForFeatures(int entityId,
			int subProductId, String subDimension, String word,
			String productId, int skip, int limit, boolean ispositive,
			String source) {
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
				matchQuery.put("SentenceScore", new BasicDBObject("$lt", -0.2));
			}
			if (!source.equals("null")) {
				if (source.equals("product")) {
					matchQuery.put("Source", "productSite");
				} else {
					matchQuery.put("Source", new BasicDBObject("$ne",
							"productSite"));
				}
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
			LOG.info(ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.info("MongoException occured during batch insert. "
					+ ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.info(ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

		return posts;
	}

	public static List<KeyWord> getTopTraits(int entityId, int subproductId,
			boolean isPositive) {
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
			ArrayList<BasicDBObject> orQueryList = new ArrayList<BasicDBObject>();
			BasicDBObject query1 = new BasicDBObject("SentenceScore",
					new BasicDBObject("$gt", 0));
			BasicDBObject query2 = new BasicDBObject("SentenceScore",
					new BasicDBObject("$lt", -0.2));
			orQueryList.add(query1);
			orQueryList.add(query2);
			DBObject matchQuery = new BasicDBObject("EntityId", entityId);
			matchQuery.put("SubProductId", subproductId);
			matchQuery.put("$or", orQueryList);
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

			DBObject limit = new BasicDBObject("$limit", 5);
			pipeline.add(limit);

			ArrayList<String> arrayValues = new ArrayList<>();
			arrayValues.add("$PosCount");
			arrayValues.add("$Count");
			DBObject projectvalues = new BasicDBObject("percentage",
					new BasicDBObject("$divide", arrayValues));
			projectvalues.put("PosCount", 1);
			projectvalues.put("Count", 1);
			DBObject project = new BasicDBObject("$project", projectvalues);
			pipeline.add(project);
			if (isPositive) {
				BasicDBObject sort2 = new BasicDBObject("$sort",
						new BasicDBObject("percentage", -1));
				pipeline.add(sort2);
			} else {
				BasicDBObject sort2 = new BasicDBObject("$sort",
						new BasicDBObject("percentage", 1));
				pipeline.add(sort2);
			}
			AggregationOutput ids = null;
			ids = collections.aggregate(pipeline);

			Iterable<DBObject> results = ids.results();

			if (results != null) {
				for (DBObject dbObject : results) {
					KeyWord keyword = new KeyWord();
					keyword.setKeyWord((String) dbObject.get("_id"));
					keyword.setcount((Integer) dbObject.get("Count"));
					keyword.setPosCount((Integer) dbObject.get("PosCount"));
					Double value = keyword.getPosCount()
							/ (double) keyword.getcount() * 100;
					keyword.setPercentage(value.intValue());
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

	public static void main(String[] args) {
		getBrandData(428, 35);
	}
}
