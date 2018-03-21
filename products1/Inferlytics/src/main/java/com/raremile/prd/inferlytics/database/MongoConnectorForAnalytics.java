package com.raremile.prd.inferlytics.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.commons.FilePropertyManager;
import com.raremile.prd.inferlytics.entity.KeyWord;
import com.raremile.prd.inferlytics.entity.Post;
import com.raremile.prd.inferlytics.entity.ProductDetails;
import com.raremile.prd.inferlytics.entity.StoreDetailsEntity;
import com.raremile.prd.inferlytics.utils.BusinessUtil;
import com.raremile.prd.inferlytics.utils.ChartData;

public class MongoConnectorForAnalytics extends MongoConnection {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(MongoConnectorForAnalytics.class);
	// private static MongoClient mongoClient = null;

	public static final Comparator<KeyWord> KEYWORDS_ORDER = new Comparator<KeyWord>() {

		@Override
		public int compare(KeyWord o1, KeyWord o2) {

			int retValue = 0;
			if (o1.getTotalCount() < o2.getTotalCount()) {
				retValue = -1;
			} else if (o1.getTotalCount() > o2.getTotalCount()) {
				retValue = 1;
			}
			return retValue;
		}
	};

	@SuppressWarnings("unchecked")
	public static List<String> getAllSubDimensions(int subProductId,
			int entityId) {
		List<String> keys = null;
		DB db = null;
		try {

			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("SubProductId", subProductId);
			whereQuery.put("EntityId", entityId);
			keys = collections.distinct("SubDimension", whereQuery);

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);

		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return keys;
	}

	public static void getAnalyticsDetailsForProducts(){
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			int entityId = 431;
			int subProductId = 38;
			String entityName = "gandermountains";
			String subProductName = "sweatshirts";
			DBCollection collections = db.getCollection("products");
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.append("Product", "gandermountain");

			DBCursor cursor = collections.find(matchQuery);
			List<ProductDetails> productDetails = new ArrayList<>();
			String previousProdId = null;
			cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);

			while (cursor.hasNext()) {

				DBObject obj = cursor.next();


				//1.All Positive traits
				//2. All Negative traits

				String productId = obj.get("_id").toString();
				System.out.println("curr " + productId + "  Prev : "
						+ previousProdId);
				if (previousProdId != null
						&& previousProdId.equals(productId.substring(0, 5))) {
					continue;
				}
				System.out.print("   not skipped");
				previousProdId = productId.substring(0, 5);

				ProductDetails product = new ProductDetails();
				MongoConnector.getNameUrl(obj.get("_id").toString(), product,
						false);
				product.setProductId(productId);
				String currPrice = BusinessUtil.getCurrencyPriceByBrand("", "");
				product.setCurrency(currPrice.split(":")[0]);
				product.addPosReview(MongoConnector.getPositiveKeyWords(
						productId, "20", 0, product, entityId, subProductId));
				product.addNegReview(MongoConnector.getNegativeKeyWords(
						productId, "20", 0, product, entityId, subProductId));

				// categories per product

				List<KeyWord> categories = MongoConnectorForAnalytics
						.getDataForPieChart(entityId, subProductId, productId);
				for (KeyWord keyword : categories) {
					int posPercentage = (int) Math.ceil(((double) keyword
							.getPosCount() / keyword.getTotalCount()) * 100);
					keyword.setPercentage(posPercentage);
				}
				Collections.sort(categories, KEYWORDS_ORDER);
				product.setCategories(categories);

				//3. Incoming review analysis by Time.

				String fromdate = "06/19/2012";
				String toDate = "07/26/2014";
				List<ChartData> data = null;
				data = DAOFactory
						.getInstance(
								ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
								.getDAOAnalyticsData()
								.getTimeLineData(entityName, subProductName, fromdate,
										toDate, true, productId);
				String result1 = "[";
				for (ChartData eachData : data) {
					result1 += eachData.toString() + ",";
				}
				result1 += "]";
				result1 = result1.replace(",]", "]");
				data = DAOFactory
						.getInstance(
								ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
								.getDAOAnalyticsData()
								.getTimeLineData(entityName, subProductName, fromdate,
										toDate, false, productId);
				String result2 = "[";
				for (ChartData eachData : data) {
					result2 += eachData.toString() + ",";
				}
				result2 += "]";
				result2 = result2.replace(",]", "]");
				String returnString = "{\"PositiveData\":" + result1
						+ ",\"NegativeData\":" + result2 + "}";
				product.setChartData(returnString);

				//4. Recent Reviews(Positive, Negative)
				List<Post> positivePosts = MongoConnector.getRecentReviews(
						entityId, subProductId, 0, 0, null, null, true,
						productId);
				double overallrating = 0.0;
				for (Post post : positivePosts) {
					overallrating += post.getRating();
				}

				List<Post> negativePosts = MongoConnector.getRecentReviews(
						entityId, subProductId, 0, 0, null, null, false,
						productId);
				for (Post post : negativePosts) {
					overallrating += post.getRating();
				}
				if (overallrating != 0) {
					overallrating /= (positivePosts.size() + negativePosts.size());
					if (overallrating != 0.0) {
						product.setAverageRating(overallrating);
					}
				}
				product.setPositivePosts(positivePosts);
				product.setNegativePosts(negativePosts);
				//5.
				//Overall Analysis
				//i) Overall comment count
				//ii) Positive comment count
				//iii) Negative comment count
				//iV) Sentiment Score of a product

				//6. Product name, Image
				productDetails.add(product);
				/*
				 * if (productDetails.size() == 2) { break; }
				 */

			}

			String json = new Gson().toJson(productDetails);
			try {
				File navigationfile = new File(
						"/home/pratyusha/C Drive/Projects/Sentiment Analysis/logs/gandermountains"
								+ new Date().toString());

				if (navigationfile.createNewFile()) {
					System.out.println("File is created!");
				} else {
					System.out.println("File already exists.");
				}
				FileWriter fw = new FileWriter(navigationfile.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(json);
				bw.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
	}

	public static List<ChartData> getchartDataForSubtopics(int entityId,
			int subProductID, Date fromDate, Date toDate, Boolean isPositive,
			Map<String, ChartData> mapOfDates, String storeId,
			String subDimension, String word) throws ParseException {

		DB db = null;
		List<ChartData> chartdatas = new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");
			BasicDBObject match = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.append("EntityId", entityId);
			matchQuery.append("SubProductId", subProductID);
			matchQuery.append("ProductId", storeId);
			matchQuery.append("SubDimension", subDimension);
			matchQuery.append("SynonymWord", word);
			if (isPositive) {
				matchQuery.append("SentenceScore", new BasicDBObject("$gt", 0));
			} else {
				matchQuery.append("SentenceScore", new BasicDBObject("$lt", 0));
			}
			if (fromDate != null) {
				matchQuery.append(
						"CreatedDate",
						BasicDBObjectBuilder.start("$gte", fromDate)
						.add("$lte", toDate).get());
			}
			match.put("$match", matchQuery);

			BasicDBObject grpfields1 = new BasicDBObject();
			grpfields1.put("_id", new BasicDBObject("PostId", "$PostId"));
			grpfields1.put("Date", new BasicDBObject("$first", "$CreatedDate"));
			BasicDBObject group1 = new BasicDBObject("$group", grpfields1);

			BasicDBObject grpFieldsYearandMonth = new BasicDBObject();
			grpFieldsYearandMonth.append("year", new BasicDBObject("$year",
					"$Date"));
			grpFieldsYearandMonth.append("month", new BasicDBObject("$month",
					"$Date"));

			BasicDBObject groupFields2 = new BasicDBObject();
			groupFields2.append("_id", grpFieldsYearandMonth);
			groupFields2.append("Count", new BasicDBObject("$sum", 1));
			BasicDBObject group2 = new BasicDBObject();
			group2.append("$group", groupFields2);

			BasicDBObject projectValues = new BasicDBObject();
			projectValues.append("Year", "$_id.year");
			projectValues.append("Month", "$_id.month");
			projectValues.append("Count", "$Count");
			BasicDBObject project = new BasicDBObject("$project", projectValues);
			BasicDBObject sortQuery = new BasicDBObject();
			sortQuery.append("_id", -1);
			BasicDBObject sort = new BasicDBObject("$sort", sortQuery);
			List<DBObject> aggregateObjects = new ArrayList<>();
			aggregateObjects.add(match);
			aggregateObjects.add(group1);
			aggregateObjects.add(group2);
			aggregateObjects.add(project);
			aggregateObjects.add(sort);
			AggregationOutput output = collections.aggregate(aggregateObjects);
			Iterable<DBObject> result = output.results();

			if (result != null) {
				for (DBObject object : result) {
					String monthYear = object.get("Month").toString() + "-"
							+ object.get("Year").toString();
					String value = object.get("Count").toString();
					ChartData chData = mapOfDates.get(monthYear);
					chData.setValue(value);
					mapOfDates.put(monthYear, chData);
				}

			} else {
				LOG.info("No posts found in the range");
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		chartdatas = new ArrayList<ChartData>(mapOfDates.values());
		return chartdatas;
	}

	public static List<KeyWord> getDataForPieChart(Integer entityId,
			Integer subProductId, String storeId) {
		DB db = null;
		List<KeyWord> keyWords = new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);

			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");

			BasicDBObject match = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.append("EntityId", entityId);
			matchQuery.append("SubProductId", subProductId);


			matchQuery.append("SentenceScore", new BasicDBObject("$ne", 0));
			/*
			 * matchQuery.append("$or", new BasicDBObject("SentenceScore", new
			 * BasicDBObject("$gt", 0.2).append("$lt", -0.2)));
			 */// new
			// BasicDBObject("$ne",
			// 0));
			if (storeId != null) {
				matchQuery.append("ProductId", storeId);
			}
			match.put("$match", matchQuery);

			DBObject unwind1 = new BasicDBObject("$unwind", "$SubDimension");
			DBObject groupFields1 = new BasicDBObject("_id", "$SubDimension");
			groupFields1.put("Count", new BasicDBObject("$sum", 1));
			groupFields1.put("SentenceScore", new BasicDBObject("$push",
					"$SentenceScore"));
			BasicDBObject group = new BasicDBObject("$group", groupFields1);

			DBObject unWind2 = new BasicDBObject("$unwind", "$SentenceScore");

			BasicDBObject matchQuery2 = new BasicDBObject();
			matchQuery2.append("SentenceScore", new BasicDBObject("$gt", 0));
			BasicDBObject match2 = new BasicDBObject("$match", matchQuery2);

			BasicDBObject groupfields2 = new BasicDBObject();
			groupfields2.append("_id", "$_id");
			groupfields2
			.append("CountOfPositive", new BasicDBObject("$sum", 1));
			groupfields2.append("TotalCount", new BasicDBObject("$first",
					"$Count"));
			BasicDBObject group2 = new BasicDBObject("$group", groupfields2);

			AggregationOutput output = collections.aggregate(match, unwind1,
					group, unWind2, match2, group2);
			System.out.println(match + "," + unwind1 + "," + group + ","
					+ unWind2 + ","
					+ match2 + "," + group2);
			Iterable<DBObject> result = output.results();
			if (result != null) {
				for (DBObject object : result) {
					KeyWord keyword = new KeyWord();
					keyword.setKeyWord(object.get("_id").toString());
					keyword.setPosCount((Integer) object.get("CountOfPositive"));
					keyword.setTotalCount((Integer) object.get("TotalCount"));
					keyWords.add(keyword);
				}
			}
		} catch (IOException ex) {
			LOG.error("", ex);
		} catch (MongoException ex) {
			LOG.error("MongoException occured during batch insert.", ex);
		} catch (Exception ex) {
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return keyWords;
	}


	public static List<String> getEmployees(Integer entityId,
			Integer subProductId, String storeId, boolean isPositive) {
		DB db = null;
		List<String> employeeNames = new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);

			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");
			BasicDBObject match = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.append("EntityId", entityId);
			matchQuery.append("SubProductId", subProductId);
			matchQuery.append("ProductId", storeId);
			matchQuery.append("SubDimension", "named employees");
			if (isPositive) {
				matchQuery.append("SentenceScore", new BasicDBObject("$gt", 0));
			} else {
				matchQuery.append("SentenceScore", new BasicDBObject("$lt", 0));
			}
			match.put("$match", matchQuery);
			BasicDBObject groupFields = new BasicDBObject();
			groupFields.put("SynonymWord", "$SynonymWord");
			groupFields.put("PostId", "$PostId");
			DBObject groupField = new BasicDBObject("_id", groupFields);
			BasicDBObject group = new BasicDBObject("$group", groupField);

			DBObject groupFields2 = new BasicDBObject("_id", "$_id.SynonymWord");
			groupFields2.put("Count", new BasicDBObject("$sum", 1));

			BasicDBObject group2 = new BasicDBObject("$group", groupFields2);
			BasicDBObject sortQuery = new BasicDBObject();
			sortQuery.append("Count", -1);
			BasicDBObject sort = new BasicDBObject("$sort", sortQuery);
			DBObject limitquery = new BasicDBObject("$limit", 5);
			AggregationOutput output = collections.aggregate(match, group,
					group2, sort, limitquery);
			Iterable<DBObject> result = output.results();
			if (result != null) {
				for (DBObject object : result) {
					employeeNames.add(object.get("_id").toString() + ":"
							+ object.get("Count").toString());
				}
			}
		} catch (IOException ex) {
			LOG.error("", ex);
		} catch (MongoException ex) {

			LOG.error("MongoException occured during batch insert.", ex);
		} catch (Exception ex) {
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return employeeNames;
	}

	/*
	 * private static DB getMongoDB(String database) throws UnknownHostException
	 * { mongoClient = new MongoClient("localhost"); DB db =
	 * mongoClient.getDB(database); return db; }
	 */

	public static ArrayList<KeyWord> getKeyWordsForShoppingExperience(
			int entityId, int subProductId, String subDimension,
			String storeId, int limits) {

		ArrayList<KeyWord> keywords = new ArrayList<>();
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");
			BasicDBObject matchByEntitySubProduct = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.put("EntityId", entityId);
			matchQuery.put("SubProductId", subProductId);
			if (storeId != null) {
				matchQuery.put("ProductId", storeId);
			}
			if (!subDimension.equals("null")) {
				matchQuery.put("SubDimension", subDimension);
			}
			matchByEntitySubProduct.put("$match", matchQuery);

			DBObject groupFields1 = new BasicDBObject("_id", new BasicDBObject(
					"SynonymWord", "$SynonymWord").append("PostId", "$PostId"));
			groupFields1.put("adjscore", new BasicDBObject("$push",
					"$SentenceScore"));

			DBObject group1 = new BasicDBObject("$group", groupFields1);

			DBObject groupFields2 = new BasicDBObject("_id", "$_id.SynonymWord");
			groupFields2.put("Count", new BasicDBObject("$sum", 1));
			groupFields2.put("adjscore2", new BasicDBObject("$push",
					"$adjscore"));
			DBObject group2 = new BasicDBObject("$group", groupFields2);
			DBObject sortOp = new BasicDBObject("$sort", new BasicDBObject(
					"Count", -1).append("_id", 1));

			DBObject limit = new BasicDBObject("$limit", limits);


			DBObject unwind = new BasicDBObject("$unwind", "$adjscore2");
			DBObject match2 = new BasicDBObject("$match", new BasicDBObject(
					"adjscore2", new BasicDBObject("$gt", 0)));
			DBObject groupfields3 = new BasicDBObject("_id", "$_id");
			groupfields3.put("thisCount", new BasicDBObject("$sum", 1));
			groupfields3.put("totCount", new BasicDBObject("$first", "$Count"));
			DBObject group3 = new BasicDBObject("$group", groupfields3);
			DBObject sortOp2 = new BasicDBObject("$sort", new BasicDBObject(
					"totCount", -1).append("_id", 1));
			AggregationOutput ids = collections.aggregate(
					matchByEntitySubProduct, group1, group2, sortOp, limit,
					unwind, match2, group3, sortOp2);

			Iterable<DBObject> results = ids.results();
			if (results != null) {
				for (DBObject dbObject : results) {
					KeyWord keyword = new KeyWord();
					keyword.setKeyWord((String) dbObject.get("_id"));
					keyword.setPosCount(((Integer) dbObject.get("thisCount")));
					keyword.setTotalCount(((Integer) dbObject.get("thisCount")));
					keywords.add(keyword);
				}
			}
			match2 = new BasicDBObject("$match", new BasicDBObject("adjscore2",
					new BasicDBObject("$lt", -0.2)));
			ids = collections.aggregate(matchByEntitySubProduct, group1,
					group2, sortOp, limit, unwind, match2, group3, sortOp2);
			Iterable<DBObject> results2 = ids.results();

			if (results2 != null) {
				for (DBObject dbObject : results2) {
					String word = (String) dbObject.get("_id");
					int negCount = (Integer) dbObject.get("thisCount");
					int flag = 0;
					for (KeyWord keyword : keywords) {

						if (keyword.getKeyWord().equals(word)) {
							flag = 1;
							keyword.setNegativeCount(negCount);
							keyword.setTotalCount(keyword.getTotalCount()
									+ negCount);
						}

					}
					if (flag == 0) {
						KeyWord keyword = new KeyWord();
						keyword.setKeyWord(word);
						keyword.setNegativeCount(negCount);
						keyword.setTotalCount(negCount);
						keywords.add(keyword);
					}
				}

			}

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		Comparator<KeyWord> reverseComparator = Collections
				.reverseOrder(KEYWORDS_ORDER);
		Collections.sort(keywords, reverseComparator);

		return keywords;
	}

	public static List<String> getPostIdsForDateRange(int entityId,
			int subProductID, Date fromDate, Date toDate) {

		/*
		 * SimpleDateFormat mongoDBFormat = new
		 * SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); Date
		 * newfromDate=mongoDBFormat.parse(fromDate); Date
		 * newtoDate=mongoDBFormat.parse(toDate);
		 */
		DB db = null;
		List<String> postIds = new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("posts");
			BasicDBObject match = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();

			matchQuery.append("EntityId", entityId);
			matchQuery.append("subpoductId", subProductID);

			matchQuery.append(
					"createdDate",
					BasicDBObjectBuilder.start("$gte", fromDate)
					.add("$lte", toDate).get());
			match.put("$match", matchQuery);
			AggregationOutput output = collections.aggregate(match);
			Iterable<DBObject> result = output.results();

			if (result != null) {
				for (DBObject object : result) {
					postIds.add(object.get("_id").toString());
				}
			} else {
				System.out.println("Null");
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {

			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return postIds;
	}

	public static List<StoreDetailsEntity> getStores(String product) {

		List<StoreDetailsEntity> stores = new ArrayList<>();
		DB db = null;
		try {

			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("products");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("Product", product);
			whereQuery.put("AvgScore", new BasicDBObject("$exists", true));
			DBCursor cursor = collections.find(whereQuery).sort(
					new BasicDBObject("AvgScore", 1));

			while (cursor.hasNext()) {

				DBObject obj = cursor.next();
				if (obj != null) {
					String storeName = (String) obj.get("ProductName");
					String imgURL = (String) obj.get("ProdImgUrl");
					String productId = (String) obj.get("ProductId");
					double avgScore = (double) obj.get("AvgScore");
					avgScore = Math.round(avgScore * 100.0) / 100.0;
					StoreDetailsEntity details = new StoreDetailsEntity();
					details.setImageSrc(imgURL);
					details.setStoreName(storeName);
					details.setId(productId);
					details.setAvgScore(avgScore);
					stores.add(details);
				}
			}

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);

		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return stores;
	}

	public static List<StoreDetailsEntity> getTopKeyWordsForStore(
			List<StoreDetailsEntity> stores, int subProductId, int entityId) {

		DB db = null;
		try {

			/*
			 * db.features.aggregate({$match:{SubProductId:9,ProductId:"d81141"}}
			 * ,
			 * {$group:{_id:"$Word",Count:{$sum:1}}},{$group:{_id:null,data:{$push
			 * :
			 * {word:"$_id",Count:"$Count"}},TotalCount:{$sum:"$Count"}}},{$unwind
			 * :"$data"},{$project:{Word:"$data.word",WordCount:"$data.Count",
			 * TotalCount:1}},{$sort:{"WordCount":-1}},{$limit:5})
			 */

			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.put("SubProductId", subProductId);
			matchQuery.put("EntityId", entityId);

			BasicDBObject match = new BasicDBObject("$match", matchQuery);

			BasicDBObject group1 = new BasicDBObject();
			group1.put("_id", "$SynonymWord");
			group1.put("Count", new BasicDBObject("$sum", 1));

			BasicDBObject groupQuery1 = new BasicDBObject("$group", group1);

			BasicDBObject group2 = new BasicDBObject();
			group2.put("_id", null);
			BasicDBObject group2Fields1 = new BasicDBObject();
			group2Fields1.put("word", "$_id");
			group2Fields1.put("Count", "$Count");
			group2.put("data", new BasicDBObject("$push", group2Fields1));
			group2.put("TotalCount", new BasicDBObject("$sum", "$Count"));

			BasicDBObject groupQuery2 = new BasicDBObject("$group", group2);

			BasicDBObject unWind = new BasicDBObject("$unwind", "$data");

			BasicDBObject projectFields = new BasicDBObject();
			projectFields.put("Word", "$data.word");
			projectFields.put("WordCount", "$data.Count");
			projectFields.put("TotalCount", 1);
			BasicDBObject project = new BasicDBObject("$project", projectFields);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject(
					"WordCount", -1));
			BasicDBObject limit = new BasicDBObject("$limit", 5);
			for (StoreDetailsEntity store : stores) {
				matchQuery.put("ProductId", store.getId());
				DBCollection collections = db.getCollection("features");

				LOG.info(match + "," + groupQuery1 + "," + groupQuery2 + ","
						+ unWind + "," + project + "," + sort + "," + limit);
				AggregationOutput output = collections.aggregate(match,
						groupQuery1, groupQuery2, unWind, project, sort, limit);
				Iterable<DBObject> result = output.results();
				List<KeyWord> keywords = new ArrayList<>();
				if (result != null) {
					for (DBObject object : result) {
						KeyWord keyword = new KeyWord();
						keyword.setKeyWord(object.get("Word").toString());
						int percentage = (int) Math.ceil(((double) (int) object
								.get("WordCount") / (int) object
								.get("TotalCount")) * 100);
						keyword.setPercentage(percentage);
						keywords.add(keyword);
					}

				} else {
					System.out.println("Null");
				}
				store.setKeyWords(keywords);
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);

		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return stores;
	}

	public static List<KeyWord> getTopTraits(int entityId, int subProductID,
			List<String> postsId, Boolean isPositive) {

		/*
		 * SimpleDateFormat mongoDBFormat = new
		 * SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); Date
		 * newfromDate=mongoDBFormat.parse(fromDate); Date
		 * newtoDate=mongoDBFormat.parse(toDate);
		 */
		DB db = null;
		List<KeyWord> keywords = new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");
			BasicDBObject match = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.append("EntityId", entityId);
			matchQuery.append("SubProductId", subProductID);
			if (isPositive) {
				matchQuery.append("SentenceScore", new BasicDBObject("$gt", 0));
			} else {
				matchQuery.append("SentenceScore", new BasicDBObject("$lt", 0));
			}
			matchQuery.append("PostId", new BasicDBObject("$in", postsId));
			match.put("$match", matchQuery);

			BasicDBObject groupFields = new BasicDBObject();
			groupFields.append("_id", "$Word");
			groupFields.append("Count", new BasicDBObject("$sum", 1));

			BasicDBObject group = new BasicDBObject("$group", groupFields);

			BasicDBObject sortQuery = new BasicDBObject();
			sortQuery.append("Count", -1);
			BasicDBObject sort = new BasicDBObject("$sort", sortQuery);
			DBObject limitquery = new BasicDBObject("$limit", 5);

			AggregationOutput output = collections.aggregate(match, group,
					sort, limitquery);

			Iterable<DBObject> result = output.results();

			if (result != null) {
				for (DBObject object : result) {

					KeyWord keyword = new KeyWord();
					keyword.setKeyWord(object.get("_id").toString());
					keyword.setcount((Integer) (object.get("Count")));
					keywords.add(keyword);
				}
			} else {
				System.out.println("Null");
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {

			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return keywords;
	}

	public static List<KeyWord> getTopTraitsForSecondPeriod(int entityId,
			int subProductID, List<String> postsId,
			Map<String, KeyWord> wordKeyword, Boolean isPositive) {

		DB db = null;
		List<KeyWord> keywords = new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");
			BasicDBObject match = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.append("EntityId", entityId);
			matchQuery.append("SubProductId", subProductID);
			if (isPositive) {
				matchQuery.append("SentenceScore", new BasicDBObject("$gt", 0));
			} else {
				matchQuery.append("SentenceScore", new BasicDBObject("$lt", 0));
			}
			matchQuery.append("PostId", new BasicDBObject("$in", postsId));
			matchQuery.append("Word", new BasicDBObject("$in", wordKeyword
					.keySet().toArray()));
			match.put("$match", matchQuery);
			BasicDBObject groupFields = new BasicDBObject();
			groupFields.append("_id", "$Word");
			groupFields.append("Count", new BasicDBObject("$sum", 1));

			BasicDBObject group = new BasicDBObject("$group", groupFields);

			BasicDBObject sortQuery = new BasicDBObject();
			sortQuery.append("Count", -1);
			BasicDBObject sort = new BasicDBObject("$sort", sortQuery);
			DBObject limitquery = new BasicDBObject("$limit", 5);

			AggregationOutput output = collections.aggregate(match, group,
					sort, limitquery);

			Iterable<DBObject> result = output.results();

			if (result != null) {
				for (DBObject object : result) {

					KeyWord keyword = new KeyWord();
					keyword.setKeyWord(object.get("_id").toString());
					keyword.setSecondCount((Integer) (object.get("Count")));
					KeyWord first = wordKeyword.get(object.get("_id")
							.toString());
					keyword.setcount(first.getcount());
					keywords.add(keyword);
				}
			} else {
				System.out.println("Null");
			}
		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {

			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return keywords;
	}

	public static ArrayList<KeyWord> getTopWords(String productId, int skip,
			int entityId, int subProductId, boolean isPositive) {
		DB db = null;
		ArrayList<KeyWord> keywords = new ArrayList<>();
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");

			BasicDBObject matchByEntitySubProduct = new BasicDBObject();

			DBObject matchQuery = new BasicDBObject("EntityId", entityId);

			matchQuery.put("SubProductId", subProductId);
			matchQuery.put("ProductId", productId);
			if (isPositive) {
				matchQuery.put("SentenceScore", new BasicDBObject("$gt", 0));
			} else {
				matchQuery.put("SentenceScore", new BasicDBObject("$lt", 0));
			}
			matchByEntitySubProduct.put("$match", matchQuery);
			DBObject groupdata = new BasicDBObject();
			groupdata.put("SynonymWord", "$SynonymWord");
			groupdata.put("PostId", "$PostId");
			DBObject groupFields = new BasicDBObject("_id", groupdata);
			DBObject groupFields2 = new BasicDBObject("_id", "$_id.SynonymWord");
			groupFields2.put("Count", new BasicDBObject("$sum", 1));
			DBObject group = new BasicDBObject("$group", groupFields);
			DBObject group2 = new BasicDBObject("$group", groupFields2);

			DBObject sortOp = new BasicDBObject("$sort", new BasicDBObject(
					"Count", -1).append("_id", 1));
			DBObject skipField = new BasicDBObject("$skip", skip);
			AggregationOutput ids = null;
			DBObject limit = new BasicDBObject("$limit", 10);

			ids = collections.aggregate(matchByEntitySubProduct, group, group2,
					sortOp, skipField, limit);

			Iterable<DBObject> results = ids.results();

			if (results != null) {
				for (DBObject dbObject : results) {
					KeyWord keyword = new KeyWord();
					keyword.setKeyWord((String) dbObject.get("_id"));
					keyword.setcount((Integer) dbObject.get("Count"));
					keywords.add(keyword);
				}

			}

			/*
			 * db.noundata.aggregate({$match: { EntityId:334,SubProductId:1,
			 * NounWord:'foot',AdjScore:{$gt:0}}}, { $sort :{ AdjScore : -1}},
			 * {$project :{PostIds:"$PostId"}},{ $skip : 200}, { $limit : 10})
			 */

			// mongoClient.close();

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		return keywords;
	}

	public static ArrayList<KeyWord> getTraitsForEnterpriseCar(
			int entityId, int subProductId, String subDimension,
			String storeId, int limits,
			String stateCode) {

		ArrayList<KeyWord> keywords = new ArrayList<>();
		DB db = null;
		try {
			String database = FilePropertyManager.getProperty(
					ApplicationConstants.MONGO_PROPERTIES_FILE,
					ApplicationConstants.MONGO_DB);
			db = getMongoDB(database);
			db.requestStart();
			db.requestEnsureConnection();
			DBCollection collections = db.getCollection("features");
			BasicDBObject matchByEntitySubProduct = new BasicDBObject();
			BasicDBObject matchQuery = new BasicDBObject();
			matchQuery.put("EntityId", entityId);
			matchQuery.put("SubProductId", subProductId);

			if (storeId != null && !storeId.contains("all")
					&& !storeId.contains("null")) {
				matchQuery.put("ProductId", storeId);
			}
			matchQuery.put("StateCode", stateCode);
			matchQuery.put("SubDimension", subDimension);

			matchByEntitySubProduct.put("$match", matchQuery);

			DBObject groupFields1 = new BasicDBObject("_id", new BasicDBObject(
					"SynonymWord", "$SynonymWord").append("PostId", "$PostId"));
			groupFields1.put("adjscore", new BasicDBObject("$push",
					"$SentenceScore"));

			DBObject group1 = new BasicDBObject("$group", groupFields1);

			DBObject groupFields2 = new BasicDBObject("_id", "$_id.SynonymWord");
			groupFields2.put("Count", new BasicDBObject("$sum", 1));
			groupFields2.put("adjscore2", new BasicDBObject("$push",
					"$adjscore"));
			DBObject group2 = new BasicDBObject("$group", groupFields2);
			DBObject sortOp = new BasicDBObject("$sort", new BasicDBObject(
					"Count", -1).append("_id", 1));

			DBObject limit = new BasicDBObject("$limit", limits);

			/*
			 * db.noundata.aggregate({$match:{SubProductId:10,NounWord:"compliment"
			 * }},
			 * {$group:{"_id":{"ProductId":"$ProductId","PostId":"$PostId"},adjscore
			 * :{"$addToSet":"$AdjScore"}}},
			 * {$group:{_id:"$_id.ProductId","Count"
			 * :{$sum:1},adjscore2:{"$push":"$adjscore"}}},
			 * {$sort:{"Count":-1}},{$limit:10},
			 * 
			 * {$unwind:"$adjscore2"},{$unwind:"$adjscore2"}, this for Second
			 * Querys {$match:{"adjscore2":{$gt:0}}},
			 * {$group:{_id:"$_id","PosCount":{$sum:1}}})
			 */

			DBObject unwind = new BasicDBObject("$unwind", "$adjscore2");
			DBObject match2 = new BasicDBObject("$match", new BasicDBObject(
					"adjscore2", new BasicDBObject("$gt", 0)));
			DBObject groupfields3 = new BasicDBObject("_id", "$_id");
			groupfields3.put("thisCount", new BasicDBObject("$sum", 1));
			groupfields3.put("totCount", new BasicDBObject("$first", "$Count"));
			DBObject group3 = new BasicDBObject("$group", groupfields3);
			DBObject sortOp2 = new BasicDBObject("$sort", new BasicDBObject(
					"totCount", -1).append("_id", 1));
			AggregationOutput ids = collections.aggregate(
					matchByEntitySubProduct, group1, group2, sortOp, limit,
					unwind, match2, group3, sortOp2);

			Iterable<DBObject> results = ids.results();
			if (results != null) {
				for (DBObject dbObject : results) {
					KeyWord keyword = new KeyWord();
					keyword.setKeyWord((String) dbObject.get("_id"));
					keyword.setPosCount(((Integer) dbObject.get("thisCount")));
					keyword.setTotalCount(((Integer) dbObject.get("thisCount")));
					keywords.add(keyword);
				}
			}
			match2 = new BasicDBObject("$match", new BasicDBObject("adjscore2",
					new BasicDBObject("$lt", 0)));
			ids = collections.aggregate(matchByEntitySubProduct, group1,
					group2, sortOp, limit, unwind, match2, group3, sortOp2);
			Iterable<DBObject> results2 = ids.results();

			if (results2 != null) {
				for (DBObject dbObject : results2) {
					String word = (String) dbObject.get("_id");
					int negCount = (Integer) dbObject.get("thisCount");
					int flag = 0;
					for (KeyWord keyword : keywords) {

						if (keyword.getKeyWord().equals(word)) {
							flag = 1;
							keyword.setNegativeCount(negCount);
							keyword.setTotalCount(keyword.getTotalCount()
									+ negCount);
						}

					}
					if (flag == 0) {
						KeyWord keyword = new KeyWord();
						keyword.setKeyWord(word);
						keyword.setNegativeCount(negCount);
						keyword.setTotalCount(negCount);
						keywords.add(keyword);
					}
				}

			}

		} catch (IOException ex) {
			// Unreachable code
			LOG.error("", ex);
		} catch (MongoException ex) {
			// Exception never thrown
			LOG.error("MongoException occured during batch insert. ", ex);
		} catch (Exception ex) {
			// Handle exception
			LOG.error("", ex);
		} finally {
			if (null != db) {
				db.requestDone();
			}
		}
		Comparator<KeyWord> reverseComparator = Collections
				.reverseOrder(KEYWORDS_ORDER);
		Collections.sort(keywords, reverseComparator);

		return keywords;
	}

	public static void main(String[] args) throws ParseException {

		// getEmployees(417, 19, "AyCwZLVsU6IwkML4qzBPHQ", true);
		// getShoppingExperienceData(417, 19, "AyCwZLVsU6IwkML4qzBPHQ", null);
		// getStores("macystore");
		// getAllSubDimensions(19,417);
		/*
		 * List<StoreDetailsEntity> details = new
		 * ArrayList<StoreDetailsEntity>(); StoreDetailsEntity detail2 = new
		 * StoreDetailsEntity(); detail2.setId("d81141"); details.add(detail2);
		 * StoreDetailsEntity detail3 = new StoreDetailsEntity();
		 * detail3.setId("d81143"); details.add(detail3); //
		 * getTopKeyWordsForStore( details, 9, 25); getTopWords("d81315", 0, 25,
		 * 9, true);
		 */

		// getAnalyticsDetailsForProducts();
		getDataForPieChart(417, 39, "1012872");

	}
}
