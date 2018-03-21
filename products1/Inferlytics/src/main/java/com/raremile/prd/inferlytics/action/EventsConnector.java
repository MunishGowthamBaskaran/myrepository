package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;

/**
 * Servlet implementation class EventsConnector
 */
public class EventsConnector extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EventsConnector() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String returnString = "";
		String entity = request.getParameter("entity");
		String subProduct = request.getParameter("subProduct");
		String category = request.getParameter("type");
		response.setCharacterEncoding("UTF-8");
		if (category.equals("pieChartData")) {
			returnString = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getPieChartData(entity, subProduct, null);
		} else if (category.equals("partitionData")) {
			returnString = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getPartitionData(entity, subProduct, null);
		} else if (category.equals("commentsForFeatures")) {

			Boolean isPositive = Boolean.parseBoolean(request
					.getParameter("isPositive"));
			String subDimension = request.getParameter("subDimension");
			String skip = request.getParameter("skip");
			returnString = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getblogsForEventsFeatures(entity, subProduct, null,
							isPositive, subDimension, Integer.parseInt(skip));

		} else if (category.equals("comments")) {
			String word = request.getParameter("word");
			Boolean isPositive = Boolean.parseBoolean(request
					.getParameter("isPositive"));
			String subDimension = request.getParameter("subDimension");
			String skip = request.getParameter("skip");
			returnString = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getblogsForEvents(entity, subProduct, null, word,
							isPositive, subDimension, Integer.parseInt(skip));
		} else if (category.equals("tagcloud")) {
			String subDimension = request.getParameter("subDimension");
			returnString = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getWordsForSubDimenion(entity, subProduct, null,
							subDimension, category);
		} else {
			String subDimension = request.getParameter("subDimension");
			returnString = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getTopTraitsForSubDimenion(entity, subProduct, null,
							subDimension, category);
		}

		PrintWriter writer = response.getWriter();
		writer.write(returnString);
		writer.flush();
		writer.close();
	}

}
