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
import static com.raremile.prd.inferlytics.database.DAOUtil.setValues;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.raremile.prd.inferlytics.exception.DAOException;



/**
 * @author Pratyusha
 * @created Jun 21, 2013
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class LexiconDAOCorrectionImpl implements LexiconCorrectionDAOInterface {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(LexiconDAOCorrectionImpl.class);
	// Vars
	// ---------------------------------------------------------------------------------------
	private static final String GET_WORDS_NOT_PRESENT = " SELECT Word,AVG(Score) AS avgScore,COUNT(Word) AS wordCount,"
			+
" AVG(CASE  WHEN Score>0 THEN Score ELSE 0 END) AS PosScore,"+
" ((SUM(CASE  WHEN Score>0 THEN 1 ELSE 0 END))/COUNT(Word))*100 AS PosPercent,"+
" AVG(CASE  WHEN Score<0 THEN Score ELSE 0 END) AS NegScore,"+
 " ((SUM(CASE  WHEN Score<0 THEN 1 ELSE 0 END))/COUNT(Word))*100 AS NegPercent"
			+
 " FROM wordsnotpresent"+ 
 " GROUP BY word"+
 " HAVING COUNT(Word)>500 AND avgScore!=0"+
 " ORDER BY COUNT(Word) DESC";

	private static final String INSERT_INTO_SENTIWORD = "INSERT INTO setiwordnet_data (word,sentiment,SampleSize) VALUES(?,?,?)";

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

	/**
	 * @param daoFactory
	 */
	LexiconDAOCorrectionImpl(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.prd.rmsentiment.database.LexiconCorrectionDAOInterface#getWordsNotPresent
	 * ()
	 */
	@Override
	public void getWordsNotPresent() {
		LOG.trace("Method: getWordsNotPresent called.");
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		PreparedStatement psForInsert = null;
		ResultSet resultSet = null;

		try {
			connection = daoFactory.getConnection();
			preparedStatement = connection
					.prepareStatement(GET_WORDS_NOT_PRESENT);
			psForInsert = connection.prepareStatement(INSERT_INTO_SENTIWORD);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {

				double score = 0.0;
				String word = resultSet.getString(1);
				double avgScore = resultSet.getDouble(2);
				int wordCount = resultSet.getInt(3);
				double posAvgScore = resultSet.getDouble(4);
				double negAvgScore = resultSet.getDouble(6);
				double posPercent = resultSet.getDouble(5);
				double negPercent = resultSet.getDouble(7);

				if (posPercent >= 75) {
					score = posAvgScore * (posPercent / 100);
				} else if (negPercent >= 75) {
					score = negAvgScore * (negPercent / 100);
				} else {
					score = avgScore;
				}

				Object[] valuesForSentiword = { word, score, wordCount };
				setValues(psForInsert, valuesForSentiword);
				psForInsert.addBatch();
			}
			psForInsert.executeBatch();

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		LOG.trace("Method: getWordsNotPresent finished.");
	}

}
