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

import java.util.Map;
import java.util.Set;

/**
 * @author pratyusha
 * @created 08-Jul-2013
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public interface LexiconDAOGenerationInterface {

	void storeLexicon(Map<String, Double> lexicons);

	void storeSubDimensionWord(Map<String, Set<String>> subDimWordMap,
			Map<String, Set<String>> subDimSynonymMap, int productId,
			int subProductId, boolean isProductSynonym);

}
