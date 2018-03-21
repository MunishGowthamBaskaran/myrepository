package com.raremile.prd.inferlytics.database;

import static com.raremile.prd.inferlytics.database.DAOUtil.close;
import static com.raremile.prd.inferlytics.database.DAOUtil.preparePlaceHolders;
import static com.raremile.prd.inferlytics.database.DAOUtil.prepareStatement;
import static com.raremile.prd.inferlytics.database.DAOUtil.setValues;
import static com.raremile.prd.inferlytics.database.DAOUtil.toSqlDate;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.commons.Sentiword;
import com.raremile.prd.inferlytics.entity.CommentsHtml;
import com.raremile.prd.inferlytics.entity.DetailedSentiment;
import com.raremile.prd.inferlytics.entity.FeatureAdjectiveEntity;
import com.raremile.prd.inferlytics.entity.FeatureWordEntity;
import com.raremile.prd.inferlytics.entity.Feed;
import com.raremile.prd.inferlytics.entity.IdMap;
import com.raremile.prd.inferlytics.entity.IdWordList;
import com.raremile.prd.inferlytics.entity.NounAdjScore;
import com.raremile.prd.inferlytics.entity.Opinion;
import com.raremile.prd.inferlytics.entity.QueryLevelCache;
import com.raremile.prd.inferlytics.entity.Relations;
import com.raremile.prd.inferlytics.entity.SENTIMENTENUM;
import com.raremile.prd.inferlytics.entity.Sentiment;
import com.raremile.prd.inferlytics.entity.StockPatternEntity;
import com.raremile.prd.inferlytics.entity.UsecaseDetails;
import com.raremile.prd.inferlytics.entity.WordPatternScore;
import com.raremile.prd.inferlytics.entity.WordsPerSentence;
import com.raremile.prd.inferlytics.exception.DAOException;
import com.raremile.prd.inferlytics.preprocessing.CacheManager;
import com.raremile.prd.inferlytics.sentiment.SentimentAnalysis;

public class LexiconDAOImpl implements LexiconDAOInterface {

	private static final Logger LOG = Logger.getLogger(LexiconDAOImpl.class);
	// Constants
	// ----------------------------------------------------------------------------------
	private static final String GET_RELATION = "SELECT  `RelationID`,LOWER(`RelationName`), `PossibleMaleRelation`,  `PossibleFemaleRelation`FROM `rm_lexicondb`.`relations`";
	private static final String GET_STOCK_PATTERNS = "SELECT pattern,value,valueFor FROM stockpatterns ";
	private static final String GET_SMILEYS = "SELECT smiley,value FROM smileys;";
	private static final String GET_MODIFIERS = "SELECT modifier,value FROM modifiers;";
	private static final String GET_SENTENCE_MODIFIERS = "SELECT modifier,value FROM sentence_modifiers;";
	private static final String GET_NEGATIONS = "SELECT negation,value FROM negations;";
	private static final String GET_LEXICON = "SELECT `word`, `sentiment` FROM `setiwordnet_data`";
	/** where sentiment > 0.35 or sentiment < -0.35"; */
	private static final String GET_WPS = "SELECT  wps.ID AS id, WORD AS word , patterns.PATTERN AS pattern ,`SCORE` AS score "
			+ "FROM word_pattern_score wps,mst_paterns patterns WHERE wps.ID<16856 AND wps.PATTERN_ID = patterns.ID AND wps.SCORE!=0";
	private static final String GET_STOPWORDS = "SELECT `word` FROM `stopwords`";
	private static final String GET_SYNONYM_BY_WORD = "SELECT Word FROM synonyms WHERE SynonymId =( SELECT SynonymId FROM synonyms WHERE Word = ?  limit 1)";
	private static final String GET_HASHTAGS = "SELECT tag,value FROM hashtags;";

	private static final String GET_POSTS = "SELECT p.feedid,p.feeddate,p.UserName,p.Age,p.UserLocation,p.Gender,s.sentiment,s.score FROM posts p,post_sentiment s WHERE s.POST_ID = p.feedid AND s.ENTITY_ID=? ORDER BY p.feedid DESC";
	private static final String GET_POSTS_FROM_SENTIMENT = " SELECT ps.post_id AS postId,ps.sentiment,ps.score FROM post_sentiment ps                      "
			+ " JOIN posts p ON p.feedId=ps.POST_ID JOIN subproducts sp ON sp.SubProductId=p.SubProductId     "
			+ " WHERE  ps.ENTITY_ID=? AND sp.SubProductName=? ";

	/*
	 * "SELECT s.post_id AS postId,s.sentiment,s.score FROM post_sentiment s  WHERE  s.ENTITY_ID=?"
	 * ;
	 */
	private static final String GET_POSTS_FROM_SENTIMENT_TOP3 = " SELECT ps.post_id AS postId,ps.sentiment,ps.score FROM post_sentiment ps                      "
			+ " JOIN posts p ON p.feedId=ps.POST_ID JOIN subproducts sp ON sp.SubProductId=p.SubProductId     "
			+ " WHERE  ps.ENTITY_ID=? AND sp.SubProductName=? ORDER BY SCORE DESC LIMIT 0,3                   ";

	/*
	 * "SELECT s.post_id AS postId,s.sentiment,s.score  FROM post_sentiment s WHERE  s.ENTITY_ID=? ORDER BY SCORE DESC LIMIT 0,3"
	 * ;
	 */
	private static final String GET_POSTS_FROM_SENTIMENT_BOTTOM3 = " SELECT ps.post_id AS postId,ps.sentiment,ps.score FROM post_sentiment ps                      "
			+ " JOIN posts p ON p.feedId=ps.POST_ID JOIN subproducts sp ON sp.SubProductId=p.SubProductId     "
			+ " WHERE  ps.ENTITY_ID=? AND sp.SubProductName=? ORDER BY SCORE ASC LIMIT 0,3                   ";

	/* "SELECT s.post_id AS postId,s.sentiment,s.score  FROM post_sentiment s WHERE  s.ENTITY_ID=? ORDER BY SCORE ASC LIMIT 0,3" */;
	private static final String GET_POSTS_FOR_FEEDBACK = "SELECT p.feedId,p.feeddate,s.sentiment,s.score "
			+ " FROM   posts p "
			+ " JOIN post_sentiment s  ON p.feedId = s.POST_ID "
			+ " JOIN feedback f  ON f.POST_ID = p.feedId "
			+ " WHERE  s.ENTITY_ID=? "
			+ " AND (WEAK_POSITIVE + POSITIVE + STRONG_POSITIVE + WEAK_NEGATIVE + NEGATIVE + STRONG_NEGATIVE + NEUTRAL ) > 0 "
			+ " GROUP BY p.feedId,p.feeddate,s.sentiment,s.score ORDER BY p.feedId DESC ";
	private static final String GET_POSTS_TOP3 = "SELECT p.feedid,p.feeddate,s.sentiment,s.score FROM posts p,post_sentiment s WHERE s.POST_ID = p.feedid AND s.ENTITY_ID=? ORDER BY SCORE DESC LIMIT 0,3";
	private static final String GET_POSTS_BOTTOM3 = "SELECT p.feedid,p.feeddate,s.sentiment,s.score  FROM posts p,post_sentiment s  WHERE s.POST_ID = p.feedid AND s.ENTITY_ID=? ORDER BY SCORE ASC LIMIT 0,3";

	private static final String GET_NOUN_STOPWORDS = "SELECT NounStopword FROM nounstopword";
	private static final String GET_DIMENSION_FOR_ENTITY = "SELECT DimensionId,NounWord FROM dimensions "
			+ "WHERE EntityId=(SELECT EntityId FROM entity WHERE Entity=?)";
	private static final String GET_WORD_DIMENSION_FOR_ENTITY = "SELECT DISTINCT(WordId) AS wordId, Word AS word "
			+ " FROM   v_products_subdimensions_words_supproducts_detail WHERE SubProductName = ? ";

	private static final String INSERT_POST = "INSERT IGNORE INTO  `posts`(FeedId, EntityId, SubProductId, UserName, UserLocation, Gender, Age, FeedDate, CreatedDate, FeedRating, SourceId,RelationId) "
			+ "  VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String INSERT_POST_SENTIMENT = "INSERT INTO post_sentiment(POST_ID,ENTITY_ID,SENTIMENT,DETAILED_SENTIMENT,SCORE) VALUES(?,?,?,?,?)";
	private static final String INSERT_FEEDBACK = "INSERT INTO feedback ( POST_ID, WORD_ID) VALUES(?,?)";
	private static final String INSERT_ENTITY = "  INSERT   INTO `entity` (`Entity`) VALUES (?) ON DUPLICATE KEY UPDATE `EntityId`=LAST_INSERT_ID(`EntityId`)";
	private static final String INSERT_PATTERN = "  INSERT   INTO `mst_paterns` (`PATTERN`) VALUES (?) ON DUPLICATE KEY UPDATE `ID`=LAST_INSERT_ID(`ID`)";
	private static final String INSERT_WPS = "INSERT  INTO `word_pattern_score` (`WORD`,`PATTERN_ID`,`SCORE`) VALUES(?,?,?) ON DUPLICATE KEY UPDATE `ID`=LAST_INSERT_ID(`ID`)";
	private static final String INSERT_HASHTAGS = "INSERT IGNORE INTO hashtags(tag,value) values(?,?)";
	private static final String INSERT_NOUN = "INSERT INTO noun(PostId,NounWord,AdjScore,EntityId,CreatedDate,SubProductId) VALUES(?,?,?,?,?,?)";
	private static final String INSERT_FEATURE = "INSERT INTO featureadj(PostId,AdjWordId,DetailedSentiment,Score,EntityId,CreatedDate) VALUES(?,?,?,?,?,?)";

	private static final String GIVE_FEEDBACK = "UPDATE feedback SET  sentiment = sentiment + 1 WHERE  POST_ID=?";
	private static final String GET_POSTS_STAGING = " SELECT ps.FeedId,ps.EntityId, ps.subProductId, ps.UserName, ps.UserLocation, ps.Gender, ps.Age, ps.FeedDate, ps.CreatedDate, ps.FeedRating, ps.SourceId, "
			+ " ps.FeedData, sp.SubProductName FROM rm_lexicondb.posts_staging ps, rm_lexicondb.subproducts sp WHERE sp.SubProductId=ps.SubProductId  AND sp.SubProductId=1 GROUP BY ps.FeedData  ORDER BY sp.SubProductId";

	private static final String GET_ALL_SENTI_WORDS = "SELECT distinct(`word`) FROM `rm_lexicondb`.`setiwordnet_data` ";
	private static final String INSERT_SYNONYMS = "INSERT IGNORE INTO `rm_lexicondb`.`synonyms`(`Word`)VALUES (?)";
	private static final String UPDATE_SYNONYMS_ID = "UPDATE `rm_lexicondb`.`synonyms` SET `SynonymId` =? WHERE Word  IN (%s);";
	private static final String SELECT_EXISTS_SYNONYMS_WORDS = "SELECT word FROM synonyms WHERE Word  IN (%s);";

	private static final String INSERT_PATTERN_SCORE = "INSERT INTO `rm_lexicondb`.`patternscore` (`Score`,`OverallScore`,`Word1`,`Word2`,`Word3`,`Word4`,`Word5`)VALUES (?,?,?,?,?,?,?)";

	private static final String INSERT_WORD_NOT_PRESENT = "INSERT IGNORE INTO `rm_lexicondb`.`wordsnotpresent`(`Word`,`OverallScore`,`Score`)VALUES (?,?,?)";

	private static final String INSERT_SENTIWORD_PATTERN = "INSERT IGNORE INTO  `sentiword_pattern`"
			+ "             (POS,ID,POSScore,NegScore,word,Glossary,pattern) "
			+ "VALUES       (?,?,?,?,?,?,?)        ";

	private static final String INSERT_WORD_USED = "INSERT INTO wordsused ( word, score) VALUES(?,?)";

	private static final String GET_STOPWORDS_FOR_SUBPRODUCTID = "SELECT DISTINCT(StopWord) FROM productbrandstopwords WHERE SubProductId = ? ";

	private static final String GET_STOPWORDS_FOR_PRODUCTNAME = " select  pbs.StopWord as Stopword,v.Entity from "
			+ " v_entity_prod_subprod v join productbrandstopwords pbs on v.SubProductId = pbs.SubProductId "
			+ " where concat(Entity,' ',ProductName,' ',SubProductName) like ? ";

	private static final String GET_ENTITY_IDS = "SELECT EntityId, Entity FROM entity";

	private static final String GET_SUBPRODUCT_IDS = "SELECT SubProductId,SubProductName FROM subproducts";

	private static final String GET_SUBPRODUCT_FOR_ENTITY = "SELECT subproduct FROM entity_subproduct where entity = ? ";
	// Vars
	// ---------------------------------------------------------------------------------------
	private static final String GET_USECASE_DETAILS="SELECT " +
			"  e.EntityId AS EntityId"+
  ", p.ProductId AS ProductId  "+
  ", p.ProductName AS ProductName"+
  ", subP.SubProductId AS SubProductId"+
" FROM"+
" entity AS e"+
" JOIN products_entity_mapping AS peMap ON e.EntityId = peMap.EntityID"+
" JOIN products AS p ON peMap.ProductID = p.ProductId"+
" JOIN subproducts AS subP ON p.ProductId = subP.ProductID"+
" WHERE e.Entity = ? AND subP.SubProductName=?";

	private final DAOFactory daoFactory;

	// Constructors
	// -------------------------------------------------------------------------------

	/**
	 * Construct a Lexicon DAO for the given DAOFactory. Package private so that
	 * it can be constructed inside the DAO package only.
	 * 
	 * @param daoFactory
	 *            The DAOFactory to construct this User DAO for.
	 */
	LexiconDAOImpl(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	// Actions
	// ------------------------------------------------------------------------------------
	@Override
	public Relations getRelations() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Relations relation = new Relations();

		List<Integer> relationId = new ArrayList<Integer>();
		List<String> relationName = new ArrayList<String>();
		List<Integer> possibleMaleRelation = new ArrayList<Integer>();
		List<Integer> possibleFemaleRelation = new ArrayList<Integer>();

		try {
			connection = daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(GET_RELATION);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {

				relationId.add(resultSet.getInt(1));
				relationName.add(resultSet.getString(2));
				possibleMaleRelation.add(resultSet.getInt(3));
				possibleFemaleRelation.add(resultSet.getInt(4));
			}

			relation.setRelationName(relationName);
			relation.setPossibleMaleRelation(possibleMaleRelation);
			relation.setPossibleFemaleRelation(possibleFemaleRelation);

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}

		return relation;
	}

	/**
	 * @return Smileys and corresponding score.
	 */
	@Override
	public Map<String, Double> getSmileys() {
		Map<String, Double> smileys = new HashMap<String, Double>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(GET_SMILEYS);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				smileys.put(resultSet.getString(1), resultSet.getDouble(2));
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}

		return smileys;
	}

	/**
	 * @return Modifier and corresponding score.
	 */
	@Override
	public Map<String, Double> getModifiers() {
		HashMap<String, Double> modifiers = new HashMap<String, Double>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(GET_MODIFIERS);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				modifiers.put(resultSet.getString(1), resultSet.getDouble(2));
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}

		return modifiers;
	}

	/**
	 * @return Negation and corresponding score.
	 */
	@Override
	public Map<String, Double> getNegations() {
		HashMap<String, Double> negations = new HashMap<String, Double>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(GET_NEGATIONS);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				negations.put(resultSet.getString(1), resultSet.getDouble(2));
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}

		return negations;
	}

	/**
	 * Retrieve the entire lexicon from the database.
	 * 
	 * These are key-value pairs of words and their sentimental value.
	 */
	@Override
	public Map<String, Double> getLexicon() {
		HashMap<String, Double> lexicon = new HashMap<String, Double>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(GET_LEXICON);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				lexicon.put(resultSet.getString(1), resultSet.getDouble(2));
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}

		return lexicon;
	}

	@Override
	public Map<String, List<WordPatternScore>> getWordPatternScore() {
		long timestart = System.nanoTime();
		HashMap<String, List<WordPatternScore>> wpsList = new HashMap<String, List<WordPatternScore>>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(GET_WPS);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				WordPatternScore wps = new WordPatternScore();
				String word = resultSet.getString(2).split("#")[0];
				wps.setId(resultSet.getInt(1));
				wps.setWord(resultSet.getString(2));
				wps.setPattern(resultSet.getString(3));
				wps.setScore(resultSet.getDouble(4));

				if (null != word && !word.isEmpty()) {
					List<WordPatternScore> list = wpsList.get(word);
					if (null == list) {
						list = new ArrayList<WordPatternScore>();
					}
					list.add(wps);
					wpsList.put(word, list);
				}

			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		long timeend = System.nanoTime();
		System.out.println(timeend - timestart);
		return wpsList;
	}

	@Override
	public Set<String> getStopword() {
		Set<String> stopwords = new HashSet<String>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(GET_STOPWORDS);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				stopwords.add(resultSet.getString(1));
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		return stopwords;
	}

	@Override
	public void storePostBatch(List<Feed> parsedList, String entity,
			boolean isPostStagingData) {

		Connection connection = null;
		long entityId = 0;
		PreparedStatement psForSentiment = null;
		PreparedStatement psForFeedback = null;
		PreparedStatement psForNoun = null;
		PreparedStatement psForFeature = null;
		PreparedStatement psForPosts = null;

		try {
			connection = daoFactory.getConnection();
			connection.setAutoCommit(true);

			// INSERT FEED AS BATCH HERE

			List<DBObject> dbObjectPostList = new ArrayList<>();
			List<DBObject> dbObjectNounList = new ArrayList<>();
			List<DBObject> dbObjectFeatureList = new ArrayList<>();
			// GET ENTITY ID HERE. IF NOT THERE INSERT ELSE GET ID
			if (isPostStagingData == false) {
				entityId = getEntityId(connection, entity);
			}

			// INSERT SENTIMENT + FEEDBACK AS BATCH HERE
			psForSentiment = connection.prepareStatement(INSERT_POST_SENTIMENT);
			psForFeedback = connection.prepareStatement(INSERT_FEEDBACK);
			psForNoun = connection.prepareStatement(INSERT_NOUN);
			psForFeature = connection.prepareStatement(INSERT_FEATURE);
			psForPosts = connection.prepareStatement(INSERT_POST);

			for (Feed feed : parsedList) {
				// //************POSTS TABLE INSERT
				Object[] valuesForPosts = {
						feed.getFeedId(),
						(isPostStagingData == true) ? feed.getEntityId()
								: entityId, feed.getSubProductId(),
						feed.getUserName(), feed.getUserLocation(),
						feed.getGender(), feed.getAge(), feed.getFeedDate(),
						feed.getCreatedDate(), feed.getFeedRating(),
						feed.getSourceId(), feed.getRelationId() };
				setValues(psForPosts, valuesForPosts);
				// psForPosts.addBatch();
				// //************POSTS TABLE INSERT

				// //************MONGO INSERT
				// PREPERATION***********////////////////////////

				BasicDBObject basicDBObject = new BasicDBObject();

				basicDBObject.put("_id", feed.getFeedId());
				basicDBObject.put("content", feed.getFeedData());
				basicDBObject.put("subpoductId", feed.getSubProductId());
				basicDBObject.put("sha1", feed.getSha1());
				if (null != feed.getOpinion()
						&& null != feed.getOpinion().getOpinionOrientation()) {
					basicDBObject.put("Score", feed.getOpinion()
							.getOpinionOrientation().getPolarity());
				}
				basicDBObject.put("createdDate", feed.getFeedDate());
				dbObjectPostList.add(basicDBObject);

				// //************MONGO INSERT
				// PREPERATION***********////////////////////////

				if (null != feed.getOpinion()
						&& null != feed.getOpinion().getOpinionOrientation()) {
					Object[] valuesForSentiment = {
							feed.getFeedId(),
							(isPostStagingData == true) ? feed.getEntityId()
									: entityId,
							feed.getOpinion().getOpinionOrientation()
									.getSentiment().ordinal(),
							feed.getOpinion().getOpinionOrientation()
									.getDetailedSentiment().ordinal(),
							feed.getOpinion().getOpinionOrientation()
									.getPolarity() };
					setValues(psForSentiment, valuesForSentiment);
					// psForSentiment.addBatch();
					// STORE IDENTIFIED HASH TAGS WITH SENTENCE SCORE

					// STORE WORD PATTERN AND GET ID's AS LIST
					List<NounAdjScore> nasList = feed.getOpinion()
							.getFeatures();
					List<FeatureAdjectiveEntity> dimensions = feed.getOpinion()
							.getFeatureDimensions();

					String productId = null;
					List<String> categories = null;
					/*
					 * List<Integer> wpsIdList = storeWordPattern(connection,
					 * feed.getWpsList(), nasList);
					 * 
					 * if (dimensions != null) {
					 * LOG.info("dimensions List count " + dimensions.size() +
					 * " for the feed " + feed.getFeedId() + "   -- " ); }
					 * 
					 * /* for (Integer integer : wpsIdList) { Object[]
					 * valuesForFeedback = { feed.getId(), integer };
					 * setValues(psForFeedback, valuesForFeedback);
					 * psForFeedback.addBatch(); }
					 */
					if (nasList != null) {
						for (NounAdjScore nas : nasList) {
							/*
							 * Object[] valuesForNoun = { feed.getFeedId(),
							 * nas.getNoun(), nas.getAdjId(), nas.getScore(),
							 * (isPostStagingData == true) ? feed .getEntityId()
							 * : entityId, toSqlDate(new Date(
							 * System.currentTimeMillis())),
							 * feed.getSubProductId() }; setValues(psForNoun,
							 * valuesForNoun); psForNoun.addBatch();
							 */

							DBObject basicDBNounObject = new BasicDBObject();

							basicDBNounObject.put("PostId", feed.getFeedId());
							basicDBNounObject.put("NounWord", nas.getNoun());
							basicDBNounObject.put("SynonymWord",
									nas.getSynonymNoun());
							basicDBNounObject.put("AdjScore", nas.getScore());
							if (isPostStagingData) {
								basicDBNounObject.put("EntityId",
										feed.getEntityId());
							} else {
								basicDBNounObject.put("EntityId", entityId);
							}

							if (feed.getProductId() != null) {
								productId = feed.getProductId();
							} else if (feed.getFeedId().contains(":")) {
								productId = feed.getFeedId().split(":")[0];
								categories = MongoConnector
										.getCategoriesForProduct(productId);
							}

							basicDBNounObject.put("ProductId", productId);
							if (null != categories) {
								basicDBNounObject.put("Categories", categories);
							}
							basicDBNounObject.put("SubProductId",
									feed.getSubProductId());
							basicDBNounObject.put("createdDate",
									feed.getFeedDate());
							dbObjectNounList.add(basicDBNounObject);
						}
					}
					// Do the same as above here
					if (null != dimensions) {
						for (FeatureAdjectiveEntity fae : dimensions) {
							for (FeatureWordEntity entry : fae
									.getFeatureWordEntity()) {
								/*
								 * Object[] valuesForDimension = {
								 * feed.getFeedId(), wordId,
								 * fae.getDetailedSentiment(), fae.getScore(),
								 * (isPostStagingData == true) ? feed
								 * .getEntityId() : entityId, toSqlDate(new
								 * Date( System.currentTimeMillis())) };
								 * setValues(psForFeature, valuesForDimension);
								 * psForFeature.addBatch();
								 */

								DBObject basicDBFeatureObject = new BasicDBObject();

								basicDBFeatureObject.put("PostId",
										feed.getFeedId());
								basicDBFeatureObject.put("Word",
										entry.getNounWord());
								basicDBFeatureObject.put("SynonymWord",
										entry.getSynonymWord());
								basicDBFeatureObject.put("SubDimension",
										entry.getSubDimension());
								basicDBFeatureObject.put("SentenceScore",
										fae.getScore());
								if (null != feed.getOpinion()
										&& null != feed.getOpinion()
												.getOpinionOrientation()) {
									basicDBFeatureObject.put("OverallScore",
											feed.getOpinion()
													.getOpinionOrientation()
													.getPolarity());
								}

								if (isPostStagingData) {
									basicDBFeatureObject.put("EntityId",
											feed.getEntityId());
								} else {
									basicDBFeatureObject.put("EntityId",
											entityId);
								}
								if (productId != null) {
									basicDBFeatureObject.put("ProductId",
											productId);
								}
								if (null != categories) {
									basicDBFeatureObject.put("Categories",
											categories);
								}
								basicDBFeatureObject.put("SubProductId",
										feed.getSubProductId());
								basicDBFeatureObject.put("CreatedDate",
										feed.getFeedDate());
								dbObjectFeatureList.add(basicDBFeatureObject);
							}

						}
					}
				}

			}

			// Insert into MONGO DB here
			MongoConnector.InsertObjectList(dbObjectPostList, "posts");// Feeds(dbObjectArray);
			// new MongoConnector(dbObjectArray);
			//
			LOG.info("Going to insert post sentiment"
					+ System.currentTimeMillis());
			// psForSentiment.executeBatch();
			// psForFeedback.executeBatch();
			LOG.info("Going to insert psForNoun" + System.currentTimeMillis()
					+ " count " + dbObjectNounList.size());
			MongoConnector.InsertObjectList(dbObjectNounList, "noundata");
			// psForNoun.executeBatch();
			LOG.info("Going to insert psForFeature"
					+ System.currentTimeMillis());
			MongoConnector.InsertObjectList(dbObjectFeatureList, "features");
			// psForFeature.executeBatch();
			LOG.info("Going to insert psForFeature"
					+ System.currentTimeMillis());
			// psForPosts.executeBatch();
			LOG.info("After inserting posts " + System.currentTimeMillis());

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection);

			close(psForSentiment);
			close(psForNoun);
			close(psForFeature);
			close(psForFeedback);
		}

	}

	@Override
	public long getEntityId(Connection connection, String entity) {
		long entityId = 0;

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		boolean closeConnection = false;
		try {
			if (null == connection) {
				connection = daoFactory.getConnection();
				closeConnection = true;
			}
			Object[] values = { entity };
			preparedStatement = prepareStatement(connection, INSERT_ENTITY,
					true, values);
			preparedStatement.executeUpdate();
			resultSet = preparedStatement.getGeneratedKeys();
			if (resultSet.next()) {
				entityId = resultSet.getInt(1);

			}
		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(preparedStatement);
			close(resultSet);
			if (closeConnection) {
				close(connection);
			}
		}
		return entityId;
	}

	@Override
	public List<Integer> storeWordPattern(Connection connection,
			List<WordPatternScore> list, List<NounAdjScore> nasList) {
		ArrayList<Integer> wpsIdList = new ArrayList<Integer>();
		// Connection connection = null;
		PreparedStatement psForPattern = null;
		PreparedStatement psForWPS = null;
		ResultSet rsForPattern = null;
		ResultSet rsForWPS = null;

		boolean closeConnection = false;
		try {
			if (null == connection) {
				connection = daoFactory.getConnection();
				closeConnection = true;
			}
			for (WordPatternScore wps : list) {
				Object[] valuesForPattern = { wps.getPattern() };
				int newId = 0;
				psForPattern = prepareStatement(connection, INSERT_PATTERN,
						true, valuesForPattern);
				psForPattern.executeUpdate();
				rsForPattern = psForPattern.getGeneratedKeys();
				if (rsForPattern.next()) {
					newId = rsForPattern.getInt(1);

				}

				Object[] valuesForWPS = { wps.getWord(), newId, wps.getScore() };
				psForWPS = prepareStatement(connection, INSERT_WPS, true,
						valuesForWPS);
				psForWPS.executeUpdate();
				rsForWPS = psForWPS.getGeneratedKeys();
				if (rsForWPS.next()) {
					int newWPSId = rsForWPS.getInt(1);
					if (null != nasList) {
						for (NounAdjScore nas : nasList) {
							if (wps.getWord().contains(nas.getAdj())) {
								nas.setAdjId(newWPSId);
							}
						}
					}

					wpsIdList.add(newWPSId);
				}
			}
		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(null, psForPattern, rsForPattern);
			close(null, psForWPS, rsForWPS);
			if (closeConnection) {
				close(connection);
			}
		}
		return wpsIdList;
	}

	@Override
	public void giveFeedback(DetailedSentiment sentiment, long postId) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = daoFactory.getConnection();
			String query = GIVE_FEEDBACK;
			query = query.replace("sentiment", sentiment.toString());
			Object[] values = { postId };
			preparedStatement = prepareStatement(connection, query, false,
					values);
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement);

		}

	}

	@Override
	public List<String> getSynonymsByWords(String word) {
		ArrayList<String> synonyms = new ArrayList<String>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = daoFactory.getConnection();
			Object[] values = { word };
			preparedStatement = prepareStatement(connection,
					GET_SYNONYM_BY_WORD, false, values);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				synonyms.add(resultSet.getString(1));
			}
		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);

		}
		return synonyms;
	}

	@Override
	public Map<String, Double> getSentenceModifiers() {

		HashMap<String, Double> modifiers = new HashMap<String, Double>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = daoFactory.getConnection();
			preparedStatement = connection
					.prepareStatement(GET_SENTENCE_MODIFIERS);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				modifiers.put(resultSet.getString(1), resultSet.getDouble(2));
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}

		return modifiers;

	}

	@Override
	public void storeHashTags(Connection connection, List<String> tags,
			double score) {
		// Connection connection = null;
		boolean closeConnection = false;
		PreparedStatement preparedStatement = null;
		try {
			if (connection == null) {
				connection = daoFactory.getConnection();
				closeConnection = true;
			}
			preparedStatement = connection.prepareStatement(INSERT_HASHTAGS);
			for (String tag : tags) {
				Object[] values = { tag, score };
				setValues(preparedStatement, values);
				preparedStatement.addBatch();
			}
			preparedStatement.executeBatch();
		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(null, preparedStatement, null);
			if (closeConnection) {
				close(connection);
			}
		}
	}

	@Override
	public Map<String, Double> getHashTags() {
		HashMap<String, Double> hashtags = new HashMap<String, Double>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(GET_HASHTAGS);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				hashtags.put(resultSet.getString(1), resultSet.getDouble(2));
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}

		return hashtags;
	}

	@Override
	public List<Feed> getFeedsForEntity(String entity, int queryToUse) {

		String query = null;
		switch (queryToUse) {
		case 0:
			query = GET_POSTS;
			break;
		case 1:
			query = GET_POSTS_FOR_FEEDBACK;
			break;
		case 2:
			query = GET_POSTS_TOP3;
			break;
		case 3:
			query = GET_POSTS_BOTTOM3;
			break;
		default:
			query = GET_POSTS_FOR_FEEDBACK;
			break;
		}

		List<Feed> feeds = new ArrayList<Feed>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			long entityid = getEntityId(connection, entity);
			connection = daoFactory.getConnection();
			Object[] values = { entityid };
			preparedStatement = prepareStatement(connection, query, false,
					values);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				Feed feed = new Feed();

				feed.setFeedId(resultSet.getString(1));

				feed.setFeedData(resultSet.getString(2));
				Opinion opinion = new Opinion();
				Sentiment opinionOrientation = new Sentiment();
				opinionOrientation.setPolarity(resultSet.getDouble(5));
				opinionOrientation.setSentiment(SENTIMENTENUM
						.fromInteger(resultSet.getInt(4)));
				// if (resultSet.getDouble(5) != 0.0)
				opinionOrientation.setDetailedSentiment(DetailedSentiment
						.getSentimentFromDouble(resultSet.getDouble(5)));
				opinion.setOpinionOrientation(opinionOrientation);
				if (null != opinion) {
					opinion.setOpinionHolder("");
				}

				feed.setOpinion(opinion);

				feeds.add(feed);
			}
		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		return feeds;
	}

	@Override
	public List<Feed> getFeedsFromMongo(String entity, String subProduct,
			int queryToUse) {

		String query = null;
		switch (queryToUse) {
		case 0:
			query = GET_POSTS_FROM_SENTIMENT_TOP3;
			break;
		case 1:
			query = GET_POSTS_FROM_SENTIMENT_BOTTOM3;
			break;
		default:
			query = GET_POSTS_FROM_SENTIMENT;
			break;
		}

		List<Feed> feeds = new ArrayList<Feed>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			long entityid = getEntityId(connection, entity);
			connection = daoFactory.getConnection();
			Object[] values = { entityid, subProduct };
			preparedStatement = prepareStatement(connection, query, false,
					values);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				Feed feed = new Feed();
				feed.setFeedId(resultSet.getString(1));

				DBObject obj = MongoConnector.getFeedsById(feed.getFeedId());
				if (null != obj) {
					feed.setFeedData((String) obj.get("content"));
				}

				Opinion opinion = new Opinion();
				Sentiment opinionOrientation = new Sentiment();
				opinionOrientation.setPolarity(resultSet.getDouble(3));
				opinionOrientation.setSentiment(SENTIMENTENUM
						.fromInteger(resultSet.getInt(2)));
				opinionOrientation.setDetailedSentiment(DetailedSentiment
						.getSentimentFromDouble(resultSet.getDouble(3)));
				opinion.setOpinionOrientation(opinionOrientation);
				if (null != opinion) {
					opinion.setOpinionHolder("");
				}
				feed.setOpinion(opinion);
				feeds.add(feed);
			}
		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		return feeds;

	}

	@Override
	public void storeNounAdjScore(List<NounAdjScore> nasList) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(INSERT_NOUN);
			for (NounAdjScore nas : nasList) {
				Object[] values = { nas.getNoun(), nas.getAdj(),
						nas.getScore(),
						toSqlDate(new Date(System.currentTimeMillis())) };
				setValues(preparedStatement, values);
				preparedStatement.addBatch();
			}
			preparedStatement.executeBatch();
		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement);

		}
	}

	@Override
	public Set<String> getNounStopword() {

		Set<String> nounStopwords = new HashSet<String>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(GET_NOUN_STOPWORDS);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				nounStopwords.add(resultSet.getString(1));
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		return nounStopwords;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.prd.rmsentiment.database.LexiconDAOInterface#getDimensionsByEntity()
	 */
	@Override
	public Map<Integer, String> getDimensionsByEntity(String entity) {
		LOG.trace("Method: getDimensionsByEntity called.");
		Map<Integer, String> idDimensionMap = null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = daoFactory.getConnection();
			Object[] values = { entity };
			preparedStatement = prepareStatement(connection,
					GET_DIMENSION_FOR_ENTITY, true, values);
			resultSet = preparedStatement.executeQuery();
			if (null != resultSet) {
				idDimensionMap = new HashMap<Integer, String>();
			}
			while (resultSet.next()) {
				idDimensionMap.put(resultSet.getInt(1), resultSet.getString(2));
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}

		LOG.trace("Method: getDimensionsByEntity finished.");
		return idDimensionMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.prd.rmsentiment.database.LexiconDAOInterface#getDimensionWordIdByEntity
	 * (java.lang.String)
	 */
	@Override
	public IdWordList getDimensionWordIdByProduct(String product) {
		LOG.trace("Method: getDimensionWordIdByEntity called.");
		IdWordList idWordList = null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = daoFactory.getConnection();
			Object[] values = { product };
			preparedStatement = prepareStatement(connection,
					GET_WORD_DIMENSION_FOR_ENTITY, true, values);
			resultSet = preparedStatement.executeQuery();
			if (null != resultSet) {
				idWordList = new IdWordList();
			}
			while (resultSet.next()) {
				idWordList.addToId(resultSet.getInt(1));
				idWordList.addToWord(resultSet.getString(2));
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}

		LOG.trace("Method: getDimensionWordIdByEntity finished.");
		return idWordList;
	}

	@Override
	public List<Feed> fillPostsFromStaging() {
		List<Feed> feeds = new ArrayList<Feed>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			connection = daoFactory.getConnection();
			preparedStatement = prepareStatement(connection, GET_POSTS_STAGING,
					false);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				Feed feed = new Feed();

				feed.setFeedId(resultSet.getString(1));
				feed.setEntityId(resultSet.getLong(2));
				feed.setSubProductId(resultSet.getLong(3));
				feed.setUserName(resultSet.getString(4));
				feed.setUserLocation(resultSet.getString(5));
				feed.setGender(resultSet.getString(6));
				feed.setAge(resultSet.getString(7));
				feed.setFeedDate(resultSet.getDate(8));
				feed.setCreatedDate(resultSet.getDate(9));
				feed.setFeedRating(resultSet.getString(10));
				feed.setSourceId(resultSet.getInt(11));
				feed.setFeedData(resultSet.getString(12));

				// getDimensionMap For products
				if (null == QueryLevelCache.idWordList
						|| !QueryLevelCache.idWordList.getProductName()
								.equalsIgnoreCase(resultSet.getString(13))) {
					// System.out.println("Call-For-Product "
					// + resultSet.getString(13));
					/*
					 * QueryLevelCache.idWordList = CacheManager
					 * .getDimensionMapForEntity(resultSet.getString(13));
					 */
					CacheManager.setWordSubDimForSubroduct("menshoes");
					// Fill Product Entity Stopword list also in cache here..
					QueryLevelCache.productBrandStopwordList = getStopWordsForProduct(
							feed.getSubProductId(), resultSet.getString(13),
							connection);
					QueryLevelCache.productSynonyms = DAOFactory
							.getInstance(
									ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
							.getSentimentDAO()
							.getSynonymsBySubProductId(feed.getSubProductId());

				}

				Opinion opinion = new Opinion();
				opinion.setObject(resultSet.getString(13));
				feed.setOpinion(opinion);
				SentimentAnalysis.setOpinion(feed);
				opinion.setOpinionHolder(resultSet.getString(4));

				feeds.add(feed);
				if (feeds.size() == 200) {

					InsertThread insertThread = new InsertThread();
					insertThread.setPostStaging(true);
					insertThread.setFeeds(feeds);
					insertThread.setSearchText("");
					insertThread.setDbMethodToInvoke("storePostBatch");
					insertThread.start();
					feeds = new ArrayList<>();
				}
			}
			InsertThread insertThread = new InsertThread();
			insertThread.setPostStaging(true);
			insertThread.setFeeds(feeds);
			insertThread.setSearchText("");
			insertThread.setDbMethodToInvoke("storePostBatch");
			insertThread.start();

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		return feeds;
	}

	/**
	 * @param subProductId
	 * @return
	 */
	@Override
	public Map<String, List<String>> getStopWordsForProduct(long subProductId,
			String productName, Connection connection) {
		LOG.trace("Method: getStopWordsForProduct called.");
		Map<String, List<String>> productStopwordMap = new HashMap<String, List<String>>();
		ArrayList<String> productStopwordList = new ArrayList<String>();

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			if (connection == null) {
				connection = daoFactory.getConnection();
			}

			if (subProductId == 0) {

				String query = "%" + productName + "%";
				Object[] values = { query };

				preparedStatement = prepareStatement(connection,
						GET_STOPWORDS_FOR_PRODUCTNAME, false);
				setValues(preparedStatement, values);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					String stopWord = resultSet.getString(1);
					if (!productStopwordList.contains(stopWord)) {
						productStopwordList.add(stopWord);
					}
					stopWord = resultSet.getString(2);
					if (!productStopwordList.contains(stopWord)) {
						productStopwordList.add(stopWord);
					}
				}

			} else {
				Object[] values = { subProductId };

				preparedStatement = prepareStatement(connection,
						GET_STOPWORDS_FOR_SUBPRODUCTID, false);
				setValues(preparedStatement, values);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					productStopwordList.add(resultSet.getString(1));
				}
			}
			if (productStopwordList.size() == 0) {
				productStopwordList.add(productName);
			}
			productStopwordMap.put(productName, productStopwordList);
		} catch (SQLException e) {

			LOG.error(
					"SQLException while performing operation in getStopWordsForProduct",
					e);
		}

		LOG.trace("Method: getStopWordsForProduct finished.");

		return productStopwordMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.prd.rmsentiment.database.LexiconDAOInterface#getStockPatterns()
	 */
	@Override
	public StockPatternEntity getStockPatterns() {
		LOG.trace("Method: getStockPatterns called.");
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		StockPatternEntity stockPatterns = new StockPatternEntity();

		List<String> patterns = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		List<String> valuesFor = new ArrayList<String>();

		try {
			connection = daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(GET_STOCK_PATTERNS);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {

				patterns.add(resultSet.getString(1));
				values.add(resultSet.getString(2));
				if (resultSet.getString(1) != null) {
					valuesFor.add(resultSet.getString(3));
				} else {
					valuesFor.add("");
				}

			}

			stockPatterns.setPatterns(patterns);
			stockPatterns.setValues(values);
			stockPatterns.setValuesFor(valuesFor);

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}

		LOG.trace("Method: getStockPatterns finished.");
		return stockPatterns;
	}

	@Override
	public List<String> removeExistSynonymWord(List<String> words,
			Connection connection)

	{

		List<String> filterWords = words;
		if (null != filterWords && filterWords.size() != 0) {
			try {
				if (null == connection) {
					connection = daoFactory.getConnection();
				}
				// filterWords = words;
				PreparedStatement psForSynonyms = null;
				ResultSet resultSet = null;
				String query = String.format(SELECT_EXISTS_SYNONYMS_WORDS,
						preparePlaceHolders(words.size()));
				psForSynonyms = connection.prepareStatement(query);
				Object[] value = words.toArray(new String[words.size()]);
				setValues(psForSynonyms, value);
				resultSet = psForSynonyms.executeQuery();
				while (resultSet.next()) {
					filterWords.remove(resultSet.getString(1));
				}
			} catch (SQLException e) {

				LOG.error(
						"SQLException while performing operation in removeExistSynonymWord",
						e);
			}
		}
		return filterWords;
	}

	@Override
	public void storePatterns(List<WordsPerSentence> wordsPerSentenceList) {
		Connection connection = null;
		PreparedStatement psForWordnotpresent = null;
		PreparedStatement psForPatterns = null;
		PreparedStatement psForWordpresent = null;

		try {
			connection = daoFactory.getConnection();
			psForPatterns = connection.prepareStatement(INSERT_PATTERN_SCORE);
			psForWordnotpresent = connection
					.prepareStatement(INSERT_WORD_NOT_PRESENT);
			psForWordpresent = connection.prepareStatement(INSERT_WORD_USED);

			for (WordsPerSentence wordsPerSentence : wordsPerSentenceList) {

				for (List<String> wordsAroundList : wordsPerSentence
						.getWordsAroundList()) {

					Object[] valuesForPatterns = wordsAroundList
							.toArray(new String[wordsAroundList.size()]);
					Object[] score = { wordsPerSentence.getSentenceScore(),
							wordsPerSentence.getOverallScore() };

					valuesForPatterns = ArrayUtils.addAll(score,
							valuesForPatterns); // concate values

					setValues(psForPatterns, valuesForPatterns);
					LOG.debug("query-1 " + psForPatterns.toString());
					psForPatterns.addBatch(); // insert patterns into
												// patternscore table

				}
				for (String word : wordsPerSentence.getWordsNotPresent()) {
					Object[] valuesWordNotPresent = { word,
							wordsPerSentence.getOverallScore(),
							wordsPerSentence.getSentenceScore() };
					setValues(psForWordnotpresent, valuesWordNotPresent);
					LOG.debug("query-2 " + psForWordnotpresent.toString());
					psForWordnotpresent.addBatch(); // insert patterns into
													// wordnotpresent table
				}

				for (String word : wordsPerSentence.getWordsPresent()) {
					Object[] valuesForWordsPresent = { word,
							wordsPerSentence.getSentenceScore() };
					setValues(psForWordpresent, valuesForWordsPresent);
					psForWordpresent.addBatch();
				}

			}
			psForPatterns.executeBatch();
			psForWordnotpresent.executeBatch();
			psForWordpresent.executeBatch();

		} catch (SQLException e) {
			LOG.error(
					"Error Occured while storing WordsNotPresent, Patterns And WordsPresent. ",
					e);
			throw new DAOException(e);
		} finally {
			close(connection);
			close(psForPatterns);
			close(psForWordnotpresent);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.prd.rmsentiment.database.LexiconDAOInterface#storeSentiwordPatterns
	 * (java.util.List)
	 */
	@Override
	public void storeSentiwordPatterns(List<Sentiword> list) {
		LOG.trace("Method: storeSentiwordPatterns called.");
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = daoFactory.getConnection();
			preparedStatement = connection
					.prepareStatement(INSERT_SENTIWORD_PATTERN);
			int count = 0;
			for (Sentiword senti : list) {

				++count;
				preparedStatement.setString(1, senti.getPOS());
				preparedStatement.setLong(2, senti.getId());
				preparedStatement.setDouble(3, senti.getPosscore());
				preparedStatement.setDouble(4, senti.getNegscore());
				preparedStatement.setString(5, senti.getWord());
				preparedStatement.setString(6, senti.getGlossary());
				preparedStatement.setString(7, senti.getPattern());

				preparedStatement.addBatch();
				if (count % 1000 == 0) {
					count = 0;
					preparedStatement.executeBatch();
				}
			}
			preparedStatement.executeBatch();
		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection);
			close(preparedStatement);

		}
		LOG.trace("Method: storeSentiwordPatterns finished.");
	}

	public Map<String, Integer> getEntityIdMap() {
		Map<String, Integer> ids = new HashMap<String, Integer>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(GET_ENTITY_IDS);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				ids.put(resultSet.getString(2), resultSet.getInt(1));
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		return ids;
	}

	public Map<String, Integer> getSubProductIdMap() {
		Map<String, Integer> ids = new HashMap<String, Integer>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(GET_SUBPRODUCT_IDS);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				ids.put(resultSet.getString(2), resultSet.getInt(1));
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		return ids;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.raremile.prd.inferlytics.database.LexiconDAOInterface#
	 * getSubProductsForAnEntity(java.lang.String) Retrieves all the subproducts
	 * for a given Entity.
	 */
	public List<CommentsHtml> getSubProductsForAnEntity(String entity) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<CommentsHtml> subProducts = new ArrayList<>();
		try {
			connection = daoFactory.getConnection();
			Object[] values = { entity };
			preparedStatement = prepareStatement(connection,
					GET_SUBPRODUCT_FOR_ENTITY, false, values);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				CommentsHtml subProduct = new CommentsHtml();
				subProduct.setTagName("option");
				subProduct.setValue(Integer.toString(IdMap
						.getSubproductId(resultSet.getString(1))));
				subProduct.setTextContent(resultSet.getString(1));
				subProducts.add(subProduct);
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		return subProducts;
	}

	@Override
	public UsecaseDetails getDetailsByEntitySubProduct(String entity,
			String subProduct) {
        LOG.trace("Method: getDetailsByEntitySubProduct called.");
        UsecaseDetails ucd = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = daoFactory.getConnection();
            Object[] values = { entity,subProduct };
            preparedStatement = prepareStatement(connection, GET_USECASE_DETAILS, false,
                    values);
            
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                ucd = new UsecaseDetails();
                ucd.setEntityId( resultSet.getInt( "EntityId" ) );
                ucd.setEntityName( entity );
                ucd.setProductId( resultSet.getInt( "ProductId" ) );
                ucd.setProductName( resultSet.getString( "ProductName" ) );
                ucd.setSubProductId( resultSet.getInt( "SubProductId" ) );
                ucd.setSubProductName( subProduct );
            }

        } catch (SQLException e) {
            LOG.error("", e);
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }
        
        LOG.trace("Method: getDetailsByEntitySubProduct finished.");
        return ucd;
    }

}
