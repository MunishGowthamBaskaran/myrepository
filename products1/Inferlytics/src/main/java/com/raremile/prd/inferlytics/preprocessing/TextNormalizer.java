/***************************************
 * This reformates the tweets
 * eliminates multiple charactes to one word greaaaaaaat (TO BE IMPLEMENTED)
 * eliminates references @MaryGer
 * eliminates urls inside tweet
 ***************************************/
package com.raremile.prd.inferlytics.preprocessing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.raremile.prd.inferlytics.entity.LexiconMap;
import com.raremile.prd.inferlytics.entity.ModifierMap;
import com.raremile.prd.inferlytics.entity.WordPatternScore;

/**
 * 
 * @author Pratyusha
 * 
 *         Works like a Util class while analysis
 */
public class TextNormalizer {
	private static Logger LOG = Logger.getLogger(TextNormalizer.class);

	public static LinkedList<String> removeStopWord(Set<String> stopWords,
			LinkedList<String> arraylist) {

		LinkedList<String> newList = new LinkedList<String>();
		int i = 0;
		while (i < arraylist.size()) {
			if (!stopWords.contains(arraylist.get(i).toLowerCase())) {
				newList.add(arraylist.get(i));
			}
			i++;
		}

		return newList;
	}

	public static List<LinkedList<String>> separateSmiley(Set<String> smileys,
			LinkedList<String> arraylist) {

		List<LinkedList<String>> list = new LinkedList<LinkedList<String>>();
		LinkedList<String> NewList = new LinkedList<String>();
		LinkedList<String> separatedSmileys = new LinkedList<String>();
		int i = 0;
		while (i < arraylist.size()) {
			if (!smileys.contains(arraylist.get(i))) {
				NewList.add(arraylist.get(i));
			} else {
				separatedSmileys.add(arraylist.get(i));
			}
			i++;
		}

		list.add(0, NewList);
		list.add(1, separatedSmileys);
		return list;
	}

	/**
	 * @param tokens
	 * @param taggedtokens
	 * @param wpsListToInsert
	 * @param lemmaWordMap 
	 * @return
	 * 
	 *         Takes cares of finding scores for multi-word modifiers and
	 *         multi-word lexicons and keeps those details in a Map ans returns
	 *         the same.
	 * 
	 *         If this method finds such words tracks those details in
	 *         wpsListToInsert which is used for inserting into feedback table.
	 * 
	 */
	public static HashMap<String, Double> processTextBeforeAnalyse(
			LinkedList<String> tokens,
			HashMap<String, List<String>> taggedtokens,
			ArrayList<WordPatternScore> wpsListToInsert, Map<String, String> lemmaWordMap) {
		LOG.debug("Entered processTextbeforeAnalyse ");
		HashMap<String, Double> patternWordScoreMap = new HashMap<String, Double>();

		Double modifierScore = 0.0;

		for (int tokenCounter = 0; tokenCounter < tokens.size(); tokenCounter++) {
			/**
			 * Send to modifier
			 */
			String token = tokens.get(tokenCounter).toLowerCase();
			if (ModifierMap.unigrams.contains(token)) {
				ArrayList<ArrayList<Integer>> patternScoreList = ModifierMap.ugramValuePointer
						.get(ModifierMap.unigrams.indexOf(token));

				HashMap<LinkedList<String>, Double> result = getModifierMultiwordScore(
						token, patternScoreList, tokens,
						ModifierMap.modifierMultiWord,lemmaWordMap);
				if (result != null) {
					for (LinkedList<String> arrayList : result.keySet()) {
						if (result.get(arrayList) != -1.0) {
							tokens = arrayList;

							modifierScore = result.get(arrayList);
							// String value = tokens.get(tokenCounter);
							patternWordScoreMap.put(tokens.get(tokenCounter),
									0.0);
							// Util.addValueToMap(sentimentFactors, value,
							// "modifier");
							break;
						}
					}

					continue;
				}
			}

			/**
			 * Send to find multiple words patterns
			 */
			Double wordScore = 0.0;

			List<String> multiwords = RedisCacheManager
					.getMultiwordsOfUnigram(token);
			if (null != multiwords) {
				HashMap<LinkedList<String>, Double> result = getMultiwordScore(
						token, multiwords, tokens,lemmaWordMap);
				if (result != null) {
					for (LinkedList<String> arrayList : result.keySet()) {
						tokens = arrayList;

						wordScore = result.get(arrayList);
						if (wordScore != -1.0) {
							if (modifierScore != 0.0) {
								wordScore = wordScore * modifierScore;
							}
							patternWordScoreMap.put(tokens.get(tokenCounter),
									wordScore);

						}
						modifierScore = 0.0;

						break;
					}

				}
			}

			/*
			 * if (LexiconMap.unigrams.contains(token)) {
			 * ArrayList<ArrayList<Integer>> patternScoreList =
			 * LexiconMap.unigramIndex .get(LexiconMap.unigrams.indexOf(token));
			 * HashMap<LinkedList<String>, Double> result = getMultiwordScore(
			 * token, patternScoreList, tokens, LexiconMap.multiwordPattern); if
			 * (result != null) { for (LinkedList<String> arrayList :
			 * result.keySet()) { tokens = arrayList;
			 * 
			 * wordScore = result.get(arrayList); if (wordScore != -1.0) { if
			 * (modifierScore != 0.0) { wordScore = wordScore * modifierScore; }
			 * patternWordScoreMap.put(tokens.get(tokenCounter), wordScore); //
			 * Util.addValueToMap(sentimentFactors,tokens.get(tokenCounter), //
			 * "word"); } modifierScore = 0.0;
			 * 
			 * break; }
			 * 
			 * }
			 * 
			 * }
			 */

			if (modifierScore != 0.0) {
				String pattern = taggedtokens.get(token).get(0);
				wordScore = LexiconMap.getUnigramScoreFromRedis(token, pattern,
						wpsListToInsert);
				if (wordScore != 0.0) {
					taggedtokens.get(token).remove(0);
					wordScore = wordScore * modifierScore;
					patternWordScoreMap.put(tokens.get(tokenCounter - 1),
							wordScore);
					patternWordScoreMap.put(token, wordScore);
					// Util.addValueToMap(sentimentFactors, token, "word");
				}

			}
			modifierScore = 0.0;
		}
		LOG.debug("Leaving processTextBeforeAnalyse");
		return patternWordScoreMap;
	}

	public static HashMap<LinkedList<String>, Double> getMultiwordScore(
			String unigram, ArrayList<ArrayList<Integer>> patternScoreList,
			LinkedList<String> tokens, ArrayList<String> multiwordPattern,Map<String,String> lemmaMap) {
		HashMap<LinkedList<String>, Double> result = null;// new
															// HashMap<LinkedList<String>,
															// Double>();
		ArrayList<Integer> patternScoremap = patternScoreList.get(2);
		/**
		 * multiword pattern is present.. Getting its score only. TODO check
		 * score for POS pattern of multiword pattern also..
		 */

		double resultScore = -1;
		if (null != patternScoremap) {
			int multiwordPosition = 0;

			for (Integer integer : patternScoremap) {
				LinkedList<String> tempTokens = new LinkedList<String>();
				tempTokens.addAll(tokens);
				String multiword = multiwordPattern.get(integer);
				List<String> multiwordAsList = Arrays.asList(multiword
						.split("_"));
				int unigramPosition = -1;

				if (multiwordAsList.contains(unigram)) {
					int questionMarkIndex = -1;

					if (multiwordAsList.contains("?")) {
						questionMarkIndex = multiwordAsList.indexOf("?");
					}
					unigramPosition = multiwordAsList.indexOf(unigram);
					int beforeCount = unigramPosition;
					int afterCount = multiwordAsList.size()
							- (unigramPosition + 1);
					int actualPosition = tokens.indexOf(unigram);
					int start = actualPosition - beforeCount;
					int end = actualPosition + afterCount;

					String multiTokens = "";
					if ((end < tempTokens.size()) && start >= 0) {
						for (int i = start; i <= end; i++) {
							if (questionMarkIndex != -1
									&& i == start + (questionMarkIndex)) {
								multiTokens += "?";
								tempTokens.remove(start);

							} else {
								multiTokens += tempTokens.remove(start);
							}

							if (i != end) {
								multiTokens += "_";
							}
						}
					}
					if (multiTokens.equalsIgnoreCase(multiword)) {
						tempTokens.add(start, multiTokens);
						resultScore = LexiconMap.score.get(patternScoreList
								.get(1).get(multiwordPosition));
						tokens.clear();
						tokens.addAll(tempTokens);
						break;
					}

				}
				multiwordPosition++;
			}
		}
		if (resultScore != -1) {
			result = new HashMap<LinkedList<String>, Double>();
			result.put(tokens, resultScore);
		}
		return result;

	}

	public static HashMap<LinkedList<String>, Double> getMultiwordScore(
			String unigram, List<String> multiwords, LinkedList<String> tokens, Map<String,String> lemmaWordMap) {
		HashMap<LinkedList<String>, Double> result = null;
		/**
		 * multiword pattern is present.. Getting its score only. TODO check
		 * score for POS pattern of multiword pattern also..
		 */

		double resultScore = -1;

		int multiwordPosition = 0;

		for (String multiword : multiwords) {
			LinkedList<String> tempTokens = new LinkedList<String>();
			tempTokens.addAll(tokens);

			List<String> multiwordAsList = Arrays.asList(multiword.split("_"));
			int unigramPosition = -1;

			if (multiwordAsList.contains(unigram)) {
				int questionMarkIndex = -1;

				if (multiwordAsList.contains("?")) {
					questionMarkIndex = multiwordAsList.indexOf("?");
				}
				unigramPosition = multiwordAsList.indexOf(unigram);
				int beforeCount = unigramPosition;
				int afterCount = multiwordAsList.size() - (unigramPosition + 1);
				int actualPosition = tokens.indexOf(unigram);
				int start = actualPosition - beforeCount;
				int end = actualPosition + afterCount;
				String lemmaLessMultokens ="";
				String multiTokens = "";
				if ((end < tempTokens.size()) && start >= 0) {
					for (int i = start; i <= end; i++) {
						if (questionMarkIndex != -1
								&& i == start + (questionMarkIndex)) {
							multiTokens += "?";
							lemmaLessMultokens += lemmaWordMap.get(tempTokens.get(start));
							tempTokens.remove(start);

						} else {
							lemmaLessMultokens += lemmaWordMap.get(tempTokens.get(start));
							multiTokens += tempTokens.remove(start);
						}

						if (i != end) {
							multiTokens += "_";
							lemmaLessMultokens += "_";
						}
					}
				}
				if (multiTokens.equalsIgnoreCase(multiword)) {
					tempTokens.add(start, multiTokens);
					resultScore = RedisCacheManager
							.getMultiwordScorebyWordIndex(unigram,
									multiwordPosition);
					lemmaWordMap.put(multiTokens, lemmaLessMultokens.replace("_", " "));
					tokens.clear();
					tokens.addAll(tempTokens);
					break;
				}

			}
			multiwordPosition++;
		}

		if (resultScore != -1) {
			result = new HashMap<LinkedList<String>, Double>();
			result.put(tokens, resultScore);
		}
		return result;

	}

	public static HashMap<LinkedList<String>, Double> getModifierMultiwordScore(
			String unigram, ArrayList<ArrayList<Integer>> patternScoreList,
			LinkedList<String> tokens, ArrayList<String> multiwordPattern, Map<String, String> lemmaWordMap) {
		HashMap<LinkedList<String>, Double> result = null;

		/**
		 * multiword pattern is present.. Getting its score only. TODO check
		 * score for POS pattern of multiword pattern also..
		 */

		int counter = 0;
		double resultScore = -1;
		for (ArrayList<Integer> list : patternScoreList) {
			if (counter != 0) {
				LinkedList<String> tempTokens = new LinkedList<String>();
				tempTokens.addAll(tokens);
				String multiword = multiwordPattern.get(list.get(0));
				List<String> multiwordAsList = Arrays.asList(multiword
						.split("_"));
				int unigramPosition = -1;
				if (multiwordAsList.contains(unigram)) {
					int questionMarkIndex = -1;

					if (multiwordAsList.contains("?")) {
						questionMarkIndex = multiwordAsList.indexOf("?");
					}
					unigramPosition = multiwordAsList.indexOf(unigram);
					int beforeCount = unigramPosition;
					int afterCount = multiwordAsList.size()
							- (unigramPosition + 1);
					int actualPosition = tokens.indexOf(unigram);
					int start = actualPosition - beforeCount;
					int end = actualPosition + afterCount;
					String lemmaLessMultokens ="";
					String multiTokens = "";
					if ((end < tempTokens.size()) && start >= 0) {
						for (int i = start; i <= end; i++) {

							// multiTokens += tempTokens.remove(start);//
							// because
							// as we are
							// removing tokens
							// index will remain
							// the same.

							if (questionMarkIndex != -1
									&& i == start + (questionMarkIndex)) {
								multiTokens += "?";
								lemmaLessMultokens += lemmaWordMap.get(tempTokens.get(start));
								tempTokens.remove(start);
							} else {
								lemmaLessMultokens += lemmaWordMap.get(tempTokens.get(start));
								multiTokens += tempTokens.remove(start);
							}

							if (i != end) {
								multiTokens += "_";
								lemmaLessMultokens += "_";
							}

						}
					}
					if (multiTokens.equalsIgnoreCase(multiword)) {
						tempTokens.add(start, multiTokens);
						lemmaWordMap.put(multiTokens, lemmaLessMultokens.replace("_", " "));
						resultScore = ModifierMap.score.get(list.get(1));
						tokens.clear();
						tokens.addAll(tempTokens);
						break;
					}
				}
			}
			counter++;
		}

		if (resultScore != -1) {
			result = new HashMap<LinkedList<String>, Double>();
			result.put(tokens, resultScore);
		}
		return result;

	}

	/**
	 * Get tweet normalized without noise
	 * 
	 * @param Tweet
	 * @return
	 */
	public static String getTweetWithoutUrlsAnnotations(String tweet,
			ArrayList<String> newHashTags) {

		StringTokenizer tokens = new StringTokenizer(tweet, " ");
		String newTweet = "";
		while (tokens.hasMoreTokens()) {
			String temp = tokens.nextToken();
			if (!temp.contains("@") && !temp.contains("http")) {
				newTweet += temp + " ";
			}
			if (temp.startsWith("#") && null != newHashTags) {
				newHashTags.add(temp.toLowerCase());
			}
		}

		tweet = "";
		tweet = newTweet;
		return tweet;

	}

	public static double detectHashtags(StringBuffer content,
			HashMap<String, Double> hashtags) {
		double score = 0;
		for (String tag : hashtags.keySet()) {
			if (content.toString().toLowerCase().contains(tag)) {
				int start = content.toString().toLowerCase().indexOf(tag);
				content.replace(start, start + tag.length(), "");
				return hashtags.get(tag);
			}
		}

		return score;
	}

	public static double detectSentenceModifier(StringBuffer content,
			Map<String, Double> sentenceModifiers) {
		double score = 0;
		for (String modifier : sentenceModifiers.keySet()) {
			if (content.toString().contains(modifier)) {
				LOG.info("SENTENCE MODIFIER FOUND" + modifier);
				int start = content.indexOf(modifier);
				content.replace(start, start + modifier.length(), "");
				return sentenceModifiers.get(modifier);
			}
		}

		return score;
	}

	/**
	 * Try to detect a smiley at the given tweet and it returns the appropriate
	 * score add this score to the general score as well ask MIhai for tokenizer
	 * 
	 * @return
	 */
	public static double detectSmiley(String tweet, Map<String, Double> smileys) {

		double score = 0;

		StringTokenizer toks = new StringTokenizer(tweet, " ");
		while (toks.hasMoreTokens()) {
			String token = toks.nextToken();
			if (smileys.containsKey(token)) {
				score = smileys.get(token);
				return score;

			}

		}

		return score;
	}

	public static String removeDuplicates(String s) {
		StringBuilder noDupes = new StringBuilder();
		int pos = 0;
		int len = s.length();
		int curLen = 0;
		while (pos < len) {
			if (pos == 0 || s.charAt(pos) != s.charAt(pos - 1)) {
				curLen = 1;
			} else {
				++curLen;
			}
			if (curLen < 3) {
				noDupes.append(s.charAt(pos));
			}
			++pos;
		}
		return noDupes.toString();
	}

	public static String toLowerCase(String tw) {

		return tw.toLowerCase();

	}

	public static void main(String[] args) throws IOException {

		/*
		 * String testStrings[] = { "GoodBoy", "greeeeaaat", "oooooo",
		 * "yeeeaaah", "abcde", "abcdeeee", "pleasee", "sleep" }; for (String s
		 * : testStrings) { System.out.println(s + "  ==>  " +
		 * removeDuplicates(s)); }
		 */

		/*
		 * String text =
		 * "films adapted from comic books have had plenty of success , whether they're about superheroes ( batman , superman , spawn ) , or geared toward kids ( casper ) or the arthouse crowd ( ghost world ) , but there's never really been a comic book like from hell before ."
		 * +
		 * "for starters , it was created by alan moore ( and eddie campbell ) , who brought the medium to a whole new level in the mid '80s with a 12-part series called the watchmen . "
		 * +
		 * "to say moore and campbell thoroughly researched the subject of jack the ripper would be like saying michael jackson is starting to look a little odd . "
		 * +
		 * "the book ( or \" graphic novel , \" if you will ) is over 500 pages long and includes nearly 30 more that consist of nothing but footnotes . "
		 * + "in other words , don't dismiss this film because of its source . "
		 * +
		 * "if you can get past the whole comic book thing , you might find another stumbling block in from hell's directors , albert and allen hughes . "
		 * +
		 * "getting the hughes brothers to direct this seems almost as ludicrous as casting carrot top in , well , anything , but riddle me this "
		 * ;
		 * 
		 * Set<String> stopwords = null; try { stopwords =
		 * Operator.getInstance().getStopword(); } catch (SQLException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 */

	}
}
