package com.raremile.prd.inferlytics.database;

import static com.raremile.prd.inferlytics.database.DAOUtil.close;
import static com.raremile.prd.inferlytics.database.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.exception.DAOException;
import com.raremile.prd.inferlytics.utils.UserCredentials;

public class Validator {

	/**
	 * @param args
	 */
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(Validator.class);

	private static final String GET_USERNAME_PASSWORD = "SELECT * FROM user_details.user_credentials WHERE userName=? and password=?";
	private static final String GET_BRANDDETAILS = "SELECT * FROM user_details.user_brand_mapping WHERE userId=? ";
	private static final String VERIFY_CREDENTIALS = "SELECT * FROM user_details.user_brand_mapping WHERE userId=? and entityName=? and subProduct=?";
	public static void main(String[] args) {

		DAOFactory
		.getInstance(ApplicationConstants.USERDETAILS_PROPERTIES_FILE)
		.getValidator().verifyCredentials("moltonbrown", "moltonbron");

	}

	private final DAOFactory daoFactory;

	public Validator(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	public UserCredentials verifyCredentials(String userName, String password) {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Integer userId = null;
		UserCredentials userCredentials=new UserCredentials();
		try {
			connection = daoFactory.getConnection();
			Object[] values = { userName, password };
			preparedStatement = prepareStatement(connection,
					GET_USERNAME_PASSWORD, false, values);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				userId = resultSet.getInt(1);
				userCredentials.setUserId(Integer.toString(userId));
				userCredentials.setUserType(resultSet.getInt(4));
				userCredentials.setUserName(resultSet.getString("userName"));
				Object[] newvalues = { userId };

				preparedStatement = prepareStatement(connection,
						GET_BRANDDETAILS, false, newvalues);
				resultSet = preparedStatement.executeQuery();

				if (resultSet.next()) {
					userCredentials.setEntityName(resultSet.getString(3));
					userCredentials.setSubProduct(resultSet.getString(4));
					userCredentials.setProductName(resultSet.getString(5));
					userCredentials.setSuccess(true);
				}
			} else {
				userCredentials.setSuccess(false);
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		return userCredentials;
	}



	public Boolean verifyDetails(String entity, String subProduct, String uid) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Boolean isTrue = null;
		try {
			connection = daoFactory.getConnection();
			Object[] values = { Integer.parseInt(uid), entity, subProduct };
			preparedStatement = prepareStatement(connection,
					VERIFY_CREDENTIALS, false, values);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				isTrue = true;
			} else {
				isTrue = false;
			}
		} catch (SQLException e) {
			System.out.println(e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}

		return isTrue;
	}

}
