package com.raremile.prd.inferlytics.sentiment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.commons.POSConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.database.MongoConnector;
import com.raremile.prd.inferlytics.entity.DetailedSentiment;
import com.raremile.prd.inferlytics.entity.FeatureAdjectiveEntity;
import com.raremile.prd.inferlytics.entity.FeatureWordEntity;
import com.raremile.prd.inferlytics.entity.Feed;
import com.raremile.prd.inferlytics.entity.LexiconMap;
import com.raremile.prd.inferlytics.entity.NounAdjScore;
import com.raremile.prd.inferlytics.entity.Opinion;
import com.raremile.prd.inferlytics.entity.OtherFactors;
import com.raremile.prd.inferlytics.entity.QueryLevelCache;
import com.raremile.prd.inferlytics.entity.SENTIMENTENUM;
import com.raremile.prd.inferlytics.entity.Sentiment;
import com.raremile.prd.inferlytics.entity.WordPatternScore;
import com.raremile.prd.inferlytics.entity.WordsPerSentence;
import com.raremile.prd.inferlytics.exception.CriticalException;
import com.raremile.prd.inferlytics.preprocessing.CacheManager;
import com.raremile.prd.inferlytics.preprocessing.DataGenerationThread;
import com.raremile.prd.inferlytics.preprocessing.ModifierStructureGenerator;
import com.raremile.prd.inferlytics.preprocessing.POSTagger;
import com.raremile.prd.inferlytics.preprocessing.RedisCacheManager;
import com.raremile.prd.inferlytics.preprocessing.TextNormalizer;
import com.raremile.prd.inferlytics.preprocessing.Tokenizer;
import com.raremile.prd.inferlytics.utils.Util;

public class SentimentAnalysis {
	private static final Logger LOG = Logger.getLogger(SentimentAnalysis.class);

	/**
	 * @param s
	 */
	public static void main(String[] s) {

		// Read string from User
		String text = "";
		try {
			StringBuffer sb = new StringBuffer();
			String filepath = SentimentAnalysis.class.getClassLoader()
					.getResource("sample-input.txt").getPath();
			BufferedReader reader = new BufferedReader(new FileReader(
					URLDecoder.decode(filepath, "UTF-8")));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			text = sb.toString();
			text = Jsoup.parse(text).text();
			reader.close();
		} catch (FileNotFoundException e1) {
			LOG.error("", e1);
		} catch (IOException e) {
			LOG.error("", e);
		}
		if (null == LexiconMap.unigrams) {
			// new LexiconStructureGenerator();
			// LexiconMap.unigrams.clear();
			new ModifierStructureGenerator();
			CacheManager.instantiateOtherFactors();
		}
		// LOG.info(LexiconMap.unigramIndex);

		long timestart = System.currentTimeMillis();
		Feed feed = new Feed();
		feed.setFeedRating(""+3);
		feed.setFeedData(text);
		Opinion opinion = new Opinion();
		opinion.setObject("fragrance");
		feed.setOpinion(opinion);

		CacheManager.setWordSubDimForSubroduct("fragranceAW");
		// Fill Product Entity Stopword list also in cache here..
		QueryLevelCache.productBrandStopwordList = DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getLexiconDAO().getStopWordsForProduct(15, "fragrances", null);
		QueryLevelCache.productSynonyms = DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getSentimentDAO().getSynonymsBySubProductId(15);

		setOpinion(feed);

		long timeend = System.currentTimeMillis();
		System.out.println("Time taken " + (timeend - timestart));
	}

	/**
	 * 
	 * @param query
	 * @param content
	 * @param sentimentFactors
	 *            This method expects sentimentFactors as an already initialized
	 *            object and changes that object itself.
	 * 
	 *            Who ever calls this method need to set dimension values for
	 *            the product in QueryLevelCache
	 * 
	 *            The main method for finding opinion of the given text.. and
	 *            setting the opinion in the feed object.
	 * @return
	 */
	public static void setOpinion(Feed feed) {

		Opinion opinion = feed.getOpinion();
		String content = feed.getFeedData();

		ArrayList<String> newHashtags = new ArrayList<String>();
		ArrayList<WordPatternScore> wpsListToInsert = new ArrayList<WordPatternScore>();
		ArrayList<NounAdjScore> nasList = new ArrayList<NounAdjScore>();
		List<FeatureAdjectiveEntity> featAdjList = new ArrayList<FeatureAdjectiveEntity>();
		List<WordsPerSentence> wordsPerSentenceList = new ArrayList<WordsPerSentence>();
		Map<String, String> lemmaWordMap = new HashMap<>();
		List<DBObject> wordsNotPresentObj = new ArrayList<>();
		List<String> wordsNotUsed = new ArrayList<>();

		Double overallScore = 0.0;
		try {

			LinkedList<String> sentences = Tokenizer.getSentences(content);
			int i = 1;

			for (String sentence : sentences) {

				ArrayList<WordPatternScore> wpsListForSentence = new ArrayList<WordPatternScore>();
				FeatureAdjectiveEntity featureAdjEntity = new FeatureAdjectiveEntity();
				Map<String, String> nounAdjPerSentenceMap = new HashMap<String, String>();
				List<FeatureWordEntity> featureWordEntityList = new ArrayList<>();
				String sentenceContent = sentence;
				/**
				 * Uncomment the below line for enabling score_improvement
				 */
				//WordsPerSentence wordsPerSentence = new WordsPerSentence();
				Double otherScore = 0.0;
				otherScore += TextNormalizer.detectSmiley(sentenceContent,
						OtherFactors.getSmileys());
				StringBuffer bufContent = new StringBuffer(sentenceContent);
				otherScore += TextNormalizer.detectSentenceModifier(bufContent,
						OtherFactors.getSentenceModifiers());
				// otherScore +=
				// TextNormalizer.detectHashtags(bufContent,OtherFactors.getHashtags());
				sentenceContent = bufContent.toString();

				// Get Tokens
				LinkedList<String> tokens = Tokenizer.getTokens(
						sentenceContent, newHashtags);
				newHashtags.trimToSize();
				// Remove Stopwords
				LOG.debug("Tokens Initially-->" + tokens);

				// Deal with smileys

				// Identify Parts Of Speech

				LinkedHashMap<String, List<String>> taggedTokens = POSTagger
						.doPOSTagging(tokens, nounAdjPerSentenceMap,
								lemmaWordMap);

				// For now add lists other than noun and interjection to analyse
				// sentiment.

				LinkedList<String> finalTokens = new LinkedList<String>();

				finalTokens.addAll(taggedTokens.keySet());
				/**
				 * Send this to identify multiple words and multiple
				 * identifiers.
				 */
				finalTokens.remove("NN");
				finalTokens.remove("JJ");
				HashMap<String, Double> multiwordScoreMap = TextNormalizer
						.processTextBeforeAnalyse(finalTokens, taggedTokens,
								wpsListForSentence,lemmaWordMap);
				// Get Sentiment of words other than nouns -> score along with
				// sentiment word
				// finalTokens.addAll(smileytokenList.get(1));

				LinkedList<String> finalTokensWithoutStopwords = TextNormalizer
						.removeStopWord(OtherFactors.getStopwords(),
								finalTokens);
				LOG.debug("removed stopwords");

				/**
				 * Send wordsPerSentence for score improvement else send null
				 */
				double thisScore = Analysis.AnalyseSentiment(feed,
						finalTokensWithoutStopwords, taggedTokens,
						multiwordScoreMap, wpsListForSentence,
						featureAdjEntity, null);
				// Calculate sentiment out of score
				overallScore = thisScore + otherScore + overallScore;
				double thisSentenceScore = thisScore + otherScore;
				wpsListToInsert.addAll(wpsListForSentence);

				List<String> nounWords = taggedTokens
						.get(POSConstants.NounSingular);
				String entity = feed.getOpinion().getObject();
				/**
				 * UNCOMMENT THE BELOW CODE FOR ANALYSING ONLY NOUN WORDS
				 */
				/*	for (String noun : nounWords) {

					addNounFeatureToList(nasList, lemmaWordMap,
							featureWordEntityList, thisSentenceScore, entity,
							noun,feed.getFeedRating());
				}*/
				/**
				 * UNCOMMENT THE BELOW CODE FOR ANALYSING ONLY ADJ WORDS
				 */
				/* List<String> adjWords =
				 * taggedTokens.get(POSConstants.Adjective); for (String adjWord
				 * : adjWords) { addNounFeatureToList(nasList, lemmaWordMap,
				 * featureWordEntityList, thisSentenceScore, entity, adjWord,feed.getFeedRating()); }
				 */
				
				/**
				 * UNCOMMENT THE BELOW CODE FOR ANALYSING ALL WORDS
				 */
				  for (String token : finalTokensWithoutStopwords) {
					//  if(!MongoConnector.isStopword(token)){
				  addNounFeatureToList(nasList, lemmaWordMap,
				  featureWordEntityList, thisSentenceScore, entity,
				  token.toLowerCase(),feed.getFeedRating(),wordsNotUsed); 
					  //}
				  }
				 

				if (featureWordEntityList.size() != 0) {
					featureAdjEntity
							.setFeatureWordEntity(featureWordEntityList);
					if (thisSentenceScore < 0 && feed.getFeedRating() != null
							& feed.getFeedRating().contains("5")) {
						featureAdjEntity.setScore(0.1);
						
					} else {
						featureAdjEntity.setScore(thisSentenceScore);
					}
					featureAdjEntity.setDetailedSentiment(DetailedSentiment
							.getSentimentFromDouble(thisSentenceScore)
							.ordinal());
					featAdjList.add(featureAdjEntity);
				}

				/**
				 * Uncomment the below line for enabling score improvement
				 */
				// wordsPerSentenceList.add(wordsPerSentence);
				
				
				List<String> wordsNP = wordsNotUsed;
				for (String string : wordsNP) {
					DBObject wnp = new BasicDBObject();
					wnp.put("_id", string);
					wordsNotPresentObj.add(wnp);
					
					
				}
				
				
				LOG.debug("score of sentence " + i + " is " + thisScore
						+ " thissentencescore is " + thisSentenceScore);
				i++;
			}
			//MongoConnector.InsertObjectList(wordsNotPresentObj, "stopwords");
			/*
			 * for (Entry<String, String> nounAdj : nounAdjMap.entrySet()) {
			 * LOG.info("key -- " + nounAdj.getKey() + "entry " +
			 * nounAdj.getValue()); }
			 */
			for (WordsPerSentence wordsPerSentence : wordsPerSentenceList) {
				wordsPerSentence.setOverallScore(overallScore);
			}
			DataGenerationThread.addAllToWordSentList(wordsPerSentenceList);

			Sentiment sentiment = new Sentiment();
			sentiment.setPolarity(overallScore);
			if (overallScore > 0) {
				sentiment.setSentiment(SENTIMENTENUM.GOOD);
			} else if (overallScore < 0) {
				sentiment.setSentiment(SENTIMENTENUM.BAD);
			} else if (overallScore == 0) {
				sentiment.setSentiment(SENTIMENTENUM.NEUTRAL);
			}
			sentiment.setDetailedSentiment(DetailedSentiment
					.getSentimentFromDouble(overallScore));

			if (nasList.size() > 0) {
				LOG.info("setting noun, adj in opinion " + nasList.size());
				opinion.setFeatures(nasList);
			}
			// LOG.info("Extracted Feature Count :" + featAdjList);
			opinion.setFeatureDimensions(featAdjList);

			opinion.setOpinionOrientation(sentiment);
			feed.setOpinion(opinion);
			feed.setWpsList(wpsListToInsert);
			feed.setHashTags(newHashtags);

			feed.setSha1(Util.generateSHA1(content.getBytes()));

			LOG.info(" The opinion extracted  is "
					+ opinion.getOpinionOrientation().getSentiment().name()
					+ " with polarity "
					+ opinion.getOpinionOrientation().getPolarity());

		} catch (IOException e) {
			LOG.error("", e);
		} catch (CriticalException ce) {
			LOG.error(
					"Critical Exception Occured While Setting Opinion "
							+ ce.getMessage(), ce);
		} catch (Exception e) {

			LOG.error("", e);
		}

	}

	/**
	 * @param nasList
	 * @param lemmaWordMap
	 * @param featureWordEntityList
	 * @param thisSentenceScore
	 * @param entity
	 * @param noun
	 */
	private static void addNounFeatureToList(ArrayList<NounAdjScore> nasList,
			Map<String, String> lemmaWordMap,
			List<FeatureWordEntity> featureWordEntityList,
			double thisSentenceScore, String entity, String noun,String feedRating, List<String> wordsNotUsed) {
		if (null != OtherFactors.getNounStopwords()
				&& !OtherFactors.getNounStopwords().contains(noun)
				&& !OtherFactors.getNegation().keySet().contains(noun)
				&& !OtherFactors.getStopwords().contains(noun)
				&& !Util.productStopword(noun, entity)) {
			// String synonymNoun = noun;
			/**
			 * Handle global synonyms here
			 */
			String synonymNoun = RedisCacheManager.getSynonymWord(noun);
			LOG.info("synonymNoun for " + noun + " is : " + synonymNoun);
			/**
			 * Get Product Synonyms here if not null, get the synonyms and put
			 * the word here instead of synonym word.
			 */
			if (null == synonymNoun && null != QueryLevelCache.productSynonyms) {
				if (QueryLevelCache.productSynonyms.containsKey(noun)) {

					synonymNoun = QueryLevelCache.productSynonyms.get(noun);

				}
			}
			if (null == synonymNoun) {
				synonymNoun = noun;
			}

			/**
			 * Checking whether the word is present in the entity and subproduct
			 * -- dimension word list if there adding their ids and finally
			 * adding to feature adj list of the feed.
			 */

			if (QueryLevelCache.getWordSubDim() != null) {
				List<String> subDim = QueryLevelCache.getWordSubDim().get(
						synonymNoun);
				if (subDim != null) {
					FeatureWordEntity featureWordEntity = new FeatureWordEntity();
					if (lemmaWordMap.containsKey(noun)) {
						featureWordEntity.setNounWord(lemmaWordMap.get(noun));
					} else {
						featureWordEntity.setNounWord(noun);
					}
					featureWordEntity.setSynonymWord(synonymNoun);
					featureWordEntity.setSubDimension(subDim);
					featureWordEntityList.add(featureWordEntity);

					LOG.info("Feature synonym word " + synonymNoun
							+ " Noun Lemma word : " + noun
							+ " original Noun Word : "
							+ featureWordEntity.getNounWord());

					// TODO Move this code outside if you wish to keep all words
					// irrespective of dimensions
					NounAdjScore nas = new NounAdjScore();

					nas.setNoun(synonymNoun);
					if (lemmaWordMap.containsKey(noun)) {
						nas.setNoun(lemmaWordMap.get(noun));
					}
					nas.setSynonymNoun(synonymNoun);
					if (thisSentenceScore < 0 && feedRating != null
							& feedRating.contains("5")) {
						
						nas.setScore(0.1);
					} else {
						nas.setScore(thisSentenceScore);
					}

					LOG.info("Noun synonym word " + synonymNoun
							+ " Noun Lemma word : " + noun
							+ " original Noun Word : " + nas.getNoun());
					nasList.add(nas);
					
				}

			}
			// Move noun code here..
			
		}
	}

	public static void initialize() {
		if (null == LexiconMap.unigrams) {
			// new LexiconStructureGenerator();
			// LOG.info(LexiconMap.unigrams);
			// LOG.info(LexiconMap.unigramIndex);
			// LOG.info(LexiconMap.posPattern);
			// LOG.info(LexiconMap.score);
			// LOG.info(LexiconMap.multiwordPattern);
			// LexiconMap.unigrams.clear();
			new ModifierStructureGenerator();
			CacheManager.instantiateOtherFactors();
		}
	}
}