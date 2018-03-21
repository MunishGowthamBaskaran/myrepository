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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.entity.Feed;
import com.raremile.prd.inferlytics.entity.LexiconMap;
import com.raremile.prd.inferlytics.entity.Opinion;
import com.raremile.prd.inferlytics.preprocessing.CacheManager;
import com.raremile.prd.inferlytics.preprocessing.LexiconStructureGenerator;
import com.raremile.prd.inferlytics.preprocessing.ModifierStructureGenerator;
import com.raremile.prd.inferlytics.sentiment.SentimentAnalysis;
import com.raremile.prd.inferlytics.utils.Util;



/**
 * @author Pratyusha
 * @created Jun 10, 2013
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class FileSystemConnector {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(FileSystemConnector.class);

	public static void analyseSentimentFromFile(String path) {

		LOG.info("processing path" + path);
		File file = new File(path);
		StringBuffer result = new StringBuffer("");
		if (file.list() != null) {
			List<Feed> feeds = new ArrayList<Feed>();
			for (int i = 0; i < file.list().length; i++) {
				File f = new File(path + File.separator + file.list()[i]);
				if (f.isFile()) {
					Feed feed = processFile(f);
					Opinion opinion = feed.getOpinion();

					if (opinion != null
							&& opinion.getOpinionOrientation() != null) {

					result.append(f.getName())
							.append(" has a sentiment: ")
							.append(opinion.getOpinionOrientation()
									.getPolarity())
							.append(" with score ")
							.append(opinion.getOpinionOrientation()
										.getPolarity()).append("\n");
					}
					// write all the results into a csv file so that original
					// value and results should be compared.
					feeds.add(feed);
				} else if (f.isDirectory()) {
					// Not doing anything as of now.
				}
			}
			try {
				LOG.info(result);
				String filePath = path + File.separator + "result.txt";
				File resultFile = new File(filePath);
			// if file does not exists, then create it
				if (!resultFile.exists()) {
					resultFile.createNewFile();
			}

				FileWriter fw = new FileWriter(resultFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

				bw.write(result.toString());
				bw.close();
			} catch (IOException e) {

				LOG.error(
						"IOException while performing operation in analyseSentimentFromFile",
						e);
			}
			DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getLexiconDAO().storePostBatch(feeds, "movies", false);
		}

	}

	/**
	 * @param f
	 */
	private static Feed processFile(File f) {
		LOG.trace("Method: processFile called.");
		Feed feed = new Feed();
		try {
			String fileContent = Util.readFile(f);

			Opinion opinion = new Opinion();
			opinion.setObject("movie");

			feed.setOpinion(opinion);
			feed.setFeedData(fileContent);
			// analyse sentiment here

			SentimentAnalysis.setOpinion(feed);

		} catch (IOException e) {

			LOG.error("IOException while performing operation in processFile",
					e);
		}
		LOG.trace("Method: processFile finished.");
		return feed;
	}


	public static void main(String[] s) {
		long timeStart = System.currentTimeMillis();

		if (null == LexiconMap.unigrams) {
			new LexiconStructureGenerator();
			LexiconMap.unigrams.clear();
			new ModifierStructureGenerator();
			CacheManager.instantiateOtherFactors();
		}
		analyseSentimentFromFile("C:\\Projects\\Sentiment Analysis\\Data\\ClassifiedData\\txt_sentoken\\neg");

		LOG.info("Time taken " + (timeStart - System.currentTimeMillis()));
	}

}
