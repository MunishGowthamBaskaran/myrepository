package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.utils.UserCredentials;

/**
 * Servlet implementation class AnalyserConnector
 */
public class AnalyserConnector extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AnalyserConnector() {
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


		HttpSession session = request.getSession(true);
		UserCredentials user = (UserCredentials) session.getAttribute("user");
		String userId = user.getUserId();
		String entity = user.getEntityName();
		String subProduct = user.getSubProduct();
		String sessionId = user.getUserId();
		Boolean isCredentialsValid = DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getValidator().verifyDetails(entity, subProduct, userId);
		String returnString = "";
		if (sessionId != null && userId.equals(sessionId) && isCredentialsValid) {
			String fromDate = request.getParameter("fromDate");
			String toDate = request.getParameter("toDate");
			String finaljson = "";
			String pageno = request.getParameter("pageno");

			switch (request.getParameter("optionName")) {
			case "topWords":
				finaljson = DAOFactory
				.getInstance(
						ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
						.getDAOAnalyticsData()
						.getTopSubDimensionandWords(entity, subProduct,
								fromDate, toDate, user.getUserType());
				break;
			case "top_Products":
				finaljson = DAOFactory
				.getInstance(
						ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
						.getDAOAnalyticsData()
						.getProducts(entity, subProduct, fromDate, toDate,
								Integer.parseInt(pageno), user.getUserType());
				break;
			case "top_Comments":

				finaljson = DAOFactory
				.getInstance(
						ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
						.getDAOAnalyticsData()
						.getPosts(entity, subProduct, fromDate, toDate,
								Integer.parseInt(pageno), user.getUserType());
				break;
			case "recentComments":
				int pagenum = Integer.parseInt(request.getParameter("pageno"));
				int limit = Integer.parseInt(request.getParameter("limit"));

				String isPositive = request.getParameter("isPositive");
				finaljson = DAOFactory
						.getInstance(
								ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
								.getDAOAnalyticsData()
								.getRecentComments(entity, subProduct, fromDate,
										toDate, (pagenum * limit), limit, isPositive);
				break;
			case "topTraits":
				finaljson = DAOFactory
				.getInstance(
						ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
						.getDAOAnalyticsData()
						.getTimelineTraits(entity, subProduct, fromDate, toDate);
				break;

			case "compareTraits":
				String fromDate1 = request.getParameter("fromDate1");
				String toDate1 = request.getParameter("toDate1");
				String fromDate2 = request.getParameter("fromDate2");
				String toDate2 = request.getParameter("toDate2");
				finaljson = DAOFactory
						.getInstance(
								ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
								.getDAOAnalyticsData()
								.getTimelineTraitsForComparison(entity, subProduct, fromDate1, toDate1,fromDate2,toDate2);
				break;
			default:
				finaljson = "{\"Fail\":true}";
				break;

			}
			returnString = finaljson;
		} else {
			returnString = "{\"Fail\":true}";
		}
		PrintWriter writer = response.getWriter();
		writer.write(returnString);
		writer.flush();
		writer.close();

	}

}
