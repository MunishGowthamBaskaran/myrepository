package com.raremile.prd.inferlytics.database;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.raremile.prd.inferlytics.commons.Sentiword;
import com.raremile.prd.inferlytics.entity.CommentsHtml;
import com.raremile.prd.inferlytics.entity.DetailedSentiment;
import com.raremile.prd.inferlytics.entity.Feed;
import com.raremile.prd.inferlytics.entity.IdWordList;
import com.raremile.prd.inferlytics.entity.NounAdjScore;
import com.raremile.prd.inferlytics.entity.Relations;
import com.raremile.prd.inferlytics.entity.StockPatternEntity;
import com.raremile.prd.inferlytics.entity.UsecaseDetails;
import com.raremile.prd.inferlytics.entity.WordPatternScore;
import com.raremile.prd.inferlytics.entity.WordsPerSentence;



public interface LexiconDAOInterface {
	public Map<String, Double> getSmileys();

	public Map<String, Double> getModifiers();

	public Map<String, Double> getSentenceModifiers();

	public Map<String, Double> getNegations();

	public Map<String, Double> getLexicon();

	public Map<String, Double> getHashTags();

	public Relations getRelations();

	public StockPatternEntity getStockPatterns();

	public long getEntityId(Connection connection, String entity);

	public Map<Integer, String> getDimensionsByEntity(String entity);

	public IdWordList getDimensionWordIdByProduct(String product);

	public Map<String, Integer> getEntityIdMap();

	public Map<String, Integer> getSubProductIdMap();
	
	public List<CommentsHtml> getSubProductsForAnEntity(String entity);
	/**
	 * 
	 * @param word
	 * @return list of synonymns
	 */
	public List<String> getSynonymsByWords(String word);

	/**
	 * Retrieve the entire word pattern from the database. joining with
	 * MST_patterns
	 * 
	 */
	public Map<String, List<WordPatternScore>> getWordPatternScore();

	public Set<String> getStopword();
	
	public Set<String> getNounStopword();
	
	
	/**
	 * This method is to fetch feeds from database . Entity is nothing but a
	 * word for which we are searching.
	 * 
	 * @param entity
	 * @return
	 */
	public List<Feed> getFeedsForEntity(String entity, int queryToUse);

	public List<Feed> getFeedsFromMongo(String entity, String subProduct,
			int queryToUse);
	/**
	 * Store a number of tweets. Inserting more tweets at once is less costly
	 * than doing it for each in the list. 4 steps 1.Insert in `posts` table
	 * with post details. 2.Inserts the search word i.e., entity into entity
	 * table if not exists. 3.Inserts into post_sentiment table with
	 * corresponding sentiment. 4.Inserts into feedback table with the
	 * corresponding sentiment factors i.e., word patterns.(If the word pattern
	 * does not exist inserts it and gets the Id )
	 * 
	 * @param parsedList
	 * @param entity
	 * 
	 */
	public void storePostBatch(List<Feed> parsedList, String entity,
			boolean flag);

	public List<Feed> fillPostsFromStaging();
	public void storeNounAdjScore(List<NounAdjScore> nasList);


	/**
	 * This is the method which takes user feedback. The method accepts postId
	 * and sentiment and updates the feedback table with the given sentiment.
	 * 
	 * @param sentiment
	 * @param postId
	 */
	public void giveFeedback(DetailedSentiment sentiment, long postId);

	public List<Integer> storeWordPattern(Connection connection,
			List<WordPatternScore> list, List<NounAdjScore> nasList
			);

	public void storeHashTags(Connection connection, List<String> tags,
			double score);

	public List<String> removeExistSynonymWord(List<String> words,
			Connection connection);

	public void storePatterns(List<WordsPerSentence> wordsPerSentenceList);

	public void storeSentiwordPatterns(List<Sentiword> list);

	public Map<String, List<String>> getStopWordsForProduct(long subProductId,
			String productName, Connection connection);

	public UsecaseDetails getDetailsByEntitySubProduct(String entity,
			String subProduct);

}
