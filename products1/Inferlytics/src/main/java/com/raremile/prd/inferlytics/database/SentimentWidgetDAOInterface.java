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

import com.raremile.prd.inferlytics.database.entity.EntityDimension;
import com.raremile.prd.inferlytics.entity.ProdCommentAnalticInfo;



/**
 * @author pratyusha
 * @created 26-Jul-2013
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public interface SentimentWidgetDAOInterface {

	 List<EntityDimension> getFeaturesByEntitySubProduct(String entity,
			String subProduct);

	 List<EntityDimension> getKeywordsByEntitySubProduct(String entity,
			String subProduct);
	
	void insertProdCommentAnalytics(ProdCommentAnalticInfo productCommentInfo);

}
