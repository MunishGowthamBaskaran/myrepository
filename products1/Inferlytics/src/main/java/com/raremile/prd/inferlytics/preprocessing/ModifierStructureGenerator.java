package com.raremile.prd.inferlytics.preprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.entity.ModifierMap;


/**
 * @author Pratyusha
 * @created May 31, 2013
 * 
 *          Responsible for creating Lexicon Structure (Modifier Map) which will
 *          be used while calculating score of the words.
 * 
 */
public class ModifierStructureGenerator {

	private static Logger LOG = Logger
			.getLogger(ModifierStructureGenerator.class);

	public ModifierStructureGenerator() {
		try {
			LOG.info("Entered ModifierStructureGenerator");
			Map<String, Double> modifiers = // Operator.getInstance().getModifiers();
				DAOFactory.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE).getLexiconDAO().getModifiers();
			CacheManager.instantiateModifierMap();

			for (String word : modifiers.keySet()) {
				String[] unigrams = word.split("_");
				int multiwordindex = -1;
				if (unigrams.length > 1) {
					multiwordindex = getModifierMultiWordIndex(word);
				}
				for (String unigram : unigrams) {
					ArrayList<ArrayList<Integer>> patternScoreList;
					if (ModifierMap.unigrams.contains(unigram)) {
						int unigramIndex = ModifierMap.unigrams
								.indexOf(unigram);
						patternScoreList = ModifierMap.ugramValuePointer
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
									getScoreIndex(modifiers.get(word)));
							originalScoreList.trimToSize();
							patternScoreList.remove(0);
							patternScoreList.add(0, originalScoreList);
						}
						if (multiwordindex != -1) {
							/**
							 * This is a multi word pattern.
							 */
							/**
							 * This is a multiword pattern.
							 */
							ArrayList<Integer> patternIndex = new ArrayList<Integer>();
							patternIndex.add(getModifierMultiWordIndex(word));
							patternIndex
									.add(getScoreIndex(modifiers.get(word)));
							patternIndex.trimToSize();
							patternScoreList.add(patternIndex);

						}
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
							scoreList
									.add(0, getScoreIndex(modifiers.get(word)));
							scoreList.trimToSize();
							patternScoreList.add(0, scoreList);

						} else {
							/**
							 * This is a multiword pattern.
							 */
							ArrayList<Integer> patternIndex = new ArrayList<Integer>();
							patternIndex.add(getModifierMultiWordIndex(word));
							patternIndex
									.add(getScoreIndex(modifiers.get(word)));
							patternIndex.trimToSize();
							patternScoreList.add(0, null);
							patternScoreList.add(1, patternIndex);
						}

						patternScoreList.trimToSize();
						ModifierMap.unigrams.add(unigram);
						ModifierMap.ugramValuePointer.add(
								ModifierMap.unigrams.size() - 1,
								patternScoreList);
					}
				}

			}
			
			// LOG.info(ModifierMap.unigrams);
			// LOG.info(ModifierMap.ugramValuePointer);
			// LOG.info(ModifierMap.score);
			// LOG.info(ModifierMap.modifierMultiWord);
		} catch (Exception ex) {
			LOG.error("", ex);
		}
	}


	public static int getModifierMultiWordIndex(String word) {
		int unigramPatternindex = 0;
		if (ModifierMap.modifierMultiWord.contains(word)) {
			unigramPatternindex = ModifierMap.modifierMultiWord.indexOf(word);
		} else {
			ModifierMap.modifierMultiWord.add(word);
			unigramPatternindex = ModifierMap.modifierMultiWord.size() - 1;
		}
		return unigramPatternindex;
	}

	private static int getScoreIndex(double score) {
		int scoreIndex = -1;
		if (ModifierMap.score.contains(score)) {
			scoreIndex = ModifierMap.score.indexOf(score);
		} else {
			ModifierMap.score.add(score);
			scoreIndex = ModifierMap.score.size() - 1;
		}
		return scoreIndex;
	}

	public static void main(String[] s) {
		List<String> words = new ArrayList<String>();
		words.add("absolutely");
		words.add("very");
		words.add("good");
		words.add("as_good_as");
		words.add("good_to_be");

		new ModifierStructureGenerator();

	}

}
