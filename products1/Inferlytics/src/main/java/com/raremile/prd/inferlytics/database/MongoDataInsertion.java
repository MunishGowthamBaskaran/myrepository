package com.raremile.prd.inferlytics.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


public class MongoDataInsertion {

	private static final Logger LOG = Logger.getLogger(MongoDataInsertion.class);

	public static Map<String,String> catParentMap;

	public static void  fillCategoryParentMap(){
		String pathTocategoryDetails = "/home/pratyusha/C Drive/Projects/Sentiment Analysis/InferlyticsRR/Data/FeedSamples/xxx_category_details.csv";
		BufferedReader csv = null;
		try{
			csv = new BufferedReader(new FileReader(pathTocategoryDetails));
			String line = "";
			catParentMap =new HashMap<>();

			while ((line = csv.readLine()) != null) {
				String content = line;

				String[] data = content.split("\t");
				if(data.length == 1){

				}
 else if (data.length == 3) {
					catParentMap.put(data[0], data[2]);
				}

			}
		}catch(IOException ex){
			LOG.error("",ex);
		}finally{
			try {
				csv.close();
			} catch (IOException e) {
				LOG.error(e);
			}
		}
	}

	public static void insertToMongoFromCsv(){
		BufferedReader br = null;
		List<DBObject> dbProductList = null;
		try {

			String sCurrentLine;


			br = new BufferedReader(new FileReader(
					"/home/pratyusha/C Drive/Projects/Sentiment Analysis/Data/Fragrance/FragranceNet.com/6Jan2014/new/secondPage/ProductsNew.csv"));
			dbProductList = new ArrayList<>();
			DBObject product = null;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] splitString = sCurrentLine.split("\t");



				System.out.println("_id Product ProductId ProductName ProductUrl ProdImgUrl "+splitString[0]+ splitString[1]+splitString[2]+splitString[5]+splitString[3]+"http://"+splitString[4]);

				product = new BasicDBObject();

				product.put("_id", ""+splitString[0]);
				product.put("Product",  splitString[1]);
				product.put("ProductId", ""+ splitString[2]);
				product.put("ProductName",  splitString[5]);
				product.put("ProductUrl",  splitString[3]);
				product.put("ProdImgUrl", "http://"+splitString[4]);
				dbProductList.add(product);
				MongoConnector.InsertObjectList(dbProductList, "products");
				dbProductList.clear();
			}


		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		fillCategoryParentMap();
		storeProductsByBrand("xxx");

		//insertToMongoFromCsv();

	}

	public static void storeProductsByBrand(String brand) {
		List<DBObject> dbProductList = null;
		String pathToProductDetails = "/home/pratyusha/C Drive/Projects/Sentiment Analysis/InferlyticsRR/Data/FeedSamples/xxx_products_details_2jun2014.csv";
		BufferedReader csv = null;
		try {

			csv = new BufferedReader(new FileReader(pathToProductDetails));
			String line = "";
			dbProductList = new ArrayList<>();
			DBObject product = null;
			while ((line = csv.readLine()) != null) {
				product = new BasicDBObject();
				String[] data = line.split("\t");
				product.put("Product", brand);
				product.put("ProductId", data[0]);
				product.put("ProductName", data[1]);
				product.put("ProductUrl", data[3]);
				product.put("ProdImgUrl", data[2]);

				String defaultCat = data[4];
				String[] catListArray = defaultCat.split("\\|");


				ArrayList<String> catList = new ArrayList<>();

				for (String string : catListArray) {
					catList.add(string);
				}
				/**
				 * get the parent category of default category and add it to the
				 * category list that the product belongs.
				 */
				catList.add(catParentMap.get(data[5]));

				product.put("Categories", catList);
				product.put("defaultCategory", data[5]);
				Double price = Double.parseDouble(data[6]);
				product.put("Price", price);
				dbProductList.add(product);				
			}
			MongoConnector.InsertObjectList(dbProductList, "products");
		} catch (Exception e) {
			LOG.error("Error occured while getting moltonbrown products from this path : "
					+ pathToProductDetails,e);
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


}
