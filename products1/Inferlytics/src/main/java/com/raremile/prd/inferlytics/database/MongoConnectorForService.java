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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.commons.FilePropertyManager;
import com.raremile.prd.inferlytics.entity.Post;
import com.raremile.prd.inferlytics.utils.Util;

/**
 * @author mallikarjuna
 * @created 05-Aug-2014
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class MongoConnectorForService extends MongoConnector {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(MongoConnectorForService.class);

	/**
	 * @param entityId
	 * @param subproductId
	 * @return
	 */
	public static String getAllTraits(int entityId, int subproductId) {
		List<BasicDBObject> list = null;
		try {
			db = getMongoDB("Inferlytics");
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection table = db.getCollection("features");

			list = table.distinct("SynonymWord", new BasicDBObject("EntityId",
					entityId).append("SubProductId", subproductId));

		} catch (UnknownHostException e) {

			LOG.error(
					"UnknownHostException while performing operation in getProductDetails",
					e);
		}
		return list.toString();

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

	public static List<Post> getPostIdsForTrait(int entityId, int subProductId,
			String word, String productId) {
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
			matchQuery.put("ProductId", productId);
			matchQuery.put("SynonymWord", word);
			matchQuery.put("SentenceScore", new BasicDBObject("$ne", 0));
			matchByEntitySubProduct.put("$match", matchQuery);

			DBObject groupFields = new BasicDBObject("_id", new BasicDBObject(
					"PostId", "$PostId"));
			groupFields.put("Word", new BasicDBObject("$first", "$Word"));
			groupFields.put("SentenceNo", new BasicDBObject("$first",
					"$SentenceNo"));
			groupFields.put("SentenceScore", new BasicDBObject("$first",
					"$SentenceScore"));

			DBObject group = new BasicDBObject("$group", groupFields);

			DBObject sortOrder = new BasicDBObject("SentenceScore", -1);
			DBObject sortOp = new BasicDBObject("$sort", sortOrder);
			DBObject projection;

			projection = new BasicDBObject("$project", new BasicDBObject(
					"PostId", "$_id.PostId").append("Word", "$Word")
					.append("SentenceNo", "$SentenceNo")
					.append("SentenceScore", "$SentenceScore"));

			// .append("OverallScore","$OverallScore"));

			/*
			 * System.out.println(matchByEntitySubProduct + "," + group + "," +
			 * sortOp + "," + projection);
			 */
			LOG.info(matchByEntitySubProduct + "," + group + "," + sortOp + ","
					+ projection);
			AggregationOutput ids = collections.aggregate(
					matchByEntitySubProduct, group, sortOp, projection);

			Iterable<DBObject> results = ids.results();
			if (results != null) {

				for (DBObject dbObject : results) {
					Post post = new Post();
					post.setPostId((String) dbObject.get("PostId"));
					post.setWord((String) dbObject.get("Word"));
					if (null != dbObject.get("SentenceNo")) {
						post.setSentenceNo((int) dbObject.get("SentenceNo"));
					}
					try {
						if (Double.valueOf(dbObject.get("SentenceScore")
								.toString()) > 0) {

							post.setPositive(true);
						} else {

							post.setPositive(false);
						}
					} catch (Exception e) {
						if (Integer.valueOf(dbObject.get("SentenceScore")
								.toString()) > 0) {
							post.setPositive(true);
						} else {
							post.setPositive(false);
						}
					}
					posts.add(post);
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
			System.out.println(ex);
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}

		return posts;
	}

	public static List<Post> getPostsForProduct(String productId, int entityId,
			int subProductId) {
		List<Post> posts = new ArrayList<>();
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("posts");

			DBCursor cursor = collections.find(new BasicDBObject("_id",
					java.util.regex.Pattern.compile(productId)).append(
					"EntityId", entityId).append("subpoductId", subProductId));

			while (cursor.hasNext()) {
				DBObject object = cursor.next();
				Post post = new Post();
				post.setPostId(object.get("_id").toString());
				post.setContent(object.get("content").toString());

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

	public static String getProductDetails(String productName, int pageNo,
			String prodCat) {
		List<DBObject> returnObjects = new ArrayList<>();
		String database = FilePropertyManager.getProperty(
				ApplicationConstants.MONGO_PROPERTIES_FILE,
				ApplicationConstants.MONGO_DB);
		int skip = (pageNo - 1) * 15;
		final int PRODUCTS_LIMIT = 15;
		try {
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection table = db.getCollection("products");
			BasicDBObject productsfind = null;
			if (prodCat != null) {
				productsfind = new BasicDBObject("Product", productName)
				.append("ProductCategory", prodCat);
			} else {
				productsfind = new BasicDBObject("Product", productName);
			}

			DBCursor cursor = table.find(productsfind)
					.sort(new BasicDBObject("reviewCount", -1)).skip(skip)
					.limit(PRODUCTS_LIMIT);
			while (cursor.hasNext()) {
				DBObject objoutput = cursor.next();
				DBObject returnObject = new BasicDBObject();
				returnObject.put("id", objoutput.get("ProductId").toString());
				returnObject.put("name", objoutput.get("ProductName")
						.toString());
				returnObject.put("imgURL", objoutput.get("ProdImgUrl")
						.toString());
				try {
					returnObject.put("averageRating", Double.valueOf(objoutput
							.get("averageRating").toString()));
				} catch (Exception e) {
					returnObject.put("averageRating", 0);

				}
				if (objoutput.get("Price") != null) {
					returnObject.put("price",
							Double.valueOf(objoutput.get("Price").toString()));
				} else {
					returnObject.put("price", 0.0);
				}
				returnObject.put("traits", objoutput.get("traits"));
				try {
					returnObject.put("reviewCount",
							(int) objoutput.get("reviewCount"));
				} catch (Exception e) {
					returnObject.put("reviewCount", 0);
				}
				returnObject.put("sizes", objoutput.get("sizes"));
				returnObject.put("productScore", objoutput.get("ProductScore"));
				returnObject.put("description", objoutput.get("Description"));
				returnObject.put("colors", objoutput.get("color"));
				returnObject.put("tags", new ArrayList<>());
				returnObjects.add(returnObject);

			}
			BasicDBObject query = new BasicDBObject("Product", productName);
			if (prodCat != null) {
				query.append("ProductCategory", prodCat);
			}

			int productsCount = table.find(query).count();
			returnObjects.add(new BasicDBObject("ProductCount", productsCount));
		} catch (UnknownHostException e) {

			LOG.error(
					"UnknownHostException while performing operation in getProductDetails",
					e);
			System.out.println(e);
		}

		return new Gson().toJson(returnObjects);
	}

	public static void main(String[] args) {
		// getProductDetails("ActiveWear", 1);
		getAllTraits(417, 39);
	}

}
