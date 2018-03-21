/**
 *  * Copyright (c) 2014 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.entity;

/**
 * @author mallikarjuna
 * @created 28-Jul-2014
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class EntitySubProductMap {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(EntitySubProductMap.class);
	private String entity;
	private String subProduct;
	/**
	 * @return the subProduct
	 */
	public String getSubProduct() {
		return subProduct;
	}
	/**
	 * @param subProduct the subProduct to set
	 */
	public void setSubProduct(String subProduct) {
		this.subProduct = subProduct;
	}
	/**
	 * @return the entity
	 */
	public String getEntity() {
		return entity;
	}
	/**
	 * @param entity the entity to set
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

}
