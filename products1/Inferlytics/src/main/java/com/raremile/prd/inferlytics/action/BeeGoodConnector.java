package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.BeeGoodDBConnector;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.utils.ChartData;

/**
 * Servlet implementation class BeeGoodConnector
 */
public class BeeGoodConnector extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BeeGoodConnector() {
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
		String type = request.getParameter("type");
		String entity = request.getParameter("entity");// "beegood";
		String subProduct = request.getParameter("subproduct");// "skincare";
		String returnString = "";
		if (type.equals("features")) {
			returnString = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getPieChartDataBeegood(entity, subProduct, null);
		} else if (type.equals("traits")) {
			String subDimension = request.getParameter("subDimension");
			returnString = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getTopTraitsForSubDimenionBeegood(entity, subProduct,
							null,
							subDimension, null);
		} else if (type.equals("comments")) {
			String subDimension = request.getParameter("subDimension");
			String trait = request.getParameter("trait");
			returnString = BeeGoodDBConnector.getComments(entity, subProduct,
					trait, subDimension, 100, "null");
		} else if (type.equals("timeLineChart")) {

			String fromdate = request.getParameter("fromDate");
			String toDate = request.getParameter("toDate");
			List<ChartData> data = null;
			data = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getTimeLineDataByWeek(entity, subProduct, fromdate,
							toDate,
							true, null);
			String result1 = "[";
			for (ChartData eachData : data) {
				result1 += eachData.toString() + ",";
			}
			result1 += "]";
			result1 = result1.replace(",]", "]");
			data = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getTimeLineDataByWeek(entity, subProduct, fromdate,
							toDate,
							false, null);
			String result2 = "[";
			for (ChartData eachData : data) {
				result2 += eachData.toString() + ",";
			}
			result2 += "]";
			result2 = result2.replace(",]", "]");
			returnString = "{\"PositiveData\":" + result1
					+ ",\"NegativeData\":" + result2 + "}";

		}
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write(returnString);
		writer.flush();
		writer.close();
	}

}
