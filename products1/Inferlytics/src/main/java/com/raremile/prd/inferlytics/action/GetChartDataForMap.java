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
 * Servlet implementation class GetChartDataForMap
 */
public class GetChartDataForMap extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetChartDataForMap() {
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
		String entity = request.getParameter("entity");
		String subProduct = request.getParameter("subProduct");
		String stateCode = request.getParameter("stateCode");
		String store=request.getParameter("store");
		String returnString = "";
		if (request.getParameter("chartType") != null
				&& request.getParameter("chartType").equals("timeLine")) {

			String fromdate = request.getParameter("fromDate");
			String toDate = request.getParameter("toDate");
			List<ChartData> data = null;
			data = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getTimeLineDataForMap(entity, subProduct, fromdate,
							toDate,
							true, stateCode, store);
			String result1 = "[";
			for (ChartData eachData : data) {
				result1 += eachData.toString() + ",";
			}
			result1 += "]";
			result1 = result1.replace(",]", "]");
			data = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getTimeLineDataForMap(entity, subProduct, fromdate,
							toDate,
							false, stateCode, store);
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
