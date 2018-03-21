package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.database.MongoConnectorForMapPage;

/**
 * Servlet implementation class EnterpriseCarConnector
 */
public class MapViewConnector extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MapViewConnector() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String returnString = "";
		String entity = request.getParameter("entity");
		String subProduct = request.getParameter("subProduct");
		String category = request.getParameter("type");
		String stateCode = request.getParameter("stateCode");
		String storeId = request.getParameter("store");
		response.setCharacterEncoding("UTF-8");
		if (category.equals("pieChartData")) {
			returnString = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getPieChartDataForEnterpriseCar(entity, subProduct,
							storeId, stateCode);
		} else if (category.equals("comments")) {
			/* Get comments when a trait is clicked. */
			String word = request.getParameter("word");
			Boolean isPositive = Boolean.parseBoolean(request
					.getParameter("isPositive"));
			String subDimension = request.getParameter("subDimension");
			String skip = request.getParameter("skip");
			returnString = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getCommentsForEnterPriseCar(entity, subProduct, stateCode,
							word, isPositive, subDimension,
							Integer.parseInt(skip), storeId);
		} else if (category.equals("avgScore")) {
			// Get the avg score to colourize the map and also the no of stores
			// in each state.
			returnString = MongoConnectorForMapPage.getAvgScoreForStates(
					entity, subProduct);
		} else if (category.equals("commentsCount")) {
			// Get the count of positive and negative counts for a given state.
			returnString = MongoConnectorForMapPage
					.getPosNegCommentsCountForMapView(entity, subProduct,
							stateCode);
		} else if (category.equals("topSubdimensions")) {
			returnString = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getTopPosorNegSubdimensionForState(entity, subProduct,
							stateCode);
		} else {
			/* Get the traits and the comments count. */
			String subDimension = request.getParameter("subDimension");
			returnString = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getTraitsForEnterpriseCar(entity, subProduct, storeId,
							subDimension, category, stateCode);
		}

		PrintWriter writer = response.getWriter();
		writer.write(returnString);
		writer.flush();
		writer.close();
	}

}
