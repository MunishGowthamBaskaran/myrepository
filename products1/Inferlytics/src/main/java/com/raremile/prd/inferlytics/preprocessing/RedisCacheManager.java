/**
 *  * Copyright (c) 2013 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.preprocessing;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.RedisConnector;
import com.raremile.prd.inferlytics.entity.LexiconMap;
import com.raremile.prd.inferlytics.entity.Product;

/**
 * @author pratyusha
 * @created 30-Jul-2013
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class RedisCacheManager {

	private static String getMultiwordsOfUnigramLua = null;
	private static String getMultiwordScoreOfUnigramLua = null;
	private static String getUnigramScoreByPatternLua = null;
	private static String getUnigramScoreWoPattern = null;
	private static String getProductDetails = null;
	private static ClassLoader loader = RedisCacheManager.class
			.getClassLoader();

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(RedisCacheManager.class);

	public static void storeStringList(String name, List<String> list) {
		Jedis jcon = null;
		try {
			jcon = RedisConnector.getConnection();
			String[] strings = list.toArray(new String[list.size()]);
			jcon.rpush(name, strings);

		} finally {
			RedisConnector.returnResource(jcon);
			System.out.println("\nDisconnected");
		}
	}

	public static void storeDoubleList(String name, List<Double> list) {
		Jedis jcon = null;
		String[] values = new String[list.size()];
		try {
			jcon = RedisConnector.getConnection();
			for (int i = 0; i < list.size(); i++) {
				values[i] = list.get(i).toString();
			}
			jcon.rpush(name, values);
		} finally {
			RedisConnector.returnResource(jcon);
			System.out.println("\nDisconnected");
		}
	}

	public static void storeUnigramIndex() {
		Jedis jedis = null;
		try {
			jedis = RedisConnector.getConnection();

			int unigramIndexsize = LexiconMap.unigramIndex.size();
			for (int indexCounter = 0; indexCounter < unigramIndexsize; indexCounter++) {
				ArrayList<ArrayList<Integer>> index = LexiconMap.unigramIndex
						.get(indexCounter);
				String key = LexiconMap.unigrams.get(indexCounter);
				int eachIndexSize = index.size();

				for (int eachIndexCounter = 0; eachIndexCounter < eachIndexSize; eachIndexCounter++) {

					// overall score of unigram
					if (eachIndexCounter == 0 && index.get(0) != null) {
						jedis.set(key + ":unigramscore", index.get(0).get(0)
								.toString());
					} else if (eachIndexCounter == 2 && index.get(2) != null) {
						jedis.rpush(key + ":multiwordIndex",
								getStringFromList(index.get(2)).split(","));
					}

					// multiwordscore of unigram
					else if (eachIndexCounter == 1 && index.get(1) != null) {
						jedis.rpush(key + ":multiwordScore",
								getStringFromList(index.get(1)).split(","));
					}

					// POS Pattern index

					else if (eachIndexCounter >= 3) {
						jedis.rpush(key + ":posPattern" + eachIndexCounter,
								getStringFromList(index.get(eachIndexCounter))
										.split(","));
					}
				}

			}
		} catch (Exception ex) {
			LOG.error("Exception while storing unigram index in redis " + ex);
		} finally {

			RedisConnector.returnResource(jedis);
			LOG.info("\nDisconnected after storing index");
		}
	}

	private static String getStringFromList(List<Integer> list) {
		StringBuilder sb = new StringBuilder(list.toString());
		sb.deleteCharAt(0);
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();

	}

	@SuppressWarnings("unchecked")
	public static List<String> getMultiwordsOfUnigram(String unigram) {
		Jedis jedis = null;
		List<String> multiwords = null;
		try {
			if (null == getMultiwordsOfUnigramLua) {
				getMultiwordsOfUnigramLua = com.raremile.prd.inferlytics.utils.Util
						.readFile(new File(
								URLDecoder
										.decode(loader
												.getResource(
														ApplicationConstants.LUA_GETMULTIWORDSOFUNIGRAM)
												.getPath(), "UTF-8")));
			}
			jedis = RedisConnector.getConnection();
			multiwords = (List<String>) jedis.eval(getMultiwordsOfUnigramLua,
					1, unigram);

		} catch (JedisConnectionException jce) {
			LOG.error("Error while connecting to Jedis " + jce);
		} catch (Exception e) {
			LOG.error("Error while retrieving multiwords of unigram " + unigram
					+ e);
		} finally {
			RedisConnector.returnResource(jedis);
			// LOG.info("\nDisconnected after getting multiwords of a unigram");
		}
		return multiwords;
	}

	public static double getMultiwordScorebyWordIndex(String word, int index) {
		Jedis jedis = null;

		double score = 0.0;
		try {
			if (null == getMultiwordScoreOfUnigramLua) {
				getMultiwordScoreOfUnigramLua = com.raremile.prd.inferlytics.utils.Util
						.readFile(new File(
								URLDecoder
										.decode(loader
												.getResource(
														ApplicationConstants.LUA_GETMULTIWORDSCOREBYPOSITION)
												.getPath(), "UTF-8")));
			}
			jedis = RedisConnector.getConnection();

			String stringScore = (String) jedis.eval(
					getMultiwordScoreOfUnigramLua, 1, word, "" + index);

			score = Double.parseDouble(stringScore);
		} catch (JedisConnectionException jce) {
			LOG.error("Error while connecting to Jedis " + jce);
		} catch (Exception e) {
			LOG.error("Error while retrieving multiword score of unigram "
					+ word + e);
		} finally {
			RedisConnector.returnResource(jedis);
			LOG.info("\nDisconnected after getting multiword score of a unigram");
		}
		return score;
	}

	public static double getUnigramScore(String unigram, String pattern) {
		Jedis jedis = null;

		double score = 0.0;
		try {
			if (null == getUnigramScoreByPatternLua) {
				getUnigramScoreByPatternLua = com.raremile.prd.inferlytics.utils.Util
						.readFile(new File(
								URLDecoder
										.decode(loader
												.getResource(
														ApplicationConstants.LUA_GETUNIGRAMSCORE)
												.getPath(), "UTF-8")));
			}
			jedis = RedisConnector.getConnection();

			String stringScore = (String) jedis.eval(
					getUnigramScoreByPatternLua, 0, unigram, pattern);

			score = Double.parseDouble(stringScore);
		} catch (JedisDataException jde) {
			LOG.info("Jedis exception while retrieving  score of unigram by pattern"
					+ jde);
		} catch (JedisConnectionException jce) {
			LOG.error("Error while connecting to Redis " + jce);
		} catch (Exception e) {
			LOG.error("Error while retrieving  score of unigram by pattern "
					+ unigram + e);
		} finally {
			RedisConnector.returnResource(jedis);
			// LOG.info("\nDisconnected after getting score of a unigram");
		}
		return score;
	}

	public static double getUnigramScore(String unigram) {
		Jedis jedis = null;

		double score = 0.0;
		try {
			if (null == getUnigramScoreWoPattern) {
				getUnigramScoreWoPattern = com.raremile.prd.inferlytics.utils.Util
						.readFile(new File(
								URLDecoder
										.decode(loader
												.getResource(
														ApplicationConstants.LUA_GETUNIGRAMSCOREWOPATTERN)
												.getPath(), "UTF-8")));
			}
			jedis = RedisConnector.getConnection();

			String stringScore = (String) jedis.eval(getUnigramScoreWoPattern,
					0, unigram);

			score = Double.parseDouble(stringScore);
		} catch (JedisDataException jde) {
			LOG.info("Jedis exception while retrieving  score of unigram "
					+ jde);
		} catch (JedisConnectionException jce) {
			LOG.error("Error while connecting to Redis " + jce);
		} catch (Exception e) {
			LOG.error("Error while retrieving  score of unigram by pattern "
					+ unigram + e);
		} finally {
			RedisConnector.returnResource(jedis);
			// LOG.info("\nDisconnected after getting score of a unigram");
		}
		return score;
	}

	public static void flushCache() {

		Jedis jedis = null;
		try {
			jedis = RedisConnector.getConnection();
			jedis.select(0);
			jedis.flushDB();

		} finally {
			RedisConnector.returnResource(jedis);
			System.out.println("\nDisconnected");
		}

	}

	public static Product getProductById(String id, String entity) {
		Product product = null;
		Jedis jedis = null;
		try {
			jedis = RedisConnector.getConnection();
			jedis.select(1);

			if (null == getProductDetails) {
				getProductDetails = com.raremile.prd.inferlytics.utils.Util
						.readFile(new File(
								URLDecoder
										.decode(loader
												.getResource(
														ApplicationConstants.LUA_GETPRODUCTDETAILS)
												.getPath(), "UTF-8")));
			}

			@SuppressWarnings("unchecked")
			List<String> productDetails = (List<String>) jedis.eval(
					getProductDetails, 0, entity + ":" + id);

			if (null != productDetails && productDetails.size() >= 3) {
				product = new Product();
				product.setProductName(productDetails.get(0));
				product.setProductId(id);
				product.setProductUrl(productDetails.get(1));
				product.setImageUrl(productDetails.get(2));
			}

		} catch (JedisDataException jde) {
			LOG.info("Jedis exception while retrieving  product " + jde);
			System.out.println("Error1");
		} catch (JedisConnectionException jce) {
			LOG.error("Error while connecting to Redis " + jce);
			System.out.println("Error2");
		} catch (Exception e) {
			LOG.error("Jedis common exception while retrieving  product with id"
					+ id + e);
			System.out.println("Error3");
		} finally {
			RedisConnector.returnResource(jedis);
			// LOG.info("\nDisconnected after getting score of a unigram");
		}

		return product;
	}

	public static void main(String[] args) {

		//getProductById("B003AT02SI", "amazoncamera");
		
	//CacheManager.storeSynonymsInRedis();	
System.out.println(getSynonymWord("orient"));
		/*
		 * JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost",
		 * 6379); // Connecting to Redis on localhost Jedis jedis =
		 * pool.getResource(); // adding a new key jedis.set("key", "value"); //
		 * getting the key value System.out.println(jedis.get("key"));
		 * pool.destroy();
		 */
		// new LexiconStructureGenerator();
		// getMultiwordsOfUnigram("best");
		// System.out.println(getMultiwordScorebyWordIndex("best", 2));

		// System.out.println(getUnigramScore("quot"));
	}

	public static void storeSynonyms(Map<String, String> synonyms) {
		Jedis jcon = null;
		try {
			jcon = RedisConnector.getConnection();
			for (Entry<String, String> synonym : synonyms.entrySet()) {
				jcon.set("synonym:" + synonym.getKey(), synonym.getValue());
			}

		} finally {
			RedisConnector.returnResource(jcon);
			System.out.println("\nDisconnected");
		}

	}

	public static String getSynonymWord(String word) {
		String synonym = null;
		Jedis jcon = null;
		try {
			jcon = RedisConnector.getConnection();
			synonym = jcon.get("synonym:"+word);

		} finally {
			RedisConnector.returnResource(jcon);
			
		}
		return synonym;
	}
}
