/**
 *  * Copyright (c) 2013 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.connectors;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.entity.Feed;
import com.raremile.prd.inferlytics.entity.LexiconMap;
import com.raremile.prd.inferlytics.entity.Opinion;
import com.raremile.prd.inferlytics.entity.OtherFactors;
import com.raremile.prd.inferlytics.entity.StockPatterns;
import com.raremile.prd.inferlytics.preprocessing.CacheManager;
import com.raremile.prd.inferlytics.preprocessing.LexiconStructureGenerator;
import com.raremile.prd.inferlytics.preprocessing.ModifierStructureGenerator;
import com.raremile.prd.inferlytics.sentiment.AnalyseStocks;
import com.raremile.prd.inferlytics.sentiment.SentimentAnalysis;


/**
 * @author Praty
 * @created May 24, 2013
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class StockTweetsConnector {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(StockTweetsConnector.class);

	public static void main(String[] s) {
		try {
			String pathToSWN = "C:\\Projects\\Sentiment Analysis\\Data\\Stocks\\StockTwitsData.csv";
			BufferedReader csv = new BufferedReader(new FileReader(pathToSWN));
			String line = "";
			OtherFactors
					.setStopwords(DAOFactory
							.getInstance(
									ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
							.getLexiconDAO().getStopword());
			StockPatterns.stockPatternsGenerator();

			new LexiconStructureGenerator();
			LexiconMap.unigrams.clear();
			new ModifierStructureGenerator();
			CacheManager.instantiateOtherFactors();

			StringBuffer resultLine = new StringBuffer();
				while ((line = csv.readLine()) != null) {
				String[] data = line.split(",");
				String content = data[5];
				Map<String, String> result = AnalyseStocks
						.analyseStock(content);

				//Analyse Sentiment Here
				Feed feed = new Feed();
				feed.setFeedData(content);
				Opinion opinion = new Opinion();
				opinion.setObject("stocks");
				feed.setOpinion(opinion);
				SentimentAnalysis.setOpinion(feed);
				
				
				
				int size = result.size();
				int counter = 0;
				for (Entry<String, String> entry : result.entrySet()) {
					resultLine.append(entry.getKey().replaceFirst(";", ""))
							.append('-')
							.append(entry.getValue());
					if (counter < size - 1) {
						resultLine.append('|');
					}
 else {
						resultLine.append(',');
					}
					counter++;
				}
				if (result.size() == 0) {
					resultLine.append(',');
				}
				resultLine.append(data[5]).append(",");

				if (null != feed.getOpinion()
						&& null != feed.getOpinion().getOpinionOrientation()) {
					resultLine.append(feed.getOpinion().getOpinionOrientation()
							.getPolarity());
				}
				resultLine.append("\n");

			}
				DataOutputStream os = new DataOutputStream(
						new FileOutputStream(
							"C:\\Projects\\Sentiment Analysis\\Data\\Stocks\\StockTwitsResults_WithUpdatedScore.csv"));

				// log.info("DataOutputStream---> " + os);
				os.write(resultLine.toString().getBytes());
				os.close();
		} 
				catch (FileNotFoundException e) {
			LOG.error("", e);
		}
 catch (IOException e) {
			LOG.error("IOException while performing operation in main", e);
		} catch (ClassNotFoundException e) {

			LOG.error(
					"ClassNotFoundException while performing operation in main",
					e);
		}
	}
}
