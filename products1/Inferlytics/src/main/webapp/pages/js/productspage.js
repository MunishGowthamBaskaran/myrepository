var overlayPosition = 140; /*
 * This variable stores the margin-left value for
 * overlay This is multiplied with the position odf
 * the keywords.
 */
var limit = 5; // Keeps the count of comments to be fetched.
$(document).ready(function() {

	$(".review_container").mCustomScrollbar({
		mouseWheel : true,
		mouseWheelPixels : "auto",
		scrollButtons : {
			enable : true
		}
	});

	$(".closeOverlay").click(function() {
		$("#posoverlay").hide();
		$("#negoverlay").hide();

	});

	$('.review_container').on("click", ".review", function() {
		if ($(this).find('.review_comment').hasClass('expand')) {

			h = $(this).find('.review_comment p').height() + 10;

			$(this).find('.review_comment').removeClass('expand');
			$(this).find('.review_comment').animate({
				'height' : h + 'px'
			}, {
				duration : 50,
				complete : function() {

				}
			}

			);

		} else {
			$(this).find('.review_comment').addClass('expand');

			$(this).find('.review_comment').animate({
				'height' : '65px'
			}, {
				duration : 50,
				complete : function() {

				}
			});

		}
		/*
		 * $(".review_container").mCustomScrollbar( "update");
		 */
	});

});

function getDetails(prodId) {
	datatosend = "ProductId=" + prodId + "&entity=" + entity + "&subProduct="
			+ subProduct;
	$
			.ajax({
				type : "POST",
				url : "/Inferlytics/GetKeywords",
				data : datatosend,
				cache : false,
				crossDomain : true,
				dataType : "json",
				success : function(data) {
					$("#keyWords").empty();
					$("#loader").hide();
					$("#productName").html(data.name);
					$("#productImg").attr("src", data.imgURL);
					$(".prodImage").show();
					$
							.each(
									data.posKeywords,
									function(i, value) {
										value.keyWord = value.keyWord.replace(
												"'", "&apos;");

										var posKeyWord = "<div class='posBlock' onclick='getPosComments("
												+ (i+1)
												+ ",\""
												+ value.keyWord
												+ "\","
												+ value.count
												+ ",0,2)' title='"
												+ value.keyWord
												+ " ("
												+ value.count
												+ ")"
												+ "'>"
												+ "<div class='nameText'>"
												+ value.keyWord
												+ " ("
												+ value.count
												+ ")</div>"
												+ "<div class='posBackground' ></div>"
												+ "</div>";
										if (i < 5)
											$("#poskeyWords")
													.append(posKeyWord);
										else
											$("#poskeywordsOverlay").append(
													posKeyWord);
										if (i == 5) {
											$("#poskeyWords")
													.append(
															"<div class='showMore' id='posShowMore'>Show More</div>");
											$("#posShowMore")
													.click(
															function() {
																
																
																
																if ($(
																		"#negShowMore")
																		.hasClass(
																				'hide')) {

																	$(
																			"#negkeywordsOverlay")
																			.hide();
																	$(
																			"#negShowMore")
																			.removeClass(
																					'hide');
																	$(
																			"#negShowMore")
																			.css(
																					"background",
																					"#FFF");
																	$("#negShowMore").css({"z-index":1,"box-shadow":""});
																}

																if ($(this)
																		.hasClass(
																				'hide')) {
																	$(
																			"#poskeywordsOverlay")
																			.hide();
																	$(this)
																			.removeClass(
																					'hide');
																	$(this)
																			.css(
																					"background",
																					"#FFF");
																	$("#posShowMore").css({"z-index":1,"box-shadow":""});
																} else {

																	$(this)
																			.addClass(
																					'hide');
															
																	$(
																			"#poskeywordsOverlay")
																			.show();
																	$("#posShowMore").css({"z-index":3,"box-shadow":"rgb(136, 136, 136) -1px -9px 17px -2px"});
																}
															});
										}

									});
					$
							.each(
									data.negKeywords,
									function(i, value) {
										value.keyWord = value.keyWord.replace(
												"'", "&apos;");
										var negKeyWord = "<div class='negBlock' onclick='getPosComments("
												+ (i+1)
												+ ",\""
												+ value.keyWord
												+ "\","
												+ value.count
												+ ",0,3)' title='"
												+ value.keyWord
												+ " ("
												+ value.count
												+ ")'"
												+ ">"
												+ "<div class='nameText' >"
												+ value.keyWord
												+ " ("
												+ value.count
												+ ")</div>"
												+ "<div class='negBackground' ></div>"
												+ "</div>";
										if (i < 5)
											$("#negkeyWords")
													.append(negKeyWord);
										else
											$("#negkeywordsOverlay").append(
													negKeyWord);

										if (i == 5) {
											$("#negkeyWords")
													.append(
															"<div class='showMore' id='negShowMore'>Show More</div>");
											$("#negShowMore")
													.click(
															function() {
																$("#negShowMore").css("z-index",3);
																$("#negShowMore").css("box-shadow","rgb(136, 136, 136) -1px -9px 17px -2px");
																$("#posShowMore").css("box-shadow","");
																$("#posShowMore").css("z-index",1);
																
																if ($(
																		"posShowMore")
																		.hasClass(
																				'hide')) {
																	$(
																			"#poskeywordsOverlay")
																			.hide();
																	$(
																			"#posShowMore")
																			.removeClass(
																					'hide');
																	$(
																			"#posShowMore")
																			.css(
																					"background",
																					"#FFF");
																	$("#posShowMore").css({"z-index":1,"box-shadow":""});
																}

																if ($(this)
																		.hasClass(
																				'hide')) {
																	$(
																			"#negkeywordsOverlay")
																			.hide();
																	$(this)
																			.removeClass(
																					'hide');
																	$(this)
																			.css(
																					"background",
																					"#FFF");
																	$("#negShowMore").css({"z-index":1,"box-shadow":""});
																} else {

																	$(this)
																			.addClass(
																					'hide');
																	
																	$(
																			"#negkeywordsOverlay")
																			.show();
																	$("#negShowMore").css({"z-index":3,"box-shadow":"rgb(136, 136, 136) -1px -9px 17px -2px"});
																}
															});
										}

									});

				}
			});
}

function getPosComments(id, keyword, count, skip, reviewType) {

	if (skip == 0) {
		$("#posoverlay").hide();
		$("#negoverlay").hide();
	}
	var marginTop=((Math.floor(id/6))*45)+95;    //Calculates the Top margin position for the comments block. 
	id = id % 6;
	  

	marginPos = (id * overlayPosition) + 245;
	
	if (reviewType == 2) {
		$("#posoverlay").css("margin-left", marginPos + "px");
		$("#posoverlay").css("margin-top", marginTop + "px");
		$('.header').text(
				"Showing All Positive User Reviews Associated With " + keyword);
	} else {
		$("#negoverlay").css("margin-left", marginPos + "px");
		$('.header').text(
				"Showing All Negative User Reviews Associated With " + keyword);
	}
	$
			.ajax({
				type : "POST",
				url : "/Inferlytics/getPostsFromMongo",
				data : "entity=" + entity + "&subproduct=" + subProduct
						+ "&word=" + keyword + "&skip=" + skip + "&reviewType="
						+ reviewType + "&limit=" + limit + "&productId="
						+ prodId,
				dataType : "json",
				success : function(data) {

					$(".next").remove();
					var dfs = FragBuilder(data);
					if (skip == 0) {
						skip = skip + 5;
						$('.review_container .mCSB_container').empty();
						$('.review_container .mCSB_container').append(dfs);
						if (skip < count)
							$('.review_container .mCSB_container')
									.append(
											"<div class='next'><center><a href='#'><div class='blueBtn'  onClick=\"getPosComments("
													+ id
													+ ",'"
													+ keyword
													+ "\',"
													+ count
													+ ","
													+ skip
													+ ","
													+ reviewType
													+ ")\"></div></a></center></div>");
					}

					else {

						skip = skip + 5;
						$('.review_container .mCSB_container').append(dfs);
						if (skip < count)
							$('.review_container .mCSB_container')
									.append(
											"<div class='next'><center><a href='#'><div class='blueBtn'   onClick=\"getPosComments("
													+ id
													+ ",'"
													+ keyword
													+ "\',"
													+ count
													+ ","
													+ skip
													+ ","
													+ reviewType
													+ ")\"></div></a></center></div>");
					}
					if (reviewType == 2)
						$('#posoverlay').show();

					else
						$('#negoverlay').show();
					$(".review_container").mCustomScrollbar("update");

				},
				async : false,
				complete : function() {
					if (reviewType == 2) {

						$.each($('.review_panel .review'),
								function(index, item) {
									var wordToHighlight = $(this).attr('word');
									var myHilitor = new Hilitor(
											"posreview_container");
									myHilitor.setMatchType("left");
									myHilitor.apply(wordToHighlight);
								});

					} else {

						$.each($('.review_panel .review'),
								function(index, item) {
									var wordToHighlight = $(this).attr('word');
									var myHilitor = new Hilitor(
											"negreview_container");
									myHilitor.setMatchType("left");
									myHilitor.apply(wordToHighlight);
								});
					}

					if (skip == 5) {
						$(".review_container").mCustomScrollbar("destroy");
						$(".review_container").mCustomScrollbar({
							scrollButtons : {
								enable : true
							},
							advanced : {
								updateOnContentResize : true,
								scrollInertia : 10
							}
						});
					}
				},
				error : function(e18188) {
					alert('Error' + e);
				}

			});

}