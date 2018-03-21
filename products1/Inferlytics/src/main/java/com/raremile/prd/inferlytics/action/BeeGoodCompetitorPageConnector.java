package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.BeeGoodDBConnector;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.entity.BeeGoodTopicData;
import com.raremile.prd.inferlytics.entity.BrandData;
import com.raremile.prd.inferlytics.entity.EntitySubProductMap;
import com.raremile.prd.inferlytics.entity.HiChartSeries;
import com.raremile.prd.inferlytics.entity.HiChartsBarChartData;
import com.raremile.prd.inferlytics.entity.IdMap;
import com.raremile.prd.inferlytics.entity.KeyWord;
import com.raremile.prd.inferlytics.entity.WordData;
import com.raremile.prd.inferlytics.utils.Util;

/**
 * Servlet implementation class BeeGoodCompetitorAnalysis
 */
public class BeeGoodCompetitorPageConnector extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BeeGoodCompetitorPageConnector() {
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

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String entity = request.getParameter("entity");
		String subProduct = request.getParameter("subProduct");
		String type = request.getParameter("type");
		String returnString = "";
		String source = request.getParameter("source");
		if (type.equals("topTraits")) {
			List<List<KeyWord>> traitsList = new ArrayList<>();
			List<KeyWord> posTraitBrand = BeeGoodDBConnector.getTopTraits(
					IdMap.getEntityId(entity),
					IdMap.getSubproductId(subProduct), true);
			List<KeyWord> negTraitBrand = BeeGoodDBConnector.getTopTraits(
					IdMap.getEntityId(entity),
					IdMap.getSubproductId(subProduct), false);
			traitsList.add(posTraitBrand);
			traitsList.add(negTraitBrand);
			List<EntitySubProductMap> compList = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDaoConnector()
					.getCompetitors(IdMap.getEntityId(entity),
							IdMap.getSubproductId(subProduct));

			for (EntitySubProductMap compEntitySubProduct : compList) {
				List<KeyWord> compposTraitBrand = BeeGoodDBConnector
						.getTopTraits(IdMap.getEntityId(compEntitySubProduct
								.getEntity()), IdMap
								.getSubproductId(compEntitySubProduct
										.getSubProduct()), true);
				List<KeyWord> compnegTraitBrand = BeeGoodDBConnector
						.getTopTraits(IdMap.getEntityId(compEntitySubProduct
								.getEntity()), IdMap
								.getSubproductId(compEntitySubProduct
										.getSubProduct()), false);
				traitsList.add(compposTraitBrand);
				traitsList.add(compnegTraitBrand);
			}

			returnString = new Gson().toJson(traitsList);

		} else if (type.equals("barChartDataforSubTopics")) {

			String subDimension = request.getParameter("subDimension");
			List<KeyWord> keyWords = BeeGoodDBConnector
					.getBarChartDataForSubTopicsTopics(
							IdMap.getEntityId(entity),
							IdMap.getSubproductId(subProduct), null,
							subDimension, source);

			ArrayList<String> words = new ArrayList<>();
			ArrayList<HiChartSeries> series = new ArrayList<>();

			HiChartSeries seriesPos = new HiChartSeries();
			seriesPos.setName("Positive");
			seriesPos.setStack(entity);
			HiChartSeries seriesNeg = new HiChartSeries();
			seriesNeg.setName("Negative");
			seriesNeg.setStack(entity);
			for (KeyWord keyword : keyWords) {
				words.add(keyword.getKeyWord());
				seriesPos.getData().add(keyword.getPosCount());
				seriesNeg.getData().add(
						keyword.getcount() - keyword.getPosCount());
			}
			series.add(seriesPos);
			series.add(seriesNeg);
			List<EntitySubProductMap> compList = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDaoConnector()
					.getCompetitors(IdMap.getEntityId(entity),
							IdMap.getSubproductId(subProduct));
			for (EntitySubProductMap compEntitySubProduct : compList) {
				int count = 0;
				List<KeyWord> keyWordsComp = BeeGoodDBConnector
						.getBarChartDataForSubTopicsTopics(IdMap
								.getEntityId(compEntitySubProduct.getEntity()),
								IdMap.getSubproductId(compEntitySubProduct
										.getSubProduct()), words, subDimension,
										source);
				HiChartSeries compSeriesPos = new HiChartSeries();
				compSeriesPos.setName(compEntitySubProduct.getEntity()
						+ " -  Positive");
				compSeriesPos.setStack(compEntitySubProduct.getEntity());
				HiChartSeries compSeriesNeg = new HiChartSeries();
				compSeriesNeg.setName(compEntitySubProduct.getEntity()
						+ " - Negative");
				compSeriesNeg.setStack(compEntitySubProduct.getEntity());
				for (KeyWord keyword : keyWordsComp) {
					while (count < words.indexOf(keyword.getKeyWord())) {
						compSeriesPos.getData().add(0);
						compSeriesNeg.getData().add(0);
						count++;
					}
					compSeriesPos.getData().add(keyword.getPosCount());
					compSeriesNeg.getData().add(
							keyword.getcount() - keyword.getPosCount());
					count++;
				}
				series.add(compSeriesPos);
				series.add(compSeriesNeg);
			}
			HiChartsBarChartData data = new HiChartsBarChartData();
			data.setSeries(series);
			data.setWords(words);
			returnString = new Gson().toJson(data);

		} else if (type.equals("topics")) {

			List<KeyWord> keyWords = BeeGoodDBConnector
					.getBarChartDataForTopics(IdMap.getEntityId(entity),
							IdMap.getSubproductId(subProduct), null, "null");

			ArrayList<WordData> subDimensions = new ArrayList<>();
			ArrayList<String> subDimensionWords = new ArrayList<>();
			HiChartSeries seriesPos = new HiChartSeries();
			seriesPos.setName("Positive");
			seriesPos.setStack(entity);
			HiChartSeries seriesNeg = new HiChartSeries();
			seriesNeg.setName("Negative");
			seriesNeg.setStack(entity);

			for (KeyWord keyword : keyWords) {
				if (keyword.getKeyWord().equals("competition")
						|| keyword.getKeyWord().equals("pricing and offers")) {
					continue;
				}
				WordData data = new WordData();
				data.setWord(keyword.getKeyWord());
				DecimalFormat df = new DecimalFormat();
				df.setMaximumFractionDigits(0);
				double percentage = keyword.getPosCount()
						/ (double) keyword.getcount() * 100;
				int totCount = keyword.getcount();
				BeeGoodTopicData topicData = new BeeGoodTopicData();
				topicData.setProductName(entity);
				topicData.setReviewsCount(totCount);
				topicData.setPosReviewspercentage(Double.valueOf(df
						.format(percentage)));
				data.getWordStats().add(topicData);
				subDimensions.add(data);
				subDimensionWords.add(keyword.getKeyWord());
			}

			int count = 0;
			List<KeyWord> keyWordsComp = BeeGoodDBConnector
					.getBarChartDataForTopics(429, 36, subDimensionWords,
							"null");

			for (KeyWord keyword : keyWordsComp) {
				while (count < subDimensionWords.indexOf(keyword.getKeyWord())) {

					BeeGoodTopicData topicData = new BeeGoodTopicData();
					topicData.setProductName("burtsbee");
					topicData.setReviewsCount(0);
					topicData.setPosReviewspercentage(0);
					subDimensions.get(count).getWordStats().add(topicData);
					count++;
				}
				DecimalFormat df = new DecimalFormat();
				df.setMaximumFractionDigits(0);
				double percentage = keyword.getPosCount()
						/ (double) keyword.getcount() * 100;
				int totCount = keyword.getcount();
				BeeGoodTopicData topicData = new BeeGoodTopicData();
				topicData.setProductName("burtsbee");
				topicData.setReviewsCount(totCount);
				topicData.setPosReviewspercentage(Double.valueOf(df
						.format(percentage)));
				subDimensions.get(count).getWordStats().add(topicData);
				count++;
			}
			returnString = new Gson().toJson(subDimensions);

		} else if (type.equals("avgScore")) {

			double score = BeeGoodDBConnector.getAvgScore(
					IdMap.getEntityId(entity),
					IdMap.getSubproductId(subProduct));

			double competitorScore = BeeGoodDBConnector.getAvgScore(429, 36);
			returnString = "{\"AvgScore\":"
					+ score
					+ ",\"CompetitorsScores\":[{\"Name\":\"Burts Bee\",\"AvgScore\":"
					+ competitorScore + "}]}";

		} else if (type.equals("prodData")) {
			List<BrandData> brandDataList = new ArrayList<>();
			BrandData prodData = BeeGoodDBConnector.getBrandData(
					IdMap.getEntityId(entity),
					IdMap.getSubproductId(subProduct));
			prodData.setEntity(entity);
			prodData.setSubProduct(subProduct);
			prodData.setOverallScore(BeeGoodDBConnector.getAvgScore(
					IdMap.getEntityId(entity),
					IdMap.getSubproductId(subProduct)));
			prodData.setOverAllRating(Util.getRatingFromScore(prodData
					.getOverallScore()));
			brandDataList.add(prodData);

			List<EntitySubProductMap> compList = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getDaoConnector()
					.getCompetitors(IdMap.getEntityId(entity),
							IdMap.getSubproductId(subProduct));
			for (EntitySubProductMap compEntitySubProduct : compList) {

				BrandData compData = BeeGoodDBConnector.getBrandData(IdMap
						.getEntityId(compEntitySubProduct.getEntity()), IdMap
						.getSubproductId(compEntitySubProduct.getSubProduct()));
				compData.setEntity(compEntitySubProduct.getEntity());
				compData.setSubProduct(compEntitySubProduct.getSubProduct());
				compData.setOverallScore(BeeGoodDBConnector.getAvgScore(IdMap
						.getEntityId(compEntitySubProduct.getEntity()), IdMap
						.getSubproductId(compEntitySubProduct.getSubProduct())));
				compData.setOverAllRating(Util.getRatingFromScore(compData
						.getOverallScore()));
				brandDataList.add(compData);
			}
			returnString = new Gson().toJson(brandDataList);
		} else if (type.equals("commentsCount")) {
			returnString = BeeGoodDBConnector.getPosNegCommentsCountFor(entity,
					subProduct);
		} else if (type.equals("comments")) {
			String subDimension = request.getParameter("subDimension");
			String trait = request.getParameter("trait");

			returnString = BeeGoodDBConnector.getComments(entity, subProduct,
					trait, subDimension, 100, source);
		}
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write(returnString);
		writer.flush();
		writer.close();
	}

}
