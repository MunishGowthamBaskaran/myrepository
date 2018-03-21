package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.utils.ChartData;

/**
 * Servlet implementation class GetChartData
 */
public class GetChartDataForStore extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetChartDataForStore() {
		super();

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String entity = request.getParameter("entity");
		String subProduct = request.getParameter("subProduct");
		String productId = request.getParameter("productId");
		String returnString = "";

		if (request.getParameter("chartType") != null
				&& request.getParameter("chartType").equals("timeLine")) {

			String fromdate = request.getParameter("fromDate");
			String toDate = request.getParameter("toDate");
			List<ChartData> data = null;
			data = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getTimeLineData(entity, subProduct, fromdate, toDate,
							true, productId);
			String result1 = "[";
			for (ChartData eachData : data) {
				result1 += eachData.toString() + ",";
			}
			result1 += "]";
			result1 = result1.replace(",]", "]");
			data = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getTimeLineData(entity, subProduct, fromdate, toDate,
							false, productId);
			String result2 = "[";
			for (ChartData eachData : data) {
				result2 += eachData.toString() + ",";
			}
			result2 += "]";
			result2 = result2.replace(",]", "]");
			returnString = "{\"PositiveData\":" + result1
					+ ",\"NegativeData\":" + result2 + "}";
		} else {
			String fromdate = request.getParameter("fromDate");
			String toDate = request.getParameter("toDate");
			String subDimension = request.getParameter("subDimension");
			String word = request.getParameter("word");
			List<ChartData> data = null;
			data = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getsubTopicsChartData(entity, subProduct, fromdate,
							toDate, true, productId, subDimension, word);
			String result1 = "[";
			for (ChartData eachData : data) {
				result1 += eachData.toString() + ",";
			}
			result1 += "]";
			result1 = result1.replace(",]", "]");
			data = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getsubTopicsChartData(entity, subProduct, fromdate,
							toDate, false, productId, subDimension, word);
			String result2 = "[";
			for (ChartData eachData : data) {
				result2 += eachData.toString() + ",";
			}
			result2 += "]";
			result2 = result2.replace(",]", "]");
			returnString = "{\"PositiveData\":" + result1
					+ ",\"NegativeData\":" + result2 + "}";

		}
		PrintWriter writer = response.getWriter();
		writer.write(returnString);
		writer.flush();
		writer.close();
	}
}
