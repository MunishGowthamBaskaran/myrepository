package com.raremile.prd.inferlytics.database;

import static com.raremile.prd.inferlytics.database.DAOUtil.close;
import static com.raremile.prd.inferlytics.database.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.entity.CommentsHtml;
import com.raremile.prd.inferlytics.entity.IdMap;
import com.raremile.prd.inferlytics.entity.KeyWord;
import com.raremile.prd.inferlytics.entity.Post;
import com.raremile.prd.inferlytics.entity.Product;
import com.raremile.prd.inferlytics.entity.StoreProductDetails;
import com.raremile.prd.inferlytics.entity.SunBurstData;
import com.raremile.prd.inferlytics.exception.DAOException;
import com.raremile.prd.inferlytics.utils.BusinessUtilityForAnalytics;
import com.raremile.prd.inferlytics.utils.ChartData;

public class DAOAnalyticsData {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(DAOAnalyticsData.class);

	// Queries
	private static final String GETDATA_SUBDIMENSION_DAY = "Select Date(act_date),countDate FROM "
			+ "rm_lexicondb.date_dim_new LEFT JOIN"
			+ "(SELECT COUNT(*) countDate,DATE(CreatedDate) as dateCreatedDate FROM "
			+ "rm_lexicondb.productcommentanalytics where Entity= ? "
			+ "and SubProduct=? and SubDimension is NOT NULL "
			+ "and ProductId is NULL and PostId IS NULL and"
			+ " ReviewType IS NULL GROUP BY DATE(CreatedDate)) as "
			+ "b on act_date=b.dateCreatedDate WHERE Date(act_date)  BETWEEN ? AND ? ";
	private static final String GETDATA_SUBDIMENSION_STORE_DAY = "Select Date(act_date),countDate FROM "
			+ "rm_lexicondb.date_dim_new LEFT JOIN"
			+ "(SELECT COUNT(*) countDate,DATE(CreatedDate) as dateCreatedDate FROM "
			+ "rm_lexicondb.productcommentanalytics where Entity= ? "
			+ "and SubProduct=?  and PostId IS NULL and"
			+ " ReviewType IS NULL GROUP BY DATE(CreatedDate)) as "
			+ "b on act_date=b.dateCreatedDate WHERE Date(act_date)  BETWEEN ? AND ? ";
	private static final String GETDATA_SUBDIMENSION_MONTH = "Select Date(act_date),SUM(countDate) "
			+ "FROM rm_lexicondb.date_dim_new LEFT JOIN(SELECT COUNT(*) countDate,DATE(CreatedDate) as "
			+ "dateCreatedDate FROM rm_lexicondb.productcommentanalytics "
			+ "where Entity= ? and SubProduct=? and SubDimension is NOT NULL and"
			+ " ProductId is NULL and PostId IS NULL and ReviewType IS NULL GROUP BY "
			+ "DAY(CreatedDate)) as b on act_date=b.dateCreatedDate "
			+ "WHERE Date(act_date)  BETWEEN ? AND ? "
			+ "GROUP BY YEAR(act_date),MONTH(act_date)";
	private static final String GETDATA_SUBDIMENSION_STORE_MONTH = "Select Date(act_date),SUM(countDate) "
			+ "FROM rm_lexicondb.date_dim_new LEFT JOIN(SELECT COUNT(*) countDate,DATE(CreatedDate) as "
			+ "dateCreatedDate FROM rm_lexicondb.productcommentanalytics "
			+ "where Entity= ? and SubProduct=?  and PostId IS NULL and ReviewType IS NULL GROUP BY "
			+ "DAY(CreatedDate)) as b on act_date=b.dateCreatedDate "
			+ "WHERE Date(act_date)  BETWEEN ? AND ? "
			+ "GROUP BY YEAR(act_date),MONTH(act_date)";

	private static final String GETDATA_SUBDIMENSION_WEEK = "Select Date(act_date),SUM(countDate) "
			+ "FROM rm_lexicondb.date_dim_new LEFT JOIN(SELECT COUNT(*) countDate,DATE(CreatedDate) as "
			+ "dateCreatedDate FROM rm_lexicondb.productcommentanalytics "
			+ "where Entity= ? and SubProduct=? and SubDimension is NOT NULL and"
			+ " ProductId is NULL and PostId IS NULL and ReviewType IS NULL GROUP BY "
			+ "DAY(CreatedDate)) as b on act_date=b.dateCreatedDate "
			+ "WHERE Date(act_date)  BETWEEN ? AND ? "
			+ "GROUP BY YEAR(act_date),week_of_year";
	private static final String GETDATA_SUBDIMENSION_STORE_WEEK = "Select Date(act_date),SUM(countDate) "
			+ "FROM rm_lexicondb.date_dim_new LEFT JOIN(SELECT COUNT(*) countDate,DATE(CreatedDate) as "
			+ "dateCreatedDate FROM rm_lexicondb.productcommentanalytics "
			+ "where Entity= ? and SubProduct=? and PostId IS NULL and ReviewType IS NULL GROUP BY "
			+ "DAY(CreatedDate)) as b on act_date=b.dateCreatedDate "
			+ "WHERE Date(act_date)  BETWEEN ? AND ? "
			+ "GROUP BY YEAR(act_date),week_of_year";

	private static final String GETDATA_TOP_SUBDIMENSIONS = "Select  SubDimension,Count(SubDimension)from rm_lexicondb.productcommentanalytics Where Entity=? and SubProduct=? and Category='feature' and Word is NULL and ProductId is NULL and "
			+ "PostId IS NULL and ReviewType IS NULL and  Date(CreatedDate) Between ? AND ? Group by SubDimension order by Count(SubDimension)  DESC LIMIT 5";
	private static final String GETDATA_TOP_SUBDIMENSIONS_FOR_STORE = "Select  SubDimension,Count(SubDimension)from rm_lexicondb.productcommentanalytics Where Entity=? and SubProduct=? and Category='analysissummary' and Word is NULL and "
			+ "PostId IS NULL and ReviewType IS NULL and  Date(CreatedDate) Between ? AND ? Group by SubDimension order by Count(SubDimension)  DESC LIMIT 5";

	private static final String GETDATA_TOP_WORDS = "Select  Word,Count(Word) from rm_lexicondb.productcommentanalytics Where Entity=? and SubProduct=? and Category='feature'  and Word is Not NULL and ProductId is NULL and "
			+ "PostId IS NULL and ReviewType IS NULL and  Date(CreatedDate) Between ? AND ? Group by Word order by Count(Word)  DESC LIMIT 5";
	private static final String GETDATA_TOP_WORDS_FOR_STORE = "Select  Word,Count(Word) from rm_lexicondb.productcommentanalytics Where Entity=? and SubProduct=? and Category='analysissummary'  and Word is Not NULL  and "
			+ "PostId IS NULL and ReviewType IS NULL and  Date(CreatedDate) Between ? AND ? Group by Word order by Count(Word)  DESC LIMIT 5";

	private static final String GETDATA_NO_USERS = "Select Count(Distinct(UserIp))  from rm_lexicondb.productcommentanalytics where  Entity=? and SubProduct=? and date(CreatedDate) Between ? AND ?";

	private static final String GETDATA_TOP_PRODUCTS = "Select ProductId,Count(ProductId) from rm_lexicondb.productcommentanalytics where Entity=? and SubProduct=? and "
			+ "	Category='feature' and ReviewType is Not NULL and PostId is NULL and date(CreatedDate)"
			+ " BETWEEN ? AND ? group by ProductId order by Count(ProductId) DESC LIMIT ?,5";
	private static final String GETDATA_TOP_PRODUCTS_FOR_STORE = "Select ProductId,Count(ProductId) from rm_lexicondb.productcommentanalytics where Entity=? and SubProduct=? and "
			+ "	Category='analysissummary' and ReviewType is Not NULL and PostId is NULL and date(CreatedDate)"
			+ " BETWEEN ? AND ? group by ProductId order by Count(ProductId) DESC LIMIT ?,5";

	private static final String GETDATA_TOTAL_PRODUCTS_COUNT = "Select Count(*) From (Select Count(*) from rm_lexicondb.productcommentanalytics where Entity=? and SubProduct=? and 	"
			+ "	Category='feature' and ReviewType is Not NULL and PostId is NULL and date(CreatedDate)"
			+ " BETWEEN ? AND ? group by ProductId) subquery";
	private static final String GETDATA_TOTAL_PRODUCTS_COUNT_FOR_STORE = "Select Count(*) From (Select Count(*) from rm_lexicondb.productcommentanalytics where Entity=? and SubProduct=? and 	"
			+ "	Category='analysissummary' and ReviewType is Not NULL and PostId is NULL and date(CreatedDate)"
			+ " BETWEEN ? AND ? group by ProductId) subquery";

	private static final String GETDATA_REVIEW_CLICKSCOUNT = "Select ReviewType,Count(*) from rm_lexicondb.productcommentanalytics where Entity=? and "
			+ "SubProduct=? and Category='feature' and ReviewType is Not NULL and PostId is NOT NULL and date(CreatedDate) BETWEEN ? AND ? group by ReviewType;";
	private static final String GETDATA_REVIEW_CLICKSCOUNT_FOR_STORE = "Select ReviewType,Count(*) from rm_lexicondb.productcommentanalytics where Entity=? and "
			+ "SubProduct=? and Category='analysissummary' and ReviewType is Not NULL and PostId is NOT NULL and date(CreatedDate) BETWEEN ? AND ? group by ReviewType;";

	private static final String GETDATA_TOP_POSTS = "Select PostId,ProductId,Count(PostId) from rm_lexicondb.productcommentanalytics where Entity=? and SubProduct=? and "
			+ "Category='feature' and  PostId is NOT NULL and date(CreatedDate) BETWEEN ? AND ? group by PostId order by Count(ProductId) DESC,PostId ASC LIMIT ?,5";
	private static final String GETDATA_TOP_POSTS_FOR_STORE = "Select PostId,ProductId,Count(PostId) from rm_lexicondb.productcommentanalytics where Entity=? and SubProduct=? and "
			+ "Category='analysissummary' and  PostId is NOT NULL and date(CreatedDate) BETWEEN ? AND ? group by PostId order by Count(ProductId) DESC,PostId ASC LIMIT ?,5";

	private static final String GETDATA_TOTAL_POSTS_COUNT = "Select Count(*) FROM (Select PostId,ProductId,Count(PostId) from rm_lexicondb.productcommentanalytics where Entity=? and SubProduct=? "
			+ "and Category='feature' and  PostId is NOT NULL and date(CreatedDate) BETWEEN ? AND ? group by PostId)s ;";
	private static final String GETDATA_TOTAL_POSTS_COUNT_FOR_STORE = "Select Count(*) FROM (Select PostId,ProductId,Count(PostId) from rm_lexicondb.productcommentanalytics where Entity=? and SubProduct=? "
			+ "and Category='analysissummary' and  PostId is NOT NULL and date(CreatedDate) BETWEEN ? AND ? group by PostId)s ;";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// TODO Auto-generated method stub

		/*
		 * DAOFactory
		 * .getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
		 * .getDAOAnalyticsData() .getSubDimensionData("moltonbrown",
		 * "fragrance", "12/01/2013", "01/20/2014", "day");
		 */

		/*
		 * DAOFactory
		 * .getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
		 * .getDAOAnalyticsData() .getTopSubDimensionandWords("moltonbrown",
		 * "fragrance", "12/01/2013", "01/20/2014");
		 */

		/*
		 * DAOFactory
		 * .getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
		 * .getDAOAnalyticsData() .getPosts("moltonbrown", "fragrance",
		 * "12/01/2013", "01/20/2014", 1);
		 */

		/*
		 * DAOFactory.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
		 * .getDAOAnalyticsData().formatDate("01/12/2014");
		 */
		/*
		 * DAOFactory.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
		 * .getDAOAnalyticsData() .getPieChartData("", "", null);
		 */

		/*
		 * Date startDateFormatted = DAOFactory
		 * .getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
		 * .getDAOAnalyticsData().formattoISODate("1/01/2014"); Date
		 * endDateFormatted = DAOFactory
		 * .getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
		 * .getDAOAnalyticsData().formattoISODate("6/06/2014");
		 * DAOFactory.getInstance
		 * (ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
		 * .getDAOAnalyticsData() .getMapOFDatesForWeek(startDateFormatted,
		 * endDateFormatted);
		 */

		DAOFactory
		.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
		.getDAOAnalyticsData()
		.getTimeLineData("", "", "1/01/2014", "6/06/2014", true, null);
		/*
		 * DAOFactory
		 * .getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
		 * .getDAOAnalyticsData() .getCommentsForEmployees("macys", "store",
		 * "AyCwZLVsU6IwkML4qzBPHQ", "alfani", true);
		 */
		/*
		 * DAOFactory
		 * .getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
		 * .getDAOAnalyticsData() .getTimelineTraitsForComparison("moltonbrown",
		 * "fragranceAW", "12/01/2009", "01/20/2014", "12/01/2012",
		 * "01/20/2014");
		 */
	}

	private final DAOFactory daoFactory;

	public static final Comparator<Product> PRODUCTS_ORDER = new Comparator<Product>() {

		@Override
		public int compare(Product o1, Product o2) {

			int retValue = 0;
			if (o1.getClickCount() < o2.getClickCount()) {
				retValue = -1;
			} else if (o1.getClickCount() > o2.getClickCount()) {
				retValue = 1;
			} /*
			 * else { // check for the productid if (o1.getProductId() != null
			 * && o2.getProductId() != null) { retValue =
			 * o1.getProductId().compareTo(o2.getProductId()); } }
			 */
			return retValue;
		}
	};

	public static final Comparator<KeyWord> KEYWORDS_ORDER = new Comparator<KeyWord>() {

		@Override
		public int compare(KeyWord o1, KeyWord o2) {

			int retValue = 0;
			if (o1.getPercentage() < o2.getPercentage()) {
				retValue = -1;
			} else if (o1.getPercentage() > o2.getPercentage()) {
				retValue = 1;
			}
			return retValue;
		}
	};

	public DAOAnalyticsData(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	/**
	 * Takes a date in mm/dd/yyyy format and returns in yyyy-mm-dd (as used in
	 * database).
	 * 
	 * @param date
	 *            Date as string in mm/dd/yyyy format.
	 * @return Date in yyyy-mm-dd format.
	 */

	public String formatDate(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yyyy");
		SimpleDateFormat databseFormat = new SimpleDateFormat("yyyy-mm-dd");
		String returnString = "";
		try {
			returnString = databseFormat.format(formatter.parse(date));
		} catch (Exception e) {
			LOG.info(e);

		}
		return returnString;
	}

	/**
	 * Takes date in MM/dd/yyyy format and returns in ISO Format.
	 * 
	 * @param date
	 * @return Date in ISO format.
	 */
	public Date formattoISODate(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat mongoDBFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS");

		Date newDate = null;
		try {
			String parsedString = mongoDBFormat.format(formatter.parse(date));
			newDate = mongoDBFormat.parse(parsedString);
		} catch (Exception e) {
			LOG.info("Excepton parsing ISO date:" + e);

		}
		return newDate;
	}

	public String getblogsForEvents(String entity, String subProduct,
			String storeId, String word, boolean isPositive,
			String subDimension, int skip) {
		List<Post> posts = new ArrayList<>();

		posts = MongoConnector.getPostIdsForFeatures(IdMap.getEntityId(entity),
				IdMap.getSubproductId(subProduct), subDimension, word, storeId,
				skip, 1, isPositive);
		MongoConnector.getBlogsByIds(posts);
		String resultJson = BusinessUtilityForAnalytics.getHtmlForBlogs(posts
				.get(0));
		resultJson = "{\"SentenceNo\":" + posts.get(0).getSentenceNo()
				+ ",\"BlogContent\":" + "[" + resultJson + "],\"Title\":\""
				+ posts.get(0).getTitle() + "\"}";
		return resultJson;
	}

	public String getblogsForEventsFeatures(String entity, String subProduct,
			String storeId, boolean isPositive, String subDimension, int skip) {
		List<Post> posts = new ArrayList<>();

		posts = MongoConnector.getPostIdsForFeatures(IdMap.getEntityId(entity),
				IdMap.getSubproductId(subProduct), subDimension, "null",
				storeId, skip, 4, isPositive);
		MongoConnector.getBlogsByIds(posts);
		String resultJson = BusinessUtilityForAnalytics.getHtmlForBlogs(posts,
				subDimension);

		return resultJson;
	}

	public String getCommentsForEnterPriseCar(String entity, String subProduct,
			String state, String word, boolean isPositive, String subDimension,
			int skip, String storeId) {
		List<Post> posts = new ArrayList<>();
		int limit = 5;

		posts = MongoConnectorForMapPage.getPostIdsForEnterPriseCar(
				IdMap.getEntityId(entity), IdMap.getSubproductId(subProduct),
				subDimension, word, state, skip, limit, isPositive, storeId);
		MongoConnector.getFeedsByIds(posts);
		String resultJson = BusinessUtilityForAnalytics
				.getCommentsHtmlForStores(posts, "-bw-");
		return resultJson;
	}

	/**
	 * Returns all the comments associated with a word and returns a json
	 * structure.
	 * 
	 * @param entity
	 * @param subProduct
	 * @param storeId
	 * @param word
	 * @param isPositive
	 * @param subDimension
	 * @return
	 */
	public String getCommentsForStore(String entity, String subProduct,
			String storeId, String word, boolean isPositive,
			String subDimension, int skip) {
		List<Post> posts = new ArrayList<>();
		int limit = 5;
		if (storeId == null) {
			limit = 1;
		}
		posts = MongoConnector.getPostIdsForFeatures(IdMap.getEntityId(entity),
				IdMap.getSubproductId(subProduct), subDimension, word, storeId,
				skip, limit, isPositive);
		MongoConnector.getFeedsByIds(posts);
		String resultJson = BusinessUtilityForAnalytics
				.getCommentsHtmlForStores(posts, "-bw-");
		return resultJson;
	}

	private Map<String, ChartData> getFormattedDateandChartData(
			Calendar calendardate) {
		String formatedStartDate = calendardate.get(Calendar.YEAR) + "-"
				+ (calendardate.get(Calendar.MONTH) + 1) + "-"
				+ calendardate.get(Calendar.DATE);
		ChartData chdata = new ChartData();
		chdata.setValue("0");
		chdata.setDate(formatedStartDate);
		String monthYearformat = (calendardate.get(Calendar.MONTH) + 1) + "-"
				+ calendardate.get(Calendar.YEAR);
		Map<String, ChartData> returndata = new HashMap<>();
		returndata.put(monthYearformat, chdata);
		return returndata;
	}

	private Map<String, ChartData> getFormattedDateandChartDataForWeek(
			Calendar calendardate) {
		String formatedStartDate = calendardate.get(Calendar.YEAR) + "-"
				+ (calendardate.get(Calendar.MONTH) + 1) + "-"
				+ calendardate.get(Calendar.DATE);
		ChartData chdata = new ChartData();
		chdata.setValue("0");
		chdata.setDate(formatedStartDate);
		String monthYearformat = (calendardate.get(Calendar.WEEK_OF_YEAR) + 1)
				+ "-" + calendardate.get(Calendar.YEAR);
		Map<String, ChartData> returndata = new HashMap<>();
		returndata.put(monthYearformat, chdata);
		return returndata;
	}

	private Map<Date, String> getMapOFDates(Date startDate, Date endDate) {

		Map<Date, String> mapOfDates = new HashMap<>();
		while (!startDate.equals(endDate)) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.add(Calendar.DATE, 1);
			startDate = calendar.getTime();
			mapOfDates.put(startDate, "0");
		}
		return mapOfDates;
	}

	private Map<String, ChartData> getMapOFDatesForMonths(Date startDate,
			Date endDate) {

		Map<String, ChartData> mapOfDates = new HashMap<>();

		Calendar calendarStartDate = Calendar.getInstance();
		calendarStartDate.setTime(startDate);
		Calendar calendarEndDate = Calendar.getInstance();
		calendarEndDate.setTime(endDate);

		// Insert start date into a map.
		mapOfDates.putAll(getFormattedDateandChartData(calendarStartDate));

		// If end date is also is in the same month then return the map.
		if ((calendarStartDate.get(Calendar.YEAR) == calendarEndDate
				.get(Calendar.YEAR))
				&& (calendarStartDate.get(Calendar.MONTH) == calendarEndDate
				.get(Calendar.MONTH))) {
			return mapOfDates;
		} else {
			// Insert end date into the map.
			mapOfDates.putAll(getFormattedDateandChartData(calendarEndDate));

			// Increment the start date to point to the next month.
			calendarStartDate.add(Calendar.MONTH, 1);

			// Update the start date to point to the first day of that month.
			calendarStartDate.set(calendarStartDate.get(Calendar.YEAR),
					calendarStartDate.get(Calendar.MONTH), 1);

			// Now insert all the months along with day and year within the
			// given range into the map.
			while (calendarStartDate.get(Calendar.MONTH) != calendarEndDate
					.get(Calendar.MONTH)
					|| calendarStartDate.get(Calendar.YEAR) != calendarEndDate
					.get(Calendar.YEAR)) {
				mapOfDates
				.putAll(getFormattedDateandChartData(calendarStartDate));
				calendarStartDate.add(Calendar.MONTH, 1);
			}
		}
		return mapOfDates;
	}

	private Map<String, ChartData> getMapOFDatesForWeek(Date startDate,
			Date endDate) {

		Map<String, ChartData> mapOfDates = new HashMap<>();

		Calendar calendarStartDate = Calendar.getInstance();
		calendarStartDate.setTime(startDate);
		Calendar calendarEndDate = Calendar.getInstance();
		calendarEndDate.setTime(endDate);

		// Insert start date into a map.
		mapOfDates
		.putAll(getFormattedDateandChartDataForWeek(calendarStartDate));

		// If end date is also is in the same week then return the map.
		if ((calendarStartDate.get(Calendar.YEAR) == calendarEndDate
				.get(Calendar.YEAR))
				&& (calendarStartDate.get(Calendar.WEEK_OF_YEAR) == calendarEndDate
				.get(Calendar.WEEK_OF_YEAR))) {
			return mapOfDates;
		} else {
			// Insert end date into the map.
			mapOfDates
			.putAll(getFormattedDateandChartDataForWeek(calendarEndDate));

			// point calendar to coming sunday
			calendarStartDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

			// Now insert all the weeks along with day month and year within the
			// given range into the map.
			while (calendarStartDate.get(Calendar.WEEK_OF_YEAR) != calendarEndDate
					.get(Calendar.WEEK_OF_YEAR)
					|| calendarStartDate.get(Calendar.YEAR) != calendarEndDate
					.get(Calendar.YEAR)) {
				mapOfDates
				.putAll(getFormattedDateandChartDataForWeek(calendarStartDate));
				calendarStartDate.add(Calendar.WEEK_OF_YEAR, 1);
			}
		}

		return mapOfDates;
	}

	public String getNamedEmployees(String entity, String subProduct,
			String storeId, boolean isPositive, String category) {

		List<String> result = MongoConnectorForAnalytics.getEmployees(
				IdMap.getEntityId(entity), IdMap.getSubproductId(subProduct),
				storeId, isPositive);
		String finalJson = BusinessUtilityForAnalytics.getNamedEmployeesHtml(
				result, isPositive, category);
		return finalJson;
	}

	/**
	 * @param entity
	 * @param subProduct
	 * @param object
	 * @return
	 */
	public String getPartitionData(String entity, String subProduct,
			String storeId) {

		List<SunBurstData> data = new ArrayList<>();
		List<KeyWord> keywords = MongoConnectorForAnalytics.getDataForPieChart(
				IdMap.getEntityId(entity), IdMap.getSubproductId(subProduct),
				storeId);
		for (KeyWord keyword : keywords) {
			SunBurstData sunBurstData = new SunBurstData();
			sunBurstData.setName(keyword.getKeyWord());
			sunBurstData.setSize(keyword.getTotalCount());
			sunBurstData.setPosCount(keyword.getPosCount());
			sunBurstData.setNegCount(keyword.getNegativeCount());
			data.add(sunBurstData);
		}
		// Collections.sort(data, BusinessUtil.FEATURE_ORDER);
		SunBurstData finalJson = new SunBurstData();
		finalJson.setName("topics");
		finalJson.setChildren(data);
		String resultantJson = new Gson().toJson(finalJson);
		return resultantJson;
	}

	public String getPieChartData(String entity, String subProduct,
			String storeId) {

		List<KeyWord> keywords = MongoConnectorForAnalytics.getDataForPieChart(
				IdMap.getEntityId(entity), IdMap.getSubproductId(subProduct),
				storeId);
		for (KeyWord keyword : keywords) {
			int posPercentage = (int) Math
					.ceil(((double) keyword.getPosCount() / keyword
							.getTotalCount()) * 100);
			keyword.setPercentage(posPercentage);
		}
		Collections.sort(keywords, KEYWORDS_ORDER);
		String resultantJson = "{\"KeywordsData\":"
				+ new Gson().toJson(keywords) + "}";
		return resultantJson;
	}

	public String getPieChartDataBeegood(String entity, String subProduct,
			String storeId) {

		List<KeyWord> keywords = BeeGoodDBConnector.getDataForPieChart(
				IdMap.getEntityId(entity), IdMap.getSubproductId(subProduct),
				storeId);
		for (KeyWord keyword : keywords) {
			int posPercentage = (int) Math
					.ceil(((double) keyword.getPosCount() / keyword
							.getTotalCount()) * 100);
			keyword.setPercentage(posPercentage);
		}
		Collections.sort(keywords, KEYWORDS_ORDER);
		String resultantJson = "{\"KeywordsData\":"
				+ new Gson().toJson(keywords) + "}";
		return resultantJson;
	}
	public String getPieChartDataForEnterpriseCar(String entity,
			String subProduct, String storeId, String state) {

		List<KeyWord> keywords = MongoConnectorForMapPage
				.getEnterpriseCarDataForPieChart(IdMap.getEntityId(entity),
						IdMap.getSubproductId(subProduct), storeId, state);
		for (KeyWord keyword : keywords) {
			int posPercentage = (int) Math
					.ceil(((double) keyword.getPosCount() / keyword
							.getTotalCount()) * 100);
			keyword.setPercentage(posPercentage);
		}
		Collections.sort(keywords, KEYWORDS_ORDER);
		String resultantJson = "{\"KeywordsData\":"
				+ new Gson().toJson(keywords) + "}";
		return resultantJson;
	}

	/**
	 * This method fetches top posts that have been viewed by the users.
	 * 
	 * @param entity
	 *            Entity name
	 * @param subProduct
	 *            subProduct Name
	 * @param startDate
	 *            Starting date for date range.
	 * @param endDate
	 *            Ending Date for date range
	 * @param limit
	 *            no of posts to fetch at once.
	 * @param integer
	 * @return returns posts in a json fromat.
	 */
	public String getPosts(String entity, String subProduct, String startDate,
			String endDate, int limit, Integer type) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		startDate = formatDate(startDate);
		endDate = formatDate(endDate);
		int limits = (limit - 1) * 5;
		List<Post> posts = new ArrayList<>();
		List<Product> productList = new ArrayList<>();
		Map<String, Product> productIdFeed = new HashMap<>();
		String returnString = null;
		int totalProductCount = 0;
		try {
			connection = daoFactory.getConnection();
			Object[] values = { entity, subProduct, startDate, endDate, limits };
			if (type == 2) {
				preparedStatement = prepareStatement(connection,
						GETDATA_TOP_POSTS_FOR_STORE, false, values);
			} else {
				preparedStatement = prepareStatement(connection,
						GETDATA_TOP_POSTS, false, values);
			}
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Product product = new Product();
				Post post = new Post();
				post.setPostId(resultSet.getString(1));
				product.setProductId(resultSet.getString(2));
				post.setClicks(resultSet.getString(3));
				product.setPostId(resultSet.getString(1));
				productList.add(product);
				posts.add(post);
			}

			/*
			 * Called only at the beginning to get the total no of posts for
			 * pagination.
			 */
			if (limit == 1) {
				Object[] valuesForSecondQuery = { entity, subProduct,
						startDate, endDate };
				if (type == 2) {
					preparedStatement = prepareStatement(connection,
							GETDATA_TOTAL_POSTS_COUNT_FOR_STORE, false,
							valuesForSecondQuery);

				} else {
					preparedStatement = prepareStatement(connection,
							GETDATA_TOTAL_POSTS_COUNT, false,
							valuesForSecondQuery);
				}
				resultSet = preparedStatement.executeQuery();
				resultSet.next();
				totalProductCount = (int) Math.ceil((double) resultSet
						.getInt(1) / 5);
			}

			if (posts.size() != 0) {
				MongoConnector.getFeedsByIds(posts);
				if (entity.equals("nike")) {
					MongoConnector.getProductDetailsById(productList);
				} else {
					MongoConnector.getProductDetails(productList);
				}
				for (Product product : productList) {
					productIdFeed.put(product.getPostId(), product);
				}
				for (Post post : posts) {
					post.setProduct(productIdFeed.get(post.getPostId()));
				}
				List<CommentsHtml> commentsHtml = BusinessUtilityForAnalytics
						.getCommentsHtmlForTopPosts(posts, "-bw-",
								totalProductCount, false);
				returnString = new Gson().toJson(commentsHtml);

			} else {
				returnString = "No Posts Found in this Given Date Range";
			}
		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		return returnString;
	}

	/**
	 * Fetches the top products based on clicks by users along with number of
	 * clicks and number of relative positive and negative Comments.
	 * 
	 * @param entity
	 *            Entity Name
	 * @param subProduct
	 *            SubProduct Name
	 * @param startDate
	 *            Startdate for the date range.
	 * @param endDate
	 *            End date for the date range.
	 * @param limit
	 *            No of Products to fetch at once.
	 * @param integer
	 * @return returns a json formatted products along with the details.
	 */

	public String getProducts(String entity, String subProduct,
			String startDate, String endDate, int limit, Integer type) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		startDate = formatDate(startDate);
		endDate = formatDate(endDate);
		int limitFirst = (limit - 1) * 5;

		int totalProductCount = 0;
		List<Product> products = new ArrayList<>();
		String returnString = null;
		try {
			connection = daoFactory.getConnection();
			Object[] values = { entity, subProduct, startDate, endDate,
					limitFirst };
			if (type == 2) {
				preparedStatement = prepareStatement(connection,
						GETDATA_TOP_PRODUCTS_FOR_STORE, false, values);
			} else {
				preparedStatement = prepareStatement(connection,
						GETDATA_TOP_PRODUCTS, false, values);
			}
			resultSet = preparedStatement.executeQuery();
			Map<String, Integer> productidAndCount = new HashMap<String, Integer>();
			while (resultSet.next()) {
				productidAndCount.put(resultSet.getString(1),
						resultSet.getInt(2));
			}
			if (limit == 1) {
				Object[] valuesForSecondQuery = { entity, subProduct,
						startDate, endDate };
				if (type == 2) {
					preparedStatement = prepareStatement(connection,
							GETDATA_TOTAL_PRODUCTS_COUNT_FOR_STORE, false,
							valuesForSecondQuery);
				} else {
					preparedStatement = prepareStatement(connection,
							GETDATA_TOTAL_PRODUCTS_COUNT, false,
							valuesForSecondQuery);
				}
				resultSet = preparedStatement.executeQuery();
				resultSet.next();
				totalProductCount = (int) Math.ceil((double) resultSet
						.getInt(1) / 5);
			}
			if (productidAndCount.size() != 0) {
				String[] productids = productidAndCount.keySet().toArray(
						new String[productidAndCount.size()]);
				products = MongoConnector
						.getTopProductCommentsCount(productids);
				if (entity.equals("nike")) {
					products = MongoConnector.getProductDetailsById(products);
				} else {
					products = MongoConnector.getProductDetails(products);
				}

				for (Product product : products) {
					product.setClickCount(productidAndCount.get(product
							.getProductId()));
				}
				Collections.sort(products,
						Collections.reverseOrder(PRODUCTS_ORDER));
				returnString = BusinessUtilityForAnalytics
						.getTopProductsHtmlForAnalytics(products,
								totalProductCount);

			} else {
				returnString = "No Products Found in this Given Date Range";
			}
		} catch (SQLException e) {
			LOG.info(e);
			throw new DAOException(e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		return returnString;
	}

	/**
	 * This method fetches the recent comments written by the users in a given
	 * date range, if no posts are found in the given date range then returns
	 * the latest posts.
	 * 
	 * @param entity
	 *            Entity Name
	 * @param subProduct
	 *            SubProductName
	 * @param startDate
	 *            Starting date for date range.
	 * @param endDate
	 *            End date for the date range.
	 * @param skip
	 *            no to skip while fetching posts,used for pagination.
	 * @param limit
	 *            No of Comments to be fetched at once.
	 * @param isPositive
	 *            tells whether to fetch positive reviews or negative
	 *            reviews.True for positive and False for negative Comments.
	 * @return returns the json string of recent posts.
	 */

	public String getRecentComments(String entity, String subProduct,
			String startDate, String endDate, int skip, int limit,
			String isPositive) {

		Date formattedstartDate = formattoISODate(startDate);
		Date formattedendDate = formattoISODate(endDate);
		List<Post> posts = new ArrayList<>();
		List<Product> productList = new ArrayList<>();
		Map<String, Product> productIdFeed = new HashMap<>();
		String returnString = null;
		int totalCommentsCount = 0;
		int noOfPages = 0;

		/*
		 * The boolean variable indicates whether any posts is found in the
		 * given date Range.
		 */
		boolean ispostsFoundInRange = true;

		try {
			posts = MongoConnector.getRecentReviews(
					IdMap.getEntityId(entity.toLowerCase()),
					IdMap.getSubproductId(subProduct), skip, limit,
					formattedstartDate, formattedendDate,
					Boolean.parseBoolean(isPositive), null);

			/*
			 * If no posts are found in the given range of dates get the latest
			 * posts independent of date.
			 */
			if (posts.size() == 0) {
				posts = MongoConnector.getRecentReviews(
						IdMap.getEntityId(entity.toLowerCase()),
						IdMap.getSubproductId(subProduct), skip, limit, null,
						null, Boolean.parseBoolean(isPositive), null);
				ispostsFoundInRange = false;
			}

			for (Post post : posts) {
				Product product = new Product();
				String productId = post.getPostId().split(":")[0];
				product.setProductId(productId);
				product.setPostId(post.getPostId());
				productList.add(product);
			}

			/*
			 * Called only at the first time when skip=0,to get the total no of
			 * pages used for pagination in the UI.
			 */
			if (skip == 0) {
				totalCommentsCount = MongoConnector.getcountRecentReviews(
						IdMap.getEntityId(entity.toLowerCase()),
						IdMap.getSubproductId(subProduct), formattedstartDate,
						formattedendDate, isPositive);
				if (totalCommentsCount == 0) {
					totalCommentsCount = MongoConnector.getcountRecentReviews(
							IdMap.getEntityId(entity.toLowerCase()),
							IdMap.getSubproductId(subProduct), null, null,
							isPositive);
				}
				noOfPages = (int) Math
						.ceil((double) totalCommentsCount / limit);
			}

			if (entity.equals("nike")) {
				MongoConnector.getProductDetailsById(productList);
			} else {
				MongoConnector.getProductDetails(productList);
			}

			/* Put all the products into a map with postId as a key */
			for (Product product : productList) {
				productIdFeed.put(product.getPostId(), product);
			}

			/* Put the product into the post object retrieving it from the map */
			for (Post post : posts) {
				post.setProduct(productIdFeed.get(post.getPostId()));
			}
			List<CommentsHtml> commentsHtml = BusinessUtilityForAnalytics
					.getCommentsHtmlForTopPosts(posts, "-bw-", noOfPages,
							ispostsFoundInRange);
			returnString = new Gson().toJson(commentsHtml);
		} catch (Exception e) {
			LOG.error("", e);
			LOG.info(e);
		}
		return returnString;
	}

	/**
	 * Returns the details associated with the store.(Storename, Store Image,
	 * Store Address)
	 * 
	 * @param productId
	 * @return
	 */
	public String getStoreDetails(String productId) {
		StoreProductDetails product = null;
		product = MongoConnector.getProductDetailsForStore(productId);
		return new Gson().toJson(product);
	}

	/**
	 * This method fetches the graph data from the database.
	 * 
	 * @param entity
	 *            Entity Name.
	 * @param subProduct
	 *            SubProduct Name.
	 * @param startDate
	 *            Start date for the date Range.
	 * @param endDate
	 *            End date for the date Range.
	 * @param time
	 *            time suggests week,day or month.
	 * @param integer
	 * @param integer
	 * @return List of type chartdata containing graph data.
	 */

	public List<ChartData> getSubDimensionData(String entity,
			String subProduct, String startDate, String endDate, String time,
			Integer type) {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<ChartData> data = new ArrayList<>();
		startDate = formatDate(startDate);
		endDate = formatDate(endDate);
		try {

			connection = daoFactory.getConnection();
			Object[] values = { entity, subProduct, startDate, endDate };
			String query = "";

			switch (time) {
			case "day":
				if (type == 2) {
					query = GETDATA_SUBDIMENSION_STORE_DAY;
				} else {
					query = GETDATA_SUBDIMENSION_DAY;
				}

				break;
			case "week":
				if (type == 2) {
					query = GETDATA_SUBDIMENSION_STORE_WEEK;
				} else {
					query = GETDATA_SUBDIMENSION_WEEK;
				}
				break;
			case "month":
				if (type == 2) {
					query = GETDATA_SUBDIMENSION_STORE_MONTH;
				} else {
					query = GETDATA_SUBDIMENSION_MONTH;
				}
				break;
			}
			preparedStatement = prepareStatement(connection, query, false,
					values);

			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				ChartData newData = new ChartData();
				newData.setDate(resultSet.getString(1));
				if (resultSet.getString(2) != null) {
					newData.setValue(resultSet.getString(2));
				} else {
					newData.setValue("0");
				}

				data.add(newData);
			}

		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		return data;
	}

	public List<ChartData> getsubTopicsChartData(String entity,
			String subProduct, String startDate, String endDate,
			boolean isPositive, String productId, String subDimension,
			String word) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<ChartData> data = new ArrayList<>();
		Date startDateFormatted = formattoISODate(startDate);
		Date endDateFormatted = formattoISODate(endDate);
		try {
			/*
			 * Get all the months within the given date range into the map. Key
			 * is a string containing the month and year in format "month-year"
			 * Eg.(01-2014) and value is the chartData object containing the
			 * corresponding date(First day of each month) and count 0.
			 */
			Map<String, ChartData> mapOfDates = getMapOFDatesForMonths(
					startDateFormatted, endDateFormatted);
			/*
			 * Now query the mongo for the chart values within the given date
			 * ranges. Check whether that date is found in the map obtained
			 * above ,if found update value for the particular chartdata object.
			 * If value is not found for a particular month then the value would
			 * be 0 for that month.
			 */
			data = MongoConnectorForAnalytics.getchartDataForSubtopics(
					IdMap.getEntityId(entity),
					IdMap.getSubproductId(subProduct), startDateFormatted,
					endDateFormatted, isPositive, mapOfDates, productId,
					subDimension, word);

		} catch (Exception e) {
			LOG.info(e + "ERROR");
			System.out.println("error " + e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		return data;
	}

	/**
	 * This method provides the chart data along with date and values for
	 * plotting the chart. It gives the count of positive or negative reviews in
	 * the given date range grouped by the months.
	 * 
	 * @param entity
	 * @param subProduct
	 * @param startDate
	 * @param endDate
	 * @param isPositive
	 * @param productId
	 * @return
	 */
	public List<ChartData> getTimeLineData(String entity, String subProduct,
			String startDate, String endDate, boolean isPositive,
			String productId) {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<ChartData> data = new ArrayList<>();
		Date startDate1 = formattoISODate(startDate);
		Date endDate1 = formattoISODate(endDate);
		try {
			/*
			 * Get all the months within the given date range into the map. Key
			 * is a string containing the month and year in format "month-year"
			 * Eg.(01-2014) and value is the chartData object containing the
			 * corresponding date(First day of each month) and count 0.
			 */
			Map<String, ChartData> mapOfDates = getMapOFDatesForMonths(
					startDate1, endDate1);
			/*
			 * Now query the mongo for the chart values within the given date
			 * ranges. Check whether that date is found in the map obtained
			 * above ,if found update value for the particular chartdata object.
			 * If value is not found for a particular month then the value would
			 * be 0 for that month.
			 */
			data = MongoConnector.getTimelineData(IdMap.getEntityId(entity),
					IdMap.getSubproductId(subProduct), startDate1, endDate1,
					isPositive, mapOfDates, productId);

		} catch (Exception e) {
			LOG.info(e + "ERROR");
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		LOG.info("Chart Data JSON:" + data);
		return data;
	}

	public List<ChartData> getTimeLineDataByWeek(String entity,
			String subProduct, String startDate, String endDate,
			boolean isPositive, String productId) {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<ChartData> data = new ArrayList<>();
		Date startDate1 = formattoISODate(startDate);
		Date endDate1 = formattoISODate(endDate);
		try {
			/*
			 * Get all the months within the given date range into the map. Key
			 * is a string containing the month and year in format "month-year"
			 * Eg.(01-2014) and value is the chartData object containing the
			 * corresponding date(First day of each month) and count 0.
			 */
			Map<String, ChartData> mapOfDates = getMapOFDatesForWeek(
					startDate1, endDate1);
			/*
			 * Now query the mongo for the chart values within the given date
			 * ranges. Check whether that date is found in the map obtained
			 * above ,if found update value for the particular chartdata object.
			 * If value is not found for a particular month then the value would
			 * be 0 for that month.
			 */
			data = MongoConnector.getTimelineDataByWeek(
					IdMap.getEntityId(entity),
					IdMap.getSubproductId(subProduct), startDate1, endDate1,
					isPositive, mapOfDates, productId);

		} catch (Exception e) {
			LOG.info(e + "ERROR");
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		LOG.info("Chart Data JSON:" + data);
		return data;
	}

	public List<ChartData> getTimeLineDataForMap(String entity,
			String subProduct, String startDate, String endDate,
			boolean isPositive, String stateCode, String store) {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<ChartData> data = new ArrayList<>();
		Date startDate1 = formattoISODate(startDate);
		Date endDate1 = formattoISODate(endDate);
		try {
			/*
			 * Get all the months within the given date range into the map. Key
			 * is a string containing the month and year in format "month-year"
			 * Eg.(01-2014) and value is the chartData object containing the
			 * corresponding date(First day of each month) and count 0.
			 */
			Map<String, ChartData> mapOfDates = getMapOFDatesForMonths(
					startDate1, endDate1);
			/*
			 * Now query the mongo for the chart values within the given date
			 * ranges. Check whether that date is found in the map obtained
			 * above ,if found update value for the particular chartdata object.
			 * If value is not found for a particular month then the value would
			 * be 0 for that month.
			 */
			data = MongoConnectorForMapPage.getTimelineData(
					IdMap.getEntityId(entity),
					IdMap.getSubproductId(subProduct), startDate1, endDate1,
					isPositive, mapOfDates, stateCode, store);

		} catch (Exception e) {
			LOG.info(e + "ERROR");
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		LOG.info("Chart Data JSON:" + data);
		return data;
	}

	/**
	 * This method returns the top positive and negative traits for a given
	 * period along with the no of occurrences. The traits are obtained based on
	 * the analyzed data(From features collection mongo).
	 * 
	 * @param entity
	 * @param subProduct
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public String getTimelineTraits(String entity, String subProduct,
			String startDate, String endDate) {

		Date formattedstartDate = formattoISODate(startDate);
		Date formattedendDate = formattoISODate(endDate);
		/*
		 * Get all the postIds first within the given date range
		 */
		List<String> postIds = MongoConnectorForAnalytics
				.getPostIdsForDateRange(IdMap.getEntityId(entity),
						IdMap.getSubproductId(subProduct), formattedstartDate,
						formattedendDate);
		/*
		 * Get all the positive traits using the postIds fobtained before.
		 */
		List<KeyWord> posTraits = MongoConnectorForAnalytics.getTopTraits(
				IdMap.getEntityId(entity), IdMap.getSubproductId(subProduct),
				postIds, true);

		/*
		 * Get all the negative traits using the postIds obtained before.
		 */
		List<KeyWord> negTraits = MongoConnectorForAnalytics.getTopTraits(
				IdMap.getEntityId(entity), IdMap.getSubproductId(subProduct),
				postIds, false);

		String returnJson = "{\"PosTraits\":" + new Gson().toJson(posTraits)
				+ ",\"NegTraits\":" + new Gson().toJson(negTraits) + "}";
		return returnJson;
	}

	/**
	 * 
	 * Takes 2 periods(dates) as input and compares the traits count in two
	 * different periods.Calculates the difference percentage and returns the
	 * percentage change along with the traits and the count of traits in the 2
	 * periods.
	 * 
	 * @param entity
	 * @param subProduct
	 * @param startDate1
	 * @param endDate1
	 * @param startDate2
	 * @param endDate2
	 * @return
	 */
	public String getTimelineTraitsForComparison(String entity,
			String subProduct, String startDate1, String endDate1,
			String startDate2, String endDate2) {

		Date formattedstartDate1 = formattoISODate(startDate1);
		Date formattedendDate1 = formattoISODate(endDate1);

		Date formattedstartDate2 = formattoISODate(startDate2);
		Date formattedendDate2 = formattoISODate(endDate2);

		List<String> postIdsFirstPeriod = MongoConnectorForAnalytics
				.getPostIdsForDateRange(IdMap.getEntityId(entity),
						IdMap.getSubproductId(subProduct), formattedstartDate1,
						formattedendDate1);

		List<String> postIdsSecondPeriod = MongoConnectorForAnalytics
				.getPostIdsForDateRange(IdMap.getEntityId(entity),
						IdMap.getSubproductId(subProduct), formattedstartDate2,
						formattedendDate2);

		List<KeyWord> posTraits = MongoConnectorForAnalytics.getTopTraits(
				IdMap.getEntityId(entity), IdMap.getSubproductId(subProduct),
				postIdsFirstPeriod, true);

		Map<String, KeyWord> wordPosKeyword = new HashMap<String, KeyWord>();
		for (KeyWord keyword : posTraits) {
			wordPosKeyword.put(keyword.getKeyWord(), keyword);
		}

		posTraits = MongoConnectorForAnalytics.getTopTraitsForSecondPeriod(
				IdMap.getEntityId(entity), IdMap.getSubproductId(subProduct),
				postIdsSecondPeriod, wordPosKeyword, true);

		String posTraitsJson = BusinessUtilityForAnalytics
				.gethtmlForTraitsCompare(posTraits);

		List<KeyWord> negTraits = MongoConnectorForAnalytics.getTopTraits(
				IdMap.getEntityId(entity), IdMap.getSubproductId(subProduct),
				postIdsFirstPeriod, false);

		Map<String, KeyWord> wordNegKeyword = new HashMap<String, KeyWord>();
		for (KeyWord keyword : negTraits) {
			wordNegKeyword.put(keyword.getKeyWord(), keyword);
		}

		negTraits = MongoConnectorForAnalytics.getTopTraitsForSecondPeriod(
				IdMap.getEntityId(entity), IdMap.getSubproductId(subProduct),
				postIdsSecondPeriod, wordNegKeyword, false);
		String negTraitsJson = BusinessUtilityForAnalytics
				.gethtmlForTraitsCompare(negTraits);

		String returnJson = "{\"PosTraitsJson\":" + posTraitsJson
				+ ",\"NegTraitsJson\":" + negTraitsJson + "}";

		return returnJson;
	}

	public String getTopPosorNegSubdimension(String entity, String subProduct,
			String storeId, boolean isPositive) {
		List<KeyWord> keywords = MongoConnectorForAnalytics.getTopWords(
				storeId, 0, IdMap.getEntityId(entity),
				IdMap.getSubproductId(subProduct), isPositive);
		String resultantJson = "";
		{
			resultantJson = "{\"Dimensions\":" + new Gson().toJson(keywords)
					+ "}";
		}

		return resultantJson;
	}

	/**
	 * @param entity
	 * @param subProduct
	 * @param state
	 * @param valueOf
	 * @return
	 */
	public String getTopPosorNegSubdimensionForState(String entity,
			String subProduct, String stateCode) {
		List<KeyWord> keywords = MongoConnectorForMapPage.getTopWords(
				stateCode, IdMap.getEntityId(entity),
				IdMap.getSubproductId(subProduct));
		String resultantJson = "";
		{
			resultantJson = "{\"Dimensions\":" + new Gson().toJson(keywords)
					+ "}";
		}

		return resultantJson;
	}

	/**
	 * Method Fetches the Top Subdimensions and words based on number of user
	 * clicks.
	 * 
	 * @param entity
	 *            Entity or Brand Name.
	 * @param subProduct
	 *            SubProductName
	 * @param startDate
	 *            Start date for the given date range.
	 * @param endDate
	 *            End date for the given date range.
	 * @param type
	 * @return returns a json formatted string of words and subdimensions.
	 */

	public String getTopSubDimensionandWords(String entity, String subProduct,
			String startDate, String endDate, Integer type) {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		startDate = formatDate(startDate);
		endDate = formatDate(endDate);
		String subDimensionData = "";
		String wordData = "";
		String usersData = "";
		String returnData = "";
		String reviewData = "";

		try {

			connection = daoFactory.getConnection();
			Object[] values = { entity, subProduct, startDate, endDate };
			/*
			 * Execute 1st query to get the top 5 subDimensions
			 */if (type == 2) {
				 preparedStatement = prepareStatement(connection,
						 GETDATA_TOP_SUBDIMENSIONS_FOR_STORE, false, values);
			 } else {
				 preparedStatement = prepareStatement(connection,
						 GETDATA_TOP_SUBDIMENSIONS, false, values);
			 }

			 resultSet = preparedStatement.executeQuery();

			 while (resultSet.next()) {
				 subDimensionData += "{" + "\"subDimension\":\""
						 + resultSet.getString(1) + "\"," + "\"Count\":"
						 + resultSet.getString(2) + "},";
			 }
			 subDimensionData = "{\"type\":\"subDimension\",\"items\":["
					 + subDimensionData + "]}";

			 /*
			  * Execute 2nd query to get the top 5 words.
			  */
			 if (type == 2) {
				 preparedStatement = prepareStatement(connection,
						 GETDATA_TOP_WORDS_FOR_STORE, false, values);
			 } else {
				 preparedStatement = prepareStatement(connection,
						 GETDATA_TOP_WORDS, false, values);
			 }
			 resultSet = preparedStatement.executeQuery();

			 while (resultSet.next()) {
				 wordData += "{" + "\"word\":\"" + resultSet.getString(1)
						 + "\"," + "\"Count\":" + resultSet.getString(2) + "},";
			 }
			 wordData = "{\"type\":\"words\",\"items\":[" + wordData + "]}";

			 /*
			  * Get the no of unique users.
			  */
			 Object[] valuestoPass = { entity, subProduct, startDate, endDate };
			 preparedStatement = prepareStatement(connection, GETDATA_NO_USERS,
					 true, valuestoPass);
			 resultSet = preparedStatement.executeQuery();

			 while (resultSet.next()) {
				 usersData += resultSet.getString(1);
			 }

			 usersData = "{\"type\":\"users\",\"Count\":" + usersData + "}";

			 /* To get the count of clicks on positive and negative reviews */
			 if (type == 2) {
				 preparedStatement = prepareStatement(connection,
						 GETDATA_REVIEW_CLICKSCOUNT_FOR_STORE, false, values);
			 } else {
				 preparedStatement = prepareStatement(connection,
						 GETDATA_REVIEW_CLICKSCOUNT, false, values);
			 }

			 resultSet = preparedStatement.executeQuery();
			 int totalCount = 0;
			 while (resultSet.next()) {

				 if (resultSet.getInt(1) == 2) {
					 reviewData += "\"PosReviewCount\":"
							 + resultSet.getString(2) + ",";

				 } else {
					 reviewData += "\"NegReviewCount\":"
							 + resultSet.getString(2) + ",";
				 }
				 totalCount += resultSet.getInt(2);

			 }
			 reviewData += "\"TotalCount\":" + totalCount;
			 reviewData = "{\"type\":\"reviewCount\"," + reviewData + "}";
			 returnData = "[" + subDimensionData + "," + wordData + ","
					 + usersData + "," + reviewData + "]";
			 returnData = returnData.replace(",]", "]");
		} catch (SQLException e) {
			LOG.error("", e);
			throw new DAOException(e);
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		return returnData;
	}

	/**
	 * 
	 * This method returns the top traits along with with the positive and
	 * negative comments count associated with each trait, for a given
	 * subDimension.
	 */
	public String getTopTraitsForSubDimenion(String entity, String subProduct,
			String storeId, String subDimension, String category) {
		/*
		 * String keyWordsCount = MongoConnectorForAnalytics
		 * .getShoppingExperienceData(IdMap.getEntityId(entity),
		 * IdMap.getSubproductId(subProduct), storeId, null); String[]
		 * posandTotalCount = keyWordsCount.split(":"); int posPercentage = 0;
		 * int negPercentage = 0; if (posandTotalCount.length != 0) { int
		 * posCount = Integer.parseInt(posandTotalCount[1]); int totalCount =
		 * Integer.parseInt(posandTotalCount[0]); posPercentage = (int) Math
		 * .ceil(((double) posCount / totalCount) * 100); negPercentage = 100 -
		 * posPercentage; }
		 */
		List<KeyWord> keywords = MongoConnectorForAnalytics
				.getKeyWordsForShoppingExperience(IdMap.getEntityId(entity),
						IdMap.getSubproductId(subProduct), subDimension,
						storeId, 50);
		BusinessUtilityForAnalytics.gethtmlForfeaturelist(keywords,
				subDimension, category);
		String resultantJson = "{\"KeywordsData\":"
				+ new Gson().toJson(keywords) + "}";

		return resultantJson;
	}

	public String getTopTraitsForSubDimenionBeegood(String entity,
			String subProduct, String storeId, String subDimension,
			String category) {
		/*
		 * String keyWordsCount = MongoConnectorForAnalytics
		 * .getShoppingExperienceData(IdMap.getEntityId(entity),
		 * IdMap.getSubproductId(subProduct), storeId, null); String[]
		 * posandTotalCount = keyWordsCount.split(":"); int posPercentage = 0;
		 * int negPercentage = 0; if (posandTotalCount.length != 0) { int
		 * posCount = Integer.parseInt(posandTotalCount[1]); int totalCount =
		 * Integer.parseInt(posandTotalCount[0]); posPercentage = (int) Math
		 * .ceil(((double) posCount / totalCount) * 100); negPercentage = 100 -
		 * posPercentage; }
		 */
		List<KeyWord> keywords = BeeGoodDBConnector.getKeyWordsForSubDimension(
				IdMap.getEntityId(entity), IdMap.getSubproductId(subProduct),
				subDimension, storeId, 50);
		BusinessUtilityForAnalytics.gethtmlForfeaturelist(keywords,
				subDimension, category);
		String resultantJson = "{\"KeywordsData\":"
				+ new Gson().toJson(keywords) + "}";

		return resultantJson;
	}
	/*
	 * Get traits for enerprise cars on click of any subdimension.
	 */
	public String getTraitsForEnterpriseCar(String entity, String subProduct,
			String storeId, String subDimension, String category, String state) {
		List<KeyWord> keywords = MongoConnectorForAnalytics
				.getTraitsForEnterpriseCar(IdMap.getEntityId(entity),
						IdMap.getSubproductId(subProduct), subDimension,
						storeId, 50, state);
		String resultantJson = "{\"KeywordsData\":"
				+ new Gson().toJson(keywords) + "}";
		return resultantJson;
	}

	public String getWordsForSubDimenion(String entity, String subProduct,
			String storeId, String subDimension, String category) {
		/*
		 * String keyWordsCount = MongoConnectorForAnalytics
		 * .getShoppingExperienceData(IdMap.getEntityId(entity),
		 * IdMap.getSubproductId(subProduct), storeId, null); String[]
		 * posandTotalCount = keyWordsCount.split(":"); int posPercentage = 0;
		 * int negPercentage = 0; if (posandTotalCount.length != 0) { int
		 * posCount = Integer.parseInt(posandTotalCount[1]); int totalCount =
		 * Integer.parseInt(posandTotalCount[0]); posPercentage = (int) Math
		 * .ceil(((double) posCount / totalCount) * 100); negPercentage = 100 -
		 * posPercentage; }
		 */

		List<KeyWord> keywords = MongoConnectorForAnalytics
				.getKeyWordsForShoppingExperience(IdMap.getEntityId(entity),
						IdMap.getSubproductId(subProduct), subDimension,
						storeId, 50);
		BusinessUtilityForAnalytics.gethtmlForfeaturelist(keywords,
				subDimension, category);
		String resultantJson = BusinessUtilityForAnalytics
				.getJsonForTagCloud(keywords);

		return resultantJson;
	}
}
