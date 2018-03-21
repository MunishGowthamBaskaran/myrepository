/**
 *  * Copyright (c) 2014 RareMile Technologies. 
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.entity.EntitySubProductMap;
import com.raremile.prd.inferlytics.exception.DAOException;

/**
 * @author mallikarjuna
 * @created 15-Jul-2014
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class DaoConnector {
	private final DAOFactory daoFactory;
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(DaoConnector.class);
	private static final String GET_COMPETITORS_LIST = "SELECT * FROM rm_lexicondb.competitorMap WHERE entityId=? and subProductId=?";

	public static void main(String[] args) {
		DAOFactory.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
		.getDaoConnector().getCompetitors(428, 35);
	}

	public DaoConnector(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	public List<EntitySubProductMap> getCompetitors(int entityId,
			int subProductId) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<EntitySubProductMap> competitorsList=new ArrayList<>();
		try {
			connection = daoFactory.getConnection();
			Object[] values = { entityId, subProductId };

			preparedStatement = prepareStatement(connection,
					GET_COMPETITORS_LIST, false, values);

			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				EntitySubProductMap entitySubProduct=new EntitySubProductMap();
				entitySubProduct.setEntity(resultSet.getString(4));

				entitySubProduct.setSubProduct(resultSet.getString(5));
				competitorsList.add(entitySubProduct);
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}

		return competitorsList;
	}
}
