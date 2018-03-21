package com.raremile.prd.inferlytics.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.raremile.prd.inferlytics.entity.CommentsHtml;
import com.raremile.prd.inferlytics.entity.Features;
import com.raremile.prd.inferlytics.entity.HTML;
import com.raremile.prd.inferlytics.entity.KeyWord;
import com.raremile.prd.inferlytics.entity.Post;
import com.raremile.prd.inferlytics.entity.Product;
import com.raremile.prd.inferlytics.entity.ProductDetails;

public class BusinessUtilityForAnalytics {

	public static String getCommentsHtmlForStores(List<Post> posts,
			String callFrom) {

		List<CommentsHtml> all = new ArrayList<>(posts.size());

		for (Post post : posts) {

			CommentsHtml review_comment = new CommentsHtml();
			review_comment.setTagName("div");
			review_comment.setClassName("inf" + callFrom
					+ "review_comment expand");

			CommentsHtml col_xs_12 = new CommentsHtml();
			col_xs_12.setTagName("div");
			col_xs_12.setClassName("col-xs-12");

			CommentsHtml p = new CommentsHtml();
			// p.setTextContent( Util.appendEmTag(reviewString) );
			p.setTagName("p");
			p.setChildNodes(Util.appendEmTag(post.getContent()));

			col_xs_12.addChildNode(p);
			review_comment.addChildNode(col_xs_12);
			CommentsHtml review = new CommentsHtml();
			review.setId(post.getPostId());
			review.setTagName("div");
			review.setWord(post.getWord());
			review.setScNo(String.valueOf(post.getSentenceNo() - 1));
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

		return new Gson().toJson(all);

	}

	public static List<CommentsHtml> getCommentsHtmlForTopPosts(
			List<Post> posts, String callFrom, int totalCount,
			boolean postsFoundInRange) {

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

			CommentsHtml review_comment = new CommentsHtml();
			review_comment.setTagName("div");
			review_comment.setClassName("inf" + callFrom
					+ "review_comment expand");

			CommentsHtml col_xs_3 = new CommentsHtml();
			col_xs_3.setTagName("div");
			col_xs_3.setClassName("col-xs-3 productdetail");

			CommentsHtml img = new CommentsHtml();
			img.setTagName("img");
			img.setClassName("widget-post-thumb img-responsive");
			img.setSrc(post.getProduct().getImageUrl());

			CommentsHtml prodName = new CommentsHtml();
			prodName.setTagName("div");
			prodName.setClassName("inf-prodname");
			prodName.setTextContent(post.getProduct().getProductName());

			col_xs_3.addChildNode(img);

			if (post.getClicks() != null) {
				CommentsHtml clicks = new CommentsHtml();
				clicks.setTagName("div");
				clicks.setClassName("clicks	");
				clicks.setTextContent("No Of Clicks:" + post.getClicks());
				col_xs_3.addChildNode(clicks);
			}
			if (post.getDate() != null) {
				CommentsHtml postsDate = new CommentsHtml();
				postsDate.setTagName("div");
				postsDate.setClassName("date");
				postsDate.setTextContent("Review Date: " + post.getDate());
				col_xs_3.addChildNode(postsDate);
			}

			review_comment.addChildNode(col_xs_3);

			CommentsHtml col_xs_9 = new CommentsHtml();
			col_xs_9.setTagName("div");
			col_xs_9.setClassName("col-xs-9");

			col_xs_9.addChildNode(prodName);
			CommentsHtml p = new CommentsHtml();
			// p.setTextContent( Util.appendEmTag(reviewString) );
			p.setTagName("p");
			p.setChildNodes(Util.appendEmTag(post.getContent()));

			col_xs_9.addChildNode(p);
			review_comment.addChildNode(col_xs_9);
			CommentsHtml review = new CommentsHtml();
			review.setId(post.getPostId());
			review.setTagName("div");
			review.setWord(post.getWord());
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
		CommentsHtml count = new CommentsHtml();
		count.setTagName("Count");
		count.setCount(totalCount);
		all.add(count);
		CommentsHtml postsInRange = new CommentsHtml();
		postsInRange.setTagName("Message");
		postsInRange.setisShowMessage(!postsFoundInRange);
		all.add(postsInRange);
		return all;

	}

	public static String getHtmlCommentsForStore(List<Post> posts,
			boolean isPositive, String subDimension) {
		/*
		 * <div id="sequence-twitter"><span class="sequence-prev"
		 * style="display: inline;"><i class="icon-left-open-big"></i></span>'+
		 * '<span class="sequence-next" style="display: inline;"><i
		 * class="icon-right-open-big"></i></span>'+ '<ul
		 * class="sequence-canvas" id="posEmployee-Comments"><li><p
		 * class="slide-tweet-text" style="">Envato is looking for a Front End
		 * Developerdccs dscdc dscdsc sdcdsc. Help us make all the things
		 * awesome. <a href="#">http://buff.ly/18TTA7w</a> </p></li><!-- Frame 1
		 * --></ul></div>
		 */

		List<CommentsHtml> all = new ArrayList<>();

		String id = "";
		String header = "";
		if (subDimension.equals("named employees")) {

			id = "sequence-twitter";
			header = "pos-header";

		} else if (subDimension.equals("purchase experience")) {
			id = "sequence-twitter_3";
			header = "shopping-header";
		} else if (subDimension.equals("competitors")) {
			id = "sequence-twitter_4";
			header = "competitors-header";
		} else {
			id = "sequence-twitter_5";
			header = "departments-header";
		}
		CommentsHtml headerdiv = new CommentsHtml();
		headerdiv.setTagName("div");
		headerdiv.setId(header);
		all.add(headerdiv);
		CommentsHtml divId = new CommentsHtml();
		divId.setTagName("div");
		divId.setId(id);

		String clickUpdate = "highlightText(\"" + id + "\");";
		CommentsHtml sequencePrev = new CommentsHtml();
		sequencePrev.setTagName("span");
		sequencePrev.setClassName("sequence-prev");
		sequencePrev.setClick(clickUpdate);
		CommentsHtml iLeft = new CommentsHtml();
		iLeft.setTagName("i");
		iLeft.setClassName("icon-left-open-big");
		sequencePrev.addChildNode(iLeft);
		divId.addChildNode(sequencePrev);

		CommentsHtml sequenceNext = new CommentsHtml();
		sequenceNext.setTagName("span");
		sequenceNext.setClassName("sequence-next");
		sequenceNext.setClick(clickUpdate);
		CommentsHtml iRight = new CommentsHtml();
		iRight.setTagName("i");
		iRight.setClassName("icon-right-open-big");
		sequenceNext.addChildNode(iRight);
		divId.addChildNode(sequenceNext);

		CommentsHtml ul = new CommentsHtml();
		ul.setTagName("ul");
		ul.setClassName("sequence-canvas");

		for (Post post : posts) {
			CommentsHtml li = new CommentsHtml();
			li.setTagName("li");
			li.setScNo(String.valueOf(post.getSentenceNo() - 1));

			CommentsHtml p = new CommentsHtml();
			p.setTagName("div");
			p.setClassName("slide-tweet-text");
			p.setChildNodes(Util.appendEmTag(post.getContent()));
			li.addChildNode(p);
			ul.addChildNode(li);
		}
		divId.addChildNode(ul);
		all.add(divId);
		return new Gson().toJson(all);
	}

	public static String getHtmlForBlogs(List<Post> posts, String subDim) {
		List<CommentsHtml> all = new ArrayList<>();
		/**
		 * <div class="blogComment head ">Comments for Hackathon <button
		 * type="button" class="num btn btn-success btn-xs">225</button> <button
		 * type="button" class="num btn btn-danger btn-xs">356</button> </div>
		 * <div class="blogComment nobg"> <div class="title"> <span>General
		 * financial issue</span> <div class="fix"></div> </div> <div
		 * class="date blogContent">12/11/2013</div> <div
		 * class="url blogContent"><a >http://google.com</a></div> <div
		 * class="blogContent"> This is a blog related to general financial
		 * issue. And is a very important crisis situation... <div
		 * class="fix"></div> </div> </div>
		 * 
		 * </div> <div class="blogComment"> <button type="button"
		 * class="btn btn-primary btn-xs center-block">show more</button> <div
		 * class="fix"></div> </div>
		 **/
		/*
		 * CommentsHtml headerdiv = new CommentsHtml();
		 * headerdiv.setTagName("div");
		 * headerdiv.setClassName("blogComment head");
		 * headerdiv.setTextContent("Blogs for " + subDim);
		 * headerdiv.setId("idBlogHeader");
		 * 
		 * CommentsHtml headerDivPosBtn = new CommentsHtml();
		 * headerDivPosBtn.setTagName("button");
		 * headerDivPosBtn.setClassName("num btn btn-success btn-xs");
		 * headerDivPosBtn.setTextContent("" + posCount);
		 * headerDivPosBtn.setId("idBlogHeaderPosBtn");
		 * 
		 * CommentsHtml headerDivNegBtn = new CommentsHtml();
		 * headerDivNegBtn.setTagName("button");
		 * headerDivNegBtn.setClassName("num btn btn-danger btn-xs");
		 * headerDivPosBtn.setTextContent("" + negCount);
		 * headerDivNegBtn.setId("idBlogHeaderNegBtn");
		 * 
		 * headerdiv.addChildNode(headerDivPosBtn);
		 * headerdiv.addChildNode(headerDivNegBtn); all.add(headerdiv);
		 */
		CommentsHtml fixDiv = new CommentsHtml();
		fixDiv.setTagName("div");
		fixDiv.setClassName("fix");

		for (Post post : posts) {
			CommentsHtml commentDiv = new CommentsHtml();
			commentDiv.setTagName("div");
			commentDiv.setClassName("blogComment nobg");

			CommentsHtml titleDivspan = new CommentsHtml();
			titleDivspan.setTextContent(post.getTitle());
			titleDivspan.setTagName("span");

			CommentsHtml titleDiv = new CommentsHtml();
			titleDiv.setTagName("div");
			titleDiv.setClassName("title");

			titleDiv.addChildNode(titleDivspan);
			titleDiv.addChildNode(fixDiv);

			commentDiv.addChildNode(titleDiv);

			CommentsHtml dateDiv = new CommentsHtml();
			dateDiv.setTagName("div");
			dateDiv.setClassName("date blogContent");
			dateDiv.setTextContent(post.getDate());

			commentDiv.addChildNode(dateDiv);

			CommentsHtml urlDiv = new CommentsHtml();
			urlDiv.setTagName("a");
			urlDiv.setClassName("url blogContent");
			urlDiv.setHref(post.getPermaLink());
			urlDiv.setTextContent(post.getPermaLink());

			commentDiv.addChildNode(urlDiv);

			CommentsHtml contentDiv = new CommentsHtml();
			contentDiv.setTagName("div");
			contentDiv.setClassName("blogContent");
			contentDiv.setTextContent(post.getContent().substring(0, 100));
			contentDiv.addChildNode(fixDiv);
			commentDiv.addChildNode(contentDiv);


			all.add(commentDiv);

		}

		return new Gson().toJson(all);
	}

	public static String getHtmlForBlogs(Post post) {
		CommentsHtml p = new CommentsHtml();
		p.setTagName("p");
		p.setChildNodes(Util.appendEmTag(post.getContent()));
		return new Gson().toJson(p);
	}


	public static String gethtmlForfeaturelist(List<KeyWord> keywords,
			String subDimension, String category) {

		List<CommentsHtml> all = new ArrayList<>();
		CommentsHtml otherCompetitors = null;
		for (KeyWord keyword : keywords) {
			CommentsHtml li = new CommentsHtml();
			li.setTagName("li");
			li.setClassName("category-list");
			li.setTextContent(keyword.getKeyWord());

			CommentsHtml spanPos = new CommentsHtml();
			spanPos.setTagName("span");
			spanPos.setClassName("posnegCount");
			if (keyword.getPosCount() != 0) {
				String clickEventPos = "getComments(\"" + keyword.getKeyWord()
						+ "\",\"true\",\"" + subDimension + "\",\"" + category
						+ "\")";
				spanPos.setClick(clickEventPos);
			}
			CommentsHtml iPos = new CommentsHtml();
			iPos.setTagName("i");
			iPos.setClassName("icon-thumbs-up");
			spanPos.addChildNode(iPos);

			CommentsHtml posText = new CommentsHtml();
			posText.setTagName("div");
			posText.setClassName("posnegText");
			posText.setTextContent(String.valueOf(keyword.getPosCount()));
			spanPos.addChildNode(posText);
			li.addChildNode(spanPos);

			CommentsHtml spanNeg = new CommentsHtml();
			spanNeg.setTagName("span");
			spanNeg.setClassName("posnegCount");

			CommentsHtml iNeg = new CommentsHtml();
			iNeg.setTagName("i");
			iNeg.setClassName("icon-thumbs-down");
			spanNeg.addChildNode(iNeg);
			if (keyword.getNegativeCount() != 0) {
				String clickEventNeg = "getComments(\"" + keyword.getKeyWord()
						+ "\",\"false\",\"" + subDimension + "\",\"" + category
						+ "\")";
				spanNeg.setClick(clickEventNeg);
			}
			CommentsHtml negText = new CommentsHtml();
			negText.setTagName("div");
			negText.setClassName("posnegText");
			negText.setTextContent(String.valueOf(keyword.getNegativeCount()));
			spanNeg.addChildNode(negText);
			li.addChildNode(spanNeg);
			if (keyword.getKeyWord().equals("other competitors")) {
				otherCompetitors = li;
			} else {
				all.add(li);
			}
		}
		if (otherCompetitors != null) {
			all.add(otherCompetitors);
		}
		return new Gson().toJson(all);
	}

	public static String gethtmlForTraitsCompare(List<KeyWord> keywords) {

		List<CommentsHtml> all = new ArrayList<>();
		int i = 1;
		for (KeyWord keyword : keywords) {

			/*
			 * <tr> <td>1</td> <td>love</td> <td>47</td> <td>47</td> <td
			 * class="negColor">-10%</td> </tr>
			 */
			int change = keyword.getSecondCount() - keyword.getcount();
			double percentage = (double) change / (double) keyword.getcount()
					* 100;
			int percentageChange = (int) Math.ceil(percentage);

			CommentsHtml tr = new CommentsHtml();
			tr.setTagName("tr");

			CommentsHtml td1 = new CommentsHtml();
			td1.setTagName("td");
			td1.setTextContent(String.valueOf(i++));
			tr.addChildNode(td1);

			CommentsHtml td2 = new CommentsHtml();
			td2.setTagName("td");
			td2.setTextContent(keyword.getKeyWord());
			tr.addChildNode(td2);

			CommentsHtml td3 = new CommentsHtml();
			td3.setTagName("td");
			td3.setTextContent(String.valueOf(keyword.getcount()));
			tr.addChildNode(td3);

			CommentsHtml td4 = new CommentsHtml();
			td4.setTagName("td");
			td4.setTextContent(String.valueOf(keyword.getSecondCount()));
			tr.addChildNode(td4);

			CommentsHtml td5 = new CommentsHtml();
			td5.setTagName("td");

			if (percentageChange < 0) {
				td5.setClassName("negColor");
				td5.setTextContent(String.valueOf(percentageChange) + "%");
			} else {
				td5.setClassName("posColor");
				td5.setTextContent("+" + String.valueOf(percentageChange) + "%");
			}
			tr.addChildNode(td5);

			all.add(tr);
		}
		return new Gson().toJson(all);
	}

	public static String gethtmlForyahooProducts(
			List<ProductDetails> productList) {
		List<CommentsHtml> html = new ArrayList<>();
		for (ProductDetails product : productList) {

			/*
			 * Image div <div class="img"> <a href=""><img
			 * src="./yahooboots_files/saved_resource" ></a> </div>
			 */

			CommentsHtml mod_content = new CommentsHtml();
			mod_content.setTagName("div");
			mod_content.setClassName("mod-content");

			CommentsHtml imgDiv = new CommentsHtml();
			imgDiv.setClassName("img");
			imgDiv.setTagName("div");

			CommentsHtml aTag = new CommentsHtml();
			aTag.setTagName("a");

			CommentsHtml imgTagCommentsHtml = new CommentsHtml();
			imgTagCommentsHtml.setSrc(product.getImgURL());
			imgTagCommentsHtml.setTagName("img");
			aTag.addChildNode(imgTagCommentsHtml);
			imgDiv.addChildNode(aTag);

			mod_content.addChildNode(imgDiv);

			/*
			 * <h2 class="title" > <a href="">DV by Dolce Vita 'Sandie'...</a>
			 * </h2>
			 */
			CommentsHtml h2 = new CommentsHtml();
			h2.setClassName("title");
			h2.setTagName("h2");

			CommentsHtml aTag2 = new CommentsHtml();
			aTag2.setTagName("a");
			aTag2.setTextContent(product.getname());
			h2.addChildNode(aTag2);
			mod_content.addChildNode(h2);

			/*
			 * <div class="inf-nike-prd-keywords inf-all-prd-keywords"> <div
			 * class="inf-mb-prd-posthumb"></div> <div
			 * id="inf-mb-prd-poskeyWords" class="inf-mb-prd-poskeywords"> <div
			 * class="inf-mb-prd-poskeyword" onclick="getCommentsPos(word)" >
			 * comfort (246) </div>
			 * 
			 * </div> </div>
			 */

			CommentsHtml posKeywordsDiv = new CommentsHtml();
			posKeywordsDiv
			.setClassName("inf-nike-prd-keywords inf-all-prd-keywords");
			posKeywordsDiv.setTagName("div");

			CommentsHtml posThumb = new CommentsHtml();
			posThumb.setClassName("inf-mb-prd-posthumb");
			posThumb.setTagName("div");
			posKeywordsDiv.addChildNode(posThumb);

			CommentsHtml poskeywordsContainer = new CommentsHtml();
			poskeywordsContainer.setClassName("inf-mb-prd-poskeywords");
			poskeywordsContainer.setId("inf-mb-prd-poskeyWords");
			poskeywordsContainer.setTagName("div");

			List<KeyWord> posKeyWords = product.getPosReviews();
			if (posKeyWords.size() != 0) {
				for (KeyWord word : posKeyWords) {
					CommentsHtml posKeyWord = new CommentsHtml();
					String onclick = "getCommentsPos('" + word.getKeyWord()
							+ "','" + product.getProductId() + "',"
							+ word.getcount() + ")";
					posKeyWord.setClassName("inf-mb-prd-poskeyword");
					posKeyWord.setTagName("div");
					posKeyWord.setClick(onclick);
					posKeyWord.setTextContent(word.getKeyWord() + " ("
							+ word.getcount() + ")");
					poskeywordsContainer.addChildNode(posKeyWord);
				}
			} else {
				poskeywordsContainer.setTextContent("No Positive Traits Found");
			}
			posKeywordsDiv.addChildNode(poskeywordsContainer);
			mod_content.addChildNode(posKeywordsDiv);
			/*
			 * <div class="inf-nike-prd-keywords inf-all-prd-keywords">
			 * 
			 * <div class="inf-mb-prd-negthumb"></div> <div
			 * id="inf-mb-prd-negkeyWords" class="inf-mb-prd-negkeywords"> <div
			 * class="inf-mb-prd-negkeyword" onclick="getCommentsNeg()" > noise
			 * (93)
			 * 
			 * </div>
			 * 
			 * ,
			 * 
			 * </div>
			 * 
			 * </div>
			 */
			CommentsHtml negKeywordsDiv = new CommentsHtml();
			negKeywordsDiv
			.setClassName("inf-nike-prd-keywords inf-all-prd-keywords");
			negKeywordsDiv.setTagName("div");

			CommentsHtml negThumb = new CommentsHtml();
			negThumb.setClassName("inf-mb-prd-negthumb");
			negThumb.setTagName("div");
			negKeywordsDiv.addChildNode(negThumb);

			CommentsHtml negkeywordsContainer = new CommentsHtml();
			negkeywordsContainer.setClassName("inf-mb-prd-negkeywords");
			negkeywordsContainer.setId("inf-mb-prd-negkeyWords");
			negkeywordsContainer.setTagName("div");

			List<KeyWord> negKeyWords = product.getNegReviews();
			if (negKeyWords.size() != 0) {
				for (KeyWord word : negKeyWords) {
					CommentsHtml negKeyWord = new CommentsHtml();
					String onclick = "getCommentsNeg('" + word.getKeyWord()
							+ "','" + product.getProductId() + "',"
							+ word.getcount() + ")";
					negKeyWord.setClassName("inf-mb-prd-negkeyword");
					negKeyWord.setTagName("div");
					negKeyWord.setClick(onclick);
					negKeyWord.setTextContent(word.getKeyWord() + " ("
							+ word.getcount() + ")");
					negkeywordsContainer.addChildNode(negKeyWord);
				}
			} else {
				negkeywordsContainer.setTextContent("No Negative Traits Found");
			}
			negKeywordsDiv.addChildNode(negkeywordsContainer);
			mod_content.addChildNode(negKeywordsDiv);
			/*
			 * <div class="compare"> <a rel="nofollow"
			 * class="price"><strong>$49.47</strong></a> <p> <a
			 * class="button primary btn-compare" rel="nofollow" >Go to
			 * Nordstrom</a> </p> </div>
			 */

			CommentsHtml priceDiv = new CommentsHtml();
			priceDiv.setTagName("div");
			priceDiv.setClassName("compare");

			CommentsHtml aTag3 = new CommentsHtml();
			aTag3.setClassName("price");
			aTag3.setTagName("a");
			CommentsHtml strong = new CommentsHtml();
			strong.setTagName("strong");
			strong.setTextContent("$" + product.getPrice());
			aTag3.addChildNode(strong);
			priceDiv.addChildNode(aTag3);
			CommentsHtml p = new CommentsHtml();
			p.setTagName("p");
			CommentsHtml aTag4 = new CommentsHtml();
			aTag4.setClassName("button primary btn-compare");
			aTag4.setTagName("a");
			aTag4.setTextContent("Go to Nordstrom");
			p.addChildNode(aTag4);
			priceDiv.addChildNode(p);
			mod_content.addChildNode(priceDiv);

			CommentsHtml shmod = new CommentsHtml();
			shmod.setClassName("shmod");
			shmod.setTagName("div");
			shmod.addChildNode(mod_content);

			CommentsHtml hproduct = new CommentsHtml();
			hproduct.setClassName("hproduct");
			hproduct.setTagName("li");
			hproduct.addChildNode(shmod);
			html.add(hproduct);

		}
		return new Gson().toJson(html);
	}

	/**
	 * @param keywords
	 */
	public static String getJsonForTagCloud(List<KeyWord> keywords) {
		List<Features> featureList = new ArrayList<Features>();
		for (KeyWord keyword : keywords) {
			Features feature = new Features();
			feature.setText(keyword.getKeyWord());
			feature.setWeight(keyword.getTotalCount());
			HTML html = new HTML();
			/*
			 * String onclick = "getCommentsForTopDimension('" +
			 * keyword.getKeyWord() + "',true,0," + keyword.getcount() + ")";
			 * html.setOnclick(onclick);
			 */
			html.setClassName("cloudTag");
			html.setTitle(keyword.getTotalCount() + " mentions");
			featureList.add(feature);
			feature.setHtml(html);

		}
		return new Gson().toJson(featureList);
	}

	public static String getNamedEmployeesHtml(List<String> employees,
			boolean isPositive, String category) {

		List<CommentsHtml> all = new ArrayList<>();

		/*
		 * <li class="category-list"
		 * onclick="getComments('melinda','false','named employees','negEmployees')"
		 * ><a>melinda (1)</a></li>
		 */

		for (String employee : employees) {
			String nameCount[] = employee.split(":");

			String clickEvent = "getComments('" + nameCount[0] + "','"
					+ isPositive + "','named employees','" + category + "')";
			CommentsHtml li = new CommentsHtml();
			li.setTagName("li");
			li.setClassName("category-list");
			li.setClick(clickEvent);
			CommentsHtml a = new CommentsHtml();
			a.setTagName("a");
			a.setTextContent(nameCount[0] + " (" + nameCount[1] + ")");
			li.addChildNode(a);
			all.add(li);
		}
		return new Gson().toJson(all);
	}

	public static String getTopProductsHtmlForAnalytics(List<Product> products,
			int count) {

		List<CommentsHtml> all = new ArrayList<>();
		CommentsHtml hr = new CommentsHtml();
		hr.setTagName("hr");

		/*
		 * <div class="row">
		 * 
		 * 
		 * 
		 * <div class="col-xs-3"> <img src="_/images/thumb-1-widget-post.jpg"
		 * alt="" class="widget-post-thumb img-responsive"> </div> <div
		 * class="col-xs-9"> <a href="#" class="widget-post-title"> Fringilla
		 * Euismod Cursus Ullamcorper Sem Nibh </a> <div class="meta-info"> <div
		 * class="float-left"> <i class="icon-comment"></i> 3 Positive Comments
		 * </div> <div class="float-right"> <i class="icon-comment"></i> 3
		 * Negative Comments </div> </div> </div> </div>
		 */

		/*
		 * <div class="float-left">
		 */
		for (Product product : products) {

			CommentsHtml row = new CommentsHtml();
			row.setTagName("div");
			row.setClassName("row");

			CommentsHtml col_xs_3 = new CommentsHtml();
			col_xs_3.setTagName("div");
			col_xs_3.setClassName("col-xs-3");

			CommentsHtml img = new CommentsHtml();
			img.setTagName("img");
			img.setClassName("widget-post-thumb img-responsive");
			img.setSrc(product.getImageUrl());
			col_xs_3.addChildNode(img);

			row.addChildNode(col_xs_3);

			CommentsHtml col_xs_9 = new CommentsHtml();
			col_xs_9.setTagName("div");
			col_xs_9.setClassName("col-xs-9");
			CommentsHtml prodName = new CommentsHtml();
			prodName.setTagName("div");
			prodName.setClassName("widget-post-title");
			prodName.setTextContent(product.getProductName());
			col_xs_9.addChildNode(prodName);

			CommentsHtml meta_info = new CommentsHtml();
			meta_info.setTagName("div");
			meta_info.setClassName("meta-info");

			/*
			 * <div class="row"><div class="col-xs-4"> <div
			 * class="img-circle posnum"><div
			 * class="img-circle num">22</div><div
			 * class="img-circle thumbsIcon"> <i
			 * class="icon-up-hand"></i></div></div></div> <div
			 * class="col-xs-8"><div class=" float-left"><div class="img-circle
			 * posnum"><div class="img-circle
			 * num">22</div><div class="img-circle
			 * thumbsIcon"><i class="icon-thumbs
			 * -up"></i></div></div></div><div class="
			 * float-right"><div class="img-circle
			 * posnum"><div class="img-circle num">3</div><div class="img-circle
			 * thumbsIcon
			 * "><i class="icon-thumbs-down"></i></div></div></div></div></div>
			 */

			CommentsHtml row2 = new CommentsHtml();
			row2.setTagName("div");
			row2.setClassName("row countRow");

			CommentsHtml col1 = new CommentsHtml();
			col1.setTagName("div");
			col1.setClassName("col-xs-4");
			col1.setTitle("No Of Clicks");
			CommentsHtml posnum1 = new CommentsHtml();
			posnum1.setTagName("div");
			posnum1.setClassName("img-circle posnum");

			CommentsHtml num3 = new CommentsHtml();
			num3.setTagName("div");
			num3.setClassName("img-circle num");
			num3.setTextContent(product.getClickCount() + "");
			posnum1.addChildNode(num3);

			CommentsHtml handUp = new CommentsHtml();
			handUp.setTagName("div");
			handUp.setClassName("img-circle thumbsIcon");

			CommentsHtml i_handUp = new CommentsHtml();
			i_handUp.setTagName("i");
			i_handUp.setClassName("icon-up-hand");
			handUp.addChildNode(i_handUp);
			posnum1.addChildNode(handUp);

			col1.addChildNode(posnum1);
			row2.addChildNode(col1);

			CommentsHtml col2 = new CommentsHtml();
			col2.setTagName("div");
			col2.setClassName("col-xs-8");

			CommentsHtml ifloat_left = new CommentsHtml();
			ifloat_left.setTagName("div");
			ifloat_left.setClassName("float-left");
			ifloat_left.setTitle(" No of Positive Reviews");
			CommentsHtml posnum = new CommentsHtml();
			posnum.setTagName("div");
			posnum.setClassName("img-circle posnum");

			CommentsHtml num1 = new CommentsHtml();
			num1.setTagName("div");
			num1.setClassName("img-circle num");
			num1.setTextContent(product.getPosCommentsCount() + "");
			posnum.addChildNode(num1);

			CommentsHtml thumbsUp = new CommentsHtml();
			thumbsUp.setTagName("div");
			thumbsUp.setClassName("img-circle thumbsIcon");

			CommentsHtml i_thumbsUp = new CommentsHtml();
			i_thumbsUp.setTagName("i");
			i_thumbsUp.setClassName("icon-thumbs-up");
			thumbsUp.addChildNode(i_thumbsUp);
			posnum.addChildNode(thumbsUp);

			ifloat_left.addChildNode(posnum);

			CommentsHtml ifloat_right = new CommentsHtml();
			ifloat_right.setTagName("div");
			ifloat_right.setClassName("float-right");
			ifloat_right.setTitle("No of Negative Reviews");

			CommentsHtml negnum = new CommentsHtml();
			negnum.setTagName("div");
			negnum.setClassName("img-circle posnum");

			CommentsHtml num2 = new CommentsHtml();
			num2.setTagName("div");
			num2.setClassName("img-circle num");
			num2.setTextContent(product.getNegCommentsCount() + "");
			negnum.addChildNode(num2);

			CommentsHtml thumbsDown = new CommentsHtml();
			thumbsDown.setTagName("div");
			thumbsDown.setClassName("img-circle thumbsIcon");

			CommentsHtml i_thumbsDown = new CommentsHtml();
			i_thumbsDown.setTagName("i");
			i_thumbsDown.setClassName("icon-thumbs-down");
			thumbsDown.addChildNode(i_thumbsDown);
			negnum.addChildNode(thumbsDown);

			ifloat_right.addChildNode(negnum);

			col2.addChildNode(ifloat_left);
			col2.addChildNode(ifloat_right);
			row2.addChildNode(col2);
			meta_info.addChildNode(row2);

			col_xs_9.addChildNode(meta_info);

			row.addChildNode(col_xs_9);
			CommentsHtml issueSummary = new CommentsHtml();
			issueSummary.setTagName("div");
			issueSummary.setClassName("issueSummary");
			all.add(row);
			all.add(hr);
		}
		CommentsHtml countofProducts = new CommentsHtml();
		countofProducts.setTagName("Count");
		countofProducts.setCount(count);
		all.add(countofProducts);
		return new Gson().toJson(all);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
