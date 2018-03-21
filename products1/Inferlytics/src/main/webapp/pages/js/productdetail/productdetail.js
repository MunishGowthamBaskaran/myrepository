var showPos = 0; // 0 indicates positive overlay is close ,1 indicates it is
// open
var showNeg = 0;// 0 indicates negative overlay is close ,1 indicates it is open
var limit = 5;
var keywordsdata;// Stores the keywords in the Json format.
var KEYCODE_ESC = 27; // Escape key keycode
$(document).ready(
		function() {
			$(window).scroll(function() {

				$(".outer_wrapper").animate({
					top : window.pageYOffset + 150
				}, 100);

			});

			$(document)
					.keyup(
							function(event) {

								var code = (event.keyCode ? event.keyCode
										: event.which);
								if (code == KEYCODE_ESC) {
									$("#inferlytics-negoverlay").hide();
									$("#inferlytics-posoverlay").hide();
									$(".arrow").hide();
									if (showNeg == 1) {
										$("#inferlytics-negShowmore").css(
												"background-position",
												"12px 10px");
										$("#inferlytics-negoverlay").hide();
										showNeg = 0;
									} else if (showPos == 1) {
										$("#inferlytics-posoverlay").hide();
										showPos = 0;
										$("#inferlytics-posShowmore").css(
												"background-position",
												"12px 10px");
									}

								}

							});
			$("#inferlytics-posShowmore").click(
					function() {
						$(".arrow").hide();
						if (showPos == 0) {
							$("#inferlytics-posoverlay").show();
							showPos = 1;
							$("#inferlytics-posShowmore").css(
									"background-position", "12px 18px");
							$("#inferlytics-negoverlay").hide();
							showNeg = 0;
							$("#inferlytics-negShowmore").css(
									"background-position", "12px 10px");
							getProductDetails();
						} else {
							$("#inferlytics-posoverlay").hide();
							showPos = 0;
							$("#inferlytics-posShowmore").css(
									"background-position", "12px 10px");
						}
						$(".inferlytics-goback").hide();
						$(".inferlytics-comments-header").hide();

					});

			$("#inferlytics-negShowmore").click(
					function() {
						$(".arrow").hide();
						if (showNeg == 0) {
							$("#inferlytics-negoverlay").show();
							showNeg = 1;
							$("#inferlytics-negShowmore").css(
									"background-position", "12px 18px");

							$("#inferlytics-posoverlay").hide();
							showPos = 0;
							$("#inferlytics-posShowmore").css(
									"background-position", "12px 10px");
							getProductDetails();
						} else {
							$("#inferlytics-negShowmore").css(
									"background-position", "12px 10px");
							$("#inferlytics-negoverlay").hide();
							showNeg = 0;
						}
						$(".inferlytics-goback").hide();
						$(".inferlytics-comments-header").hide();

					});

			$('.review_container').on("click", ".review", function() {

				if ($(this).find('.review_comment').hasClass('expand')) {

					h = $(this).find('.review_comment p').height() + 20;

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
						'height' : '58px'
					}, {
						duration : 50,
						complete : function() {

						}
					});

				}

				$(".review_container").mCustomScrollbar("update");

			});

			$(".review_container").mCustomScrollbar({
				mouseWheel : true,
				mouseWheelPixels : "auto",
				scrollButtons : {
					enable : true
				},
				advanced : {
					updateOnContentResize : true
				}
			});

			$(".inferlytics-closebutton").click(
					function() {
						$("#inferlytics-posoverlay").hide();
						$(".arrow").hide();
						$("#inferlytics-posoverlay").hide();
						showPos = 0;
						$("#inferlytics-posShowmore").css(
								"background-position", "12px 10px");
						$("#inferlytics-negoverlay").hide();
						$(".arrow").hide();
						$("#inferlytics-negoverlay").hide();
						showNeg = 0;
						$("#inferlytics-negShowmore").css(
								"background-position", "12px 10px");
					});

			$(".inferlytics-goback").click(function() {
				getProductDetails();
				$(".inferlytics-goback").hide();
			});

		});

function getKeywords(prodId) {
	datatosend = "ProductId=" + prodId + "&entity=" + entity + "&subProduct="
			+ subProduct;
	$.ajax({
		type : "POST",
		url : "/Inferlytics/GetKeywords",
		data : datatosend,
		cache : false,
		crossDomain : true,
		dataType : "json",
		success : function(data) {

			keywordsdata = data;
			getProductDetails();

		},
		error : function(e) {
			alert('Error' + e);
		}

	});

}
function getProductDetails() {

	$("#poskeyWords").empty();
	$("#negkeyWords").empty();
	$('.review_container .mCSB_container').empty();
	$("#productImg").attr("src", keywordsdata.imgURL);
	$('.inferlytics-comments-header').hide();
	if (keywordsdata.posKeywords.length == 0) {
		$("#poskeyWords").append(" No positive traits found");

	} else {
		$
				.each(
						keywordsdata.posKeywords,
						function(i, value) {
							value.keyWord = value.keyWord
									.replace("'", "&apos;");
							var posKeyWord = null;
							if (i < 3) {
								if (i != 0) {
									$("#poskeyWords").append(", ");
								}
								posKeyWord = "<div class='inferlytics-poskeyword' onclick='getComments("
										+ (i + 1)
										+ ",\""
										+ value.keyWord
										+ "\","
										+ value.count
										+ ",0,2)' >"
										+ value.keyWord
										+ " ("
										+ value.count
										+ ")"
										+ "<div class='arrow'><div class='arrow-down'></div><div class='arrow-shadow'></div></div>";

								$("#poskeyWords").append(posKeyWord);
							} else {

								if (i == 3) {
									$("#inferlytics-posShowmore").show();

								}
								posKeyWord = "<div class='inferlytics-allkeyword' onclick='getComments("
										+ (i + 1)
										+ ",\""
										+ value.keyWord
										+ "\","
										+ value.count
										+ ",0,2)' "
										+ " title='"
										+ value.keyWord
										+ " ("
										+ value.count
										+ ")'"
										+ ">"
										+ value.keyWord
										+ " ("
										+ value.count
										+ ")" + "</div>";

								$(
										'.posoverlay .review_container .mCSB_container')
										.append(posKeyWord);

							}

						});
	}
	if (keywordsdata.negKeywords.length == 0) {
		$("#negkeyWords").append(" No negative traits found");
	} else {
		$
				.each(
						keywordsdata.negKeywords,
						function(i, value) {

							value.keyWord = value.keyWord
									.replace("'", "&apos;");
							var negKeyWord = null;
							if (i < 3) {
								if (i != 0) {
									$("#negkeyWords").append(", ");
								}
								negKeyWord = "<div class='inferlytics-negkeyword' onclick='getComments("
										+ (i + 1)
										+ ",\""
										+ value.keyWord
										+ "\","
										+ value.count
										+ ",0,3)' >"
										+ value.keyWord
										+ " ("
										+ value.count
										+ ")"
										+ "<div class='arrow'><div class='arrow-down'></div><div class='arrow-shadow'></div></div>";

								$("#negkeyWords").append(negKeyWord);
							} else {
								if (i == 3) {
									$("#inferlytics-negShowmore").show();
								}
								negKeyWord = "<div class='inferlytics-allkeyword' onclick='getComments("
										+ (i + 1)
										+ ",\""
										+ value.keyWord
										+ "\","
										+ value.count
										+ ",0,3)'"
										+ " title='"
										+ value.keyWord
										+ " ("
										+ value.count
										+ ")'"
										+ " >"
										+ value.keyWord
										+ " ("
										+ value.count
										+ ")" + "</div>";

								$(
										'.negoverlay .review_container .mCSB_container')
										.append(negKeyWord);

							}

						});
	}
	registerEvents();

}

function getComments(id, keyword, count, skip, reviewType) {

	if (reviewType == 2) {
		$('.inferlytics-comments-header').text(
				"All Positive User Reviews Associated With '" + keyword + "'");

	} else {

		$('.inferlytics-comments-header').text(
				"All Negative User Reviews Associated With '" + keyword + "'");
	}
	if (skip == 0) {
		$('.inferlytics-comments-header').show();
		$(".inferlytics-goback").show();
	}
	$.ajax({
		type : "POST",
		url : "/Inferlytics/getPostsFromMongo",
		data : "entity=" + entity + "&subproduct=" + subProduct + "&word="
				+ keyword + "&skip=" + skip + "&reviewType=" + reviewType
				+ "&limit=" + limit + "&productId=" + prodId+"&moltonBrown=true",
		dataType : "json",
		success : function(data) {

			$(".next").remove();
			var dfs = FragBuilder(data);
			if (skip == 0) {
				skip = skip + 5;
				$('.review_container .mCSB_container').empty();
				$('.review_container .mCSB_container').append(dfs);
				if (skip < count)
					$('.review_container .mCSB_container').append(
							"<div class='next'><center><a><div class='blueBtn'  onClick=\"getComments("
									+ id + ",'" + keyword + "\'," + count + ","
									+ skip + "," + reviewType
									+ ")\"></div></a></center></div>");
			}

			else {

				skip = skip + 5;
				$('.review_container .mCSB_container').append(dfs);
				if (skip < count)
					$('.review_container .mCSB_container').append(
							"<div class='next'><center><a><div class='blueBtn'   onClick=\"getComments("
									+ id + ",'" + keyword + "\'," + count + ","
									+ skip + "," + reviewType
									+ ")\"></div></a></center></div>");
			}

			$(".review_container").mCustomScrollbar("update");

		},
		async : false,
		complete : function() {
			if (reviewType == 2) {

				$.each($('.review_panel .review'), function(index, item) {
					var wordToHighlight = $(this).attr('word');
					var myHilitor = new Hilitor("posreview_container");
					myHilitor.setMatchType("left");
					myHilitor.apply(wordToHighlight);
				});

			} else {

				$.each($('.review_panel .review'), function(index, item) {
					var wordToHighlight = $(this).attr('word');
					var myHilitor = new Hilitor("negreview_container");
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
		error : function(e) {
			alert('Error' + e);
		}

	});
}

function registerEvents() {

	$(".inferlytics-poskeyword").click(function() {
		$("#inferlytics-posoverlay").show();
		$(".arrow").hide();
		$(this).children(".arrow").css("display", "inline");
		$("#inferlytics-negoverlay").hide();
		showNeg = 0;
		$("#inferlytics-negShowmore").css("background-position", "12px 10px");
		$(".inferlytics-goback").hide();
		showPos = 0;
		$("#inferlytics-posShowmore").css("background-position", "12px 10px");

	});

	$(".inferlytics-negkeyword").click(function() {
		$(".inferlytics-goback").hide();
		$("#inferlytics-negoverlay").show();
		$(".arrow").hide();
		$(this).children(".arrow").css("display", "inline");
		$("#inferlytics-posoverlay").hide();
		showNeg = 0;
		$("#inferlytics-posShowmore").css("background-position", "12px 10px");

		showPos = 0;
		$("#inferlytics-negShowmore").css("background-position", "12px 10px");

	});

}
