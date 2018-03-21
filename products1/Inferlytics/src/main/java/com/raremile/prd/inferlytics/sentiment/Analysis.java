package com.raremile.prd.inferlytics.sentiment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.raremile.prd.inferlytics.entity.FeatureAdjectiveEntity;
import com.raremile.prd.inferlytics.entity.Feed;
import com.raremile.prd.inferlytics.entity.LexiconMap;
import com.raremile.prd.inferlytics.entity.ModifierMap;
import com.raremile.prd.inferlytics.entity.OtherFactors;
import com.raremile.prd.inferlytics.entity.QueryLevelCache;
import com.raremile.prd.inferlytics.entity.WordPatternScore;
import com.raremile.prd.inferlytics.entity.WordsPerSentence;
import com.raremile.prd.inferlytics.exception.CriticalException;
import com.raremile.prd.inferlytics.utils.Util;



/**
 * Perform text analysis.
 * 
 * This will perform the actual semantic, lexical and sentimental analysis of
 * post content
 * 
 * @author Pratyusha
 */
public class Analysis {
	private static final Logger LOG = Logger.getLogger(Analysis.class);

	private final double[] gaussian = new double[6];
	private double score = 0.0;

	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @param score
	 *            the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}

	// private final String query = null;

	public static double AnalyseSentiment(Feed feed, List<String> tokens,
			LinkedHashMap<String, List<String>> taggedTokens,
			HashMap<String, Double> multiwordScoreMap,
			ArrayList<WordPatternScore> wpsListToInsert,
			FeatureAdjectiveEntity fae, WordsPerSentence wordsPerSentence)
			throws CriticalException {

		double sentiment = 0.0;
		LOG.debug("Analyzing sentiment for " + tokens);

		sentiment = getSentiment(tokens, taggedTokens, multiwordScoreMap,
				wpsListToInsert, fae, feed, wordsPerSentence);
		return sentiment;
	}

	/**
	 * Retrieve the sentiment value of a tweet.
	 * 
	 * @param fae
	 * @param wordsPerSentence
	 * 
	 * @param sentimentFactors
	 * 
	 * @param tweet
	 * @return
	 */
	private static double getSentiment(List<String> tokens,
			final LinkedHashMap<String, List<String>> taggedTokens,
			HashMap<String, Double> multiwordScoreMap,
			ArrayList<WordPatternScore> wpsListToInsert,
			FeatureAdjectiveEntity fae, Feed feed,
			WordsPerSentence wordsPerSentence) {
		// LinkedList<String> tokens = tokenizer.getTokens(tweet);
		Double score = 0.0;
		Double negation = 1.0;
		Double modifier = 1.0;
		int range = 0;
		// Double gauss = 1.0;
		// int i = 0;

		List<List<String>> wordsAroundList = new ArrayList<List<String>>();
		List<String> wordsNotPresent = new ArrayList<String>();
		List<String> wordsPresent = new ArrayList<String>();
		
		

		int tokenSize = tokens.size();

		try {

		LinkedList<String> taggedTokensKeys = new LinkedList<String>();
		taggedTokensKeys.addAll(taggedTokens.keySet());
		taggedTokensKeys.remove("NN");
		taggedTokensKeys.remove("JJ");
		List<String> nounWords = taggedTokens.get("NN");
		for (int tokenCounter = 0; tokenCounter < tokenSize; tokenCounter++) {
			List<String> wordsAround = new ArrayList<String>(5);
			wordsAround.add(0, "");
			wordsAround.add(1, "");
			wordsAround.add(2, "");
			wordsAround.add(3, "");
			wordsAround.add(4, "");
			boolean presentInMultiwordMap = false;
			// for (String token : tokens) {
			String token = tokens.get(tokenCounter);
			double thisScore = 0;
			// gauss = getDistance(token, tokens, i, this.gaussian);

			// TODO put proper conditions here

			// Set Relation in feed object TODO uncomment this
			//Util.setRelation(feed, token);

			
			String loweCaseToken = token.toLowerCase();
			if (multiwordScoreMap.containsKey(token)) {
				presentInMultiwordMap = true;
				thisScore = multiwordScoreMap.get(loweCaseToken);
				score += (negation) * (modifier) * thisScore;
			} else {
				/*
				 * String pattern = ""; if (null !=
				 * taggedTokens.get(loweCaseToken)) pattern =
				 * taggedTokens.get(loweCaseToken).toString() .replace(',',
				 * ' ');
				 */
				

				String pattern = taggedTokens.get(loweCaseToken).remove(0);
					LOG.debug("Pattern of " + loweCaseToken + " is" + pattern);
					// TODO replace zzzzz with pattern if u need to find score
					// for pattern
					thisScore = LexiconMap.getUnigramScoreFromRedis(
							loweCaseToken, "zzzzz",
						wpsListToInsert);
					LOG.debug("This Score is " + thisScore);
				score += (negation) * (modifier) * /* (gauss)* */thisScore;// .get(token.toLowerCase());
			}

			int taggedTokenIndex = taggedTokensKeys.indexOf(token);
			if ((taggedTokenIndex - 2) >= 0) {
				wordsAround.remove(0);
				String word = taggedTokensKeys.get(taggedTokenIndex - 2);
					if (null != nounWords && nounWords.contains(word)) {
					word = "?";
				}
				wordsAround.add(0, word);
			}
			if ((taggedTokenIndex - 1) >= 0) {
				wordsAround.remove(1);
				String word = taggedTokensKeys.get(taggedTokenIndex - 1);
					if (null != nounWords && nounWords.contains(word)) {
					word = "?";
				}
				wordsAround.add(1, word);
			}
			{
				wordsAround.remove(2);
				String word = token;
					if (null != nounWords && nounWords.contains(word)) {
					word = "?";
				}
				wordsAround.add(2, word);
			}

			if ((taggedTokenIndex + 1) < taggedTokensKeys.size()) {
				wordsAround.remove(3);
				String word = taggedTokensKeys.get(taggedTokenIndex + 1);
					if (null != nounWords && nounWords.contains(word)) {
					word = "?";
				}
				wordsAround.add(3, word);
			}
			if ((taggedTokenIndex + 2) < taggedTokensKeys.size()) {
				wordsAround.remove(4);
				String word = taggedTokensKeys.get(taggedTokenIndex + 2);
					if (null != nounWords && nounWords.contains(word)) {
					word = "?";
				}
				wordsAround.add(4, word);
			}
			wordsAroundList.add(wordsAround);

			// Deal with negations
			if (isNegation(loweCaseToken)) {
				// Util.addValueToMap(sentimentFactors, token, "negation");
				negation = -1.0;
				range = 2;
			} else if (range > 1) {
				range--;
			} else {
				negation = 1.0;
			}

			// Deal with modifiers (like 'very')
			modifier = getModifierValue(loweCaseToken, negation);

			if (range != 2 && modifier == 1.0) {
				if (thisScore == 0
						&& !presentInMultiwordMap
							&& !Util.productStopword(loweCaseToken, feed
									.getOpinion()
								.getObject())) {
					wordsNotPresent.add(loweCaseToken);
				} else {
					wordsPresent.add(loweCaseToken);
				}
			}

			// if (modifier != 1.0)
			// Util.addValueToMap(sentimentFactors, token, "modifier");
				LOG.debug("token = " + loweCaseToken + " AND  score = "
 + score);

		}

		// System.out.println("Score:\t" + score);

		// if (score > 1 || score < -1)
		// System.out.println(tweet.getText());

		
			if (null != wordsPerSentence) {

		wordsPerSentence.setSentenceScore(score);
		LOG.debug("score Of Sentence " + score);
		wordsPerSentence.setWordsAroundList(wordsAroundList);
		LOG.debug("wordsAroundList " + wordsAroundList);
		wordsPerSentence.setWordsNotPresent(wordsNotPresent);
		LOG.debug("wordsNotPresent " + wordsNotPresent);
		wordsPerSentence.setWordsPresent(wordsPresent);
		LOG.debug("wordsPresent " + wordsPresent);
			}
		// DataGenerationThread.addToWordSentList(wordsPerSentence);
		LOG.debug("Noun Words " + nounWords);
		} catch (Exception ex) {
			LOG.error(
					"Exception Occured While analysing sentiment throwing a new critical exception ",
					ex);
			throw new CriticalException(ex.getMessage());
		}
		return score;
	}



	private static boolean isNegation(String token) {

		return OtherFactors.getNegation().containsKey(token);
	}

	private static double getModifierValue(String token, Double negation) {
		double m = ModifierMap.getModifierUnigramScore(token);

		if (m != 0.0) {

			if ((negation == -1.0) && (m == 2)) {
				m = 0.8;
			}
		} else {
			m = 1.0;
		}
		return m;
	}

	/*
	 * The distance is calculated thanks to a gaussian distribution with :
	 * variance sigma = 1 mean i equals to the position of the token height is
	 * equal to the value of the token in the lexicon
	 * 
	 * If the distance between the entity and the token is 1, the value of
	 * gaussian is equal to the sentiment score of the token. When the distance
	 * increases, the value of gaussian decreases.
	 */
	public double[] CalculateDistance() {
		double sigma = 1.0;
		double gaussian_max = Math.exp(-(1 / (sigma * sigma * 2)))
				/ (sigma * Math.sqrt(2 * Math.PI));
		for (int i = 0; i <= 5; i++) {
			gaussian[i] = (1 / gaussian_max)
					* Math.exp(-(((i) / sigma) * ((i) / sigma)) / 2)
					/ (sigma * Math.sqrt(2 * Math.PI));
		}
		return gaussian;
	}

	/*
	 * private double getDistance(String token, LinkedList<String> tokens, int
	 * i, double gaussian[]) { String entity = this.query; int x =
	 * Math.abs(tokens.indexOf(entity)); double height =
	 * Math.abs(lexicon.get(token)); if (Math.abs(x - i) <= 5) return height *
	 * gaussian[Math.abs(x - i)]; else return 0.0; }
	 */
	public static void main(String[] args) {
		DecimalFormat df = new DecimalFormat("#.####");
		double scorr = 0.04473;
		double ttsdsd = -0.04411;

		scorr = scorr + ttsdsd;
		System.out.println(df.format(scorr));
	}
}