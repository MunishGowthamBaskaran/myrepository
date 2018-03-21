/**
 *  * Copyright (c) 2013 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.sentiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.entity.OtherFactors;
import com.raremile.prd.inferlytics.entity.StockPatterns;
import com.raremile.prd.inferlytics.preprocessing.TextNormalizer;
import com.raremile.prd.inferlytics.preprocessing.Tokenizer;



/**
 * @author Pratyusha
 * @created May 24, 2013
 * 
 *          This class is supposed to identify stock and its corresponding
 *          result "BUY/SELL/HOLD"
 * 
 */
public class AnalyseStocks {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(AnalyseStocks.class);

	/**
	 * @param content
	 * @return stock,corresponding result as map
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Map<String, String> analyseStock(String content)
			throws IOException, ClassNotFoundException {
		Map<String, String> result = new HashMap<String, String>();
		LinkedList<String> sentences = Tokenizer.getSentences(content);
		for (String sentenceContent : sentences) {
			StringBuffer newSentenceContent = new StringBuffer("");

			StringTokenizer st = new StringTokenizer(sentenceContent);
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (token.startsWith("$")) {

					if (!Character.isDigit(token.charAt(1))) {
						newSentenceContent.append(token.replace("$", "AHAH"))
								.append(" ");
					}
				} else {
					newSentenceContent.append(token).append(" ");
				}
			}
			LinkedList<String> tokens = Tokenizer.getTokens(
newSentenceContent
					.toString().trim(),
					null);

			LinkedList<String> tokensToBeTagged = TextNormalizer
					.removeStopWord(OtherFactors.getStopwords(), tokens);
			result.putAll(findResult(tokensToBeTagged));

		}
		for (Entry<String, String> entry : result.entrySet()) {
			System.out.println(entry.getKey() + "=" + entry.getValue());

		}
		return result;

	}

	private static Map<String, String> findResult(
			LinkedList<String> tokens) {
		// int tokenCounter = 0;
		Map<String, String> result = new HashMap<String, String>();

		for (int tokenCounter = 0; tokenCounter < tokens.size(); tokenCounter++) {
			String token = tokens.get(tokenCounter).toLowerCase();
			String key = null;
			if (StockPatterns.getUnigrams().contains(token)) {
				int tokenIndex = StockPatterns.getUnigrams().indexOf(token);
				ArrayList<ArrayList<Integer>> tokenIndexList = StockPatterns
						.getUgramValuePointer().get(tokenIndex);
				if (tokenIndexList != null && tokenIndexList.size() > 1) {

					/*int indexCount = 0;
					for (ArrayList<Integer> arrayList : tokenIndexList) {
						indexCount++;
						if(indexCount == 0)
						{
						continue;
						}
						String word = StockPatterns.getMultiwordPatterns().get(
								arrayList.get(1));
						int wordSize = word.split("_").length;

					}*/
					for (int tokenIndexCounter = 0; tokenIndexCounter < tokenIndexList
							.size(); tokenIndexCounter++) {

						if (tokenIndexCounter != 0) {
							ArrayList<Integer> list = tokenIndexList
									.get(tokenIndexCounter);
							LinkedList<String> tempTokens = new LinkedList<String>();
							tempTokens.addAll(tokens);
							String multiword = StockPatterns
									.getMultiwordPatterns().get(list.get(0));
							List<String> multiwordAsList = Arrays.asList(multiword
									.split("_"));
							int unigramPosition = -1;

							if (multiwordAsList.contains(token)) {
								HashMap<Integer, String> questionMarkWord = null;
								int questionMarkIndexCount = 0;
								int wordIndex = 0;
								List<Integer> questionMarkIndex = null;
								for (String string : multiwordAsList) {
									if (string.equals("?")) {
										if (questionMarkIndex == null) {
											questionMarkWord = new HashMap<Integer, String>();
											questionMarkIndex = new ArrayList<Integer>();
										}

										questionMarkIndex.add(wordIndex);
										questionMarkIndexCount++;

									}
									wordIndex++;
								}
								/*
								 * else if (multiwordAsList.contains("1?")) {
								 * questionMarkIndex = multiwordAsList
								 * .indexOf("1?"); } else if
								 * (multiwordAsList.contains("2?")) {
								 * questionMarkIndex = multiwordAsList
								 * .indexOf("2?"); }
								 */
								unigramPosition = multiwordAsList
										.indexOf(token);
								int beforeCount = unigramPosition;
								int afterCount = multiwordAsList.size()
										- (unigramPosition + 1);
								int actualPosition = tokens.indexOf(token);
								int start = actualPosition - beforeCount;
								int end = actualPosition + afterCount;

								String multiTokens = "";
								if ((end < tempTokens.size()) && start >= 0) {
									int questionMarkWordCount = 0;
									for (int i = start; i <= end; i++) {

										// multiTokens += tempTokens.remove(start);//
										// because
										// as we are
										// removing tokens
										// index will remain
										// the same.

										if (questionMarkIndex != null
												&& questionMarkIndex.size() != 0
												&& i == start
														+ (questionMarkIndex
																.get(0))) {
											questionMarkWord.put(
													++questionMarkWordCount,
													tempTokens.get(0));
											multiTokens += "?";
											questionMarkIndex.remove(0);

											tempTokens.remove(start);

										} else {
											multiTokens += tempTokens.remove(start);
										}

										if (i != end) {
											multiTokens += "_";
										}

									}
								}

								if (multiTokens.equalsIgnoreCase(multiword)) {
									tempTokens.add(start, multiTokens);
									addResultToMap(result, list,
											questionMarkWord, tokens, start,
											questionMarkIndexCount);
									if (tokenIndexCounter + 1 < tokenIndexList
											.size()) {
									List<Integer> nextList = tokenIndexList
											.get(tokenIndexCounter + 1);
									if (nextList.get(0) == list.get(0)) {
										addResultToMap(result, nextList,
												questionMarkWord, tokens,
												start,
												questionMarkIndexCount);
									}
									}
									tokens.clear();
									tokens.addAll(tempTokens);
									break;
								}
							}
						}

					}

				}

				
					
				
				if (key == null && tokenIndexList.get(0) != null) {
					String action = StockPatterns.getValues().get(
							tokenIndexList.get(0).get(1));
					key = getImmediateRightStock(tokenCounter, tokens, false);
					if (null != key) {
						result.put(key, action);
					} else {
						key = getImmediateLeftStock(tokenCounter, tokens, false);
						if (null != key) {
							result.put(key, action);
						}
					}
					}

				}

			}

		return result;
		
	}

	private static List<String> getAllStocks(LinkedList<String> tokens) {
		List<String> stocks = new ArrayList<String>();
		for (String token : tokens) {
			if (token.startsWith("AHAH")) {
				stocks.add(token);
			}
		}
		return tokens;

	}

	private static String getImmediateLeftStock(int index,
			LinkedList<String> tokens, boolean onlyOne) {
		StringBuilder immediateLeftStock = null;
		for (int i = 0; i < index; i++) {
			if (tokens.get(i).startsWith("AHAH")) {
				if (null == immediateLeftStock) {
					immediateLeftStock = new StringBuilder();
				}
				immediateLeftStock.append(";").append(
						tokens.get(i).replace("AHAH", "$"));
				if (onlyOne) {
					break;
				}
			}
		}

		return (immediateLeftStock != null) ? immediateLeftStock.toString()
				: null;
	}

	private static String getImmediateRightStock(int index,
			LinkedList<String> tokens, boolean onlyOne) {
		StringBuilder immediateRightStock = null;
		int lastStockIndex = -1;
		for (int i = index; i < tokens.size(); i++) {
			if (tokens.get(i).startsWith("AHAH")) {
				if ((lastStockIndex != -1) && lastStockIndex != (i - 1)
						&& !tokens.get(i - 1).equalsIgnoreCase("and")) {


						continue;


				}
				if (null == immediateRightStock) {
					immediateRightStock = new StringBuilder();
				}
				immediateRightStock.append(";").append(
						tokens.get(i).replace("AHAH", "$"));

				if (onlyOne) {
					break;
				}
				lastStockIndex = i;
			}
		}

		return (immediateRightStock != null) ? immediateRightStock.toString()
				: null;
	}

	private static void addResultToMap(Map<String, String> result,
			List<Integer> list, HashMap<Integer, String> questionMarkWord,
			LinkedList<String> tokens, int start,
			int questionMarkIndexCount) {
		String key = null;
		String action = StockPatterns.getValues().get(list.get(1));
		String valuesFor = StockPatterns.getValuesFor().get(list.get(2));

		if (questionMarkIndexCount == 2 && null != valuesFor) {
			if (valuesFor.charAt(0) == '1') {
				key = questionMarkWord.get(1).replace("AHAH", "$");
			} else {
				key = questionMarkWord.get(2).replace("AHAH", "$");
			}
			if (null != key) {
				result.put(key, action);
			}
		} else {
			key = getImmediateRightStock(start, tokens, false);
			if (null != key) {
				result.put(key, action);
			} else {
				key = getImmediateLeftStock(start, tokens, false);
				if (null != key) {
					result.put(key, action);
				}
			}
		}
	}
	public static void main(String[] s) {
		try {
			OtherFactors
					.setStopwords(DAOFactory
							.getInstance(
									ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
							.getLexiconDAO().getStopword());
			StockPatterns.stockPatternsGenerator();
			analyseStock("Added to $SPY long. Very big GULP!");

		} catch (IOException e) {

			LOG.error("IOException while performing operation in main", e);
		} catch (ClassNotFoundException e) {

			LOG.error(
					"ClassNotFoundException while performing operation in main",
					e);
		}
	}

}
