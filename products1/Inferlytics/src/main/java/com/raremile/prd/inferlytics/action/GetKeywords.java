package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.raremile.prd.inferlytics.cache.ApplicationCache;
import com.raremile.prd.inferlytics.database.MongoConnector;
import com.raremile.prd.inferlytics.entity.ProductDetails;
import com.raremile.prd.inferlytics.entity.UsecaseDetails;
import com.raremile.prd.inferlytics.utils.BusinessUtil;

/**
 * Servlet implementation class GetKeywords
 */
public class GetKeywords extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(GetKeywords.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetKeywords() {
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

		String productId = request.getParameter("ProductId");
		String subProduct = request.getParameter("subProduct");
		String entity = request.getParameter("entity");
		int skip = Integer.parseInt(request.getParameter("skip"));
		String reviewType = request.getParameter("reviewType");
		UsecaseDetails ucb =  ApplicationCache.getUsecaseDetail(entity, subProduct);	
		ProductDetails product = new ProductDetails();
		if (skip == 0) {
			if (entity.equals("nike")) {
				MongoConnector.getNameUrl(productId, product, true);
			} else {
				MongoConnector.getNameUrl(productId, product, false);
			}
			String currPrice = BusinessUtil.getCurrencyPriceByBrand(entity,subProduct);
			product.setCurrency(currPrice.split(":")[0]);
			product.addPosReview(MongoConnector.getPositiveKeyWords(productId,
					"20", skip, product,ucb.getEntityId(),
					ucb.getSubProductId()));
			product.addNegReview(MongoConnector.getNegativeKeyWords(productId,
					"20", skip, product, ucb.getEntityId(),
					ucb.getSubProductId()));

		} else {
			if (reviewType.contains("positive")) {
				product.addPosReview(MongoConnector.getPositiveKeyWords(
						productId, "20", skip, product,
						ucb.getEntityId(),
						ucb.getSubProductId()));
			} else {
				product.addNegReview(MongoConnector.getNegativeKeyWords(
						productId, "20", skip, product,
						ucb.getEntityId(),
						ucb.getSubProductId()));
			}
		}
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write(new Gson().toJson(product));
		writer.flush();
		writer.close();

	}

}
