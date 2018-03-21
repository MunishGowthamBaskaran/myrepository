package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.entity.DetailedSentiment;
import com.raremile.prd.inferlytics.entity.Features;
import com.raremile.prd.inferlytics.entity.Feed;
import com.raremile.prd.inferlytics.entity.Output;
import com.raremile.prd.inferlytics.entity.SentimentComparator;
import com.raremile.prd.inferlytics.entity.Serie;
import com.raremile.prd.inferlytics.entity.Series;
import com.raremile.prd.inferlytics.entity.SunBurstData;
import com.raremile.prd.inferlytics.utils.BusinessUtil;
import com.raremile.prd.inferlytics.utils.Util;


/**
 * Servlet implementation class PlotChart
 */
public class PlotChart extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(PlotChart.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PlotChart() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		performOperation(request, response);
	}

	/**
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void performOperation(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// String chartType = request.getParameter("chartType");
		// if ("partition".equals(chartType)) {
		// String brand = request.getParameter("brand");
		// String product = request.getParameter("product");

		Map<String, List<String>> entityProdMap = new HashMap<String, List<String>>();
		entityProdMap = DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getSentimentDAO().getAllProductToRespEntity();

		Gson g = new Gson();
		String jsonEntityProdMap = g.toJson(entityProdMap);
		request.setAttribute("jsonEntityProdMap", jsonEntityProdMap);

		String brand = request.getParameter("entities");
		String product = request.getParameter("products");

		if (null != brand && null != product) {
		/*
		 * System.out.println("Product-Chart brand " + brand);
		 * System.out.println("Product-Chart product " + product);
		 */

		SunBurstData sdata = BusinessUtil.getSunBurstData(brand,
 product);
			String json = new Gson().toJson(sdata);
			LOG.info("Sun burst json " + json);
			request.setAttribute("json", json);
			/*request.getRequestDispatcher("partition.jsp").forward(request,
					response);*/
		/*
		 * } else {
		 */
			// String method = request.getParameter("method");
			// if ("individual".equals(method)) {
		// String brand = request.getParameter("brand");

			List<Serie> listData = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getSentimentDAO().getPieChartData(brand, product);

			int total = 0;
			for (Serie data : listData) {
				total = total + data.getData();
			}
			int dataSize = listData.size();

			int i = 0;
			int valuesTotal = 0;
			for (Serie data : listData) {
				int roundedValue = Math.round(data.getData() * 100 / total);
				valuesTotal = valuesTotal + roundedValue;
				if (i == (dataSize - 1)) {
					roundedValue = roundedValue + (100 - valuesTotal);
				}
				Integer value = Integer.valueOf(roundedValue);
				data.setData(value);
				i = i + 1;
			}

			Collections.sort(listData, new SentimentComparator());
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("series", listData);
			// ...
			String json2 = new Gson().toJson(listData);
			request.setAttribute("json2", json2);
			/*
			 * response.setContentType("application/json"); PrintWriter writer =
			 * response.getWriter();
			 * 
			 * writer.write(json); writer.flush(); writer.close();
			 */
			// }

			// else if ("collective".equals(method)) {
			// String brand = request.getParameter("brand");

			List<Output> outputList = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getSentimentDAO().getMultipleData(brand, product);

			List<String> categoryList = new ArrayList<String>();
			List<Series> seriesList = new ArrayList<Series>(
					DetailedSentiment.values().length);
			Series series;
			for (DetailedSentiment sentiment : DetailedSentiment.values()) {
				series = new Series();
				series.setName(sentiment.name());
				seriesList.add(series);
				// do something
			}

			// int
			i = 0;
			for (Output output : outputList) {
				DetailedSentiment sentiment = DetailedSentiment.valueOf(output
						.getSentiment().toUpperCase());
				if (categoryList.contains(output.getDate())) {

					int index = categoryList.indexOf(output.getDate());

					seriesList.get(sentiment.getValue()).replace(index,
							output.getCount());
				}

				/*
				 * seriesList.get(sentiment.getValue()).addToDataList(
				 * output.getCount());
				 */else {
					categoryList.add(output.getDate());
					int seriesCounter = 0;
					for (Series seriesObj : seriesList) {
						if (seriesCounter == sentiment.getValue()) {
							seriesObj.addToDataList(output.getCount());
						} else {
							seriesObj.addToDataList(0);
						}
						seriesCounter++;
					}
				}
				// if(seriesList.contains(output.getSentiment())) {
				// Series series = seriesList.get(i);
				// series.addToDataList(output.getCount());
				// } else {
				// Series series = new Series();
				// series.setName(output.getSentiment());
				// series.addToDataList(output.getCount());
				// seriesList.add(series);
				// }

				i++;
			}

			// Map<String, Object>
			Collections.sort(seriesList, new SentimentComparator());
			data = new HashMap<String, Object>();
			data.put("categories", categoryList);
			data.put("series", seriesList);

			// Gson gson = new
			// GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			Gson gson = new Gson();
			JsonElement je = gson.toJsonTree(categoryList);
			JsonElement je1 = gson.toJsonTree(seriesList);
			JsonObject jo = new JsonObject();
			jo.add("categories", je);
			jo.add("series", je1);
			request.setAttribute("categories", je);
			request.setAttribute("series", je1);
			// String json = new Gson().toJson(data);
			/*
			 * response.setContentType("application/json"); // PrintWriter
			 * writer = response.getWriter();
			 * 
			 * writer.write(jo.toString()); writer.flush(); writer.close();
			 */
			List<Feed> feeds = new ArrayList<Feed>();
			/*
			 * feeds.addAll(DAOFactory
			 * .getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
			 * .getLexiconDAO().getFeedsForEntity(brand, 2));
			 */
			feeds.addAll(DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getLexiconDAO().getFeedsFromMongo(brand, product, 0));
			feeds.addAll(DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getLexiconDAO().getFeedsFromMongo(brand, product, 1));

			if (feeds != null) {
				Collections.sort(feeds, Util.FEED_ORDER);
				LOG.info("Feeds size -->" + feeds.size());
				request.setAttribute("feeds", feeds);
				request.setAttribute("query", brand);
				}

			if (!"marriot".equals(brand)) {
				List<Features> posfeatureList = DAOFactory
						.getInstance(
								ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
						.getSentimentDAO().getPositiveFeatures(brand, product);
				request.setAttribute("posfeaturesList",
						new Gson().toJson(posfeatureList));
				List<Features> negfeatureList = DAOFactory
						.getInstance(
								ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
						.getSentimentDAO().getNegativeFeatures(brand, product);
				request.setAttribute("negfeaturesList",
						new Gson().toJson(negfeatureList));
			request.getRequestDispatcher("plotcharttwoclouds.jsp")
					.forward(
						request, response);
			} else {
				List<Features> featureList = new ArrayList<Features>();
				featureList.addAll(DAOFactory
						.getInstance(
								ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
						.getSentimentDAO().getPositiveFeatures(brand, product));

				featureList.addAll(DAOFactory
						.getInstance(
								ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
						.getSentimentDAO().getNegativeFeatures(brand, product));
				request.setAttribute("featuresList",
						new Gson().toJson(featureList));
				request.getRequestDispatcher("plotchart.jsp").forward(request,
						response);
				}


	}
 else {
			request.getRequestDispatcher("plotcharttwoclouds.jsp").forward(
					request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		performOperation(request, response);
	}
	}


