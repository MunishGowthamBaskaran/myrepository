$.noConflict();
var productId = "";
var selectedSubdimension = "";
var selectedWord = "";
var analysisTabActive = false;
function getUrlParams() {
	var vars = {};
	var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi,
			function(m, key, value) {
				vars[key] = value;
			});
	return vars;
}

/* When the Document is ready register events and load data */

jQuery(function() {
	registerEvents();
});

function highlightText() {
	jQuery.each(jQuery('#top_Comments .inf-bw-review'), function(index, item) {

		var sentenceToHighlight = jQuery(this).attr('scno');
		if (sentenceToHighlight != "-1") {
			jQuery(this).find('em:eq(' + sentenceToHighlight + ')').css(
					'backgroundColor', '#ff6');
		}
	});
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

function getPieChartData() {
	var appendHtml = "";
	var i = 0;
	jQuery
			.ajax({
				type : "POST",
				url : "/Inferlytics/MapViewConnector",
				data : "type=pieChartData&subProduct=" + subProduct
						+ "&entity=" + entity + "&stateCode=" + selectedState
						+ "&store=" + selectedStore,
				dataType : "json",
				async : true,
				success : function(returnData) {

					if (returnData.KeywordsData == 0) {
						jQuery("#pieChartSlider").html(
								"<h4><center>No data Found</center><h4>");
						jQuery("#subContainer").html(
								"<h4><center>No data Found</center><h4>");
						jQuery("#top_Comments .mCSB_container").html(
								"<h4><center>No data Found</center><h4>");
						$(".nexts").hide();
						$(".prevs").hide();
					} else {
						
							for(var i in returnData.KeywordsData){
								if(returnData.KeywordsData[i].keyWord==="customer service"){
									appendHtml += "<div class='categoriesDiv'>"
										+ returnData.KeywordsData[i].keyWord+ "</div>";
									returnData.KeywordsData.splice(i, 1);
									break;
								}
							}
							for(var i in returnData.KeywordsData){
								if(returnData.KeywordsData[i].keyWord==="vehicle condition"){
									appendHtml += "<div class='categoriesDiv'>"
										+ returnData.KeywordsData[i].keyWord+ "</div>";
									returnData.KeywordsData.splice(i, 1);
									break;
								}
							}
							
							for(var i in returnData.KeywordsData){
								if(returnData.KeywordsData[i].keyWord==="competition"){
									var dataReturned=returnData.KeywordsData.splice(i, 1);
									returnData.KeywordsData.push(dataReturned[0]);
									break;
								}
							}
						
						
						jQuery.each(returnData.KeywordsData, function(index,
								items) {
							appendHtml += "<div class='categoriesDiv'>"
									+ items.keyWord + "</div>";
							i = index;
						});
						
						
						jQuery("#categoriesContainer .mCSB_container").html(
								appendHtml);
						jQuery(".categoriesDiv").click(
								function() {
									jQuery(".selectedCategory").removeClass(
											"selectedCategory");
									jQuery(this).addClass("selectedCategory");
									selectedSubdimension = jQuery(this).text();
									getDimensions(selectedSubdimension);
								});
						jQuery(".categoriesDiv").first().click();
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
					"Reviews where customers compared it to \"" + selectedWord
							+ "\"");
		} else {
			jQuery("#commentsHeader").text(
					"Showing reviews for \"" + selectedWord + "\"");
		}
	} else {
		jQuery.ajax({
			type : "POST",
			url : "/Inferlytics/MapViewConnector",
			data : "type=comments&subProduct=" + subProduct + "&entity="
					+ entity + "&isPositive=" + isPositive + "&subDimension="
					+ encodeURIComponent(selectedSubdimension) + "&word="
					+ encodeURIComponent(selectedWord) + "&skip=" + skip
					+ "&stateCode=" + selectedState + "&store=" + selectedStore,
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
		url : "/Inferlytics/MapViewConnector",
		data : "type=dimension&subProduct=" + subProduct + "&entity=" + entity
				+ "&subDimension=" + encodeURIComponent(subDimension)
				+ "&stateCode=" + selectedState + "&store=" + selectedStore,
		dataType : "json",
		async : true,
		success : function(returnData) {
			appendHtmlForDimensions(returnData);
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

function registerEvents() {

	jQuery(".inf-bw-data_container1").mCustomScrollbar({
		advanced : {
			updateOnContentResize : true,
			autoHideScrollbar : true
		}
	});
	jQuery("#categoriesContainer").mCustomScrollbar({
		advanced : {
			updateOnContentResize : true
		}
	});

	jQuery("#dashboardButton").click(function() {
		jQuery("#analysisTab").hide();
		jQuery("#trendsTab").hide();
		jQuery("#mapTab").fadeIn(800);
		jQuery("#headerAnalytics").hide();
		jQuery(".selectedWidget").text("");
		analysisTabActive = false;
	});

	jQuery("#analysisLink").click(function() {

		jQuery("#headerAnalytics").show();
		jQuery("#trendsTab").hide();
		jQuery("#analysisTab").fadeIn(800);
		jQuery("#mapTab").hide();
		jQuery(".selectedWidget").text("> Topic Analysis");
		getPieChartData();
		analysisTabActive = true;
	});
	jQuery("#trendsLink").click(function() {
		jQuery("#headerAnalytics").show();
		jQuery("#analysisTab").hide();
		jQuery("#trendsTab").fadeIn(800);
		jQuery("#mapTab").hide();
		jQuery(".selectedWidget").text("> Trends");
		getChartDataForTimeLine();
	});
	jQuery('#statesoptions')
			.change(
					function() {
						var optionSelected = $(this).find("option:selected");
						selectedState = optionSelected.val();
						selectedStore = "all";
						for ( var i in stateScoreMap) {
							if (stateScoreMap[i].id === selectedState) {
								if (stateScoreMap[i].avgScore > 0) {
									jQuery("#overallScore2").text(
											"+"
													+ stateScoreMap[i].avgScore
															.toFixed(2));
								} else {
									jQuery("#overallScore2").text(
											"-"
													+ stateScoreMap[i].avgScore
															.toFixed(2));
								}

								// loop through the store names and add it as a
								// dropdown list.
								var htmlStoreNames = "<option id='all' value='all'>ALL</option>";
								for ( var j in stateScoreMap[i].storenames) {
									var nameProductId = stateScoreMap[i].storenames[j]
											.split("|");
									htmlStoreNames += "<option id='"
											+ nameProductId[1] + "' value='"
											+ nameProductId[1] + "'>"
											+ nameProductId[0] + "</option>";

								}
								jQuery("#storeoptions").html(htmlStoreNames);
							}
						}
						if (analysisTabActive) {
							getPieChartData();
						}
					});
	jQuery('#storeoptions').change(function() {
		var optionSelected = $(this).find("option:selected");
		selectedStore = optionSelected.val();
		if (analysisTabActive) {
		getPieChartData();
		}	
	});
	jQuery("#top_Comments").mCustomScrollbar({
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
	});

	jQuery(".inf-bw-positive_review").click(function() {
		getComments(true, 0, parseInt(jQuery(this).text()));
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
				} else {
					jQuery(this).find('.inf-bw-review_comment').addClass(
							'expand');
					jQuery(this).find('.inf-bw-review_comment').animate({
						'height' : '108px'
					});
				}
			});

}

function setDatesFordatePicker() {
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
}

function getChartDataForTimeLine() {
	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/GetChartDataForMap",
		data : "&subProduct=" + subProduct + "&entity=" + entity + "&stateCode="
				+ selectedState + "&fromDate="
				+ jQuery('#fromDatepicker').val() + "&toDate="
				+ jQuery('#toDatepicker').val() + "&chartType=timeLine&store="
				+ selectedStore,
		dataType : "json",
		async : true,
		success : function(returnData) {

			plotForTimeLine(returnData);

		}
	});

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

function submitClicked() {
	getChartDataForTimeLine();
}