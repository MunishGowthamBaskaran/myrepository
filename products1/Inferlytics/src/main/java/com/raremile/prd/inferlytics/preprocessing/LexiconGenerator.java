package com.raremile.prd.inferlytics.preprocessing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.raremile.prd.inferlytics.database.InsertThread;



/**
 * @author Pratyusha
 * @created May 31, 2013
 * 
 *          One time use that calculates overall score of a lexicon from
 *          sentiwornet dump
 * 
 */
public class LexiconGenerator {
	private static final Logger LOG = Logger.getLogger(LexiconGenerator.class);
	private final String pathToSWN = "/home/pratyusha/C Drive/Projects/Sentiment Analysis/Softwares/SentiWordNet_3.0.0/home/swn/www/admin/dump/SentiWordNet_3.0.0_20130122.txt";
	private final HashMap<String, String> _dict;
	Map<String, Double> lexicon;

	public LexiconGenerator() throws IOException {

		_dict = new HashMap<String, String>();
		lexicon = new HashMap<String, Double>();
		HashMap<String, Vector<Double>> _temp = new HashMap<String, Vector<Double>>();
		BufferedReader csv = null;
		try {
			csv = new BufferedReader(new FileReader(pathToSWN));
			String line = "";
			while ((line = csv.readLine()) != null) {
				String[] data = line.split("\t");

				Double score = Double.parseDouble(data[2])
						- Double.parseDouble(data[3]);
				String[] words = data[4].split(" ");
				for (String w : words) {

					if (w.contains("_")) {
						LOG.info("Multiword Here -- >");
					}

					String[] w_n = w.split("#");
					// w_n[0] += "#"+data[0];
					int index = Integer.parseInt(w_n[1]) - 1;
					if (_temp.containsKey(w_n[0])) {
						Vector<Double> v = _temp.get(w_n[0]);
						if (index > v.size()) {
							for (int i = v.size(); i < index; i++) {
								v.add(0.0);
							}
						}
						v.add(index, score);
						_temp.put(w_n[0], v);
					} else {
						Vector<Double> v = new Vector<Double>();
						for (int i = 0; i < index; i++) {
							v.add(0.0);
						}
						v.add(index, score);
						_temp.put(w_n[0], v);
					}
				}
			}

			int nultiplescorecount = 0;
			int counter = 0;
			Set<String> temp = _temp.keySet();
			for (String word : temp) {
				if (word.contains("_")) {
					System.out.println("Multiword Here -- >");
				}
				Vector<Double> v = _temp.get(word);
				double score = 0.0;
				double sum = 0.0;

				for (int i = 0; i < v.size(); i++) {
					score += ((double) 1 / (double) (i + 1)) * v.get(i);
					if (i == 1) {
						nultiplescorecount++;
					}
				}
				for (int i = 1; i <= v.size(); i++) {
					sum += (double) 1 / (double) i;
				}
				score /= sum;

				/*
				 * String sent = ""; if (score >= 0.75) { sent =
				 * "strong_positive"; } else if (score > 0.25 && score <= 0.5) {
				 * sent = "positive"; } else if (score > 0 && score >= 0.25) {
				 * sent = "weak_positive"; } else if (score < 0 && score >=
				 * -0.25) { sent = "weak_negative"; } else if (score < -0.25 &&
				 * score >= -0.5) { sent = "negative"; } else if (score <=
				 * -0.75) { sent = "strong_negative"; }
				 */
				// _dict.put(word, sent);
				if (score != 0) {
					lexicon.put(word, score);
					counter++;

					if (counter == 500) {
						InsertThread thread = new InsertThread();
						thread.setDbMethodToInvoke("storeLexicon");
						Map<String, Double> lexiconToInsert = new HashMap<String, Double>();
						lexiconToInsert.putAll(lexicon);
						lexicon = new HashMap<String, Double>();
						thread.setLexiconMap(lexiconToInsert);
						thread.start();

						counter = 0;
					}
				}

			}
			LOG.info(lexicon.size());
			long timestart = System.currentTimeMillis();
			// Operator.getInstance().storeLexicon(lexicon);
			InsertThread thread = new InsertThread();
			thread.setDbMethodToInvoke("storeLexicon");
			thread.setLexiconMap(lexicon);
			thread.start();
			long timeEnd = System.currentTimeMillis();
			LOG.info("  time taken to insert " + (timeEnd - timestart));
			LOG.info("multiplescorecount" + nultiplescorecount);
		} catch (Exception e) {
			LOG.error("",e);
		} finally {
			if (csv != null) {
				csv.close();
			}
		}
	}

	public String extract(String word, String pos) {
		return _dict.get(word + "#" + pos);
	}

	public static void main(String[] s) throws IOException {
		new LexiconGenerator();

	}
}