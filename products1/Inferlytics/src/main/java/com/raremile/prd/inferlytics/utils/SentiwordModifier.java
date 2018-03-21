package com.raremile.prd.inferlytics.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.commons.Sentiword;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.database.InsertThread;
import com.raremile.prd.inferlytics.preprocessing.POSTagger;
import com.raremile.prd.inferlytics.preprocessing.Tokenizer;



public class SentiwordModifier {
	private static final Logger LOG = Logger.getLogger(SentiwordModifier.class);
	private final static String pathToSWN = "C:\\Projects\\Sentiment Analysis\\DB\\SentiwordNet\\sentiwordnet_updateddata.csv";
	private static HashMap<String, String> _dict = null;

	static List<Sentiword> sentiList = new ArrayList<Sentiword>();

	public static void sentiwordModifier() {

		_dict = new HashMap<String, String>();

		try {
			Set<String> stopwords = DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getLexiconDAO().getStopword();
			BufferedReader csv = new BufferedReader(new FileReader(pathToSWN));
			String line = "";
			int lineno = 0;
			while ((line = csv.readLine()) != null) {
				lineno++;
				String[] data = line.split("\t");

				String word = data[4];// .split(" ");
				String[] onlyword = word.split("#");
				String[] glossary = data[5].split(";");
				Sentiword senti = new Sentiword();
				senti.setPOS(data[0]);
				senti.setId(Long.parseLong(data[1]));
				senti.setPosscore(Double.parseDouble(data[2]));
				senti.setNegscore(Double.parseDouble(data[3]));
				senti.setWord(word);
				senti.setGlossary(data[5]);
				String sentencePattern = "";
				for (String sentence : glossary) {

					if (null != onlyword[0] && !onlyword[0].isEmpty()
							&& sentence.contains(onlyword[0])) {
						System.out.println("HERE --> " + onlyword[0]);
						sentencePattern = getPOSPatterns(sentence, onlyword[0],
								stopwords);
						senti.setPattern(sentencePattern);
						sentiList.add(senti);
						break;
					}

				}

				if (sentiList.size() == 200) {
					InsertThread ist = new InsertThread();
					ist.setSentiList(sentiList);
					ist.setDbMethodToInvoke("storeSentiwordPatterns");
					ist.start();
					sentiList = new ArrayList<Sentiword>();
				}

			}
			// Collections.sort(sentiList, Util.SENTIWORD_ORDER);
			csv.close();
			System.out.println(sentiList.size());
			long timestart = System.currentTimeMillis();
			DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getLexiconDAO().storeSentiwordPatterns(sentiList);
			long timeEnd = System.currentTimeMillis();
			System.out.println("  time taken to insert "
					+ (timeEnd - timestart));

		} catch (Exception e) {
			LOG.error("",e);
		}
	}

	public String extract(String word, String pos) {
		return _dict.get(word + "#" + pos);
	}

	public static String getPOSPatterns(String sentence, String word,
			Set<String> stopwords) throws IOException, SQLException,
			ClassNotFoundException {
		// Get Tokens
		LinkedList<String> tokens = Tokenizer.getTokens(sentence, null);
		// Remove Stopwords
		/*
		 * LOG.info("Tokens Initially-->" + tokens);
		 * 
		 * LinkedList<String> tokensToBeTagged = TextNormalizer.removeStopWord(
		 * stopwords, tokens); LOG.info("Tokens after removing stopwords-->" +
		 * tokensToBeTagged);
		 */
		String POSpattern = POSTagger.getPOSPattern(tokens, word);
		return POSpattern;

	}

	public static void main(String[] s) {
		sentiwordModifier();

	}
}
