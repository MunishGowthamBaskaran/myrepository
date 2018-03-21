/**
 *  * Copyright (c) 2013 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.database;

import static com.raremile.prd.inferlytics.database.DAOUtil.close;
import static com.raremile.prd.inferlytics.database.DAOUtil.prepareStatement;
import static com.raremile.prd.inferlytics.database.DAOUtil.setValues;
import static com.raremile.prd.inferlytics.database.DAOUtil.toSqlDate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.raremile.prd.inferlytics.database.entity.EntityDimension;
import com.raremile.prd.inferlytics.entity.DetailedSentiment;
import com.raremile.prd.inferlytics.entity.Features;
import com.raremile.prd.inferlytics.entity.Feed;
import com.raremile.prd.inferlytics.entity.HTML;
import com.raremile.prd.inferlytics.entity.Output;
import com.raremile.prd.inferlytics.entity.Serie;
import com.raremile.prd.inferlytics.exception.CriticalException;
import com.raremile.prd.inferlytics.exception.DAOException;
	
/**
 * @author Praty
 * @created Apr 8, 2013
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class SentimentDAOImpl implements SentimentDAOInterface {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(SentimentDAOImpl.class);

	private static final String SELECT_PIE = " SELECT  ps.DETAILED_SENTIMENT AS SENTIMENT, COUNT(ps.POST_ID) COUNT_TOTAL "
			+ " FROM  post_sentiment ps  JOIN posts p ON p.FeedId=ps.POST_ID "
			+ " JOIN entity e ON ps.ENTITY_ID = e.EntityId "
			+ " JOIN subproducts sp ON sp.SubProductId=p.SubProductId "
			+ " WHERE ps.ENTITY_ID = ? AND sp.SubProductName=?  "
			+ " GROUP BY  ps.DETAILED_SENTIMENT ";
	private static final String OUTPUT_QUERY = " SELECT YEAR(p.feeddate) AS  YEAR, ps.DETAILED_SENTIMENT AS SENTIMENT, "
			+ " COUNT(ps.DETAILED_SENTIMENT) COUNT FROM posts p "
			+ " LEFT OUTER JOIN subproducts spd ON spd.SubProductId = p.SubProductId "
			+ " LEFT OUTER JOIN post_sentiment ps ON p.feedid = ps.POST_ID "
			+ " WHERE ps.ENTITY_ID = ? AND spd.SubProductName=? "
			+ " GROUP BY yEAR(p.feeddate), ps.DETAILED_SENTIMENT ORDER BY year ";

	/*
	 * " SELECT DATE(p.date) DATE, ps.DETAILED_SENTIMENT AS SENTIMENT, COUNT(ps.DETAILED_SENTIMENT) COUNT"
	 * + " FROM posts p " +
	 * " LEFT OUTER JOIN post_sentiment ps ON p.id = ps.POST_ID " +
	 * " LEFT OUTER JOIN entity e ON ps.ENTITY_ID = e.id   " +
	 * "WHERE ps.ENTITY_ID = ? GROUP BY DATE(p.date), ps.DETAILED_SENTIMENT  LIMIT 0, 20"
	 * ;
	 */

	private static final String INSERT_POSTS_STAGING = "INSERT  INTO  `posts_staging`(FeedId, EntityId, SubProductId, UserName, UserLocation, Gender, Age, FeedDate, CreatedDate, FeedRating, SourceId, FeedData) "
			+ "  VALUES(?          ,?           ,?,        ?,            ?,      ?,   ?,        ?,           ?,          ?,        ?,         ?)";

	private static final String SELECT_POSITIVE_NOUN = "SELECT COUNT(n.NounId) WEIGHT,n.NounWord FEATURE,AdjScore SCORE"
			+ "   FROM noun n 	JOIN subproducts sp ON sp.SubProductID=n.SubproductID"
			+ "	WHERE n.EntityId=? AND sp.SubProductName=? AND AdjScore>0 	"
			+ "GROUP BY FEATURE HAVING COUNT(FEATURE)> 1 	ORDER BY WEIGHT DESC, SCORE DESC LIMIT 0,100";

	// LIMIT 0,50";
	private static final String SELECT_NEGATIVE_NOUN = "SELECT COUNT(n.NounId) WEIGHT,n.NounWord FEATURE,AdjScore SCORE "
			+ "  FROM noun n 	JOIN subproducts sp ON sp.SubProductID=n.SubproductID	WHERE n.EntityId=? "
			+ "AND sp.SubProductName=? AND AdjScore<0 	GROUP BY FEATURE HAVING COUNT(FEATURE)> 1 	ORDER BY WEIGHT DESC, SCORE DESC LIMIT 0,100";
	// LIMIT 0,50";
	private static final String SELECT_DIMENSION_SUNBURST_FOR_ENTITY = " SELECT DIMENSION,SubDimension,DetailedSentiment, COUNT(*) AS CountSentiment ,PostId, Word ,EntityId,SubProductName ,Score      "
			+ " FROM (                                                                                                                         "
			+ " SELECT DIMENSION,SubDimension,DetailedSentiment,PostId, Word,f.EntityId,SubProductName,Score                                   "
			+ " FROM featureadj f  JOIN posts p ON p.FeedId=f.PostId                                                                           "
			+ " JOIN v_subproduct_dimension_subdimension_word_details v                                                                        "
			+ " ON f.AdjWordId = v.Wordid AND  p.SubProductId=v.SubproductID                                                                   "
			+ "                                                                                                                                "
			+ " UNION                                                                                                                          "
			+ "                                                                                                                                "
			+ " SELECT 'Relations' AS DIMENSION  , r.relationName AS SubDimension,ps.DETAILED_SENTIMENT,ps.Post_Id , '' AS Word                "
			+ " ,p.EntityId,sp.SubProductName ,ps.Score FROM post_sentiment ps  JOIN posts p  ON ( p.FeedId=ps.Post_Id  AND p.RelationId <>0 ) "
			+ " JOIN relations r ON r.RelationID=p.RelationId                                                                                  "
			+ " JOIN subproducts sp ON sp.SubProductId=p.SubProductId                                                                          "
			+ "                                                                                                                                "
			+ " ) AllData                                                                                                                      "
			+ " WHERE  EntityId= ?  AND  SubProductName=?                                                                         "
			+ " GROUP BY DIMENSION,SubDimension,DetailedSentiment,PostId,Word                                                                  "
			+ " ORDER BY DIMENSION,SubDimension,DetailedSentiment,Score ASC                                                                    ";
	/*
	 * " SELECT DIMENSION,SubDimension,DetailedSentiment,COUNT(*) CountSentiment ,PostId, Word"
	 * + " FROM FeatureAdj f JOIN posts p ON p.FeedId=f.PostId  " +
	 * " JOIN v_subproduct_Dimension_Subdimension_Word_Details v " +
	 * " ON f.AdjWordId = v.Wordid AND  p.SubProductId=v.SubproductID" +
	 * " AND f.EntityId= ?  AND  v.SubProductName= ? " +
	 * " GROUP BY DIMENSION,SubDimension,DetailedSentiment,PostId,Word  " +
	 * " ORDER BY DIMENSION,SubDimension,DetailedSentiment,Score ASC";
	 */

	private static final String INSERT_PRODUCT = " INSERT   INTO `subproducts` (`SubProductName`) VALUES (?) ON DUPLICATE KEY UPDATE `SubProductId`=LAST_INSERT_ID(`SubProductId`)  ";

	private static final String INSERT_SOURCE = " INSERT   INTO `source` (`SourceName`) VALUES (?) ON DUPLICATE KEY UPDATE `SouceId`=LAST_INSERT_ID(`SouceId`) ";

	// Get all entities from Entity Table

	private static final String GET_ENTITIES_SUB_PRODUCT_MAP = " SELECT e.entityID,e.entity,sp.SubProductId,sp.SubProductName FROM products_entity_mapping epm "
			+ " JOIN entity e ON epm.entityId=e.entityID                                                      "
			+ " LEFT OUTER JOIN subproducts sp ON epm.ProductID=sp.ProductID AND                              "
			+ " sp.SubProductId IN (SELECT DISTINCT subproductID FROM posts)                                  ";

	// private static final String GET_PRODUCTS_BY_ENTITY_ID =
	// "SELECT p.ProductName FROM products_entity_mapping pem JOIN products p ON pem.ProductID=p.ProductID WHERE pem.EntityID=?";

	private static final String GET_SYNONYMS_FOR_SUBPRODUCT = "SELECT p1.word as word,p2.word as synonymword FROM productsynonyms p1 "
			+ " join productsynonyms p2 on p1.SynonymId = p2.Id "
			+ " where p1.SubProductId = ? ";
	
	private static final String GET_SYNONYMS= "SELECT p1.word as word,p2.word as synonymword FROM synonyms p1 "
			+ " join synonyms p2 on p1.SynonymId = p2.Id ";

	private static final String GET_WORD_SUBDIMENSION_FOR_SUBPRODUCT = "	SELECT Word ,SubDimension "
			+ " FROM   v_products_subdimensions_words_supproducts_detail "
			+ " WHERE SubProductName = ? "
			+ " GROUP BY Word,SubDImension "
			+ " ORDER BY Word,SubDimension ";

	private static final String GET_WORD_SUBDIMENSION = "	SELECT Word ,SubDimension "
			+ " FROM   v_products_subdimensions_words_supproducts_detail "
			+ " GROUP BY Word,SubDImension "
			+ " ORDER BY Word,SubDimension ";

	// Vars
	// ---------------------------------------------------------------------------------------

	private final DAOFactory daoFactory;

	// Constructors
	// -------------------------------------------------------------------------------

	/**
	 * Construct a Sentiment DAO for the given DAOFactory. Package private so
	 * that it can be constructed inside the DAO package only.
	 * 
	 * @param daoFactory
	 *            The DAOFactory to construct this User DAO for.
	 */
	SentimentDAOImpl(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.prd.rmsentiment.database.SentimentDAOInterface#getMultipleData(java
	 * .lang.String)
	 */
	@Override
	public List<Output> getMultipleData(String brand, String subproductName) {
		LOG.trace("Method: getPieChartData called.");
		List<Output> outputList = new ArrayList<Output>();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = daoFactory.getConnection();
			long entityId = daoFactory.getLexiconDAO().getEntityId(connection,
					brand);
			Object[] values = { entityId, subproductName };
			preparedStatement = prepareStatement(connection, OUTPUT_QUERY,
					true, values);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Output out = new Output();
				out.setDate(resultSet.getString("YEAR"));
				out.setSentiment(DetailedSentiment.getSentimentFromInteger(
						resultSet.getInt("SENTIMENT")).name());
				out.setCount(resultSet.getInt("COUNT"));
				outputList.add(out);
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		LOG.trace("Method: getPieChartData finished.");
		return outputList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.prd.rmsentiment.database.SentimentDAOInterface#getPieChartData(java
	 * .lang.String)
	 */
	@Override
	public List<Serie> getPieChartData(String brand, String subproductName) {
		List<Serie> serieList = new ArrayList<Serie>();
		LOG.trace("Method: getPieChartData called.");
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = daoFactory.getConnection();
			long entityId = daoFactory.getLexiconDAO().getEntityId(connection,
					brand);

			Object[] values = { entityId, subproductName };
			preparedStatement = prepareStatement(connection, SELECT_PIE, true,
					values);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Serie data = new Serie(DetailedSentiment
						.getSentimentFromInteger(resultSet.getInt("SENTIMENT"))
						.name(), resultSet.getInt("COUNT_TOTAL"));
				serieList.add(data);
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}

		LOG.trace("Method: getPieChartData finished.");
		return serieList;
	}

	@Override
	public List<Features> getPositiveFeatures(String brand,
			String subproductName) {

		List<Features> featureDataList = new ArrayList<Features>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;

		try {
			connection = daoFactory.getConnection();
			long entityId = daoFactory.getLexiconDAO().getEntityId(connection,
					brand);
			Object[] values = { entityId, subproductName };
			preparedStatement = prepareStatement(connection,
					SELECT_POSITIVE_NOUN, true, values);

			rs = preparedStatement.executeQuery();
			while (rs.next()) {

				Features feature = new Features();
				feature.setText(rs.getString("FEATURE"));
				feature.setWeight(rs.getInt("WEIGHT"));
				HTML html = new HTML();
				html.setClassName("positive");
				html.setTitle(String.valueOf(rs.getInt("WEIGHT")));
				feature.setHtml(html);
				featureDataList.add(feature);
			}
		} catch (SQLException e) {
			LOG.error("SQLException while fetching the tweet. "
					+ e.getMessage());
			throw new CriticalException(
					"SQLException while fetching the tweet. " + e.getMessage());
		} finally {
			close(connection, preparedStatement, rs);
		}
		return featureDataList;
	}

	@Override
	public List<Features> getNegativeFeatures(String brand,
			String subproductName) {

		List<Features> featureDataList = new ArrayList<Features>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;

		try {
			connection = daoFactory.getConnection();
			long entityId = daoFactory.getLexiconDAO().getEntityId(connection,
					brand);
			Object[] values = { entityId, subproductName };
			preparedStatement = prepareStatement(connection,
					SELECT_NEGATIVE_NOUN, true, values);

			rs = preparedStatement.executeQuery();
			while (rs.next()) {

				Features feature = new Features();
				feature.setText(rs.getString("FEATURE"));
				feature.setWeight(rs.getInt("WEIGHT"));
				HTML html = new HTML();
				html.setClassName("negative");
				html.setTitle(String.valueOf(rs.getInt("WEIGHT")));
				feature.setHtml(html);
				featureDataList.add(feature);
			}
		} catch (SQLException e) {
			LOG.error("SQLException while fetching the tweet. "
					+ e.getMessage());
			throw new CriticalException(
					"SQLException while fetching the tweet. " + e.getMessage());
		} finally {
			close(connection, preparedStatement, rs);
		}
		return featureDataList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.prd.rmsentiment.database.SentimentDAOInterface#getEntityDimension
	 * (java.lang.String)
	 */
	@Override
	public List<EntityDimension> getEntityDimension(String entity,
			String product) {
		LOG.trace("Method: getEntityDimension called.");
		List<EntityDimension> entityDimensionList = null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;

		try {
			connection = daoFactory.getConnection();
			long entityId = daoFactory.getLexiconDAO().getEntityId(connection,
					entity);

			Object[] values = { entityId, product };
			preparedStatement = prepareStatement(connection,
					SELECT_DIMENSION_SUNBURST_FOR_ENTITY, true, values);

			rs = preparedStatement.executeQuery();
			EntityDimension entityDimension = null;
			if (rs != null) {
				entityDimensionList = new ArrayList<EntityDimension>();
			}
			while (rs.next()) {

				entityDimension = new EntityDimension();
				entityDimension.setDimension(rs.getString(1));
				entityDimension.setSubDimension(rs.getString(2));
				entityDimension.setDetailedSentiment(rs.getInt(3));
				entityDimension.setCount(rs.getInt(4));
				entityDimension.setPostId("" + rs.getString(5));
				entityDimension.setWord(rs.getString(6));
				entityDimensionList.add(entityDimension);
			}
		} catch (SQLException e) {
			LOG.error("SQLException while fetching the tweet. "
					+ e.getMessage());
			throw new CriticalException(
					"SQLException while fetching the tweet. " + e.getMessage());
		} finally {
			close(connection, preparedStatement, rs);
		}
		LOG.trace("Method: getEntityDimension finished.");
		return entityDimensionList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.prd.rmsentiment.database.SentimentDAOInterface#storePostsIntoStaging
	 * (java.util.List, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void storePostsIntoStaging(List<Feed> feeds, String brandName,
			String productName, String sourceName) {
		LOG.trace("Method: storePostsIntoStaging called.");
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = daoFactory.getConnection();
			/*
			 * long entityId = DAOFactory
			 * .getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
			 * .getLexiconDAO().getEntityId(connection, brandName); long
			 * productId = getId(connection, productName, INSERT_PRODUCT); long
			 * sourceId = getId(connection, sourceName, INSERT_SOURCE);
			 */
			preparedStatement = connection
					.prepareStatement(INSERT_POSTS_STAGING);
			for (Feed feed : feeds) {
				// FeedId, EntityId, ProductId, UserName, UserLocation, Gender,
				// Age, FeedDate, CreatedDate, FeedRating, SourceId, FeedData
				String uuid = UUID.randomUUID().toString().replace("-", "");
				Object[] values = { feed.getFeedId(), feed.getBrandId(),
						feed.getSubProductId(), feed.getUserName(),
						feed.getUserLocation(), feed.getGender(),
						feed.getAge(), toSqlDate(feed.getFeedDate()),
						toSqlDate(new Date()), feed.getFeedRating(),
						feed.getSourceId(), feed.getFeedData() };
				setValues(preparedStatement, values);
				preparedStatement.addBatch();

			}

			preparedStatement.executeBatch();

		} catch (SQLException e) {
			LOG.error("SQLException while storing the feed. " + e.getMessage());
			throw new CriticalException("SQLException while storing the feed. "
					+ e.getMessage());
		} finally {
			close(connection, preparedStatement);
		}

		LOG.trace("Method: storePostsIntoStaging finished.");
	}

	private long getId(Connection connection, String name, final String query) {
		long entityId = 0;

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		boolean closeConnection = false;
		try {
			if (null == connection) {
				connection = daoFactory.getConnection();
				closeConnection = true;
			}
			Object[] values = { name };
			preparedStatement = prepareStatement(connection, query, true,
					values);
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
	public Map<String, List<String>> getAllProductToRespEntity() {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		String entity = null;
		String product = null;

		List<String> subproducts = null;

		Map<String, List<String>> entityProdMap = new HashMap<String, List<String>>();

		try {
			connection = daoFactory.getConnection();
			preparedStatement = connection
					.prepareStatement(GET_ENTITIES_SUB_PRODUCT_MAP);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				entity = resultSet.getString(2);
				product = resultSet.getString(4);
				if (entityProdMap.containsKey(entity)) {

					subproducts = entityProdMap.get(entity);
					subproducts.add(product);
					entityProdMap.put(entity, subproducts);
				} else {
					subproducts = new ArrayList<String>();
					subproducts.add(product);
					entityProdMap.put(entity, subproducts);

				}

			}
		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}

		return entityProdMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.prd.rmsentiment.database.SentimentDAOInterface#getSynonymsBySubProductId
	 * ()
	 */
	@Override
	public Map<String, String> getSynonymsBySubProductId(long subProductId) {
		LOG.trace("Method: getSynonymsBySubProductId called.");
		Map<String, String> productSynonymMap = null;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = daoFactory.getConnection();

			Object[] values = { subProductId };
			preparedStatement = prepareStatement(connection,
					GET_SYNONYMS_FOR_SUBPRODUCT, true, values);
			resultSet = preparedStatement.executeQuery();
			if (resultSet != null) {
				productSynonymMap = new HashMap<>();
			}
			while (resultSet.next()) {
				productSynonymMap.put(resultSet.getString(1),
						resultSet.getString(2));
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}

		LOG.trace("Method: getSynonymsBySubProductId finished.");
		return productSynonymMap;
	}

	
	public Map<String, String> getSynonyms() {
		LOG.trace("Method: getSynonymsBySubProductId called.");
		Map<String, String> productSynonymMap = null;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = daoFactory.getConnection();
			
		
			preparedStatement =connection.prepareStatement(GET_SYNONYMS);
			resultSet = preparedStatement.executeQuery();
			if (resultSet != null) {
				productSynonymMap = new HashMap<>();
			}
			while (resultSet.next()) {
				productSynonymMap.put(resultSet.getString(1),
						resultSet.getString(2));
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}

		LOG.trace("Method: getSynonymsBySubProductId finished.");
		return productSynonymMap;
	}

	@Override
	public Map<String, List<String>> getWordSubDimensionForSubProduct(
			String subProduct) {
		LOG.trace("Entered method getWordSubDimensionForSubProduct");
		Map<String, List<String>> wordSubDimMap = null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = daoFactory.getConnection();

			if(null != subProduct ){
				Object[] values = { subProduct };
				preparedStatement = prepareStatement(connection,
						GET_WORD_SUBDIMENSION_FOR_SUBPRODUCT, true, values);
	}else preparedStatement = connection.prepareStatement(GET_WORD_SUBDIMENSION);
			resultSet = preparedStatement.executeQuery();
			if (resultSet != null) {
				wordSubDimMap = new HashMap<>();
			}
			while (resultSet.next()) {
				String word = resultSet.getString(1);
				if(wordSubDimMap.containsKey(word)){
					wordSubDimMap.get(word).add(resultSet.getString(2));
				}else{
					List<String> subDim = new ArrayList<>();
					subDim.add(resultSet.getString(2));
				wordSubDimMap.put(word,
						subDim);
				}
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		
		LOG.trace("Leaving method getWordSubDimensionForSubProduct");
		return wordSubDimMap;
	}

}
