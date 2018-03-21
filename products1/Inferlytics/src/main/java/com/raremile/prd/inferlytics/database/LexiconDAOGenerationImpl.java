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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.raremile.prd.inferlytics.exception.DAOException;

/**
 * @author pratyusha
 * @created 08-Jul-2013
 * 
 * 
 * 
 */
public class LexiconDAOGenerationImpl implements LexiconDAOGenerationInterface {

	private static final Logger LOG = Logger
			.getLogger(LexiconDAOGenerationImpl.class);

	// Constants
	// ------------------------------------------------------------------------------------
	private static final String INSERT_SENTIWORDNET_DATA = "INSERT INTO setiwordnet_data_test(word,sentiment) VALUES(?,?)";

	private static final String INSERT_DIMENSION_WORD = "INSERT IGNORE INTO `rm_lexicondb`.`dimensionword` (`Word`) VALUES(?)  ON DUPLICATE KEY UPDATE `WordId`=LAST_INSERT_ID(`WordId`)";
	private static final String INSERT_SUBDIMENSION = "INSERT IGNORE INTO `rm_lexicondb`.`subdimension`(`SubDimension`) VALUES(?) ON DUPLICATE KEY UPDATE `SubDimensionId`=LAST_INSERT_ID(`SubDimensionId`);";
	private static final String INSERT_SUBDIMENSION_WORD_MAP = "INSERT IGNORE INTO `rm_lexicondb`.`wordsubdimensionmap`(`SubDimensioId`,`WordId`,`productId`)VALUES(?,?,?);";
	private static final String INSERT_PRO_SUBDIM_MAP = "INSERT IGNORE INTO `rm_lexicondb`.`productsubdimensionmap`(`ProductId`,`SubDimensionId`)VALUES(?,?);";
	private static final String INSERT_SUBDIM_SYNONYM_MAP = "INSERT IGNORE INTO `rm_lexicondb`.`subdimensions_synonyms`(`SubDimensionId`,`Word`,`SynonymWord`)VALUES(?,?,?);";

	// private static final String INSERT_UPDATE_SYNONYMS =
	// "INSERT IGNORE INTO `rm_lexicondb`.`synonyms` (`Word`,`SubProductId`) VALUES(?,?)  ON DUPLICATE KEY UPDATE `Id`=LAST_INSERT_ID(`Id`)";
	// private static final String INSERT_SYNONYMS =
	// "INSERT IGNORE INTO `rm_lexicondb`.`synonyms` (`SynonymId`,`Word`,`SubProductId`) VALUES(?,?,?)";
	private static final String CHECK_FOR_SYNONYMS = "SELECT * FROM `rm_lexicondb`.`productsynonyms` WHERE Word=? and SubProductId=?";

	// "INSERT IGNORE INTO `rm_lexicondb`.`synonyms` (`SynonymId`,`Word`,`SubProductId`) VALUES(?,?,?)  ";
	// private static final String UPDATE_SYNONYMS =
	// "UPDATE `rm_lexicondb`.`synonyms`  SET `SynonymId` = ? WHERE `ID` = ? ";

	private static final String INSERT_UPDATE_SYNONYMS = "INSERT INTO `rm_lexicondb`.`productsynonyms`(`Word`,`SubProductId`) VALUES(?,?)";
	private static final String INSERT_SYNONYMS = "INSERT IGNORE INTO `rm_lexicondb`.`productsynonyms` (`SynonymId`,`Word`,`SubProductId`) VALUES(?,?,?)  ";
	private static final String UPDATE_SYNONYMS = "UPDATE `rm_lexicondb`.`productsynonyms`  SET `SynonymId` = ? WHERE `ID` = ? ";

	// Vars
	// ---------------------------------------------------------------------------------------

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
	LexiconDAOGenerationImpl(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	// Actions
	// ----------------------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.prd.rmsentiment.database.LexiconDAOGenerationInterface#storeLexicon
	 * (java.util.Map)
	 */
	@Override
	public void storeLexicon(Map<String, Double> lexicons) {
		LOG.trace("Method: storeLexicon called.");
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = daoFactory.getConnection();
			preparedStatement = connection
					.prepareStatement(INSERT_SENTIWORDNET_DATA);

			int i = 0;
			for (String key : lexicons.keySet()) {
				preparedStatement.setString(1, key);
				preparedStatement.setDouble(2, lexicons.get(key));

				preparedStatement.addBatch();
				i++;

				if (i == 1000) {
					preparedStatement.executeBatch();
					i = 0;
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
		LOG.trace("Method: storeLexicon finished.");
	}

	@Override
	public void storeSubDimensionWord(Map<String, Set<String>> subDimWordMap,
			Map<String, Set<String>> subDimSynonymMap, int productId,
			int subProductId, boolean isProductSynonym) {
		LOG.trace("Entered method storeSubDimensionWord");
		Connection connection = null;
		PreparedStatement psForSubDimension = null;
		PreparedStatement psForSubDimensionWordMap = null;
		PreparedStatement psForWord = null;
		PreparedStatement psForProSubDimMap = null;
		PreparedStatement psForInsertUpdateSynonym = null;

		PreparedStatement psForUpdateSynonym = null;
		PreparedStatement psForInsertSynonym = null;
		PreparedStatement psSubdimensionSynonymMap = null;
		ResultSet resultSet = null;

		if (null != subDimWordMap) {
			try {
				connection = daoFactory.getConnection();

				psForProSubDimMap = connection
						.prepareStatement(INSERT_PRO_SUBDIM_MAP);
				psForInsertSynonym = connection
						.prepareStatement(INSERT_SYNONYMS);

				psForSubDimensionWordMap = connection
						.prepareStatement(INSERT_SUBDIMENSION_WORD_MAP);
				psSubdimensionSynonymMap = connection
						.prepareStatement(INSERT_SUBDIM_SYNONYM_MAP);

				for (Entry<String, Set<String>> subDimWordEntry : subDimWordMap
						.entrySet()) {
					int subDimId = 0;
					/**
					 * Insert SubDimension
					 */
					Object[] values = { subDimWordEntry.getKey()
							// .toLowerCase(Locale.ENGLISH)
					};
					psForSubDimension = prepareStatement(connection,
							INSERT_SUBDIMENSION, true, values);
					psForSubDimension.executeUpdate();
					resultSet = psForSubDimension.getGeneratedKeys();
					if (resultSet.next()) {

						subDimId = resultSet.getInt(1);

					}
					Set<String> subDimensionSynonyms = subDimSynonymMap
							.get(subDimWordEntry.getKey());

					if (subDimensionSynonyms != null) {
						for (String synonymWord : subDimensionSynonyms) {
							Object[] insertValues = { subDimId,
									subDimWordEntry.getKey(), synonymWord };
							setValues(psSubdimensionSynonymMap, insertValues);
							psSubdimensionSynonymMap.addBatch();
						}
					}
					/**
					 * Insert Product - Subdimension Map
					 */
					Object[] proSubDimMapvalues = { productId, subDimId };

					setValues(psForProSubDimMap, proSubDimMapvalues);
					psForProSubDimMap.addBatch();

					/**
					 * Insert WORD
					 */
					Set<String> words = subDimWordEntry.getValue();
					for (String word : words) {
						Integer wordId = 0;
						String[] synonymWords = null;
						String wordToInsert = word;
						LOG.info("word is " + word);
						if (word.contains("|")) {
							synonymWords = word.split("\\|");
							LOG.info("synonyms = " + synonymWords);
							wordToInsert = synonymWords[0].trim();
						}
						LOG.info("wordToInsert = " + wordToInsert);
						Object[] wordvalue = { wordToInsert
								// .toLowerCase(Locale.ENGLISH)
						};
						psForWord = prepareStatement(connection,
								INSERT_DIMENSION_WORD, true, wordvalue);
						psForWord.executeUpdate();
						resultSet = psForWord.getGeneratedKeys();
						if (resultSet.next()) {
							wordId = resultSet.getInt(1);

						}
						if (wordId != 0) {
							Object[] mapvalues = { subDimId,
									resultSet.getInt(1), productId };
							setValues(psForSubDimensionWordMap, mapvalues);
							psForSubDimensionWordMap.addBatch();
						}

						if (synonymWords != null) {
							/**
							 * Insert Synonyms if any. 1.Get first words's Id.
							 * 2. update its synonymId 3. Insert all other words
							 * with the synonym Id of first one
							 */

							int synonnymId = 0;
							Object[] mainSynonym = { synonymWords[0]
									// .toLowerCase(Locale.ENGLISH)
									.trim(), subProductId };
							PreparedStatement psCheckForSynonym = prepareStatement(
									connection, CHECK_FOR_SYNONYMS, false,
									mainSynonym);
							resultSet = psCheckForSynonym.executeQuery();
							if (resultSet.next()) {
								synonnymId = resultSet.getInt(1);
							} else {
								psForInsertUpdateSynonym = prepareStatement(
										connection, INSERT_UPDATE_SYNONYMS,
										true, mainSynonym);
								psForInsertUpdateSynonym.executeUpdate();
								resultSet = psForInsertUpdateSynonym
										.getGeneratedKeys();

								if (resultSet.next()) {
									synonnymId = resultSet.getInt(1);
								}
							}
							System.out.println(synonnymId + ":"
									+ synonymWords[0]);
							if (synonnymId != 0) {
								Object[] objsynonymId = { synonnymId,
										synonnymId };
								psForUpdateSynonym = prepareStatement(
										connection, UPDATE_SYNONYMS, false,
										objsynonymId);
								psForUpdateSynonym.executeUpdate();

								for (int synWordCounter = 1; synWordCounter < synonymWords.length; synWordCounter++) {
									Object[] insertSynValues = { synonnymId,
											synonymWords[synWordCounter]
													// .toLowerCase(Locale.ENGLISH)
													.trim(), subProductId };
									System.out.println(synonnymId + ":"
											+ synonymWords[synWordCounter]);
									setValues(psForInsertSynonym,
											insertSynValues);
									psForInsertSynonym.addBatch();
								}
								psForInsertSynonym.executeBatch();

							}

						}

					}

				}
				psSubdimensionSynonymMap.executeBatch();
				psForSubDimensionWordMap.executeBatch();
				psForProSubDimMap.executeBatch();

			} catch (SQLException e) {
				LOG.error("", e);
				throw new DAOException(e);
			} finally {
				close(connection);
				close(psForSubDimension);
				close(psForSubDimensionWordMap);
				close(psForWord);
				close(psForProSubDimMap);

			}
		}

		LOG.trace("Leaving method storeSubDimensionWord");
	}

}
