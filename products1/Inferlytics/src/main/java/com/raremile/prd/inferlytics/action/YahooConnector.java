package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.raremile.prd.inferlytics.database.MongoConnector;
import com.raremile.prd.inferlytics.entity.ProductDetails;
import com.raremile.prd.inferlytics.utils.BusinessUtilityForAnalytics;

/**
 * Servlet implementation class YahooConnector
 */
public class YahooConnector extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public YahooConnector() {
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
		String productName = request.getParameter("product");
		int skip = Integer.parseInt(request.getParameter("skip"));
		int limit = Integer.parseInt(request.getParameter("limit"));
		List<ProductDetails> productsList = MongoConnector.getProductsForYahoo(
				skip, limit, productName);
		String result = BusinessUtilityForAnalytics
				.gethtmlForyahooProducts(productsList);
		PrintWriter writer = response.getWriter();
		writer.write(result);
		writer.flush();
		writer.close();
	}

}
