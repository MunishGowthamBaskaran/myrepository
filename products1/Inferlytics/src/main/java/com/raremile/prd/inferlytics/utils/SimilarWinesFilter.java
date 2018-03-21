/**
 *  * Copyright (c) 2014 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.utils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.raremile.prd.inferlytics.database.MongoUpdater;

/**
 * @author mallikarjuna
 * @created 07-May-2014
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class SimilarWinesFilter {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(SimilarWinesFilter.class);

	public static void main(String[] args){
		try {
			MongoUpdater.updateProductsWithSimilarWinesData(1000);
		} catch (UnknownHostException e) {

			LOG.error(
					"UnknownHostException while performing operation in main",
					e);
		}
	}

	public static List<String> sortProductIds(
			Map<String, ArrayList<String>> dimensionList) {
		List<String> productIdList = new ArrayList<>();

		if (dimensionList.get("pairing suggestions") != null) {
			productIdList.addAll(dimensionList.get("pairing suggestions"));
		}
		if (dimensionList.get("wine by body") != null) {
			List<String> bodyList = dimensionList.get("wine by body");
			bodyList.removeAll(productIdList);
			productIdList.addAll(bodyList);
		}
		if (dimensionList.get("wine by style") != null) {
			List<String> bodyList = dimensionList.get("wine by style");
			bodyList.removeAll(productIdList);
			productIdList.addAll(bodyList);
		}
		if (dimensionList.get("wines by aromas") != null) {
			List<String> bodyList = dimensionList.get("wines by aromas");
			bodyList.removeAll(productIdList);
			productIdList.addAll(bodyList);
		}
		if (dimensionList.get("wine by finish") != null) {
			List<String> bodyList = dimensionList.get("wines by aromas");
			bodyList.removeAll(productIdList);
			productIdList.addAll(bodyList);
		}
		if (productIdList.size() > 10) {
			productIdList = productIdList.subList(0, 10);
		}
		return productIdList;
	}

}
