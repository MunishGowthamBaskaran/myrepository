/**
 *  * Copyright (c) 2014 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.raremile.prd.inferlytics.database.MongoConnector;
import com.raremile.prd.inferlytics.database.MongoConnectorForService;
import com.raremile.prd.inferlytics.entity.IdMap;
import com.raremile.prd.inferlytics.entity.Post;

/**
 * @author mallikarjuna
 * @created 04-Aug-2014
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
@Path("/GetData")
public class GetProductsData {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(GetProductsData.class);

	@POST
	@Path("/getTraits")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllTraits(@FormParam("entity") String entity,
			@FormParam("subProduct") String subProduct) {

		String responseJson = MongoConnectorForService.getAllTraits(
				IdMap.getEntityId(entity), IdMap.getSubproductId(subProduct));




		return responseJson;
	}

	@POST
	@Path("/products")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String getProductsInformation(
			@FormParam("productName") String productName,
			@FormParam("pageNo") String pagenum,
			@FormParam("productCategory") String prodCat) {
		int pageNo = Integer.valueOf(pagenum);
		String returnJson = MongoConnectorForService
				.getProductDetails(
						productName, pageNo, prodCat);
		return returnJson;
	}

	@POST
	@Path("/productReviews")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String getReviewsForProduct(@FormParam("entity") String entity,
			@FormParam("subProduct") String subProduct,
			@FormParam("productId") String productId) {

		List<Post> posts = MongoConnectorForService.getPostsForProduct(
				productId, IdMap.getEntityId(entity),
				IdMap.getSubproductId(subProduct));
		MongoConnector.getFeedsByIds(posts);

		return new Gson().toJson(posts);
	}

	@POST
	@Path("/traitReviews")
	@Produces(MediaType.APPLICATION_JSON)
	public String getReviewsForTraits(@FormParam("entity") String entity,
			@FormParam("subProduct") String subProduct,
			@FormParam("productId") String productId,
			@FormParam("trait") String trait) {

		List<Post> posts = MongoConnectorForService.getPostIdsForTrait(
				IdMap.getEntityId(entity), IdMap.getSubproductId(subProduct),
				trait, productId);

		MongoConnectorForService.getFeedsByIds(posts);


		return new Gson().toJson(posts);
	}

}
