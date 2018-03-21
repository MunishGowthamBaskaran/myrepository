$.noConflict();
var productId = "";
var selectedSubdimension = "";
var selectedWord = "";
var selectedTopCatPosWord = "";
var selectedTopCatNegWord = "";
var reviewType="2";
function getUrlParams() {
	var vars = {};
	var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi,
			function(m, key, value) {
				vars[key] = value;
			});
	return vars;
}
function logout() {
	  jQuery("form:first").submit();
	}
/* When the Document is ready register events and load data */

function getChartDataForTimeLine() {
	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/GetChartDataForStore",
		data : "&subProduct=" + subProduct + "&entity=" + entity
				+ "&productId=" + productId + "&fromDate="
				+ jQuery('#fromDatepicker').val() + "&toDate="
				+ jQuery('#toDatepicker').val() + "&chartType=timeLine",
		dataType : "json",
		async : true,
		success : function(returnData) {
			if (returnData.Fail) {
				sendtoLoginPage();
			} else {
				plotForTimeLine(returnData);
			}
		}
	});

}

function getChartDataForSubTopics() {

	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/GetChartDataForStore",
		data : "&subProduct=" + subProduct + "&entity=" + entity
				+ "&productId=" + productId + "&fromDate="
				+ jQuery('#fromDateSubTopic').val() + "&toDate="
				+ jQuery('#toDateSubTopic').val() + "&subDimension="
				+ encodeURIComponent(selectedSubdimension) + "&word="
				+ encodeURIComponent(selectedWord)
				+ "&chartType=subTopicsChart",
		async : true,
		dataType : "json",
		success : function(returnData) {
			plotForSubTopics(returnData);
			jQuery("#subTopicHeader").text(
					"Sentiment trend for '" + selectedWord + "'");
		}
	});

}

function plotForSubTopics(jsond) {
	jQuery("#subTopics_chart").empty();
	var plot2 = jQuery
			.jqplot(
					'subTopics_chart',
					[ jsond.PositiveData, jsond.NegativeData ],
					{
						seriesColors : [ "#8BC141", "#C3262F" ],
						seriesDefaults : {
							lineWidth : 2
						},
						grid : {
							drawGridLines : true, // wether to draw lines
							// across the grid or not.
							gridLineColor : '#ecf0f1', // *Color of the grid
							// lines.
							background : '#fff',
							borderColor : '#999999', // CSS color spec for
							// border around grid.
							borderWidth : 0,
							shadow : false
						},
						highlighter : {
							show : true
						},
						series : [
								{
									label : 'Positive Review',
									highlighter : {
										show : true,
										sizeAdjust : 7.5,
										tooltipAxes : 'both',
										formatString : '<table class="jqplot-highlighter"><tr><td>Positive</td><td>Reviews</td></tr>'
												+ '<tr><td>Date:</td><td>%s</td></tr>'
												+ '<tr><td>Count:</td><td> %d</td></tr></table>',
									}
								},
								{
									label : 'Negative Review',
									highlighter : {
										show : true,
										sizeAdjust : 7.5,
										tooltipAxes : 'both',
										formatString : '<table class="jqplot-highlighter"><tr><td>Negative</td><td>Reviews</td></tr>'
												+ '<tr><td>Date:</td><td>%s</td></tr>'
												+ '<tr><td>Count:</td><td> %d</td></tr></table>',
									}
								}, ],
						legend : {
							show : true

						},
						axes : {
							xaxis : {
								renderer : jQuery.jqplot.DateAxisRenderer,
								tickOptions : {
									formatString : '%b&nbsp,%y'
								},
								label : 'Date',
								labelRenderer : jQuery.jqplot.CanvasAxisLabelRenderer

							},
							yaxis : {
								min : 0,
								tickOptions : {
									formatString : '%d'
								},
								label : 'No of Mentions',
							}
						},
						seriesDefaults: {
							markerOptions: {
					            show: true,             
					            style: 'filledCircle'
							}
						},
				
						cursor : {
							show : true,
							zoom : true
						}
					});
	jQuery("#resetzoomSubtopic").unbind('click');
	jQuery("#resetzoomSubtopic").click(function() {
		plot2.resetZoom;
	});
}

function submitClicked() {
	getChartDataForTimeLine();
}

function plotForTimeLine(jsond) {
	jQuery("#charts").empty();
	var plotter = jQuery
			.jqplot(
					'charts',
					[ jsond.PositiveData, jsond.NegativeData ],
					{
						seriesColors : [ "#8BC141", "#C3262F" ],
						seriesDefaults : {
							lineWidth : 2
						},
						grid : {
							drawGridLines : true, // wether to draw lines
							// across the grid or not.
							gridLineColor : '#ecf0f1', // *Color of the grid
							// lines.
							background : '#fff',
							borderColor : '#999999', // CSS color spec for
							// border around grid.
							borderWidth : 0,
							shadow : false
						},
						highlighter : {
							show : true,
						},
						series : [
								{
									label : 'Positive Review',
									highlighter : {
										show : true,
										sizeAdjust : 7.5,
										tooltipAxes : 'both',
										formatString : '<table class="jqplot-highlighter"><tr><td>Positive</td><td>Reviews</td></tr>'
												+ '<tr><td>Date:</td><td>%s</td></tr>'
												+ '<tr><td>Count:</td><td> %d</td></tr></table>',
									}
								},
								{
									label : 'Negative Review',
									highlighter : {
										show : true,
										sizeAdjust : 7.5,
										tooltipAxes : 'both',
										formatString : '<table class="jqplot-highlighter"><tr><td>Negative</td><td>Reviews</td></tr>'
												+ '<tr><td>Date:</td><td>%s</td></tr>'
												+ '<tr><td>Count:</td><td> %d</td></tr></table>',
									}
								}, ],
						legend : {
							show : true

						},
						axes : {
							xaxis : {
								renderer : jQuery.jqplot.DateAxisRenderer,
								tickOptions : {
									formatString : '%b&nbsp,%y'
								},
								label : 'Date',
								labelRenderer : jQuery.jqplot.CanvasAxisLabelRenderer

							},
							yaxis : {
								min : 0,
								tickOptions : {
									formatString : '%d'
								},
								label : 'No of Reviews',
							}
						},

						cursor : {
							show : true,
							zoom : true
						}
					});
	jQuery("#resetzoomTimeline").unbind('click');
	jQuery("#resetzoomTimeline").click(function() {
		plotForTimeLine(jsond);
	});

}

function registerEvents() {

	jQuery(".inf-bw-data_container1").mCustomScrollbar({
		advanced : {
			updateOnContentResize : true
		}
	});

	jQuery("#top_Comments").mCustomScrollbar({
		advanced : {
			updateOnContentResize : true
		}
	});

	jQuery("#pos_Comments").mCustomScrollbar({
		advanced : {
			updateOnContentResize : true
		}
	});

	jQuery("#storeHolder").mCustomScrollbar({
		advanced : {
			updateOnContentResize : true
		}
	});
	jQuery("#neg_Comments").mCustomScrollbar({
		advanced : {
			updateOnContentResize : true
		}
	});

	jQuery(".allTopics").mCustomScrollbar({
		advanced : {
			updateOnContentResize : true
		}
	});


	jQuery(".dimensions .tag").click(function() {
		jQuery(".selectedtag").removeClass("selectedtag");
		jQuery(this).addClass("selectedtag");
		selectedSubdimension = jQuery(this).text();
	});
	jQuery(".inf-bw-negative_review").click(function() {
		getComments(false, 0, parseInt(jQuery(this).text()));
		reviewType="3";
	});

	jQuery(".inf-bw-positive_review").click(function() {
		getComments(true, 0, parseInt(jQuery(this).text()));
		reviewType="2";
	});

	jQuery('#top_Comments').on(
			"click",
			".inf-bw-review",
			function() {
				if (jQuery(this).find('.inf-bw-review_comment').hasClass(
						'expand')) {
					var h = jQuery(this).find('.inf-bw-review_comment p')
							.height();
					if ((h + 30) > 128) {
						jQuery(this).find('.inf-bw-review_comment')
								.removeClass('expand');
						jQuery(this).find('.inf-bw-review_comment').animate({
							'height' : (h + 30) + 'px'
						});
					}
					jQuery.ajax({
												type : "POST",
												url : "/Inferlytics/RegisterEventForStorePage",
												data : "category=analysissummary&productId=" + productId + "&type=registerComment&reviewType="+reviewType+"&subDimension="+encodeURIComponent(selectedSubdimension)+"&word="+encodeURIComponent(selectedWord)
														+"&subProduct="+subProduct + "&entity=" + entity+"&postId="+jQuery(this).attr('id') ,
												dataType : "json",
												async : true,
											});
				} else {
					jQuery(this).find('.inf-bw-review_comment').addClass(
							'expand');
					jQuery(this).find('.inf-bw-review_comment').animate({
						'height' : '108px'
					});
				}
			});
	jQuery('#pos_Comments').on(
			"click",
			".inf-bw-review",
			function() {
				if (jQuery(this).find('.inf-bw-review_comment').hasClass(
						'expand')) {
					var h = jQuery(this).find('.inf-bw-review_comment p')
							.height();
					if ((h + 30) > 128) {
						jQuery(this).find('.inf-bw-review_comment')
								.removeClass('expand');
						jQuery(this).find('.inf-bw-review_comment').animate({
							'height' : (h + 30) + 'px'
						});
					}
					jQuery.ajax({
						type : "POST",
						url : "/Inferlytics/RegisterEventForStorePage",
						data : "category=topcategories&productId=" + productId + "&type=registerComment&reviewType=2"+"&word="+encodeURIComponent(selectedTopCatPosWord)
								+"&subProduct="+subProduct + "&entity=" + entity+"&postId="+jQuery(this).attr('id') ,
						dataType : "json",
						async : true,
					});
				} else {
					jQuery(this).find('.inf-bw-review_comment').addClass(
							'expand');
					jQuery(this).find('.inf-bw-review_comment').animate({
						'height' : '108px'
					});
				}
			});

	jQuery('#neg_Comments').on(
			"click",
			".inf-bw-review",
			function() {
				if (jQuery(this).find('.inf-bw-review_comment').hasClass(
						'expand')) {
					var h = jQuery(this).find('.inf-bw-review_comment p')
							.height();
					if ((h + 30) > 128) {
						jQuery(this).find('.inf-bw-review_comment')
								.removeClass('expand');
						jQuery(this).find('.inf-bw-review_comment').animate({
							'height' : (h + 30) + 'px'
						});
					}
						jQuery.ajax({
						type : "POST",
						url : "/Inferlytics/RegisterEventForStorePage",
						data : "category=topcategories&productId=" + productId + "&type=registerComment&reviewType=3"+"&word="+encodeURIComponent(selectedTopCatNegWord)
								+"&subProduct="+subProduct + "&entity=" + entity+"&postId="+jQuery(this).attr('id') ,
						dataType : "json",
						async : true,
					});
				} else {
					jQuery(this).find('.inf-bw-review_comment').addClass(
							'expand');
					jQuery(this).find('.inf-bw-review_comment').animate({
						'height' : '108px'
					});
				}
			});

}

function setDateForSubtopicschart() {
	var date = new Date();
	date.setDate(date.getDate() - 30);

	var newFormattedDate = ("0" + (date.getMonth() + 1).toString()).substr(-2)
			+ "/" + ("0" + date.getDate().toString()).substr(-2) + "/"
			+ (date.getFullYear().toString()).substr(2);

	jQuery("#fromDateSubTopic").datepicker(
			{
				onClose : function(selectedDate) {
					jQuery("#toDateSubTopic").datepicker("option", "minDate",
							selectedDate);
				}
			});
	jQuery("#toDateSubTopic").datepicker(
			{
				onClose : function(selectedDate) {
					jQuery("#fromDateSubTopic").datepicker("option", "maxDate",
							selectedDate);
				}
			});
	jQuery("#fromDateSubTopic").datepicker("setDate", newFormattedDate);
	jQuery("#toDateSubTopic").datepicker("setDate", new Date());
	jQuery("#fromDateSubTopic").datepicker("option", "maxDate", new Date());
	jQuery("#toDateSubTopic").datepicker("option", "maxDate", new Date());
}

jQuery(function() {
	registerEvents();

	var date = new Date();
	date.setDate(date.getDate() - 365);
	var newFormattedDate = ("0" + (date.getMonth() + 1).toString()).substr(-2)
			+ "/" + ("0" + date.getDate().toString()).substr(-2) + "/"
			+ (date.getFullYear().toString()).substr(2);

	jQuery("#fromDatepicker").datepicker(
			{
				onClose : function(selectedDate) {
					jQuery("#toDatepicker").datepicker("option", "minDate",
							selectedDate);
				}
			});
	jQuery("#toDatepicker").datepicker(
			{
				onClose : function(selectedDate) {
					jQuery("#fromDatepicker").datepicker("option", "maxDate",
							selectedDate);
				}
			});
	jQuery("#fromDatepicker").datepicker("setDate", newFormattedDate);
	jQuery("#toDatepicker").datepicker("setDate", new Date());
	jQuery("#fromDatepicker").datepicker("option", "maxDate", new Date());
	jQuery("#toDatepicker").datepicker("option", "maxDate", new Date());
	setDateForSubtopicschart();
	jQuery(".product-thumbnail").first().click();
	var owl = $("#pieChartSlider");

	owl.owlCarousel({
		items : 8, // 10 items above 1000px browser width
		itemsDesktop : [ 1000, 5 ], // 5 items between 1000px and 901px
		itemsDesktopSmall : [ 900, 3 ], // betweem 900px and 601px
		itemsTablet : [ 600, 2 ], // 2 items between 600 and 0
		itemsMobile : false
	// itemsMobile disabled - inherit from itemsTablet option
	});

	// Custom Navigation Events
	$(".nexts").click(function() {
		owl.trigger('owl.next');
	});
	$(".prevs").click(function() {
		owl.trigger('owl.prev');
	});
});

function highlightText() {
	jQuery.each(jQuery('#top_Comments .inf-bw-review'), function(index, item) {

		var sentenceToHighlight = jQuery(this).attr('scno');
		if (sentenceToHighlight != "-1") {
			jQuery(this).find('em:eq(' + sentenceToHighlight + ')').css(
					'backgroundColor', '#ff6');
		}
	});
	setTimeout(function() {
	}, 300);

	jQuery.each(jQuery('#pos_Comments .inf-bw-review'), function(index, item) {

		var sentenceToHighlight = jQuery(this).attr('scno');
		if (sentenceToHighlight != "-1") {
			jQuery(this).find('em:eq(' + sentenceToHighlight + ')').css(
					'backgroundColor', '#ff6');
		}
	});
	setTimeout(function() {
	}, 300);

	jQuery.each(jQuery('#neg_Comments .inf-bw-review'), function(index, item) {
		var sentenceToHighlight = jQuery(this).attr('scno');
		if (sentenceToHighlight != "-1") {
			jQuery(this).find('em:eq(' + sentenceToHighlight + ')').css(
					'backgroundColor', '#ff6');
		}
	});
	setTimeout(function() {
	}, 300);
}

function loadfeatureeffect() {
	// setup array of colors and a variable to store the current
	// index
	var poscolors = [ "metro-green", "metro-blue", "metro-turquoise",
			"metro-yellow" ];
	var negcolors = [ "metro-red", "metro-orange", "metro-violet", "metro-pink" ];
	var classes = [ "cell2x2", "cell2x1" ];
	var widtharray = [ 140, 140 ];
	var currPosColor = 0;
	var currNegColor = 0;
	var currCell = 0;

	jQuery('.inf-bw-keys_block').each(
			function(index, element) {

				var w = jQuery(this).children().width();
				/*
				 * w = jq(this).children().width(); h =
				 * jq(this).children().height(); jq(this).css({'width':w+'px'});
				 * jq(this).css({'height':h+'px'});
				 * 
				 * jq(this).children().css({'width':w+'p_x'});
				 */
				var percentage = 0;
				var posCount = jQuery(this)
						.find(".inf-bw-keyword_block_header").attr("poscount");
				var negCount = jQuery(this)
						.find(".inf-bw-keyword_block_header").attr("negcount");
				if (posCount > 0) {
					percentage = (negCount / posCount) * 100;
				}
				var currentclass = classes[currCell];
				if (w > widtharray[currCell]) {
					currentclass = 'cell2x1';
				}
				jQuery(this).addClass(currentclass);// .addClass(colors[currColor]);
				if (percentage < 50 && percentage != 0) {
					jQuery(this).children().addClass(currentclass).addClass(
							poscolors[currPosColor]);
					currPosColor++;
				} else {
					jQuery(this).children().addClass(currentclass).addClass(
							negcolors[currNegColor]);
					currNegColor++;
				}
				// increment the current index
				currCell++;
				// if the next index is greater than then number of
				// colors then reset to zero
				if (currPosColor == poscolors.length) {
					currPosColor = 0;
				}
				if (currNegColor == negcolors.length) {
					currNegColor = 0;
				}
				if (currCell >= classes.length) {
					currCell = 1;
				}
				/*
				 * jq(this).children().css({ 'width' : w + 'px' });
				 */
			});

	portfoliocall();
}

function portfoliocall() {
	// alert();
	// <!-- PORTFOLIO -->
	jQuery('#subContainer').portfolio({
		// <!-- GRID SETTINGS -->
		gridOffset : 42, // <!-- Manual Right Padding
		// Offset for
		// 100% Width -->
		cellWidth : 70, // <!-- The Width of one CELL in
		// PX-->
		cellHeight : 70, // <!-- The Height of one CELL
		// in PX-->
		cellPadding : 8, // <!-- Spaces Between the CELLS
		// -->
		entryProPage : 50, // <!-- The Max. Amount of the
		// Entries per Page, Rest made by
		// Pagination -->

		// <!-- CAPTION SETTING -->
		captionOpacity : 85,

		// <!-- FILTERING -->
		filterList : "#feature_sorting" // <!-- Which
	// Filter is
	// used
	// for
	// the
	// Filtering / Pagination -->
	// <!-- title:"#selected-filter-title", Which Div should be
	// used for
	// showing the Selected Title of the Filter -->

	});

}

function setProductId(prodId,ele) {
	productId = prodId;
	jQuery(".selectedstore").removeClass("selectedstore");
	jQuery(ele).addClass("selectedstore");
	jQuery("#selectedHotel").text(jQuery(this).next().text());
	jQuery.ajax({
					type : "POST",
					url : "/Inferlytics/RegisterEventForStorePage",
					data : "productId=" + productId + "&type=registerProduct&subProduct="
							+ subProduct + "&entity=" + entity ,
					dataType : "json",
					async : true,
				});
	
	jQuery(".inf-bw-review_type").hide();
	jQuery("#commentsHeader").hide();
	jQuery("#top_Comments .mCSB_container").empty();
	getPieChartData();
	getChartDataForTimeLine();
	getTopDimension(true);
	getTopDimension(false);
}

function getPieChartData() {
	var html = "";
	var i = 0;
	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/StoreConnector",
		data : "productId=" + productId + "&type=pieChartData&subProduct="
				+ subProduct + "&entity=" + entity + "&isPositive=",
		dataType : "json",
		async : true,
		success : function(returnData) {

			if (returnData.KeywordsData == 0) {
				jQuery("#pieChartSlider").html(
						"<h4><center>No data Found</center><h4>");
				$(".nexts").hide();
				$(".prevs").hide();
			} else {
				jQuery.each(returnData.KeywordsData, function(index, items) {
					html += "	<div class='pie_chart_holder normal' >"
							+ "<div class='percentage' data-percent='"
							+ items.percentage + "' data-linewidth='16'"
							+ "data-active='#000' data-noactive='#fff'>"
							+ "<canvas class='doughnutChart' data-percent='"
							+ items.percentage + "'></canvas>"
							+ "<div class='pie_chart_text'>"
							+ "<span class='tocounter'>" + items.percentage
							+ "</span><h5 class='dimNames'>" + items.keyWord
							+ "</h5>" + "</div></div></div>";
					i = index;
				});
				jQuery("#pieChartSlider").html(html);
				var owl = $("#pieChartSlider").data('owlCarousel');

				owl.reinit({
					items : 8, // 10 items above 1000px browser width
					itemsDesktop : [ 1000, 5 ], // 5 items between 1000px and
					// 901px
					itemsDesktopSmall : [ 900, 3 ], // betweem 900px and 601px
					itemsTablet : [ 600, 2 ], // 2 items between 600 and 0
					itemsMobile : false
				// itemsMobile disabled - inherit from itemsTablet option
				});
				owl.goTo(i);
				initDoughnutProgressBar2();
				$(".nexts").show();
				$(".prevs").show();

				jQuery(".pie_chart_holder").click(
						function() {
							selectedSubdimension = jQuery(this).find(
									".dimNames").text();
							jQuery(".selectedDoughnut").removeClass(
									"selectedDoughnut");
							getDimensions(selectedSubdimension);
							jQuery(this).addClass("selectedDoughnut");
						});
				jQuery(".pie_chart_holder").first().click();
				setTimeout(function() {
					owl.goTo(0);
				}, 300);
				// If the no of dimensions is less than 8 hide the navigation
				// buttons
				if (i <= 8) {
					jQuery(".customNavigation").hide();
				} else {
					jQuery(".customNavigation").show();
				}
			}
		}
	});
}

function registerEventForDimension() {
	jQuery("#subContainer .inf-bw-keyword_block_header").click(
			function() {
				var posCount = parseInt(jQuery(this).attr("posCount"));
				jQuery("#inf-bw-positiveCountno").text(
						jQuery(this).attr("posCount"));
				jQuery("#inf-bw-negativeCountno").text(
						jQuery(this).attr("negCount"));
				selectedWord = jQuery(this).children(":first").children(
						":first").text();
				jQuery(".inf-bw-metro-color-selected").removeClass(
						"inf-bw-metro-color-selected");
				jQuery(this).parent().addClass("inf-bw-metro-color-selected");
				getComments(true, 0, posCount);
				getChartDataForSubTopics();
			});

}

function getComments(isPositive, skip, totCount) {
	if (totCount == 0) {
		jQuery("#top_Comments .mCSB_container").html(
				"No reviews found for this category");
		jQuery(".inf-bw-review_type").show();
		jQuery("#commentsHeader").show();
		if (isPositive) {
			jQuery("#posarrow").show();
			jQuery("#negarrow").hide();
		} else {
			jQuery("#negarrow").show();
			jQuery("#posarrow").hide();
		}
		if (selectedSubdimension == "competition") {
			jQuery("#commentsHeader").text(
					"Reviews where customers compared it to \""
							+ selectedWord + "\"");
		} else {
			jQuery("#commentsHeader").text(
					"Showing reviews for \"" + selectedWord + "\"");
		}
	} else {
		jQuery.ajax({
			type : "POST",
			url : "/Inferlytics/StoreConnector",
			data : "productId=" + productId + "&analyticscategory=analysissummary&type=comments&subProduct="
					+ subProduct + "&entity=" + entity + "&isPositive="
					+ isPositive + "&subDimension="
					+ encodeURIComponent(selectedSubdimension) + "&word="
					+ encodeURIComponent(selectedWord) + "&skip=" + skip,
			dataType : "json",
			async : true,
			success : function(returnData) {
				appendComments(returnData, skip, totCount, isPositive);
				jQuery(".inf-bw-review_type").show();
				jQuery("#commentsHeader").show();
				if (selectedSubdimension == "competition") {
					jQuery("#commentsHeader").text(
							"Reviews where customers compared it to \""
									+ selectedWord + "\"");
				} else {
					jQuery("#commentsHeader").text(
							"Showing reviews for \"" + selectedWord + "\"");
				}
				if (isPositive) {
					jQuery("#posarrow").show();
					jQuery("#negarrow").hide();
				} else {
					jQuery("#negarrow").show();
					jQuery("#posarrow").hide();
				}
			}
		});
	}
}

function appendComments(returnData, skip, totCount, isPositive) {
	var html = FragBuilder(returnData);

	if (skip == 0) {
		jQuery("#top_Comments .mCSB_container").empty();
	}
	jQuery("#top_Comments .mCSB_container").append(html);
	jQuery("#inf-bw-next").remove();
	skip = skip + 5;
	if (skip < totCount)
		jQuery('#top_Comments .mCSB_container').append(
				"<div id='inf-bw-next'><center><a><div class='blueBtn' onClick=getComments("
						+ isPositive + "," + skip + "," + totCount
						+ ")></div></a></center></div>");
	if (skip == 5) {
		jQuery("#top_Comments").mCustomScrollbar("scrollTo", "top");
	}
	highlightText();
}

function getDimensions(subDimension) {
	jQuery(".inf-bw-review_type").hide();
	jQuery("#commentsHeader").hide();
	jQuery("#top_Comments .mCSB_container").empty();
	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/StoreConnector",
		data : "productId=" + productId + "&type=dimension&subProduct="
				+ subProduct + "&entity=" + entity + "&subDimension="
				+ encodeURIComponent(subDimension),
		dataType : "json",
		async : true,
		success : function(returnData) {
			appendHtmlForDimensions(returnData);
		}
	});

}

function getTopDimension(isPositive) {
	var html = "";
	jQuery
			.ajax({
				type : "POST",
				url : "/Inferlytics/StoreConnector",
				data : "productId=" + productId
						+ "&type=topSubdimensions&subProduct=" + subProduct
						+ "&entity=" + entity + "&isPositive=" + isPositive,
				dataType : "json",
				async : true,
				success : function(returnData) {
					var thumbs="";
					if(isPositive)
						thumbs="icon-thumbs-up";
					else
						thumbs="icon-thumbs-down";
					jQuery
							.each(
									returnData.Dimensions,
									function(index, items) {
										html = html
												+ "<li class=\"category-list\" onclick=\"getCommentsForTopDimension('"
												+ items.keyWord.replace("'","\\'")
												+ "',"
												+ isPositive
												+ ",0,"
												+ items.count
												+ ")\">"
												+ items.keyWord
												+ "<span class=\"posnegCount\"><i class="+thumbs+"></i><div class=\"countData\">"
												+ items.count + "</div></span></li>";
									});
					if (isPositive) {
						jQuery("#topPosDimensions").html(html);
						jQuery('#topPosDimensions').find(".category-list")
								.first().click();
					} else {
						jQuery("#topNegDimensions").html(html);
						jQuery('#topNegDimensions').find(".category-list")
								.first().click();
					}

				}
			});
}

function getCommentsForTopDimension(word, isPositive, skip, totCount) {
	if(isPositive){
		selectedTopCatPosWord = word;
}
if(!isPositive){
	 selectedTopCatNegWord = word;
}
	jQuery
			.ajax({
				type : "POST",
				url : "/Inferlytics/StoreConnector",
				data : "productId=" + productId + "&analyticscategory=topcategories&type=comments&subProduct="
						+ subProduct + "&entity=" + entity + "&isPositive="
						+ isPositive + "&subDimension=null" + "&word="
						+ encodeURIComponent(word) + "&skip=" + skip,
				dataType : "json",
				async : true,
				success : function(returnData) {
					var html = FragBuilder(returnData);
					if (isPositive) {
						if (skip == 0) {
							jQuery("#pos_Comments .mCSB_container").empty();
						}
						jQuery("#pos_Comments .mCSB_container").append(html);
						jQuery("#inf-bw-next-for-PosComments").remove();
						skip = skip + 5;
						if (skip < totCount)
							jQuery('#pos_Comments .mCSB_container')
									.append(
											"<div id='inf-bw-next-for-PosComments' onClick='getCommentsForTopDimension(\""
													+ word
													+ "\","
													+ isPositive
													+ ","
													+ skip
													+ ","
													+ totCount
													+ ")'><center><a><div class='blueBtn'></div></a></center></div>");
						if (skip == 5) {
							jQuery("#pos_Comments").mCustomScrollbar(
									"scrollTo", "top");
						}
						highlightText();
						jQuery("#commentsHeaderForPos").text(
								"Showing reviews associated with '" + word
										+ "'");
					} else {
						if (skip == 0) {
							jQuery("#neg_Comments .mCSB_container").empty();
						}
						jQuery("#neg_Comments .mCSB_container").append(html);
						jQuery("#inf-bw-next-for-NegComments").remove();
						skip = skip + 5;
						if (skip < totCount)
							jQuery('#neg_Comments .mCSB_container')
									.append(
											"<div id='inf-bw-next-for-NegComments' onClick='getCommentsForTopDimension(\""
													+ word
													+ "\","
													+ isPositive
													+ ","
													+ skip
													+ ","
													+ totCount
													+ ")'><center><a><div class='blueBtn'></div></a></center></div>");
						if (skip == 5) {
							jQuery("#neg_Comments").mCustomScrollbar(
									"scrollTo", "top");
						}
						highlightText();
						jQuery("#commentsHeaderForNeg").text(
								"Showing reviews associated with '" + word
										+ "'");
					}

				}
			});

}
function appendHtmlForDimensions(jsonData) {
	var container = "";
	if (jsonData.KeywordsData.length != 0) {
		jQuery
				.each(
						jsonData.KeywordsData,
						function(index, items) {
							container += "<div class='inf-bw-keys_block animated rotateIn all_block catall positive_block cata negative_block catb'>"
									+ "<ul ><li class='inf-bw-keyword_block_header' posCount="
									+ items.posCount
									+ " negCount="
									+ items.negativeCount
									+ ">"
									+ "<div class='inf-bw-front'><span>"
									+ items.keyWord
									+ "</span>"
									+ "<label class='posBlock'>"
									+ items.posCount
									+ " Pos Reviews "
									+ "</label>"
									+ "<label class='negBlock'>"
									+ items.negativeCount
									+ " Neg Reviews "
									+ "</label>"
									+ "</div>"
									+ "</li></ul></div>";
						});
		jQuery("#subContainer").html(container);
		loadfeatureeffect();
		registerEventForDimension();
		jQuery("#subContainer .inf-bw-keyword_block_header").first().click();
	} else {
		jQuery("#subContainer").html(
				"No data found in this Category for this Store");
	}

}