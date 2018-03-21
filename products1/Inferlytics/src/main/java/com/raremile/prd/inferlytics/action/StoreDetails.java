package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.database.MongoConnectorForAnalytics;
import com.raremile.prd.inferlytics.entity.StoreDetailsEntity;

/**
 * Servlet implementation class StoreDetails
 */
public class StoreDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public StoreDetails() {
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
		String userId = request.getParameter("userId");
		String product = request.getParameter("product");
		String entity = request.getParameter("brand");
		String subProduct = request.getParameter("subProduct");
		HttpSession session = request.getSession(true);
		String sessionId = (String) session.getAttribute("userId");
		if (null != userId) {
			Boolean isCredentialsValid = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getValidator().verifyDetails(entity, subProduct, userId);

			if (userId.equals(sessionId) && isCredentialsValid) {

				response.setCharacterEncoding("UTF-8");

				List<StoreDetailsEntity> stores = MongoConnectorForAnalytics
						.getStores(product);
				request.setAttribute("allStores", stores);
				// request.setAttribute("subDimension", subDimension);
				request.setAttribute("entity", entity);
				request.setAttribute("subProduct", subProduct);


			}
		}
		RequestDispatcher x = request
				.getRequestDispatcher("Analytics/StoreDetailsNew.jsp");
		x.forward(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

}
