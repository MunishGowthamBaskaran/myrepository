package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.raremile.prd.inferlytics.database.MongoConnector;
import com.raremile.prd.inferlytics.entity.IdMap;
import com.raremile.prd.inferlytics.entity.Product;
import com.raremile.prd.inferlytics.utils.BusinessUtil;

/**
 * Servlet implementation class GetSimilarWines
 */
public class KlwinesConnector extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public KlwinesConnector() {
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
		String productId = request.getParameter("productId");
		String entity = request.getParameter("entity");
		String subProduct = request.getParameter("subProduct");
		String type = request.getParameter("type");
		String result = "";
		if (type.equals("similarWines")) {
			List<Product> similarproducts = new ArrayList<>();
			similarproducts = MongoConnector.getSimilarProductsKlwines(
					IdMap.getEntityId(entity),
					IdMap.getSubproductId(subProduct), productId);
			result = BusinessUtil.getJsonForsimilarproducts(similarproducts);
		} else if (type.equals("keyWords")) {
			result = MongoConnector.getFeatureWordsForKlwines(
					IdMap.getEntityId(entity),
					IdMap.getSubproductId(subProduct), productId);
		}
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write(result);
		writer.flush();
		writer.close();

	}

}
