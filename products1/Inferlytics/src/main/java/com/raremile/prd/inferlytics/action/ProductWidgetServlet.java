package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raremile.prd.inferlytics.cache.ApplicationCache;
import com.raremile.prd.inferlytics.database.MongoConnector;
import com.raremile.prd.inferlytics.entity.UsecaseDetails;
import com.raremile.prd.inferlytics.utils.BusinessUtil;



/**
 * Servlet implementation class ProductWidgetServlet
 */
public class ProductWidgetServlet extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(ProductWidgetServlet.class);
	private static final long serialVersionUID = 1L;



	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ProductWidgetServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {/*

		String brand = request.getParameter("entity");
		String subProduct = request.getParameter("subProduct");
		String category=request.getParameter("category");
		String priceRange=request.getParameter("price");
		//get minMax Price by Brand
		Integer minPrice = 0;
		Integer maxPrice = 200;
		LOG.info(brand);
		LOG.info(subProduct);
		LOG.info(category);
		LOG.info(priceRange);
		if(null != priceRange){
			String[] stringPriceArray =priceRange.split("-");
			minPrice =Integer.parseInt(stringPriceArray[0].substring(1));
			maxPrice =Integer.parseInt(stringPriceArray[1].substring(1));
		}
		LOG.info("came here to Get method of ProductWidgetServlet");
		if (null != brand && null != subProduct) {
			int entityId = IdMap.getEntityId(brand);
			int subProductId = IdMap.getSubproductId(subProduct);
			String featureList=MongoConnector.getFeatureProductCountForSubProduct(entityId, subProductId, category, minPrice, maxPrice);
			request.setAttribute("featureList",featureList);
			LOG.debug("Set features in response in ProductWidgetServlet");
			request.getRequestDispatcher("EcomWidgetWithProducts.jsp").forward(request,
					response);

		}

	 */}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOG.info("Entered ProductWidget Servlet");
		long startTime = System.currentTimeMillis();
		String brand = request.getParameter("entity");
		String subProduct = request.getParameter("subProduct");
		if (subProduct.equals("tescowinesnew")) {
			subProduct = "tescowines";
		}

		String category= request.getParameter("category");
		String priceRange=request.getParameter("price");
		//get minMax Price by Brand
		Integer minPrice = 0;
		Integer maxPrice = null;


		String currPrice =BusinessUtil.getCurrencyPriceByBrand(brand,subProduct);
		String currency = currPrice.split(":")[0];
		String priceString = currPrice.split(":")[1];
		if(!priceString.equals("null")){
			maxPrice = Integer.parseInt(priceString);
		}

		if (null != brand && null != subProduct) {
			UsecaseDetails ucb =  ApplicationCache.getUsecaseDetail(brand, subProduct);				 
			int entityId = ucb.getEntityId();
			int subProductId = ucb.getSubProductId();
			if (null != priceRange) {
				String[] stringPriceArray =priceRange.split("-");
				minPrice =Integer.parseInt(stringPriceArray[0].trim());
				maxPrice =Integer.parseInt(stringPriceArray[1].trim());
			}
			JsonElement featureList = null;
			LOG.info("Entering DB Class here");
			if(subProductId==19){
				/* List<SunBurstData> posFeatureList= MongoConnector.getFeatureCommentCountForEntitySubProduct(entityId, subProductId, category, minPrice, maxPrice,true);
			 List<SunBurstData> negFeatureList= MongoConnector.getFeatureCommentCountForEntitySubProduct(entityId, subProductId, category, minPrice, maxPrice,false);
				 */			 featureList = BusinessUtil.genarateFeatureJsonForEntitySubProduct(entityId, subProductId, category, minPrice, maxPrice);
			}else{
				featureList=MongoConnector.getFeatureProductCountForSubProduct(entityId, subProductId, category, minPrice, maxPrice);
			}


			JsonObject priceobj = new JsonObject();
			priceobj.addProperty("maxprice", maxPrice);
			priceobj.addProperty("currency", currency);
			JsonObject responseObj = new JsonObject();
			responseObj.add("price", priceobj);
			responseObj.add("features", featureList);
			request.setAttribute("featureList",featureList);
			/*request.getRequestDispatcher("EcomWidget.jsp").forward(request,
					response);*/
			//LOG.info(responseObj.toString());
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer = response.getWriter();
			writer.write(responseObj.toString());
			writer.flush();
			writer.close();
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			LOG.error(" Peformance message: Time Taken in milliseconds is   "
					+ elapsedTime);
		}

	}

}