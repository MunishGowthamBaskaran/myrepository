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


import java.util.List;
import java.util.Map;

/**
 * @author Pratyusha
 * @created Apr 24, 2013
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class QueryLevelCache {

	public static IdWordList idWordList;
	public static Map<String, List<String>> productBrandStopwordList;
	public static Map<String, Map<String, Product>> productDetailsMap;
	public static Map<String, String> productSynonyms;	
	private static Map<String, List<String>> wordSubDim;


	/**
	 * @return the getWordSubDim
	 */
	public static Map<String, List<String>> getWordSubDim() {
		return wordSubDim;
	}


	/**
	 * @param getWordSubDim the getWordSubDim to set
	 */
	public static void setWordSubDim(Map<String, List<String>> wordSubDim) {
		QueryLevelCache.wordSubDim = wordSubDim;
	}

	

}
