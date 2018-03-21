/**
 * * Copyright (c) 2013 RareMile Technologies. All rights reserved. No part of this document may be
 * reproduced or transmitted in any form or by any means, electronic or mechanical, whether now
 * known or later invented, for any purpose without the prior and express written consent.
 */
package com.raremile.prd.inferlytics.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.commons.FilePropertyManager;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.database.MongoConnector;
import com.raremile.prd.inferlytics.database.entity.EntityDimension;
import com.raremile.prd.inferlytics.entity.CommentsHtml;
import com.raremile.prd.inferlytics.entity.DetailedSentiment;
import com.raremile.prd.inferlytics.entity.FeatureWords;
import com.raremile.prd.inferlytics.entity.IdMap;
import com.raremile.prd.inferlytics.entity.Post;
import com.raremile.prd.inferlytics.entity.Product;
import com.raremile.prd.inferlytics.entity.SunBurstData;
import com.raremile.prd.inferlytics.entity.WidgetFeatureEntity;

/**
 * @author Pratyusha
 * @created Apr 25, 2013 TODO: Write a quick description of what the class is
 *          supposed to do.
 */
public class BusinessUtil {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(BusinessUtil.class);

	public static final Comparator<WidgetFeatureEntity> WFE_ORDER = new Comparator<WidgetFeatureEntity>() {

		@Override
		public int compare(WidgetFeatureEntity o1, WidgetFeatureEntity o2) {
			return (o1.getCount() > o2.getCount() ? -1 : (o1.getCount() == o2
					.getCount() ? 0 : 1));
		}
	};

	public static final Comparator<SunBurstData> FEATURE_ORDER = new Comparator<SunBurstData>() {

		@Override
		public int compare(SunBurstData o1, SunBurstData o2) {
			return (o1.getSize() > o2.getSize() ? -1 : (o1.getSize() == o2
					.getSize() ? 0 : 1));
		}
	};

	public static void addSentimentToList(List<SunBurstData> list,
			int sentiment, int count, String postId, String word) {

		SunBurstData obj = null;
		List<String> postIds = null;
		List<String> words = null;
		int index = -1;
		String name = DetailedSentiment.getSentimentStringFromInt(sentiment);
		String colour = DetailedSentiment.getSentimentColorFromInt(sentiment);
		if (list == null) {
			list = new ArrayList<SunBurstData>();
		} else {

			for (SunBurstData sunBurstData : list) {
				if (sunBurstData.getNameWithOutcount().equalsIgnoreCase(name)) {
					sunBurstData.setSize(count + sunBurstData.getSize());
					index = list.indexOf(sunBurstData);
					postIds = sunBurstData.getPostIds();
					words = sunBurstData.getWords();
					if ((postIds.size() != 6)) {
						if (!postIds.contains(postId)) {
							postIds.add(postId);
						}
						if (!words.contains(word)) {
							words.add(word);
						}
						sunBurstData.setPostIds(postIds);
						sunBurstData.setWords(words);
					}
					obj = sunBurstData;
					obj.setName(name + "(" + obj.getSize() + ")");
					break;

				}
			}
		}
		if (null == obj) {
			obj = new SunBurstData();
			obj.setName(name + "(" + count + ")");
			obj.setNameWithOutcount(name);
			obj.setSize(count);
			obj.setColor(colour);
			postIds = new ArrayList<String>();
			postIds.add(postId);
			words = new ArrayList<String>();
			words.add(word);
			obj.setPostIds(postIds);
			obj.setWords(words);
			// obj.setName(obj.getNameWithOutcount() + "(" + obj.getCount() +
			// ")");

		}
		if (index != -1) {
			list.remove(index);
		}
		list.add(obj);

	}

	public static void checkAndSetSubDimToDim(SunBurstData sunBurstData) {
		List<SunBurstData> dimList = new ArrayList<SunBurstData>();
		dimList.addAll(sunBurstData.getChildren());
		for (SunBurstData dim : dimList) {
			if (dim.getChildren() != null && dim.getChildren().size() == 1) {
				if (dim.getChildren().get(0).getName()
						.equalsIgnoreCase(dim.getName())) {
					sunBurstData.getChildren().remove(dim);
					sunBurstData.getChildren().add(dim.getChildren().get(0));
				}
			}
		}

	}

	public static JsonElement genarateFeatureJsonForEntitySubProduct(
			int entityId, int subProductId, String category, Integer minPrice,
			Integer maxPrice) {
		LOG.trace("Entered genarateFeatureJsonForEntitySubProduct ");
		List<SunBurstData> featureList = null;

		List<EntityDimension> entityDimList = MongoConnector
				.getFeaturesForEntitySubproducts(entityId, subProductId,
						category, minPrice, maxPrice);

		SunBurstData subDim = null;
		SunBurstData word = null;
		List<SunBurstData> WordList = null;

		int productCount = 0;

		int posWordCounter = 0;
		int negWordCounter = 0;

		List<String> featureArray = new ArrayList<>();
		if (null != entityDimList) {
			subDim = new SunBurstData();
			featureList = new ArrayList<>();
		}
		int listSize = entityDimList.size();
		EntityDimension entityDimension = null;
		boolean lastOne = false;
		for (int counter = 0; counter <= listSize; counter++) {
			if (counter == listSize) {
				lastOne = true;
			} else {
				entityDimension = entityDimList.get(counter);
			}

			// First time
			if (subDim.getName() == null) {
				subDim.setName(entityDimension.getSubDimension());
				featureArray.add(entityDimension.getSubDimension());

				word = new SunBurstData();
				word.setName(entityDimension.getWord());
				WordList = new ArrayList<>();
			}// check if subdimension is same as before
			else if ((!subDim.getName().equals(
					entityDimension.getSubDimension()))
					|| lastOne) {
				int wfeIndex = -1;
				if (featureArray.contains(subDim.getName())) {
					wfeIndex = featureArray.indexOf(subDim.getName());
				}
				if (wfeIndex != -1 && featureList.size() > wfeIndex) {
					SunBurstData subDimExisting = featureList.get(featureArray
							.indexOf(subDim.getName()));

					/**
					 * Get All childs, loop through them, and in turn loop
					 * through word list and set negative, total counts
					 */
					List<SunBurstData> existingChilds = subDimExisting
							.getChildren();
					int posChildCount = existingChilds.size();
					int negChildCount = WordList.size();

					for (int negCounter = 0; negCounter < negChildCount; negCounter++) {
						boolean childFound = false;
						inner: for (int posCounter = 0; posCounter < posChildCount; posCounter++) {
							if (existingChilds.get(posCounter).getName()
									.equals(WordList.get(negCounter).getName())) {
								existingChilds.get(posCounter).setNegCount(
										WordList.get(negCounter).getNegCount());
								existingChilds.get(posCounter).setSize(0);
								// existingChilds.get(posCounter).setSize(entityDimension.getTotalCount());
								childFound = true;
								break inner;
							} else {// remove it later if u want productcount
								existingChilds.get(posCounter).setSize(0);
							}

						}
						if (!childFound) {
							WordList.get(negCounter).setSize(0);
							existingChilds.add(WordList.get(negCounter));
							subDimExisting.setSize(WordList.get(negCounter)
									.getSize());
							subDimExisting.setSize(0);
						}
						// existingChilds.get(posCounter).setSize(0);
					}

					subDimExisting.setNegCount(negWordCounter);
					// subDimExisting.setSize(productCount);
					subDimExisting.setSize(0);
					featureList.set(wfeIndex, subDimExisting);
					if (lastOne) {
						break;
					}
				} else {
					subDim.setChildren(WordList);
					subDim.setPosCount(posWordCounter);
					subDim.setSize(productCount);
					subDim.setSize(0);
					featureList.add(featureArray.size() - 1, subDim);
				}
				WordList = new ArrayList<>();
				word = new SunBurstData();
				word.setName(entityDimension.getWord());
				posWordCounter = 0;
				negWordCounter = 0;
				productCount = 0;

				subDim = new SunBurstData();
				subDim.setName(entityDimension.getSubDimension());

				if (!featureArray.contains(entityDimension.getSubDimension())) {
					featureArray.add(entityDimension.getSubDimension());
				}

			}

			// Handle words here.
			if (!word.getName().equals(entityDimension.getWord())) {

				word = new SunBurstData();
				word.setName(entityDimension.getWord());

			}
			productCount += entityDimension.getTotalCount();
			word.setSize(entityDimension.getTotalCount());
			word.setSize(0);
			if (entityDimension.isSenti()) {
				posWordCounter += entityDimension.getCount();
				word.setPosCount(entityDimension.getCount());
			} else {
				negWordCounter += entityDimension.getCount();
				word.setNegCount(entityDimension.getCount());
			}
			WordList.add(word);
		}
		for (int counter = 0; counter < featureList.size(); counter++) {
			SunBurstData feature = featureList.get(counter);
			feature.setSize(0);
			Collections.sort(feature.getChildren(), FEATURE_ORDER);
		}

		Collections.sort(featureList, FEATURE_ORDER);
		return new Gson().toJsonTree(featureList);
	}

	public static List<CommentsHtml> getCommentsHtmlFromFeeds(
			Map<String, String> feeds, int posCount, int negCount,
			String callFrom) {

		/**
		 * Generate a structure like this here. <div
		 * class="inf-pw-review_panel"> <div class="review positivereview">
		 * <span><label class="user">User abc </label><label
		 * class="icon_label"></label></span> <div
		 * class="inf-pw-review_comment expand">
		 * <p>
		 * sdf odsj dsdfs h odsfds <mark>shoe</mark> hdsf jh odsf sadasd sa sd
		 * posd hsd sdfhdsf h
		 * </p>
		 * </div> </div> </div>
		 */

		int localPosCount = 0;
		int localNegCount = 0;
		List<CommentsHtml> all = new ArrayList<>(feeds.size());
		for (Entry<String, String> reviewString : feeds.entrySet()) {

			CommentsHtml review_comment = new CommentsHtml();
			review_comment.setTagName("div");
			review_comment.setClassName("inf-" + callFrom
					+ "-review_comment expand");

			CommentsHtml p = new CommentsHtml();
			// p.setTextContent( Util.appendEmTag(reviewString) );
			p.setTagName("p");
			p.setChildNodes(Util.appendEmTag(reviewString.getValue()));

			review_comment.addChildNode(p);

			CommentsHtml review = new CommentsHtml();
			review.setTagName("div");

			String reviewclass = "inf-" + callFrom + "-review";
			if (localPosCount < posCount) {
				reviewclass += " positivereview";
				localPosCount++;
			} else if (localNegCount < negCount) {
				reviewclass += " negativereview";
				localNegCount++;
			}
			// review.setOnclick("reviewClick(this)");
			review.setClassName(reviewclass);

			review.addChildNode(review_comment);
			review.setId(reviewString.getKey());
			CommentsHtml reviewPanel = new CommentsHtml();

			reviewPanel.setClassName("inf-pw-review_panel");
			reviewPanel.setTagName("div");
			reviewPanel.addChildNode(review);

			all.add(reviewPanel);

		}

		return all;
	}

	public static List<CommentsHtml> getCommentsHtmlFromPosts(List<Post> posts,
			String word, String subDimension, String callFrom) {

		/**
		 * Generate a structure like this here. <div
		 * class="inf-pw-review_panel"> <div> class="review positivereview">
		 * <span><label class="user">User abc </label><label
		 * class="icon_label"></label></span> <div
		 * class="review_comment expand">
		 * <p>
		 * sdf odsj dsdfs h odsfds <mark>shoe</mark> hdsf jh odsf sadasd sa sd
		 * posd hsd sdfhdsf h
		 * </p>
		 * </div> </div> </div>
		 */

		/*
		 * <div><img class="productImage" /><div class="productTitle"
		 * title="Name">Name</div></div>
		 */

		List<CommentsHtml> all = new ArrayList<>(posts.size());
		for (Post post : posts) {

			Product product = post.getProduct();

			CommentsHtml review_comment = new CommentsHtml();
			review_comment.setTagName("div");
			review_comment.setClassName("inf" + callFrom
					+ "review_comment expand");

			if (null != product) {
				CommentsHtml prodDiv = new CommentsHtml();
				prodDiv.setTagName("div");

				String method2 = "addProductDetailEvent('" + word + "','"
						+ product.getProductId() + "','" + subDimension + "');";
				CommentsHtml prodImg = new CommentsHtml();
				prodImg.setTagName("img");
				prodImg.setClassName("productImage");
				prodImg.setSrc(product.getImageUrl());
				CommentsHtml a1 = new CommentsHtml();
				a1.setHref(product.getProductUrl());
				a1.setTagName("a");
				a1.setClick(method2);
				a1.addChildNode(prodImg);
				prodDiv.addChildNode(a1);

				CommentsHtml prodTitle = new CommentsHtml();
				prodTitle.setTagName("div");
				prodTitle.setClassName("productTitle");
				prodTitle.setTextContent(product.getProductName());
				prodTitle.setTitle(product.getProductName());
				CommentsHtml a2 = new CommentsHtml();
				a2.setHref(product.getProductUrl());
				a2.setTagName("a");
				a2.addChildNode(prodTitle);
				a2.setClick(method2);
				prodDiv.addChildNode(a2);

				review_comment.addChildNode(prodDiv);
			}

			CommentsHtml p = new CommentsHtml();
			// p.setTextContent( Util.appendEmTag(reviewString) );
			p.setTagName("p");
			p.setChildNodes(Util.appendEmTag(post.getContent()));

			review_comment.addChildNode(p);

			CommentsHtml review = new CommentsHtml();
			review.setsubDimension(subDimension);
			review.setId(post.getPostId());
			review.setTagName("div");
			review.setWord(post.getWord());
			if (post.getSentenceNo() != 0) {
				review.setScNo("" + (post.getSentenceNo() - 1));
			} else {
				review.setScNo("-1");
			}
			String reviewclass = "inf" + callFrom + "review";
			if (post.isPositive()) {
				reviewclass += " positivereview";

			} else {
				reviewclass += " negativereview";

			}
			// review.setOnclick("reviewClick(this)");
			review.setClassName(reviewclass);

			// add productinfo here
			/*
			 * if(null != product){
			 * review.addChildNode(getCommentsHtmlForProduct
			 * (product,word,post.getPostId())); }
			 */
			review.addChildNode(review_comment);

			CommentsHtml reviewPanel = new CommentsHtml();
			reviewPanel.setClassName("inf" + callFrom + "review_panel");
			reviewPanel.setTagName("div");
			reviewPanel.addChildNode(review);

			all.add(reviewPanel);

		}

		return all;

	}

	public static List<CommentsHtml> getCommentsHtmlFromProdIds(
			List<Product> productDetails, String brand, String word,
			String subDimension, String subProduct) {

		List<CommentsHtml> all = new ArrayList<>(productDetails.size());

		for (Product product : productDetails) {
			if (product.getProductName() == null) {
				continue;
			}
			CommentsHtml review_commentstart = new CommentsHtml();
			review_commentstart.setTagName("div");
			review_commentstart
			.setClassName("inf-bw-prod_review_p inf-bw-prod_block");

			CommentsHtml review_comment = new CommentsHtml();
			review_comment.setTagName("div");
			review_comment.setClassName("inf-bw-prod_review_comment expand");

			String method = "basicWidget.featuresWidget.showCommentsForProd(\""
					+ product.getProductId() + "\",\"" + word + "\"," + "0,2,"
					+ product.getPosCommentsCount() + ",'"
					+ product.getProductName().replace("'", "&apos;") + "',\""
					+ subDimension + "\",this);";

			String method3 = "basicWidget.featuresWidget.showCommentsForProd(\""
					+ product.getProductId()
					+ "\",\""
					+ word
					+ "\","
					+ "0,3,"
					+ product.getNegCommentsCount()
					+ ",'"
					+ product.getProductName().replace("'", "&apos;")
					+ "',\""
					+ subDimension + "\",this);";
			String method2 = "basicWidget.featuresWidget.addProductDetailEvent('"
					+ word
					+ "','"
					+ product.getProductId()
					+ "','"
					+ subDimension + "');";
			CommentsHtml imgDiv = new CommentsHtml();
			String productUrl = null;
			if (brand.equals("moltonbrown")) {
				productUrl = FilePropertyManager.getProperty(
						ApplicationConstants.APPLICATION_PROPERTIES_FILE,
						ApplicationConstants.PRODUCTION_DOMAIN)
						+ "/Inferlytics/products_fragrance/"
						+ product.getProductId();
			} else if (brand.equals("nike")) {
				productUrl = FilePropertyManager.getProperty(
						ApplicationConstants.APPLICATION_PROPERTIES_FILE,
						ApplicationConstants.PRODUCTION_DOMAIN)
						+ "/Inferlytics/products_menshoes/"
						+ product.getProductId();
			} else if (brand.equals("macys") && subProduct.equals("womencoats")) {
				productUrl = FilePropertyManager.getProperty(
						ApplicationConstants.APPLICATION_PROPERTIES_FILE,
						ApplicationConstants.PRODUCTION_DOMAIN)
						+ "/Inferlytics/products_womencoats/"
						+ product.getProductId();
			} else if (brand.equals("guitarcenter")
					&& subProduct.equals("amplifiers")) {
				productUrl = FilePropertyManager.getProperty(
						ApplicationConstants.APPLICATION_PROPERTIES_FILE,
						ApplicationConstants.PRODUCTION_DOMAIN)
						+ "/Inferlytics/products_amplifiers/"
						+ product.getProductId();
			} else if (brand.equals("guitarcenter")
					&& subProduct.equals("effects")) {
				productUrl = FilePropertyManager.getProperty(
						ApplicationConstants.APPLICATION_PROPERTIES_FILE,
						ApplicationConstants.PRODUCTION_DOMAIN)
						+ "/Inferlytics/products_effects/"
						+ product.getProductId();
			} else if (brand.equals("klwines") && subProduct.equals("winesnew")) {
				productUrl = FilePropertyManager.getProperty(
						ApplicationConstants.APPLICATION_PROPERTIES_FILE,
						ApplicationConstants.PRODUCTION_DOMAIN)
						+ "/Inferlytics/products_wines/"
						+ product.getProductId();
			} else if (brand.equals("tescowines")
					&& (subProduct.equals("tescowines") || subProduct
							.equals("tescowinesnew"))) {
				productUrl = FilePropertyManager.getProperty(
						ApplicationConstants.APPLICATION_PROPERTIES_FILE,
						ApplicationConstants.PRODUCTION_DOMAIN)
						+ "/Inferlytics/products_tescowines/"
						+ product.getProductId();
			} else if (brand.equals("oldnavy")
					&& subProduct.equals("womendress")) {
				productUrl = FilePropertyManager.getProperty(
						ApplicationConstants.APPLICATION_PROPERTIES_FILE,
						ApplicationConstants.PRODUCTION_DOMAIN)
						+ "/Inferlytics/products_oldnavy/"
						+ product.getProductId();
			}
			imgDiv.setTagName("div");
			CommentsHtml imgP = new CommentsHtml();
			imgP.setTagName("p");
			if (brand.equals("macys")) {
				imgP.setClassName("inf-bw-prod_atg_store_productImage_macys");
			} else if (brand.equals("klwines")) {
				imgP.setClassName("inf-bw-prod_atg_store_productImage_klwines");
			} else if (brand.equals("tescowines")) {
				imgP.setClassName("inf-bw-prod_atg_store_productImage_tescowines");
			} else {
				imgP.setClassName("inf-bw-prod_atg_store_productImage");
			}
			CommentsHtml imgA = new CommentsHtml();
			imgA.setTagName("a");
			if (null != productUrl) {
				imgA.setHref(productUrl);
			}
			// imgA.setHref(product.getProductUrl());
			imgA.setClick(method2);

			CommentsHtml imgTag = new CommentsHtml();
			imgTag.setSrc(product.getImageUrl());
			imgTag.setTagName("img");
			imgA.addChildNode(imgTag);
			imgP.addChildNode(imgA);
			imgDiv.addChildNode(imgP);
			review_comment.addChildNode(imgDiv);

			CommentsHtml secondDiv = new CommentsHtml();
			secondDiv.setTagName("div");

			CommentsHtml prodNameDiv = new CommentsHtml();
			prodNameDiv.setTagName("div");
			prodNameDiv.setClassName("inf-bw-prod_productName");

			CommentsHtml prodNameA = new CommentsHtml();
			prodNameA.setTagName("a");
			prodNameA.setClassName("inf-bw-prod_viewProduct");

			if (null != productUrl) {
				prodNameA.setHref(productUrl);
			}
			prodNameA.setTitle(product.getProductName());
			prodNameA.setTextContent(product.getProductName());
			prodNameA.setClick(method2);
			prodNameDiv.addChildNode(prodNameA);
			secondDiv.addChildNode(prodNameDiv);

			CommentsHtml em = new CommentsHtml();
			em.setTagName("em");
			if (brand.equals("klwines") || subProduct.equals("tescowinesnew")) {
				CommentsHtml posCount = new CommentsHtml();
				posCount.setClassName("inf-bw-prod_Commentsklwines");
				posCount.setTagName("div");
				if (product.getPosCommentsCount() > 0) {
					posCount.setClick(method);
				}
				posCount.setTextContent("Tasting Notes");
				em.addChildNode(posCount);
			} else {
				CommentsHtml posCount = new CommentsHtml();
				posCount.setClassName("inf-bw-prod_positiveComments");
				posCount.setTagName("div");
				if (product.getPosCommentsCount() > 0) {
					posCount.setClick(method);
				}
				posCount.setTextContent(Integer.toString(product
						.getPosCommentsCount()));
				CommentsHtml negCount = new CommentsHtml();
				negCount.setClassName("inf-bw-prod_negativeComments");
				negCount.setTagName("div");
				if (product.getNegCommentsCount() > 0) {
					negCount.setClick(method3);
				}
				negCount.setTextContent(Integer.toString(product
						.getNegCommentsCount()));

				em.addChildNode(posCount);
				em.addChildNode(negCount);
			}
			secondDiv.addChildNode(em);

			review_comment.addChildNode(secondDiv);
			review_commentstart.addChildNode(review_comment);
			all.add(review_commentstart);

		}

		return all;
	}

	public static String getCurrencyPriceByBrand(String brand, String subProduct) {
		String currency = "";
		Integer maxPrice = null;
		if (brand.equals("moltonbrown")) {
			maxPrice = 200;
			currency = "&pound";
		} else if (brand.equals("nike")) {
			maxPrice = 300;
			currency = "$";
		} else if (brand.equals("macys") && subProduct.equals("womencoats")) {
			maxPrice = 670;
			currency = "$";
		} else if (brand.equals("guitarcenter")
				&& subProduct.equals("amplifiers")) {
			maxPrice = 5000;
			currency = "$";
		} else if (brand.equals("guitarcenter") && subProduct.equals("effects")) {
			maxPrice = 2500;
			currency = "$";
		} else if (brand.equals("klwines") && subProduct.equals("winesnew")) {
			maxPrice = 7000;
			currency = "$";
		} else if (brand.equals("tescowines")
				&& subProduct.equals("tescowines")) {
			maxPrice = 7000;
			currency = "&pound;";
		} else if (brand.equals("oldnavy") && subProduct.equals("womendress")) {
			maxPrice = 40;
			currency = "$";
		} else {
			maxPrice = null;
		}
		return currency + ":" + maxPrice;
	}

	public static List<SunBurstData> getDummySunBurstData() {
		List<SunBurstData> sunBurstDataListDimensions = new ArrayList<SunBurstData>();

		List<SunBurstData> sunBurstDataListSentiment = new ArrayList<SunBurstData>();
		SunBurstData sentiment = new SunBurstData();
		sentiment.setName("Positive(10)");
		sunBurstDataListSentiment.add(sentiment);
		sentiment = new SunBurstData();
		sentiment.setName("Negative(10)");
		sunBurstDataListSentiment.add(sentiment);
		sentiment = new SunBurstData();
		sentiment.setName("Strong Positive(10)");
		sunBurstDataListSentiment.add(sentiment);
		sentiment = new SunBurstData();
		sentiment.setName("Strong Negative(10)");
		sunBurstDataListSentiment.add(sentiment);
		List<SunBurstData> sunBurstDataListSentimentWord = new ArrayList<SunBurstData>();
		SunBurstData word = new SunBurstData();
		word.setName("cushion");
		word.setChildren(sunBurstDataListSentiment);
		sunBurstDataListSentimentWord.add(word);
		word = new SunBurstData();
		word.setName("light");
		word.setChildren(sunBurstDataListSentiment);
		sunBurstDataListSentimentWord.add(word);

		List<SunBurstData> sunBurstDataListSentimentWord2 = new ArrayList<SunBurstData>();
		SunBurstData word2 = new SunBurstData();
		word2.setName("fresh");
		word2.setChildren(sunBurstDataListSentiment);
		sunBurstDataListSentimentWord.add(word2);
		word2 = new SunBurstData();
		word2.setName("smooth");
		word2.setChildren(sunBurstDataListSentiment);
		sunBurstDataListSentimentWord2.add(word2);

		List<SunBurstData> sunBurstDataListSentimentsubDim = new ArrayList<SunBurstData>();
		SunBurstData subDim = new SunBurstData();
		subDim.setName("comfortable");
		subDim.setChildren(sunBurstDataListSentimentWord);
		sunBurstDataListSentimentsubDim.add(subDim);
		subDim = new SunBurstData();
		subDim.setName("Feel");
		subDim.setChildren(sunBurstDataListSentimentWord2);
		sunBurstDataListSentimentsubDim.add(subDim);

		SunBurstData dim = new SunBurstData();
		dim.setName("design");
		dim.setChildren(sunBurstDataListSentimentsubDim);
		sunBurstDataListDimensions.add(dim);

		return sunBurstDataListDimensions;
	}

	public static JsonElement getFeatureJsonFromFeatureLists(
			List<SunBurstData> posFeatureList, List<SunBurstData> negFeatureList) {
		LOG.trace("Method: getFeatureJsonFromFeatureLists called.");
		List<SunBurstData> featureList = new ArrayList<>();
		for (SunBurstData posSunBurstData : posFeatureList) {
			SunBurstData subDim = posSunBurstData;
			negSubDim: for (SunBurstData negSunBurstData : negFeatureList) {
				if (subDim.getName().equals(negSunBurstData.getName())) {
					List<SunBurstData> posChilds = subDim.getChildren();
					List<SunBurstData> negChilds = negSunBurstData
							.getChildren();
					for (SunBurstData possunBurstDataChild : posChilds) {
						negSubDimChild: for (SunBurstData negsunBurstDataChild : negChilds) {
							if (possunBurstDataChild.getName().equals(
									negsunBurstDataChild.getName())) {
								possunBurstDataChild
								.setSize(possunBurstDataChild.getSize()
										+ negsunBurstDataChild
										.getSize());
								break negSubDimChild;
							}
						}
					}
					subDim.setSize(subDim.getSize() + negSunBurstData.getSize());
					break negSubDim;
				}
			}
			featureList.add(subDim);
		}
		Collections.sort(featureList, FEATURE_ORDER);
		LOG.trace("Method: getFeatureJsonFromFeatureLists finished.");
		LOG.info(new Gson().toJsonTree(featureList));
		return new Gson().toJsonTree(featureList);
	}

	public static List<WidgetFeatureEntity> getFeaturesByEntitySubProduct(
			String entity, String subProduct) {
		List<WidgetFeatureEntity> wfeList = new ArrayList<WidgetFeatureEntity>();

		List<EntityDimension> entityDimList = DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getSentimentWidgetDAO()
				.getFeaturesByEntitySubProduct(entity, subProduct);

		WidgetFeatureEntity wfe = new WidgetFeatureEntity();
		int posPostIdCounter = 0;
		int negPostIdCounter = 0;

		List<String> featureArray = new ArrayList<>();
		for (EntityDimension entityDimension : entityDimList) {
			// check if subdimension is same as before
			if (wfe.getName() == null) {
				wfe.setName(entityDimension.getSubDimension());
				featureArray.add(entityDimension.getSubDimension());
			} else if (!wfe.getName().equals(entityDimension.getSubDimension())) {
				int wfeIndex = -1;
				if (featureArray.contains(wfe.getName())) {
					wfeIndex = featureArray.indexOf(wfe.getName());
				}
				if (wfeIndex != -1 && wfeList.size() > wfeIndex) {
					WidgetFeatureEntity wfeExisting = wfeList.get(featureArray
							.indexOf(wfe.getName()));

					wfeExisting.getWords().addAll(wfe.getWords());
					wfeExisting.setNegativeCount(wfe.getNegativeCount());
					wfeExisting.setNegPostIds(wfe.getNegPostIds());
					wfeList.set(wfeIndex, wfeExisting);

				} else {
					wfeList.add(featureArray.size() - 1, wfe);
				}
				wfe = new WidgetFeatureEntity();
				wfe.setName(entityDimension.getSubDimension());
				posPostIdCounter = 0;
				negPostIdCounter = 0;
				if (!featureArray.contains(entityDimension.getSubDimension())) {
					featureArray.add(entityDimension.getSubDimension());
				}

			}

			if (Util.fromDetailedSentiInteger(entityDimension
					.getDetailedSentiment())) {
				wfe.incPosCount();
				if (posPostIdCounter < 24) {
					wfe.addWordToList(entityDimension.getWord());
					wfe.addPosPostIdToList(entityDimension.getPostId());
					posPostIdCounter++;
				}
			} else {
				wfe.incNegCount();
				if (negPostIdCounter < 24) {
					wfe.addWordToList(entityDimension.getWord());
					wfe.addNegPostIdToList(entityDimension.getPostId());
					negPostIdCounter++;
				}
			}

		}

		Collections.sort(wfeList, WFE_ORDER);
		return wfeList;
	}

	public static List<WidgetFeatureEntity> getFeatureWordByEntitySubProduct(
			String entity, String subProduct, String category) {
		List<WidgetFeatureEntity> wfeList = new ArrayList<WidgetFeatureEntity>();
		int entityId = IdMap.getEntityId(entity);
		int subProductId = IdMap.getSubproductId(subProduct);
		List<EntityDimension> entityDimList = MongoConnector
				.getFeaturesForEntitySubproducts(entityId, subProductId,
						category, null, null);
		WidgetFeatureEntity wfe = new WidgetFeatureEntity();
		int posPostIdCounter = 0;
		int negPostIdCounter = 0;

		String word = null;
		Integer posWordCounter = 0;
		Integer negWordCounter = 0;

		List<String> featureArray = new ArrayList<>();
		for (EntityDimension entityDimension : entityDimList) {
			// check if subdimension is same as before
			if (wfe.getName() == null) {
				wfe.setName(entityDimension.getSubDimension());
				featureArray.add(entityDimension.getSubDimension());
				word = entityDimension.getWord();
			} else if (!wfe.getName().equals(entityDimension.getSubDimension())) {
				int wfeIndex = -1;
				if (featureArray.contains(wfe.getName())) {
					wfeIndex = featureArray.indexOf(wfe.getName());
				}
				if (wfeIndex != -1 && wfeList.size() > wfeIndex) {
					WidgetFeatureEntity wfeExisting = wfeList.get(featureArray
							.indexOf(wfe.getName()));

					wfeExisting.getWords().addAll(wfe.getWords());
					wfeExisting.setNegativeCount(wfe.getNegativeCount());
					wfeExisting.setNegPostIds(wfe.getNegPostIds());
					wfeList.set(wfeIndex, wfeExisting);

				} else {
					wfeList.add(featureArray.size() - 1, wfe);
				}

				word = entityDimension.getWord();
				posWordCounter = 0;
				negWordCounter = 0;

				wfe = new WidgetFeatureEntity();
				wfe.setName(entityDimension.getSubDimension());
				posPostIdCounter = 0;
				negPostIdCounter = 0;
				if (!featureArray.contains(entityDimension.getSubDimension())) {
					featureArray.add(entityDimension.getSubDimension());
				}

			}

			// Handle words here.
			if (!word.equals(entityDimension.getWord())) {

				Map<String, Integer[]> wordCount = wfe.getWordCount();
				if (wordCount != null) {
					if (wordCount.containsKey(word)) {
						Integer[] posNegCount = wordCount.get(word);
						posNegCount[1] = negWordCounter;
					} else {
						Integer[] posNegCount = new Integer[2];
						posNegCount[0] = posWordCounter;
						wordCount.put(word, posNegCount);
					}

				} else {
					wordCount = new HashMap<String, Integer[]>();
					Integer[] posNegCount = new Integer[2];
					posNegCount[0] = posWordCounter;
					wordCount.put(word, posNegCount);

				}
				wfe.setWordCount(wordCount);
				word = entityDimension.getWord();
				posWordCounter = 0;
				negWordCounter = 0;

			}

			if (entityDimension.isSenti()) {
				wfe.incPosCount();
				posWordCounter += entityDimension.getCount();
				if (posPostIdCounter < 24) {
					wfe.addWordToList(entityDimension.getWord());
					wfe.addPosPostIdToList(entityDimension.getPostId());
					posPostIdCounter++;
				}
			} else {
				wfe.incNegCount();
				negWordCounter += entityDimension.getCount();
				if (negPostIdCounter < 24) {
					wfe.addWordToList(entityDimension.getWord());
					wfe.addNegPostIdToList(entityDimension.getPostId());
					negPostIdCounter++;
				}
			}

		}

		Collections.sort(wfeList, WFE_ORDER);
		return wfeList;
	}

	/**
	 * @param similarproducts
	 */
	public static String getJsonForsimilarproducts(List<Product> similarproducts) {
		List<CommentsHtml> products = new ArrayList<>();
		CommentsHtml etcDiv = new CommentsHtml();
		etcDiv.setTagName("div");
		etcDiv.setTextContent("etc..");
		etcDiv.setClassName("inf-etc");
		for (Product product : similarproducts) {

			CommentsHtml similarDivs = new CommentsHtml();
			similarDivs.setTagName("div");
			similarDivs.setClassName("inf-bw-klwinesSimilar-div");

			CommentsHtml prodDetailsDiv = new CommentsHtml();
			prodDetailsDiv.setTagName("div");
			prodDetailsDiv.setClassName("prodDetails");

			CommentsHtml prodImg = new CommentsHtml();
			prodImg.setClassName("inf-bw-klwinessimilar-img");
			prodImg.setTagName("img");
			prodImg.setSrc(product.getImageUrl());

			CommentsHtml a = new CommentsHtml();
			a.setTagName("a");
			a.setHref("/Inferlytics/products_wines/" + product.getProductId());

			CommentsHtml nameDiv = new CommentsHtml();
			nameDiv.setTagName("div");
			nameDiv.setTitle(product.getProductName());
			nameDiv.setClassName("inf-bw-klwinessimilar-name");
			nameDiv.setTextContent(product.getProductName());
			a.addChildNode(prodImg);
			a.addChildNode(nameDiv);
			prodDetailsDiv.addChildNode(a);

			CommentsHtml priceDiv = new CommentsHtml();
			priceDiv.setTagName("div");
			priceDiv.setClassName("inf-bw-klwines-similarwinesPrice");
			priceDiv.setTextContent("$" + product.getPrice());
			prodDetailsDiv.addChildNode(priceDiv);
			// similarDivs.addChildNode(prodDetailsDiv);

			CommentsHtml sharedtraitsDiv = new CommentsHtml();
			sharedtraitsDiv.setTagName("div");
			sharedtraitsDiv.setClassName("sharedtraitsDetails");

			CommentsHtml header = new CommentsHtml();
			header.setTagName("div");
			header.setClassName("head");
			header.setTextContent("Shared Traits");
			sharedtraitsDiv.addChildNode(header);
			CommentsHtml scoreDiv = new CommentsHtml();
			scoreDiv.setTagName("div");
			scoreDiv.setTextContent("Score: "
					+ Math.round(product.getSimilarityScore() * 100) / 100.0d);
			// sharedtraitsDiv.addChildNode(scoreDiv);
			CommentsHtml sharedTraits = new CommentsHtml();
			sharedTraits.setClassName("sharedtraits");
			sharedTraits.setTagName("div");
			for (FeatureWords featureWord : product.getFeatureWords()) {
				CommentsHtml trait = new CommentsHtml();
				trait.setClassName("trait");
				trait.setTagName("div");
				String textContent = featureWord.getWords().get(0).getWord()
						;
				if (featureWord.getWords().size() > 1) {
					textContent += ", ";
					trait.addChildNode(etcDiv);
				}
				trait.setTextContent(featureWord.getFeature()
						.replace("wine by", "").replace("wines by", "")
						+ " : " + textContent);
				sharedTraits.addChildNode(trait);
			}
			sharedtraitsDiv.addChildNode(sharedTraits);

			CommentsHtml divInner = new CommentsHtml();
			divInner.setTagName("div");
			divInner.setClassName("similarDiv");
			divInner.addChildNode(prodDetailsDiv);
			divInner.addChildNode(sharedtraitsDiv);
			similarDivs.addChildNode(divInner);
			products.add(similarDivs);

		}
		return new Gson().toJson(products);
	}

	public static List<WidgetFeatureEntity> getKeywordsByEntitySubProduct(
			String entity, String subProduct) {
		List<WidgetFeatureEntity> wfeList = new ArrayList<WidgetFeatureEntity>();
		int entityId = IdMap.getEntityId(entity);
		int subProductId = IdMap.getSubproductId(subProduct);
		List<EntityDimension> entityDimList = MongoConnector
				.getKewordsForEntitySubproduct(entityId, subProductId);
		WidgetFeatureEntity wfe = null;
		List<String> wordArray = new ArrayList<>();

		for (EntityDimension entityDimension : entityDimList) {
			wfe = new WidgetFeatureEntity();
			wfe.setName(entityDimension.getWord());
			if (entityDimension.isSenti()) {
				wfe.setPositiveCount(entityDimension.getCount());

			} else {
				wfe.setNegativeCount(entityDimension.getCount());
			}
			if (!wordArray.contains(entityDimension.getWord())) {
				wordArray.add(entityDimension.getWord());
			}

			int wfeIndex = -1;
			if (wordArray.contains(wfe.getName())) {
				wfeIndex = wordArray.indexOf(wfe.getName());
			}
			if (wfeIndex != -1 && wfeList.size() > wfeIndex) {
				WidgetFeatureEntity wfeExisting = wfeList.get(wordArray
						.indexOf(wfe.getName()));
				wfeExisting.setNegativeCount(wfe.getNegativeCount());
				wfeList.set(wfeIndex, wfeExisting);

			} else {
				wfeList.add(wordArray.size() - 1, wfe);
			}

		}
		return wfeList;
	}

	public static List<WidgetFeatureEntity> getKeywordsByEntitySubProduct(
			String entity, String subProduct, String category,
			boolean showProduct) {
		List<WidgetFeatureEntity> wfeList = new ArrayList<WidgetFeatureEntity>();

		List<EntityDimension> entityDimList = MongoConnector
				.getKeywordsForEntitySubproducts(IdMap.getEntityId(entity),
						IdMap.getSubproductId(subProduct), category);
		WidgetFeatureEntity wfe = new WidgetFeatureEntity();

		for (EntityDimension entityDimension : entityDimList) {

			wfe.setName(entityDimension.getWord());
			wfe.setCount(entityDimension.getCount());
			wfeList.add(wfe);
			wfe = new WidgetFeatureEntity();

			// wfe.addWordToList(entityDimension.getWord());

		}

		return wfeList;
	}

	public static SunBurstData getSunBurstData(String entity, String subProduct) {

		List<SunBurstData> dimList = new ArrayList<SunBurstData>();
		List<EntityDimension> entityDimensionList = DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getSentimentDAO().getEntityDimension(entity, subProduct);

		SunBurstData dim = null;
		SunBurstData subDim = null;
		List<SunBurstData> subDimList = null;
		List<SunBurstData> sentimentList = null;
		if (!entityDimensionList.isEmpty()) {
			for (EntityDimension entityDimension : entityDimensionList) {

				if (null != dim
						&& dim.getName().equalsIgnoreCase(
								entityDimension.getDimension())) {

					// check dimension is same as before

					// check if subdimension is same as before
					if (subDim.getName().equalsIgnoreCase(
							entityDimension.getSubDimension())) {
						// create new detailed sentiment
						// add to sub dimension
						addSentimentToList(sentimentList,
								entityDimension.getDetailedSentiment(),
								entityDimension.getCount(),
								entityDimension.getPostId(),
								entityDimension.getWord());
						// does this gets added to subdim as well?

					}
					// if sub dim is not same as before
					else {
						// create new sunBurstDataListSentiment
						sentimentList = new ArrayList<SunBurstData>();
						addSentimentToList(sentimentList,
								entityDimension.getDetailedSentiment(),
								entityDimension.getCount(),
								entityDimension.getPostId(),
								entityDimension.getWord());

						// add existing subdim to dim
						// As its the same reference all
						// changes must be affectted directly to the list in
						// dim.

						// create new subdimen
						// add to dim 's subdim list.
						subDim = new SunBurstData();
						subDim.setName(entityDimension.getSubDimension());
						subDim.setChildren(sentimentList);
						subDimList.add(subDim);
						// TODO test again here if its adding to dim's subdim
						// list or not
					}
				}
				// if dim is not same as bfore
				// create everything new

				else {
					// Sentiment
					sentimentList = new ArrayList<SunBurstData>();
					addSentimentToList(sentimentList,
							entityDimension.getDetailedSentiment(),
							entityDimension.getCount(),
							entityDimension.getPostId(),
							entityDimension.getWord());

					// SubDim
					subDimList = new ArrayList<SunBurstData>();
					subDim = new SunBurstData();
					subDim.setName(entityDimension.getSubDimension());
					subDim.setChildren(sentimentList);
					subDimList.add(subDim);

					// Dimension
					dim = new SunBurstData();
					dim.setName(entityDimension.getDimension());
					dim.setChildren(subDimList);
					dimList.add(dim);
				}
			}
			SunBurstData main = new SunBurstData();
			main.setName(entity);
			main.setChildren(dimList);

			checkAndSetSubDimToDim(main);

			return main;
		}
		return null;
	}

	public static void main(String[] s) {

		/*
		 * List<SunBurstData> featurelist =
		 * genarateFeatureJsonForEntitySubProduct( "moltonbrown",
		 * "fragrance","null"); LOG.info(featurelist.size()); LOG.info(new
		 * Gson().toJson(featurelist));
		 */
	}

}
