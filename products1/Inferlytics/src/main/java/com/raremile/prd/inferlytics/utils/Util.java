package com.raremile.prd.inferlytics.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.BreakIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.commons.FilePropertyManager;
import com.raremile.prd.inferlytics.commons.POSConstantsShortened;
import com.raremile.prd.inferlytics.commons.Sentiword;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.entity.CommentsHtml;
import com.raremile.prd.inferlytics.entity.Feed;
import com.raremile.prd.inferlytics.entity.LexiconMap;
import com.raremile.prd.inferlytics.entity.OtherFactors;
import com.raremile.prd.inferlytics.entity.Product;
import com.raremile.prd.inferlytics.entity.QueryLevelCache;
import com.raremile.prd.inferlytics.entity.Relations;
import com.raremile.prd.inferlytics.preprocessing.CacheManager;
import com.raremile.prd.inferlytics.preprocessing.LexiconStructureGenerator;
import com.raremile.prd.inferlytics.preprocessing.ModifierStructureGenerator;
import com.raremile.prd.inferlytics.preprocessing.POSTagger;

public class Util {

	private static final Logger LOG = Logger.getLogger(Util.class);
	private static final String TWITTER_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";

	public static final Comparator<Feed> FEED_ORDER = new Comparator<Feed>() {

		@Override
		public int compare(Feed feed1, Feed feed2) {
			return feed2.getTime().compareTo(feed1.getTime());
		}
	};

	public static final Comparator<Sentiword> SENTIWORD_ORDER = new Comparator<Sentiword>() {

		@Override
		public int compare(Sentiword sentiword1, Sentiword sentiword2) {

			return sentiword1.getWord().compareTo(sentiword2.getWord());
		}
	};

	public static void addValueToMap(
			HashMap<String, ArrayList<String>> sentimentFactors, String value,
			String key) {
		if (null != sentimentFactors) {
			ArrayList<String> list;
			if (sentimentFactors.containsKey(key)) {
				list = sentimentFactors.get(key);
			} else {
				list = new ArrayList<String>();
			}
			list.add(value);
			list.trimToSize();
			LOG.info("key --" + key + " values=" + list);
			sentimentFactors.put(key, list);
		}
	}

	public static List<CommentsHtml> appendEmTag(String feed) {

		List<CommentsHtml> pcontent = null;
		if (feed != null) {
			pcontent = new ArrayList<CommentsHtml>();

			try {
				List<String> sentences = getSentences(feed);
				for (String sentence : sentences) {
					CommentsHtml emElement = new CommentsHtml();
					emElement.setTagName("em");
					emElement.setTextContent(sentence);
					pcontent.add(emElement);
				}

			} catch (Exception e) {
				LOG.info("Exception" + e);
			}

		}

		return pcontent;
	}

	public static String appendEmTagForComment(String feed, int sentNo) {

		String pcontent = "";
		if (feed != null) {
			try {
				List<String> sentences = getSentences(feed);
				int count = 1;
				for (String sentence : sentences) {
					if (count == sentNo) {
						sentence = "<em class='highlight'>" + sentence
								+ "</em>";
					} else {
						sentence = "<em>" + sentence + "</em>";
					}
					count++;
					pcontent += sentence;
				}

			} catch (Exception e) {
				LOG.info("Exception" + e);
			}

		}

		return pcontent;
	}

	// Method to appendSpanTag to Feed
	public static void appendSpanTag(Map<String, String> feeds)
			throws IOException {
		String strFeed = new String();

		for (Entry<String, String> entry : feeds.entrySet()) {
			String feed = entry.getValue();
			strFeed = "";
			BreakIterator iterator = BreakIterator
					.getSentenceInstance(Locale.US);
			iterator.setText(feed);
			int start = iterator.first();
			for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator
					.next()) {
				strFeed = strFeed + "<span class='sentence'>"
						+ feed.substring(start, end) + "</span>";
			}

			entry.setValue(strFeed);
		}

	}

	public static String formatDate(String date) {
		SimpleDateFormat toformatter = new SimpleDateFormat("MMM dd, yyyy");
		SimpleDateFormat fromFormattedDate = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss z yyyy");
		String dateString = "";
		try {
			Date parseddate = fromFormattedDate.parse(date);
			dateString = toformatter.format(parseddate);

		} catch (Exception e) {
			LOG.info(e);
		}

		return dateString;
	}

	public static boolean fromDetailedSentiInteger(int detailedSenti) {
		boolean senti = false;
		switch (detailedSenti) {
		case 0:
			senti = true;
			break;
		case 1:
			senti = true;
			break;
		case 2:
			senti = true;
			break;
		case 3:
			senti = false;
			break;
		case 4:
			senti = false;
			break;

		case 5:
			senti = false;
			break;
		case 6:
			senti = true;
			break;
		}
		return senti;
	}

	public static String generateSHA1(byte[] dataBytes) throws IOException {
		StringBuffer sb = new StringBuffer("");
		try {
			if (dataBytes == null) {
				return null;
			}
			MessageDigest md = MessageDigest.getInstance("SHA1");
			byte[] mdbytes = md.digest(dataBytes);

			// convert the byte to hex format
			for (byte mdbyte : mdbytes) {
				sb.append(Integer.toString((mdbyte & 0xff) + 0x100, 16)
						.substring(1));
			}
			mdbytes = null;
			dataBytes = null;
		} catch (NoSuchAlgorithmException e) {
			LOG.error("Error in getting SHA1.", e);

			// Giving it a unique UUID, so that it doesnt show any duplicates.
			sb.append(UUID.randomUUID());
		}

		return sb.toString();

	}

	public static String getCommonPOS(String POStag) {
		if (Arrays.asList(POSConstantsShortened.JJ).contains(POStag)) {
			return "JJ";
		} else if (Arrays.asList(POSConstantsShortened.NN).contains(POStag)) {
			return "NN";
		} else if (Arrays.asList(POSConstantsShortened.PP).contains(POStag)) {
			return "PP";
		} else if (Arrays.asList(POSConstantsShortened.RB).contains(POStag)) {
			return "RB";
		} else if (Arrays.asList(POSConstantsShortened.VB).contains(POStag)) {
			return "VB";
		}
		return POStag;
	}

	public static Map<String, Product> getProductsByBrand(String brand) {
		Map<String, Product> idProductMap = null;
		String pathToProductDetails = "PATH_TO_" + brand.toUpperCase()
				+ "_DETAILS";
		BufferedReader csv = null;
		try {
			String pathToFile = FilePropertyManager.getProperty(
					ApplicationConstants.APPLICATION_PROPERTIES_FILE,
					pathToProductDetails);
			csv = new BufferedReader(new FileReader(pathToFile));
			String line = "";
			idProductMap = new HashMap<>();
			while ((line = csv.readLine()) != null) {
				String[] data = line.split("\t");
				Product product = new Product();
				product.setProductName(data[1]);
				product.setProductId(data[0]);
				product.setImageUrl(data[3]);
				product.setProductUrl(data[2]);
				if (!idProductMap.containsKey(product.getProductId())) {
					idProductMap.put(data[0], product);

				}
			}
		} catch (Exception e) {
			LOG.error(
					"Error occured while getting moltonbrown products from this path : "
							+ pathToProductDetails, e);
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
		return idProductMap;
	}

	public static double getRatingFromScore(double score) {

		double rating = 1;
		if (score < (-0.1)) {
			rating = 1;
		} else if (score > (-.1) && score <= 0) {
			rating = 2;
		} else if (score > 0 && score <= 0.25) {
			rating = 3;
		} else if (score > 0.25 && score <= 0.35) {
			rating = 3.5;
		} else if (score > 0.35 && score <= 0.6) {
			rating = 4;
		} else if (score > 0.6 && score <= 0.9) {
			rating = 4.5;
		} else {
			rating = 5;
		}
		return rating;

	}

	public static LinkedList<String> getSentences(String text)
			throws IOException {
		LinkedList<String> sentences = new LinkedList<String>();
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		iterator.setText(text);
		int start = iterator.first();
		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator
				.next()) {
			String sentence = text.substring(start, end);
			if (sentence.contains(". ")) {
				sentences.addAll(splitSentences(sentence));
			} else {
				sentences.add(sentence);
			}
		}

		return sentences;
	}

	public static HashMap<String, String> getSentiwordPOS() {

		HashMap<String, String> sentiwordPOS = new HashMap<String, String>();

		sentiwordPOS.put("a", "JJ");
		sentiwordPOS.put("n", "NN");
		sentiwordPOS.put("r", "RB");
		sentiwordPOS.put("v", "VB");

		return sentiwordPOS;

	}

	public static Date getTwitterDateFromString(String textDate)
			throws ParseException {
		if (textDate != null && !textDate.isEmpty()) {
			SimpleDateFormat sf = new SimpleDateFormat(TWITTER_DATE_FORMAT,
					Locale.ENGLISH);
			sf.setLenient(true);
			return sf.parse(textDate);
		} else {
			return null;
		}

	}

	/**
	 * Check for a collection value
	 * 
	 * @param data
	 *            Collection
	 * @return success/failure boolean
	 */
	public static boolean isEmpty(Collection<?> data) {

		if (null == data || data.isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(String[] arg) {

		return arg == null || arg.length == 0;
	}

	/**
	 * Check for null and empty string
	 * 
	 * @param value
	 *            String
	 * @return success/failure boolean
	 */
	public static boolean isEmptyString(String value) {

		if (null == value || value.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static void main(String[] s) throws Exception {

		/*
		 * String sentence = "I love Find My iPhone";
		 * 
		 * LinkedList<String> tokens = Tokenizer.getTokens(sentence, null);
		 * 
		 * LOG.info("Tokens Initially-->" + tokens);
		 */

		long timeStrat = System.currentTimeMillis();

		if (null == LexiconMap.unigrams) {
			new LexiconStructureGenerator();
			LexiconMap.unigrams.clear();
			new ModifierStructureGenerator();
			CacheManager.instantiateOtherFactors();
			POSTagger.initializeTagger();
		}
		// DataGenerationThread thread = new DataGenerationThread();
		// thread.start();

		DAOFactory.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
		.getLexiconDAO().fillPostsFromStaging();
		// thread.terminate();
		long timeEnd = System.currentTimeMillis();
		System.out.println("Time-Taken " + (timeEnd - timeStrat));

		// String feed = Util
		// .appendSpanTag("From ordering to receiving the Tesco WMV510 washing machine, the service was excellent. We were well informed by email all along the way. The product was delivered on time, the delivery staff were efficient and helpful. The machine so far has been first class. As pentioners there is a need to spend money carefully, the reviews helped with the decision to buy and so far this has proved to be an excellent choice. It is simple to operate with a good range of programes and energy efficiency with cold wash, quick wash and the seperate rinse, spin and drain facilities are really useful. This was also the cheapest product that we found, there were no hidden charges and the clubcard points received, made this an even better buy. This has been a good buying experience");
		// System.out.println(feed);
	}

	public static boolean productStopword(String loweCaseToken, String query) {
		LOG.trace("Method: productStopword called.");
		boolean isProductStopword = false;
		if (null != QueryLevelCache.productBrandStopwordList) {
			List<String> stopwords = QueryLevelCache.productBrandStopwordList
					.get(query);
			if (null != stopwords) {
				for (String stopword : stopwords) {
					if (stopword.contains(loweCaseToken)) {
						isProductStopword = true;
						break;
					}
				}
			}

			LOG.trace("Method: productStopword finished.");
		}
		return isProductStopword;
	}

	/**
	 * @param file
	 * @throws IOException
	 */
	public static String readFile(File file) throws IOException {
		LOG.trace("Method: processFile called.");

		BufferedReader reader = null;
		StringBuilder stringBuilder = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			stringBuilder = new StringBuilder();
			String ls = System.getProperty("line.separator");

			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
		} catch (Exception ex) {
			LOG.error("Error while reading file " + ex);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		LOG.trace("Method: processFile finished.");
		return stringBuilder.toString();

	}

	// Method to set relation from feed
	public static void setRelation(Feed feed, String token) {
		Relations relation = OtherFactors.getRelation();
		if (relation.getRelationName().contains(token.toLowerCase())) {

			int index = relation.getRelationName().indexOf(token);
			if (feed.getGender() == null) {
				// Do Nothing
			} else {
				if (feed.getGender().equals("M")) {
					feed.setRelationId(relation.getPossibleMaleRelation().get(
							index));
				} else if (feed.getGender().equals("F")) {
					feed.setRelationId(relation.getPossibleFemaleRelation()
							.get(index));
				}
			}

		}
	}

	public static List<String> splitSentences(String text) {

		String[] sentencesSplit = text.split("\\.[ ]");
		List<String> sentences = new ArrayList<>();
		String sentence = "";
		for (String string : sentencesSplit) {
			if (!string.equals(" ")) {
				sentence = string + ". ";
				sentences.add(sentence);
			}
		}

		return sentences;
	}

}
