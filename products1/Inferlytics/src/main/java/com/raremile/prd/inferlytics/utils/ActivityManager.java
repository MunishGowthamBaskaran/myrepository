package com.raremile.prd.inferlytics.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.entity.ProdCommentAnalticInfo;

public class ActivityManager implements Job {
	private static Logger LOG = Logger.getLogger(ActivityManager.class);
	private static String LOGFILELOCAL = "/home/pratyusha/C Drive/Projects/Sentiment Analysis/logs/activity.log";
	private static String LOGFILE = "/var/lib/tomcat7/logs/activity.log";

	/**
	 * @param b 
	 * 
	 */
	public static void insertActivityToDB(boolean b) {
		ProdCommentAnalticInfo prodCommentAnalticInfo = new ProdCommentAnalticInfo();
		BufferedReader br = null;
		int j = 1;
		while (true) {
			String logfileExtension = null;
			if(b){
				logfileExtension = LOGFILE + "." + Integer.toString(j);
			}
			logfileExtension = LOGFILE;
			try {
				String sCurrentLine;
				br = new BufferedReader(new FileReader(logfileExtension));

				while ((sCurrentLine = br.readLine()) != null) {
					Pattern pattern = Pattern.compile("<(.*?)>");
					Matcher matcher = pattern.matcher(sCurrentLine);
					if (matcher.find()) {
						String data = matcher.group(1);
						String splitdata[] = data.split("\\|");
						int i = 0;
						for (String individualdata : splitdata) {

							individualdata = (individualdata.equals("null")) ? null
									: individualdata;
							switch (i) {
							case 0:
								prodCommentAnalticInfo
								.setEntity(individualdata);
								break;
							case 1:
								prodCommentAnalticInfo
								.setSubProduct(individualdata);
								break;
							case 2:
								prodCommentAnalticInfo
								.setCategory(individualdata);
								break;
							case 3:
								prodCommentAnalticInfo
								.setSubDimension(individualdata);
								break;
							case 4:
								prodCommentAnalticInfo.setWord(individualdata);
								break;
							case 5:
								prodCommentAnalticInfo
								.setProductId(individualdata);
								break;
							case 6:
								prodCommentAnalticInfo
								.setReviewType(individualdata);
								break;
							case 7:
								prodCommentAnalticInfo
								.setPostId(individualdata);
								break;
							case 8:
								prodCommentAnalticInfo
								.setUserIP(individualdata);
								break;
							case 9:
								prodCommentAnalticInfo.setDate(individualdata);
								break;
							default:
								break;

							}
							i++;

						}
						DAOFactory
						.getInstance(
								ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
								.getSentimentWidgetDAO()
								.insertProdCommentAnalytics(
										prodCommentAnalticInfo);

					}

				}
				j++;
			} catch (IOException e) {
				LOG.error(e);
				break;

			} finally {
				try {
					if (br != null) {
						br.close();

						File file = new File(logfileExtension);
						if (file.delete()) {
							LOG.info("Deleted file after inserting analytics into database");
						}
					}
				} catch (IOException ex) {
					LOG.error(ex);
				}
			}

		}
	}

	public static void insertToDatabase() {

		ProdCommentAnalticInfo prodCommentAnalticInfo = new ProdCommentAnalticInfo();
		BufferedReader br = null;
		while (true) {
			String logfileExtension = LOGFILELOCAL;
			try {
				String sCurrentLine;
				br = new BufferedReader(new FileReader(logfileExtension));

				while ((sCurrentLine = br.readLine()) != null) {
					System.out.println(sCurrentLine);
					Pattern pattern = Pattern.compile("<(.*?)>");
					Matcher matcher = pattern.matcher(sCurrentLine);
					if (matcher.find()) {
						String data = matcher.group(1);
						String splitdata[] = data.split("\\|");
						int i = 0;
						for (String individualdata : splitdata) {

							individualdata = (individualdata.equals("null")) ? null
									: individualdata;
							switch (i) {
							case 0:
								prodCommentAnalticInfo
								.setEntity(individualdata);
								break;
							case 1:
								prodCommentAnalticInfo
								.setSubProduct(individualdata);
								break;
							case 2:
								prodCommentAnalticInfo
								.setCategory(individualdata);
								break;
							case 3:
								prodCommentAnalticInfo
								.setSubDimension(individualdata);
								break;
							case 4:
								prodCommentAnalticInfo.setWord(individualdata);
								break;
							case 5:
								prodCommentAnalticInfo
								.setProductId(individualdata);
								break;
							case 6:
								prodCommentAnalticInfo
								.setReviewType(individualdata);
								break;
							case 7:
								prodCommentAnalticInfo
								.setPostId(individualdata);
								break;
							case 8:
								prodCommentAnalticInfo
								.setUserIP(individualdata);
								break;
							case 9:
								prodCommentAnalticInfo.setDate(individualdata);
								break;
							default:
								break;

							}
							i++;

						}
						DAOFactory
						.getInstance(
								ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
								.getSentimentWidgetDAO()
								.insertProdCommentAnalytics(
										prodCommentAnalticInfo);

					}

				}
			} catch (IOException e) {
				LOG.info(e);
				break;

			} finally {
				try {
					if (br != null) {
						br.close();

						File file = new File(logfileExtension);
						if (file.delete()) {
							LOG.info("Deleted file after inserting analytics into database");
						}
					}
				} catch (IOException ex) {
					System.out.println("Error");
					LOG.info(ex);
				}
			}

		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		insertToDatabase();
	}

	@Override
	public void execute(JobExecutionContext jExeCtx)
			throws JobExecutionException {

		insertActivityToDB(true);

	}

}
