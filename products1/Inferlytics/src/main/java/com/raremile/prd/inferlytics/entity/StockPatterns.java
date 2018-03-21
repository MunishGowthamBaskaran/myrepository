/**
 *  * Copyright (c) 2013 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;



/**
 * @author Praty
 * @created May 24, 2013
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class StockPatterns {
	private static Logger LOG = Logger.getLogger(StockPatterns.class);

	private static List<String> unigrams;
	private static List<ArrayList<ArrayList<Integer>>> ugramValuePointer;
	private static List<String> values;
	private static List<String> valuesFor;
	private static List<String> multiwordPatterns;

	StockPatterns() {
		unigrams = new ArrayList<String>();
		ugramValuePointer = new ArrayList<ArrayList<ArrayList<Integer>>>();
		values = new ArrayList<String>();
		valuesFor = new ArrayList<String>();
		multiwordPatterns = new ArrayList<String>();

	}

	private static boolean doesUnigramListExist() {
		if (null != unigrams && unigrams.size() > 0) {
			return true;
		}
		return false;
	}

	private static boolean doesUnigramExist(String unigram) {
		if (doesUnigramListExist() && unigrams.contains(unigram)) {
			return true;
		}
		return false;
	}

	public static List<String> getUnigrams() {
		return unigrams;
	}

	public static void setUnigrams(ArrayList<String> unigrams) {
		StockPatterns.unigrams = unigrams;
	}



	public static List<String> getValues() {
		return values;
	}

	public static void setValues(List<String> values) {
		StockPatterns.values = values;
	}

	public static List<String> getValuesFor() {
		return valuesFor;
	}

	public static void setValuesFor(List<String> valuesFor) {
		StockPatterns.valuesFor = valuesFor;
	}

	public static List<String> getMultiwordPatterns() {
		return multiwordPatterns;
	}

	public static void setMultiwordPatterns(List<String> multiwordPatterns) {
		StockPatterns.multiwordPatterns = multiwordPatterns;
	}

	/**
	 * @param ugramValuePointer
	 *            the ugramValuePointer to set
	 */
	public static void setUgramValuePointer(
			List<ArrayList<ArrayList<Integer>>> ugramValuePointer) {
		StockPatterns.ugramValuePointer = ugramValuePointer;
	}

	/**
	 * @return the ugramValuePointer
	 */
	public static List<ArrayList<ArrayList<Integer>>> getUgramValuePointer() {
		return ugramValuePointer;
	}

	public static void stockPatternsGenerator() {
		new StockPatterns();
		StockPatternEntity stockPatternEntity = DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getLexiconDAO().getStockPatterns();

		List<String> patterns = stockPatternEntity.getPatterns();
		int patternsSize = patterns.size();
		for (int wordCounter = 0; wordCounter < patternsSize; wordCounter++) {

			String word = patterns.get(0);
			String[] wordunigrams = word.split("_");
			int multiwordindex = -1;
			if (wordunigrams.length > 1) {
				multiwordindex = getMultiWordPatternIndex(word);
			}
			for (String unigram : wordunigrams) {
				ArrayList<ArrayList<Integer>> patternValueList;
				if (StockPatterns.getUnigrams().contains(unigram)) {
					int unigramIndex = unigrams.indexOf(unigram);
					patternValueList =ugramValuePointer.get(unigramIndex);
					/**
					 * Handle Original Score Here
					 */
					ArrayList<Integer> unigramValueIndex = patternValueList.get(0);
					if (null == unigramValueIndex && wordunigrams.length == 1) {
						unigramValueIndex = new ArrayList<Integer>();
						/**
						 * If is Not Multiword
						 */
						unigramValueIndex.add(null);
						unigramValueIndex.add(getValueIndex(stockPatternEntity
								.getValues().get(patterns.indexOf(word))));
						unigramValueIndex.add(getValueForIndex(stockPatternEntity
								.getValuesFor().get(patterns.indexOf(word))));
						unigramValueIndex.trimToSize();
						patternValueList.remove(0);
						patternValueList.add(0, unigramValueIndex);
					
					}
					if (multiwordindex != -1) {
						/**
						 * This is a multi word pattern.
						 */
						ArrayList<Integer> patternvalueIndex = new ArrayList<Integer>();
						patternvalueIndex.add(getMultiWordPatternIndex(word));
						patternvalueIndex.add(getValueIndex(stockPatternEntity
								.getValues().get(patterns.indexOf(word))));
						patternvalueIndex
								.add(getValueForIndex(stockPatternEntity
										.getValuesFor().get(
												patterns.indexOf(word))));
						patternvalueIndex.trimToSize();
						patternValueList.add(patternvalueIndex);

					}
				} else {
					patternValueList = new ArrayList<ArrayList<Integer>>();
					/**
					 * Handle Original Score Here
					 */

					if (wordunigrams.length == 1) {
						/**
						 * If is Not Multiword
						 */
						ArrayList<Integer> valueIndex = new ArrayList<Integer>();
						valueIndex.add(null);
						valueIndex.add(getValueIndex(stockPatternEntity
								.getValues().get(patterns.indexOf(word))));
						valueIndex.add(getValueForIndex(stockPatternEntity
								.getValuesFor().get(patterns.indexOf(word))));
						valueIndex.trimToSize();
						patternValueList.add(0, valueIndex);

					} else {
						/**
						 * This is a multiword pattern.
						 */
						ArrayList<Integer> patternvalueIndex = new ArrayList<Integer>();
						patternvalueIndex.add(getMultiWordPatternIndex(word));
						patternvalueIndex.add(getValueIndex(stockPatternEntity
								.getValues().get(patterns.indexOf(word))));
						patternvalueIndex
								.add(getValueForIndex(stockPatternEntity
										.getValuesFor().get(
												patterns.indexOf(word))));
						patternvalueIndex.trimToSize();
						patternValueList.add(0, null);
						patternValueList.add(patternvalueIndex);
					}

					patternValueList.trimToSize();
					unigrams.add(unigram);
					ugramValuePointer
							.add(unigrams.size() - 1, patternValueList);
				}
			}

			patterns.remove(0);
			stockPatternEntity.getValues().remove(0);
			stockPatternEntity.getValuesFor().remove(0);


		}
		LOG.info("unigrams ---- " + unigrams);
		LOG.info("multigrams ---- " + multiwordPatterns);
		LOG.info("ugramValuePointer ---" + ugramValuePointer);
		LOG.info("values----" + values);
		LOG.info("valuesFor----" + valuesFor);

	}

	private static int getMultiWordPatternIndex(String word) {
		int unigramPatternindex = 0;
		if (multiwordPatterns.contains(word)) {
			unigramPatternindex = multiwordPatterns.indexOf(word);
		} else {
			multiwordPatterns.add(word);
			unigramPatternindex = multiwordPatterns.size() - 1;
		}
		return unigramPatternindex;
	}

	private static int getValueIndex(String word) {
		int valueIndex = -1;
		if (values.contains(word)) {
			valueIndex = values.indexOf(word);
		} else {
			values.add(word);
			valueIndex = values.size() - 1;
		}
		return valueIndex;
	}

	private static int getValueForIndex(String word) {
		int valueIndex = -1;
		if (valuesFor.contains(word)) {
			valueIndex = valuesFor.indexOf(word);
		} else {
			valuesFor.add(word);
			valueIndex = valuesFor.size() - 1;
		}
		return valueIndex;
	}

	public static void main(String[] s) {

		stockPatternsGenerator();
	}

}
