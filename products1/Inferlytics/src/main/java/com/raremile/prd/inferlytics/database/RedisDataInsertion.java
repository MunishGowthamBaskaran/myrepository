package com.raremile.prd.inferlytics.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.commons.FilePropertyManager;
public class RedisDataInsertion {
	private static final Logger LOG = Logger.getLogger(RedisDataInsertion.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MongoDataInsertion.fillCategoryParentMap();
		 storeProductsFromMongo("amazonbooks");

	}
	public static void storeProductsByBrand(String brand) {
		
		String pathToProductDetails = "PATH_TO_"+brand.toUpperCase()+"_DETAILS";
		BufferedReader csv = null;
		Jedis jedis = null;
 int i=0;
			
		try {
			jedis = RedisConnector.getConnection();
			
			jedis.select(1);
			
			String pathToFile = "/home/pratyusha/C Drive/Projects/Sentiment Analysis/Data/MoltonBrown/moltonbrown_products_4112013.csv";
					//FilePropertyManager.getProperty(ApplicationConstants.APPLICATION_PROPERTIES_FILE, pathToProductDetails);
			csv = new BufferedReader(new FileReader(pathToFile));
			String line = "";
			String productName = brand+":";
			while ((line = csv.readLine()) != null) {
				String[] data = line.split("\t");
			    jedis.hset(productName+ data[0], "ProductName", data[1]);
				jedis.hset(productName+ data[0], "ProductUrl", data[3]);
				jedis.hset(productName+ data[0], "ProdImgUrl", data[2]);
				System.out.println(data[0]);
				System.out.println(data[1]);
				System.out.println(data[2]);
				System.out.println(data[3]);
				i++;
				}
			System.out.println(i);
		
		} catch (Exception e) {
			LOG.error("Error occured while getting moltonbrown products from this path : "
					+ pathToProductDetails,e);
			System.out.println("Error");
		} finally {
			if (csv != null) {
				try {
					csv.close();
				} catch (IOException e) {

					LOG.error(
							"IOException while performing operation in getMoltonBrownProducts",
							e);
				}
			}
		}
	
	}
	
	
public static void storeProductsFromMongo(String brand) {
		
		
	
		Jedis jedis = null;

			
		try {
			System.out.println("k");
			jedis = RedisConnector.getConnection();
					jedis.select(1);
				
			String host = FilePropertyManager.getProperty(ApplicationConstants.MONGO_PROPERTIES_FILE, ApplicationConstants.MONGO_HOST);
			String port=FilePropertyManager.getProperty(ApplicationConstants.MONGO_PROPERTIES_FILE, ApplicationConstants.MONGO_PORT);
			String database=FilePropertyManager.getProperty(ApplicationConstants.MONGO_PROPERTIES_FILE, ApplicationConstants.MONGO_DB);
			MongoClient mongoClient = new MongoClient(host, Integer.parseInt(port));
			
			DB db = mongoClient.getDB(database);
			DBCollection table = db.getCollection("products");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("Product", brand);
			DBCursor cursor = table.find(whereQuery);
	
			while (cursor.hasNext()){
				DBObject object= cursor.next();
				
				String id=(String)object.get("ProductId");
				System.out.println(id);
			    jedis.hset(brand+":"+ id, "ProductName", (String)object.get("ProductName"));
				jedis.hset(brand+":"+ id, "ProductUrl", (String)object.get("ProductUrl"));
				jedis.hset(brand+":"+ id, "ProdImgUrl", (String)object.get("ProdImgUrl"));
				}
		
		} catch (Exception e) {
			LOG.error("Error occured"
					,e);
			System.out.println(e);
		} 
	
	}

}
