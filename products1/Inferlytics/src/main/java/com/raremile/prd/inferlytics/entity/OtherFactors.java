package com.raremile.prd.inferlytics.entity;

import java.util.Map;
import java.util.Set;

public class OtherFactors {
	private static Map<String, Double> smileys;
	private static Set<String> stopwords;
	private static Set<String> nounStopwords;
	private static Map<String, Double> negation;
	private static Map<String, Double> sentenceModifiers;
	private static Map<String, Double> hashtags;
	private static StockPatterns stockPatterns;
	private static Map<String, String> synonyms;	

	private static Relations relation;

	public static Relations getRelation() {
		return relation;
	}

	public static void setRelation(Relations relation) {
		OtherFactors.relation = relation;
	}
	private OtherFactors() {

	}

	public static void setSmileys(Map<String, Double> map) {
		OtherFactors.smileys = map;
	}

	public static void setStopwords(Set<String> stopwords) {
		OtherFactors.stopwords = stopwords;
	}

	public static void setNounStopwords(Set<String> nounStopwords) {
		OtherFactors.nounStopwords = nounStopwords;
	}

	public static void setNegation(Map<String, Double> map) {
		OtherFactors.negation = map;
	}

	public static void setSentenceModifiers(
Map<String, Double> map) {
		OtherFactors.sentenceModifiers = map;
	}

	public static void setHashtags(Map<String, Double> map) {
		OtherFactors.hashtags = map;
	}

	public static Map<String, Double> getHashtags() {
		return hashtags;
	}

	public static Map<String, Double> getNegation() {
		return negation;
	}

	public static Set<String> getStopwords() {
		return stopwords;
	}


	/**
	 * @return the nounStopwords
	 */
	public static Set<String> getNounStopwords() {
		return nounStopwords;
	}

	public static Map<String, Double> getSmileys() {
		return smileys;
	}

	public static Map<String, Double> getSentenceModifiers() {
		return sentenceModifiers;
	}

	/**
	 * @param stockPatterns
	 *            the stockPatterns to set
	 */
	public static void setStockPatterns(StockPatterns stockPatterns) {
		OtherFactors.stockPatterns = stockPatterns;
	}

	/**
	 * @return the stockPatterns
	 */
	public static StockPatterns getStockPatterns() {
		return stockPatterns;
	}

	/**
	 * @return the synonyms
	 */
	public static Map<String, String> getSynonyms() {
		return synonyms;
	}

	/**
	 * @param synonyms the synonyms to set
	 */
	public static void setSynonyms(Map<String, String> synonyms) {
		OtherFactors.synonyms = synonyms;
	}


}






