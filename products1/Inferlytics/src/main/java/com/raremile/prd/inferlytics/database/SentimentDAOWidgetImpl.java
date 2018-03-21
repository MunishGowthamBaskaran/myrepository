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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.entity.EntityDimension;
import com.raremile.prd.inferlytics.entity.ProdCommentAnalticInfo;
import com.raremile.prd.inferlytics.exception.CriticalException;

/**
 * @author pratyusha
 * @created 26-Jul-2013
 * 
 *          This class has all Database calls for widget operations.
 * 
 */
public class SentimentDAOWidgetImpl implements SentimentWidgetDAOInterface {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(SentimentDAOWidgetImpl.class);
	private static final String SELECT_FEATURES_FOR_ENTITY_SUBPRODUCT_POS = " SELECT SubDimension,DetailedSentiment, COUNT(*) AS CountSentiment ,PostId, Word, Score "
			+ " FROM ( "
			+ " SELECT SubDimension,DetailedSentiment,PostId, Word,f.EntityId,SubProductName,Score "
			+ " FROM featureadj f  JOIN posts p ON p.FeedId=f.PostId "
			+ " JOIN v_products_subdimensions_words_supproducts_detail v  "
			+ "ON f.AdjWordId = v.Wordid AND  p.SubProductId=v.SubProductId "

			+ "UNION "

			+ "SELECT  r.relationName AS SubDimension,ps.DETAILED_SENTIMENT,ps.Post_Id , '' AS Word  "
			+ ",p.EntityId,sp.SubProductName ,ps.Score FROM post_sentiment ps  JOIN posts p  ON "
			+ "( p.FeedId=ps.Post_Id  AND p.RelationId <>0 ) "
			+ " JOIN relations r ON r.RelationID=p.RelationId "
			+ " JOIN subproducts sp ON sp.SubProductId=p.SubProductId "

			+ " ) AllData    "
			+ " WHERE  EntityId= (select EntityId from entity where entity = ?)  AND  SubProductName= ? AND SCORE>0 "
			+ "  GROUP BY SubDimension,Word,PostId "
			+ "  ORDER BY SubDimension,Word,Score desc ";

	private static final String SELECT_FEATURES_FOR_ENTITY_SUBPRODUCT_NEG = " SELECT SubDimension,DetailedSentiment, COUNT(*) AS CountSentiment ,PostId, Word, Score "
			+ " FROM ( "
			+ " SELECT SubDimension,DetailedSentiment,PostId, Word,f.EntityId,SubProductName,Score "
			+ " FROM featureadj f  JOIN posts p ON p.FeedId=f.PostId "
			+ " JOIN v_products_subdimensions_words_supproducts_detail v  "
			+ "ON f.AdjWordId = v.Wordid AND  p.SubProductId=v.SubProductId "

			+ "UNION "

			+ "SELECT  r.relationName AS SubDimension,ps.DETAILED_SENTIMENT,ps.Post_Id , '' AS Word  "
			+ ",p.EntityId,sp.SubProductName ,ps.Score FROM post_sentiment ps  JOIN posts p  ON "
			+ "( p.FeedId=ps.Post_Id  AND p.RelationId <>0 ) "
			+ " JOIN relations r ON r.RelationID=p.RelationId "
			+ " JOIN subproducts sp ON sp.SubProductId=p.SubProductId "

			+ " ) AllData    "
			+ " WHERE  EntityId= (select EntityId from entity where entity = ?)  AND  SubProductName= ? AND SCORE<0 "
			+ "  GROUP BY SubDimension,Word,PostId "
			+ "  ORDER BY SubDimension,Word,Score asc ";

	private static final String SELECT_KEYWORDS_FOR_ENTITY_SUBPRODUCT = " SELECT PostId,WEIGHT, FEATURE, SCORE  "
			+ " FROM  "
			+ " ( "
			+ " SELECT n.PostId AS PostId,COUNT(n.NounId) WEIGHT,TRIM(n.NounWord) AS FEATURE,AdjScore SCORE  "
			+ " FROM noun n 	 "
			+ " JOIN subproducts sp ON sp.SubProductID=n.SubproductID "
			+ " WHERE n.EntityId= (SELECT EntityId FROM entity WHERE entity = ?) "
			+ " AND sp.SubProductName= ? AND AdjScore > 0  "
			+ " GROUP BY n.NounWord ,PostId    "
			+ " HAVING COUNT(n.NounWord)> 1 "
			+ " ORDER BY WEIGHT DESC, PostId,SCORE DESC LIMIT 0,100 "
			+ " ) AS positiveQuery "
			+ " UNION ALL "
			+ " SELECT PostId,WEIGHT, FEATURE, SCORE FROM "
			+ " ( "
			+ " SELECT n.PostId AS PostId,COUNT(n.NounId) WEIGHT,TRIM(n.NounWord) FEATURE,AdjScore SCORE  "
			+ " FROM noun n 	 "
			+ " JOIN subproducts sp ON sp.SubProductID=n.SubproductID "
			+ " WHERE n.EntityId= (SELECT EntityId FROM entity WHERE entity = ?)  "
			+ " AND sp.SubProductName= ? AND AdjScore < 0  "
			+ " GROUP BY n.NounWord ,PostId    "
			+ " HAVING COUNT(n.NounWord)> 1  "
			+ " ORDER BY WEIGHT DESC,PostId, SCORE DESC LIMIT 0,100 "
			+ " ) AS negativeQuery "
			+ " GROUP BY PostId,FEATURE,WEIGHT,SCORE "
			+ " ORDER BY FEATURE,PostId,WEIGHT,SCORE ";

	private static final String INSERT_ANALYTIC_INFO = "INSERT INTO productcommentanalytics( Entity, SubProduct,Category, SubDimension, Word, ProductId, PostId, UserIP,ReviewType,CreatedDate) "
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, INET_ATON(?),?,?)";

	private final DAOFactory daoFactory;

	/**
	 * @param daoFactory
	 */
	public SentimentDAOWidgetImpl(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.prd.rmsentiment.database.SentimentWidgetDAOInterface#
	 * getFeaturesByEntitySubProduct()
	 */
	@Override
	public List<EntityDimension> getFeaturesByEntitySubProduct(String entity,
			String subProduct) {
		LOG.trace("Method: getFeaturesByEntitySubProduct called.");
		List<EntityDimension> entityDimList = null;
		Connection connection = null;
		PreparedStatement preparedStatementPos = null;
		PreparedStatement preparedStatementNeg = null;
		ResultSet rs = null;

		try {
			connection = daoFactory.getConnection();

			Object[] values = { entity, subProduct };
			preparedStatementPos = prepareStatement(connection,
					SELECT_FEATURES_FOR_ENTITY_SUBPRODUCT_POS, true, values);
			preparedStatementNeg = prepareStatement(connection,
					SELECT_FEATURES_FOR_ENTITY_SUBPRODUCT_NEG, true, values);

			rs = preparedStatementPos.executeQuery();
			EntityDimension entityDimension = null;
			if (rs != null) {
				entityDimList = new ArrayList<EntityDimension>();
			}
			while (rs.next()) {

				entityDimension = new EntityDimension();

				entityDimension.setSubDimension(rs.getString(1));
				entityDimension.setDetailedSentiment(rs.getInt(2));
				entityDimension.setCount(rs.getInt(3));
				entityDimension.setPostId("" + rs.getString(4));
				entityDimension.setWord(rs.getString(5));
				entityDimension.setScore(rs.getDouble(6));
				entityDimList.add(entityDimension);
			}
			rs = null;
			rs = preparedStatementNeg.executeQuery();

			while (rs.next()) {

				entityDimension = new EntityDimension();

				entityDimension.setSubDimension(rs.getString(1));
				entityDimension.setDetailedSentiment(rs.getInt(2));
				entityDimension.setCount(rs.getInt(3));
				entityDimension.setPostId("" + rs.getString(4));
				entityDimension.setWord(rs.getString(5));
				entityDimension.setScore(rs.getDouble(6));
				entityDimList.add(entityDimension);
			}
		} catch (SQLException e) {
			LOG.error("SQLException while fetching the tweet. "
					+ e.getMessage());
			throw new CriticalException(
					"SQLException while fetching the tweet. " + e.getMessage());
		} finally {
			close(connection, preparedStatementPos, rs);
			close(preparedStatementNeg);
		}

		LOG.trace("Method: getFeaturesByEntitySubProduct finished.");
		return entityDimList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.prd.rmsentiment.database.SentimentWidgetDAOInterface#
	 * getKeywordsByEntitySubProduct(java.lang.String, java.lang.String)
	 */
	@Override
	public List<EntityDimension> getKeywordsByEntitySubProduct(String entity,
			String subProduct) {
		LOG.trace("Method: getKeywordsByEntitySubProduct called.");
		List<EntityDimension> entityDimList = null;
		Connection connection = null;
		CallableStatement callableStatement = null;
		ResultSet rs = null;

		try {
			connection = daoFactory.getConnection();

			/*
			 * Object[] values = { entity, subProduct, entity, subProduct };
			 * preparedStatement = prepareStatement(connection,
			 * SELECT_KEYWORDS_FOR_ENTITY_SUBPRODUCT, true, values);
			 * 
			 * rs = preparedStatement.executeQuery();
			 */

			String getDBUSERCursorSql = "{call sp_GetAllNouns(?,?)}";
			callableStatement = connection.prepareCall(getDBUSERCursorSql);
			callableStatement.setString(1, entity);
			callableStatement.setString(2, subProduct);

			// execute getDBUSERCursor store procedure
			rs = callableStatement.executeQuery();

			EntityDimension entityDimension = null;
			if (rs != null) {
				entityDimList = new ArrayList<EntityDimension>();
			}
			while (rs.next()) {

				entityDimension = new EntityDimension();

				// entityDimension.setSubDimension(rs.getString(3));
				// entityDimension.setDetailedSentiment(rs.getInt(2));
				entityDimension.setCount(rs.getInt(2));
				entityDimension.setPostId("" + rs.getString(3));
				entityDimension.setWord(rs.getString(1));

				double score = rs.getDouble(4);
				if (score > 0) {
					entityDimension.setSenti(true);
				} else {
					entityDimension.setSenti(false);
				}

				entityDimList.add(entityDimension);
			}
		} catch (SQLException e) {
			LOG.error("SQLException while fetching the tweet. "
					+ e.getMessage());
			throw new CriticalException(
					"SQLException while fetching the tweet. " + e.getMessage());
		} finally {
			close(connection, null, rs);
		}

		LOG.trace("Method: getKeywordsByEntitySubProduct finished.");
		return entityDimList;
	}

	public static void main(String[] s) {
		List<EntityDimension> xx = DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getSentimentWidgetDAO()
				.getKeywordsByEntitySubProduct("nike", "menshoes");
		System.out.println(xx.size());
	}

	@Override
	public void insertProdCommentAnalytics(
			ProdCommentAnalticInfo productCommentInfo) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {

			connection = daoFactory.getConnection();
			preparedStatement = connection
					.prepareStatement(INSERT_ANALYTIC_INFO);
			Object[] values = { productCommentInfo.getEntity(),
					productCommentInfo.getSubProduct(),
					productCommentInfo.getCategory(),
					productCommentInfo.getSubDimension(),
					productCommentInfo.getWord(),
					productCommentInfo.getProductId(),
					productCommentInfo.getPostId(),
					productCommentInfo.getUserIP(),
					productCommentInfo.getReviewType(),
					productCommentInfo.getDate() };
			setValues(preparedStatement, values);

			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			LOG.error("SQLException while insertProdCommentAnalytics. "
					+ e.getMessage());
			throw new CriticalException(
					"SQLException whileinsertProdCommentAnalytics. "
							+ e.getMessage());
		} finally {
			close(connection, preparedStatement, null);
		}

	}

}
