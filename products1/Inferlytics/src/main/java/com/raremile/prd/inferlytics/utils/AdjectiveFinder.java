/**
 *  * Copyright (c) 2013 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.raremile.prd.inferlytics.commons.POSConstants;
import com.raremile.prd.inferlytics.preprocessing.POSTagger;
import com.raremile.prd.inferlytics.preprocessing.Tokenizer;



import edu.stanford.nlp.ling.TaggedWord;

/**
 * @author Praty
 * @created Apr 19, 2013
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class AdjectiveFinder {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(AdjectiveFinder.class);

	public static void main(String[] s) {
		try {
			String pathToSWN = "C:\\Projects\\Sentiment Analysis\\DB\\FeedDump\\NIkeWomen.csv";
			BufferedReader csv = new BufferedReader(new FileReader(pathToSWN));
			Map<String, Integer> adjWordCounterMap = new HashMap<String, Integer>();
			String line = "";
			try {
				while ((line = csv.readLine()) != null) {
					String content = line;

					ArrayList<TaggedWord> taggedSentence = POSTagger
							.getTaggedWords(Tokenizer
									.getStandardTokens(content));

					for (TaggedWord taggedWord : taggedSentence) {
						if (Util.getCommonPOS(taggedWord.tag()) == POSConstants.NounSingular) {
							if (!adjWordCounterMap.containsKey(taggedWord
									.word().toLowerCase())) {
								adjWordCounterMap.put(taggedWord.word()
										.toLowerCase(), 1);
							} else {
								adjWordCounterMap.put(taggedWord.word()
										.toLowerCase(),
										adjWordCounterMap.get(taggedWord.word()
												.toLowerCase()) + 1);
							}
						}

					}
				}
				StringBuffer sb = new StringBuffer();

				for (Entry<String, Integer> entry : adjWordCounterMap
						.entrySet()) {
					sb.append(entry.getKey() + "\t" + entry.getValue()
							+ "\n");
				}

				DataOutputStream os = new DataOutputStream(
						new FileOutputStream(
								"C:\\Projects\\Sentiment Analysis\\Data\\NounForWomenShoes.csv"));

				// log.info("DataOutputStream---> " + os);
				os.write(sb.toString().getBytes());
				os.close();

			} catch (IOException e) {

				LOG.error("IOException while performing operation in main", e);
			} catch (ClassNotFoundException e) {

				LOG.error(
						"ClassNotFoundException while performing operation in main",
						e);
			}

		} catch (FileNotFoundException e) {
			LOG.error("", e);
		}
	}

	/*
	 * static Map<String, Integer> sortByValue(Map<String, Integer> map) {
	 * List<String> list = new LinkedList(map.entrySet());
	 * Collections.sort(list, new Comparator() {
	 * 
	 * @Override public int compare(Object o1, Object o2) { return ((Comparable)
	 * ((Map.Entry) (o1)).getValue()) .compareTo(((Map.Entry) (o2)).getValue());
	 * } });
	 * 
	 * Map result = new LinkedHashMap(); for (Iterator it = list.iterator();
	 * it.hasNext();) { Map.Entry entry = (Map.Entry) it.next();
	 * result.put(entry.getKey(), entry.getValue()); } return result; }
	 */
}
