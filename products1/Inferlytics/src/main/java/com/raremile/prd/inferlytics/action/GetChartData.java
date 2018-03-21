package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.utils.ChartData;
import com.raremile.prd.inferlytics.utils.UserCredentials;

/**
 * Servlet implementation class GetChartData
 */
public class GetChartData extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetChartData() {
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

		HttpSession session = request.getSession(true);

		UserCredentials user = (UserCredentials) session.getAttribute("user");
		String userId = user.getUserId();
		String entity = user.getEntityName();
		String subProduct = user.getSubProduct();
		String sessionId = user.getUserId();
		String returnString = "";
		Boolean isCredentialsValid = DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getValidator().verifyDetails(entity, subProduct, userId);

		if (userId.equals(sessionId) && isCredentialsValid) {
			if (request.getParameter("chartType") != null
					&& request.getParameter("chartType").equals("timeLine")) {

				String fromdate = request.getParameter("fromDate");
				String toDate = request.getParameter("toDate");
				List<ChartData> data = null;
				data = DAOFactory
						.getInstance(
								ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
								.getDAOAnalyticsData()
								.getTimeLineData(entity, subProduct, fromdate, toDate,
										true, null);
				String result1 = "[";
				for (ChartData eachData : data) {
					result1 += eachData.toString() + ",";
				}
				result1 += "]";
				result1 = result1.replace(",]", "]");
				data = DAOFactory
						.getInstance(
								ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
								.getDAOAnalyticsData()
								.getTimeLineData(entity, subProduct, fromdate, toDate,
										false, null);
				String result2 = "[";
				for (ChartData eachData : data) {
					result2 += eachData.toString() + ",";
				}
				result2 += "]";
				result2 = result2.replace(",]", "]");
				returnString = "{\"PositiveData\":" + result1
						+ ",\"NegativeData\":" + result2 + "}";
				System.out.println(returnString);
			}
			else {
				String time = request.getParameter("time");
				String fromdate = request.getParameter("fromDate");
				String toDate = request.getParameter("toDate");
				List<ChartData> data = null;
				data = DAOFactory
						.getInstance(
								ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
								.getDAOAnalyticsData()
								.getSubDimensionData(entity, subProduct, fromdate,
										toDate, time, user.getUserType());
				String result = "[";
				for (ChartData eachData : data) {
					result += eachData.toString() + ",";
				}
				result += "]";
				result = result.replace(",]", "]");
				returnString = result;

			}
		} else {
			returnString = "{\"Fail\":true}";
		}

		PrintWriter writer = response.getWriter();
		writer.write(returnString);
		writer.flush();
		writer.close();
	}
}
