package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.utils.ActivityLog;

/**
 * Servlet implementation class StoreConnector
 */
public class StoreConnector extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public StoreConnector() {
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
		String productId = request.getParameter("productId");
		String entity = request.getParameter("entity");
		String subProduct = request.getParameter("subProduct");
		String category = request.getParameter("type");
		Object userName = request.getSession().getAttribute("userName");
		String name = null;
		if (userName != null) {
			name = (String) userName;
		}
		String analyticsCategory = request.getParameter("analyticscategory");
		String ipAddress = request.getHeader("X-FORWARDED-FOR");

		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		if (category.equals("productDetails")) {
			returnString = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData().getStoreDetails(productId);

			if (name != null && !name.contains("dev")) {
				ActivityLog.addClientDetailstoLog(entity, subProduct, "store",
						null, null, productId, null, null, ipAddress);
			}
		} else if (category.equals("comments")) {
			String word = request.getParameter("word");
			Boolean isPositive = Boolean.parseBoolean(request
					.getParameter("isPositive"));

			String subDimension = request.getParameter("subDimension");
			String skip = request.getParameter("skip");
			returnString = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getCommentsForStore(entity, subProduct, productId, word,
							isPositive, subDimension, Integer.parseInt(skip));
			if (name != null && !name.contains("dev")) {
				ActivityLog.addClientDetailstoLog(entity, subProduct,
						analyticsCategory,
						subDimension, word, productId, null,
						null, ipAddress);
			}
		} else if (category.equals("topSubdimensions")) {
			String isPositive = request.getParameter("isPositive");
			returnString = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getTopPosorNegSubdimension(entity, subProduct, productId,
							Boolean.valueOf(isPositive));
		} else if (category.equals("pieChartData")) {
			returnString = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getPieChartData(entity, subProduct, productId);
		} else {
			String subDimension = request.getParameter("subDimension");
			returnString = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDAOAnalyticsData()
					.getTopTraitsForSubDimenion(entity, subProduct, productId,
							subDimension, category);
			if (name != null && !name.contains("dev")) {
				ActivityLog.addClientDetailstoLog(entity, subProduct,
						"analysissummary", subDimension, null, productId, null,
						null, ipAddress);
			}

		}
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write(returnString);
		writer.flush();
		writer.close();

	}
}
