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

import com.raremile.prd.inferlytics.exception.DAOException;

/**
 * @author mallikarjuna
 * @created 30-May-2014
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class DAOUtility {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(DAOUtility.class);
	private final DAOFactory daoFactory;
	private static final String GETDATA_NO_USERS = "SELECT competitorEntityId,competitorSubProductId FROM competitorMap where entityId=?;";

	/**
	 * @param daoFactory
	 */

	public DAOUtility(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	public ArrayList<String> getCompetitorIds(int entityId) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		ArrayList<String> ids = new ArrayList<>();
		try {
			connection = daoFactory.getConnection();
			Object[] values = { entityId };

			preparedStatement = prepareStatement(connection, GETDATA_NO_USERS,
					false, values);

			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				String compentityId = resultSet.getString(1);
				String compSubProductId = resultSet.getString(2);
				String id = compentityId + ":" + compSubProductId;
				ids.add(id);

			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		return ids;

	}
}
