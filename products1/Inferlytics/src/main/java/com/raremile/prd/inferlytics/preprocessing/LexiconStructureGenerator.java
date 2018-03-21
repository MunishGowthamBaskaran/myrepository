package com.raremile.prd.inferlytics.preprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.entity.LexiconMap;
import com.raremile.prd.inferlytics.entity.LexiconPattern;
import com.raremile.prd.inferlytics.entity.WordPatternScore;



/**
 * @author Pratyusha
 * @created May 31, 2013
 * 
 *          Responsible for creating Lexicon Structure (Lexicon Map) which will
 *          be used while calculating score of the words.
 * 
 */
public class LexiconStructureGenerator {
	private static Logger LOG = Logger
			.getLogger(LexiconStructureGenerator.class);

	public LexiconStructureGenerator() {
		try {
			Map<String, Double> lexicon =
				DAOFactory.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE).getLexiconDAO().getLexicon();
			CacheManager.instantiateLexiconPattern();
			CacheManager.instantiateLexiconMap();

			int lexiconCounter = 0;
			for (String word : lexicon.keySet()) {
				String[] unigrams = word.split("_");
				int multiwordindex = -1;
				if (unigrams.length > 1) {
					multiwordindex = getMultiWordIndex(word);
				}

				for (String unigram : unigrams) {
										
					ArrayList<ArrayList<Integer>> patternScoreList;
					if (LexiconMap.unigrams.contains(unigram)) {

						int unigramIndex = LexiconMap.unigrams.indexOf(unigram);
						patternScoreList = LexiconMap.unigramIndex
								.get(unigramIndex);
						/**
						 * Handle Original Score Here
						 */
						ArrayList<Integer> originalScoreList = patternScoreList
								.get(0);
						if (null == originalScoreList && unigrams.length == 1) {
							originalScoreList = new ArrayList<Integer>();
							/**
							 * If is Not Multiword
							 */
							originalScoreList.add(0,
									getScoreIndex(lexicon.get(word)));
							originalScoreList.trimToSize();
							patternScoreList.remove(0);
							patternScoreList.add(0, originalScoreList);
						}
						if (unigrams.length > 1) {
							/**
							 * This is a multi word pattern.
							 */
							ArrayList<Integer> multiwordIndexList = patternScoreList
									.get(2);
							ArrayList<Integer> multiwordScoreIndexList = patternScoreList
									.get(1);
							if (null != multiwordIndexList) {
								/**
								 * Patterns already exists just need to add at
								 * the end.
								 */
								if (!multiwordIndexList
										.contains(multiwordindex)) {
									multiwordIndexList.add(multiwordindex);
									multiwordScoreIndexList.add(
											multiwordIndexList.size() - 1,
											getScoreIndex(lexicon.get(word)));
								}
							} else {
								/**
								 * Patterns doesn't exist need to create
								 */
								multiwordIndexList = new ArrayList<Integer>();
								multiwordScoreIndexList = new ArrayList<Integer>();
								multiwordIndexList.add(multiwordindex);
								multiwordScoreIndexList.add(
										multiwordIndexList.size() - 1,
										getScoreIndex(lexicon.get(word)));
							}
							patternScoreList.remove(1);
							multiwordScoreIndexList.trimToSize();
							patternScoreList.add(1, multiwordScoreIndexList);
							multiwordIndexList.trimToSize();
							patternScoreList.remove(2);
							patternScoreList.add(2, multiwordIndexList);

						}

						/**
						 * Check for POS Patterns from here.. Assumption: This
						 * should not come here. As if the word already exists
						 * while adding first time only we are adding all POS
						 * patterns and scores.
						 */

						List<WordPatternScore> wpsList;
						if (multiwordindex != -1) {
							wpsList = LexiconPattern.lexiconPatternMap
									.get(word);
						} else {
							wpsList = LexiconPattern.lexiconPatternMap
									.get(unigram);
						}
						if (null != wpsList) {
							int indexCounter = 3;
							for (WordPatternScore wps : wpsList) {
								ArrayList<Integer> patternIndex = new ArrayList<Integer>();
								/**
								 * If multiword POS add first value as 0.. else
								 * add First value as 1.
								 * 
								 */
								if (multiwordindex != -1) {
									patternIndex.add(0);
								} else {
									patternIndex.add(1);
								}
								patternIndex.add(getposPatternIndex(wps
										.getPattern()));
								patternIndex.add(getScoreIndex(wps.getScore()));
								patternIndex.trimToSize();
								patternScoreList
										.add(indexCounter, patternIndex);
								indexCounter++;
							}
						}

						/**
						 * Finally Add Unigram to list and its corresponding
						 * index
						 */
						LexiconMap.unigramIndex.remove(unigramIndex);
						LexiconMap.unigramIndex.add(unigramIndex,
								patternScoreList);
					} else {
						patternScoreList = new ArrayList<ArrayList<Integer>>();
						/**
						 * Handle Original Score Here
						 */
						ArrayList<Integer> scoreList = new ArrayList<Integer>();
						if (unigrams.length == 1) {
							/**
							 * If is Not Multiword
							 */
							scoreList.add(0, getScoreIndex(lexicon.get(word)));
							scoreList.trimToSize();
							patternScoreList.add(0, scoreList);
							patternScoreList.add(1, null);
							patternScoreList.add(2, null);
						} else {
							/**
							 * This is a multiword pattern.
							 */
							ArrayList<Integer> multiwordIndex = new ArrayList<Integer>();
							multiwordIndex.add(multiwordindex);
							multiwordIndex.trimToSize();
							scoreList.add(getScoreIndex(lexicon.get(word)));
							scoreList.trimToSize();
							patternScoreList.add(0, null);
							patternScoreList.add(1, scoreList);
							patternScoreList.add(2, multiwordIndex);
						}
						/**
						 * Handle POS Pattern..
						 */
						List<WordPatternScore> wpsList;
						if (multiwordindex != -1) {
							wpsList = LexiconPattern.lexiconPatternMap
									.get(word);
						} else {
							wpsList = LexiconPattern.lexiconPatternMap
									.get(unigram);
						}
						if (null != wpsList) {
							int indexCounter = 3;
							for (WordPatternScore wps : wpsList) {
								ArrayList<Integer> patternIndex = new ArrayList<Integer>();
								/**
								 * If multiword POS add first value as 0.. else
								 * add First value as 1.
								 * 
								 */
								if (multiwordindex != -1) {
									patternIndex.add(0);

								} else {
									patternIndex.add(1);
								}
								patternIndex.add(getposPatternIndex(wps
										.getPattern()));
								patternIndex.add(getScoreIndex(wps.getScore()));
								patternIndex.trimToSize();
								patternScoreList
										.add(indexCounter, patternIndex);
								indexCounter++;
							}
						}
						/**
						 * Finally Add Unigram to list and its corresponding
						 * index
						 */
						patternScoreList.trimToSize();
						LexiconMap.unigrams.add(unigram);
						LexiconMap.unigramIndex.add(
								LexiconMap.unigrams.size() - 1,
								patternScoreList);

					}

				}
				lexiconCounter++;
			}

			// flush existing cache before inserting new ones.
			RedisCacheManager.flushCache();

			// store score in redis
			RedisCacheManager
					.storeDoubleList("wordscorelist", LexiconMap.score);
			// store multiwordPattern in redis
			RedisCacheManager.storeStringList("multiwordpatternlist",
					LexiconMap.multiwordPattern);
			// store posPattern in redis
			RedisCacheManager.storeStringList("pospatternlist",
					LexiconMap.posPattern);

			// store lexicon unigram value pointer index in redis
			RedisCacheManager.storeUnigramIndex();

			LOG.info("lexiconCounter = " + lexiconCounter);
		}

		catch (Exception e) {
			LOG.error("", e);
		}

	}

	private static int getMultiWordIndex(String word) {
		int unigramPatternindex = -1;
		if (LexiconMap.multiwordPattern.contains(word)) {
			unigramPatternindex = LexiconMap.multiwordPattern.indexOf(word);
		} else {
			LexiconMap.multiwordPattern.add(word);
			unigramPatternindex = LexiconMap.multiwordPattern.size() - 1;
		}
		return unigramPatternindex;
	}

	private static int getScoreIndex(double score) {
		int scoreIndex = -1;
		if (LexiconMap.score.contains(score)) {
			scoreIndex = LexiconMap.score.indexOf(score);
		} else {
			LexiconMap.score.add(score);
			scoreIndex = LexiconMap.score.size() - 1;
		}
		return scoreIndex;
	}

	private static int getposPatternIndex(String pattern) {
		int patternIndex = -1;
		if (LexiconMap.posPattern.contains(pattern)) {
			patternIndex = LexiconMap.posPattern.indexOf(pattern);
		} else {
			LexiconMap.posPattern.add(pattern);
			patternIndex = LexiconMap.posPattern.size() - 1;
		}
		return patternIndex;
	}

	public static void main(String[] s) {
		new LexiconStructureGenerator();
		// LOG.info(LexiconMap.unigrams);
		// LOG.info(LexiconMap.unigramIndex);
		// LOG.info(LexiconMap.posPattern);
		// LOG.info(LexiconMap.score);
		// LOG.info(LexiconMap.multiwordPattern);
		System.out.println(LexiconMap.unigramIndex.get(LexiconMap.unigrams
				.indexOf("better")));
		System.out.println(LexiconMap.getUnigramScore("better",
				"CC VB JJ NN IN",
				new ArrayList<WordPatternScore>()));
		System.out.println(LexiconMap.getUnigramScoreFromRedis("nike",
				"CC VB JJ NN IN",
				new ArrayList<WordPatternScore>()));

	}
}
