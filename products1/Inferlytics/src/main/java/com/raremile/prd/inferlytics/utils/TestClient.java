package com.raremile.prd.inferlytics.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.sentiment.SentimentAnalysis;

public class TestClient {
	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(TestClient.class);


	private static void analysePostsFromStaging() {
		SentimentAnalysis.initialize();
		DAOFactory.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
		.getLexiconDAO().fillPostsFromStaging();
	}


	public static void main(String[] args) {
		//testSentimentService();
		// synonymGenerator();
		analysePostsFromStaging();

	}

	/**
	 * This method is supposed to collect all synonyms for the words which are
	 * there in our sentiment database and Insert in a meaning full way in
	 * Synonym table.
	 */
	private static void synonymGenerator() {


		String proxylistFileLoc = "/home/pratyusha/C Drive/Projects/Sentiment Analysis/Data/proxyipport.csv";
		Map<String, String> proxylist = new HashMap<String, String>();
		BufferedReader csv = null;
		try {
			csv = new BufferedReader(new FileReader(proxylistFileLoc));
			String line = "";
			while ((line = csv.readLine()) != null) {
				String[] data = line.split(",");
				proxylist.put(data[0], data[1]);
			}
		} catch (Exception ex) {

		} finally {
			if (csv != null) {
				try {
					csv.close();
				} catch (IOException e) {

					LOG.error(
							"IOException while performing operation in synonymGenerator",
							e);
				}
			}
		}
		// System.out.println(proxylist);
		//
		//		DAOFactory.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
		//				.getLexiconDAO().getSynonyms(1854, 38500, proxylist);

	}

	/**
	 * Method to test Sentiment Service. Hardcoded Url and other parameters.
	 */
	private static void testSentimentService() {/*
		try {

			Client client = Client.create();

			WebResource webResource = client
					.resource("http://localhost:8080/RMSentimentAnalysis/rest/SentimentService");

			QueryContent queryContent = new QueryContent();
			queryContent.setQuery("nike");
			ArrayList<SimplePost> posts = new ArrayList<SimplePost>();
			SimplePost  post = new SimplePost();
			post.setPostId("12345");
			post.setContent("Great fit, fabric stays dry even in tough workouts. Very comfortable! ");

			posts.add(post);

	 * post = new SimplePost(); post.setPostId("1234567");
	 * post.setContent("Samsung galaxy is better than a iPhone ");
	 * posts.add(post);


			posts.trimToSize();
			queryContent.setPost(posts);

			String input = GsonAction.objectToJson(queryContent);
			// String input =
			// "{\"emailType\":0,\"host\":\"imap.gmail.com\",\"port\":0,\"username\":\"jasjiyo@gmail.com\",\"password\":\"bubbagump\",\"dsType\":0,\"analysisType\":0,\"emailFolderList\":\"\",\"SSL\":true,\"projectId\":0}";

			ClientResponse response = webResource.type("application/json")
					.post(ClientResponse.class, input);

			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			LOG.info("Output from Server .... \n");
			String output = response.getEntity(String.class);
			LOG.info(output);

		} catch (Exception e) {

			e.printStackTrace();

		}
	 */}
}
