package com.raremile.prd.inferlytics.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.exception.CriticalException;
import com.raremile.prd.inferlytics.preprocessing.RedisCacheManager;



/**
 * @author Pratyusha
 * @created Apr 4, 2013
 * 
 *          Lexicon Map as the name suggests this contains all lexicons
 *          including multiwords, Parts Of Speech(POS)patterns, corresponding
 *          scores
 * 
 * 
 */
public class LexiconMap {

	private static final Logger LOG = Logger.getLogger(LexiconMap.class);

	public static ArrayList<String> unigrams;
	/**
	 * 1)UnigramIndex a three level ArrayList First level every element is a map
	 * to {@link #unigrams}.{@link #unigrams} [0] � {@link #unigramIndex} [0].
	 * 2) At second level- Details of every Index: . (a) 0th element - Overall
	 * score, this value is a map to {@link #score}. (b) 1st element -
	 * multiwordScoreIndexList each element in this list is a map to
	 * {@link #score} (c) 2nd Element - multiwordIndexList each element in this
	 * list is a map to {@link #multiwordPattern}. (d) 4th to n elements has the
	 * same pattern as follows contails 3 elements : (i)1st element is either
	 * zero (for multiword POS ) or one( for Normal POS). (ii) 2nd element is
	 * POS index which again points to {@link #posPattern}. (iii) 2nd element is
	 * score index which again points to {@link #score}.
	 * 
	 * 
	 */
	public static ArrayList<ArrayList<ArrayList<Integer>>> unigramIndex;
	public static ArrayList<String> posPattern;
	public static ArrayList<Double> score;
	public static ArrayList<String> multiwordPattern;

	private static List<String> checkedSynonyms = null;



	private LexiconMap() {

	}

	private static boolean doesUnigramListExist() {
		if (null != unigrams && unigrams.size() > 0) {
			return true;
		}
		return false;
	}

	private static boolean doesUnigramExist(String unigram) {
		if (doesUnigramListExist() && unigrams.contains(unigram)) {
				return true;
		}
		return false;
	}

	public static double getUnigramScore(String unigram, String pattern,
			ArrayList<WordPatternScore> wpsListToInsert)
			throws CriticalException {
		double unigramScore = 0.0;
		try{
			LOG.info("Finding Score for " + unigram);
		if (doesUnigramExist(unigram)) {

			int unigramIndexNo = unigrams.indexOf(unigram);

			ArrayList<ArrayList<Integer>> patternScoreList = unigramIndex
					.get(unigramIndexNo);

			int counter = 0;
			for (ArrayList<Integer> patternScoremap : patternScoreList) {

				if (counter == 2) {
					counter++;
					continue;
					/**
					 * Deal with multi word patterns here how? /** multiword
					 * pattern is present.. Doing nothing as we already did this
					 * earlier.
					 */
				}
				if (counter > 2) {

					if (patternScoremap.get(0) != 0) {// Index 0 is a multiword
														// POS pattern
						String unigramPattern = getposPattern(patternScoremap
								.get(1));
						if (null != unigramPattern
								&& pattern.contains(unigramPattern)) {
								unigramScore = getScore(patternScoremap
									.get(2));
							/*
							 * String value = unigram + ":" + unigramPattern +
							 * ":" + unigramScore;
							 */
							// Util.addValueToMap(sentimentFactors, value,
							// "pospattern");
							WordPatternScore wps = new WordPatternScore();
							wps.setWord(unigram);
							wps.setPattern(pattern);
							wps.setScore(unigramScore);
							wpsListToInsert.add(wps);
								break;
						}
					}
				}
				counter++;
			}
			ArrayList<Integer> scoreList = patternScoreList.get(0);

			if (null != scoreList) {
					unigramScore = getScore(scoreList.get(0));
				// String value = unigram + ":" + pattern + ":" + unigramScore;
				// Util.addValueToMap(sentimentFactors, value, "pospattern");
				WordPatternScore wps = new WordPatternScore();
				wps.setWord(unigram);
				wps.setPattern(pattern);
				wps.setScore(unigramScore);
				wpsListToInsert.add(wps);

			}

		} else {
				if (null == checkedSynonyms
						|| !checkedSynonyms.contains(unigram)) {
			List<String> synonyms = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getLexiconDAO().getSynonymsByWords(unigram);
				if (null == checkedSynonyms) {
					checkedSynonyms = new ArrayList<String>();
				}
				checkedSynonyms.add(unigram);
			for (String synonym : synonyms) {
				if (!synonym.equalsIgnoreCase(unigram)) {
							unigramScore = getUnigramScore(synonym, pattern,
									wpsListToInsert);
							break;
				}
				}
				}
			}
		}
		catch(Exception ex){
			LOG.error(ex.getMessage(), ex);
			throw new CriticalException(ex);
		}
		checkedSynonyms = null;
		return unigramScore;
	}

	public static double getUnigramScoreFromRedis(String unigram,
			String pattern, ArrayList<WordPatternScore> wpsListToInsert)
			throws CriticalException {
		double unigramScore = 0.0;
		try {
			LOG.info("Finding Score for " + unigram);

			unigramScore = RedisCacheManager.getUnigramScore(unigram);
			LOG.debug("Score for "+unigram+" is "+unigramScore);
			if (unigramScore != 0.0) {
				WordPatternScore wps = new WordPatternScore();
				wps.setWord(unigram);
				wps.setPattern(pattern);
				wps.setScore(unigramScore);
				wpsListToInsert.add(wps);
			}

			else {
				if (null == checkedSynonyms
						|| !checkedSynonyms.contains(unigram)) {
					List<String> synonyms = DAOFactory
							.getInstance(
									ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
							.getLexiconDAO().getSynonymsByWords(unigram);
					if (null == checkedSynonyms) {
						checkedSynonyms = new ArrayList<String>();
					}
					checkedSynonyms.add(unigram);
					for (String synonym : synonyms) {
						if (!synonym.equalsIgnoreCase(unigram)) {
							unigramScore = getUnigramScoreFromRedis(synonym,
									pattern,
									wpsListToInsert);
							break;
						}
					}
				}
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			throw new CriticalException(ex);
		}
		checkedSynonyms = null;
		return unigramScore;
	}

	private static String getposPattern(int index) {
		if (index >= 0 && index < posPattern.size()) {
			return posPattern.get(index);
		}
		return null;
	}

	private static double getScore(int index) {
		if (index >= 0 && index < score.size()) {
			return score.get(index);
		}
		return 0.0;
	}

	public static HashMap<String, Double> getMultiwordScore(
			LinkedHashMap<String, List<String>> taggedTokens) {
		for (String token : taggedTokens.keySet()) {

		}
		return null;
	}
}
