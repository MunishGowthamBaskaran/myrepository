/**
 * * Copyright (c) 2013 RareMile Technologies. All rights reserved. No part of this document may be
 * reproduced or transmitted in any form or by any means, electronic or mechanical, whether now
 * known or later invented, for any purpose without the prior and express written consent.
 */
package com.raremile.prd.inferlytics.preprocessing;

import java.util.ArrayList;
import java.util.Map;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.entity.IdMap;
import com.raremile.prd.inferlytics.entity.IdWordList;
import com.raremile.prd.inferlytics.entity.LexiconMap;
import com.raremile.prd.inferlytics.entity.LexiconPattern;
import com.raremile.prd.inferlytics.entity.ModifierMap;
import com.raremile.prd.inferlytics.entity.OtherFactors;
import com.raremile.prd.inferlytics.entity.QueryLevelCache;

/**
 * @author Pratyusha
 * @created Apr 16, 2013 Instantiate and cache all the lexicons needed for
 *          calculating sentiment.
 */
public class CacheManager {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(CacheManager.class);

	public static void instantiateLexiconPattern() {
		if (null == LexiconPattern.lexiconPatternMap) {
			LexiconPattern.lexiconPatternMap = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getLexiconDAO().getWordPatternScore();
		}
	}

	public static void instantiateLexiconMap() {
		LexiconMap.unigrams = new ArrayList<String>();
		LexiconMap.unigramIndex = new ArrayList<ArrayList<ArrayList<Integer>>>();
		LexiconMap.posPattern = new ArrayList<String>();
		LexiconMap.score = new ArrayList<Double>();
		LexiconMap.multiwordPattern = new ArrayList<String>();
	}

	public static void instantiateModifierMap() {

		ModifierMap.unigrams = new ArrayList<String>();
		ModifierMap.ugramValuePointer = new ArrayList<ArrayList<ArrayList<Integer>>>();
		ModifierMap.score = new ArrayList<Double>();
		ModifierMap.modifierMultiWord = new ArrayList<String>();

	}

	public static void instantiateOtherFactors() {

		OtherFactors.setSmileys(DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getLexiconDAO().getSmileys());

		OtherFactors.setStopwords(DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getLexiconDAO().getStopword());
		OtherFactors.setNounStopwords(DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getLexiconDAO().getNounStopword());
		OtherFactors.setNegation(DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getLexiconDAO().getNegations());
		OtherFactors.setSentenceModifiers(DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getLexiconDAO().getSentenceModifiers());
		OtherFactors.setHashtags(DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getLexiconDAO().getHashTags());

		OtherFactors.setRelation(DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getLexiconDAO().getRelations());
	}

	public static void instantiateIds() {

		IdMap.setEntityIdMap(DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getLexiconDAO().getEntityIdMap());
		IdMap.setsubproductIdMap(DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getLexiconDAO().getSubProductIdMap());

	}

	public static IdWordList getDimensionMapForEntity(String product) {

		IdWordList idDimensionMap = null;

		idDimensionMap = DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getLexiconDAO().getDimensionWordIdByProduct(product);
		idDimensionMap.setProductName(product);
		return idDimensionMap;
	}

	public static void setWordSubDimForSubroduct(String subProduct) {
		QueryLevelCache.setWordSubDim(DAOFactory
				.getInstance(
						ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getSentimentDAO()
				.getWordSubDimensionForSubProduct(subProduct));
				
	}
	
	public static void storeSynonymsInRedis(){
		Map<String,String> synonyms = DAOFactory.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE).getSentimentDAO().getSynonyms();
		RedisCacheManager.storeSynonyms(synonyms);
	}

}
