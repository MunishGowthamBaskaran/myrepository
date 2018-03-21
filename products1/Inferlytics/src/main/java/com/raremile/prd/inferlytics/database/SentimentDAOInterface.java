/**
 *  * Copyright (c) 2013 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.database;

import java.util.List;
import java.util.Map;

import com.raremile.prd.inferlytics.database.entity.EntityDimension;
import com.raremile.prd.inferlytics.entity.Features;
import com.raremile.prd.inferlytics.entity.Feed;
import com.raremile.prd.inferlytics.entity.Output;
import com.raremile.prd.inferlytics.entity.Serie;


/**
 * @author Pratyusha
 * @created Apr 8, 2013
 * 
 *          All DB Operations related to sentiment should come here.
 * 
 */
public interface SentimentDAOInterface {

	 List<Output> getMultipleData(String brand, String subproductName);

	 List<Serie> getPieChartData(String brand, String subproductName);
	
	 List<Features> getNegativeFeatures(String brand,
			String subproductName);
	
	 List<Features> getPositiveFeatures(String brand,
			String subproductName);

	 List<EntityDimension> getEntityDimension(String entity,
			String projectID);

	 void storePostsIntoStaging(List<Feed> feeds,
			String brandName, String productName, String sourceName);

	 Map<String, List<String>> getAllProductToRespEntity();

	 Map<String, String> getSynonymsBySubProductId(long subProductId);
	 
	 Map<String, String> getSynonyms();
	
	 Map<String,List<String>> getWordSubDimensionForSubProduct(String subProduct);
}
