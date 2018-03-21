$.noConflict();
var productId = "";
var selectedSubdimension = "";
var selectedWord = "";
var negBlogsCount=0;
var posBlogsCount=0;
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
	getPieChartData();

	$(".inf-bw-prod_positiveComments").click(function(){
		if(posBlogsCount!=0){
		var options = {
				currentPage : 1,
				totalPages :posBlogsCount,
				onPageClicked : function(e, originalEvent, type, page) {
					getComments(true, (page-1),posBlogsCount);
				}
			};
		jQuery('#pagination_blog').show();
		jQuery('#pagination_blog').bootstrapPaginator(options);
		getComments(true, 0, posBlogsCount);
		
		}else{
			$("#blog_text").text("No blogs found");
			jQuery('#pagination_blog').hide();
			$("#blog_data .mCSB_container").empty();
		}
	});
	$(".inf-bw-prod_negativeComments").click(function(){
		if(negBlogsCount!=0){
		var options = {
				currentPage : 1,
				totalPages :posBlogsCount,
				onPageClicked : function(e, originalEvent, type, page) {
					getComments(false, (page-1),negBlogsCount);
				}
			};
		jQuery('#pagination_blog').bootstrapPaginator(options);
		getComments(false, 0, negBlogsCount);
		jQuery('#pagination_blog').show();
		}else{
			$("#blog_text").text("No blogs found");
			jQuery('#pagination_blog').hide();
			$("#blog_data .mCSB_container").empty();
			}
	});
});

function highlightText(scno) {
	setTimeout(function() {
		var sentenceToHighlight = scno-1;
		if (sentenceToHighlight != "-1") {
			jQuery("#blog_data").find('em:eq(' + sentenceToHighlight + ')').css(
					'backgroundColor', '#ff6');
		}
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


function getPieChartData() {
	var html = "";
	var i = 0;
	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/EventsConnector",
		data : "type=pieChartData&subProduct="
				+ subProduct + "&entity=" + entity ,
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
				posBlogsCount = parseInt(jQuery(this).attr("posCount"));
				negBlogsCount=parseInt(jQuery(this).attr("negCount"));
				jQuery("#inf-bw-positiveCountno").text(
						jQuery(this).attr("posCount"));
				jQuery("#inf-bw-negativeCountno").text(
						jQuery(this).attr("negCount"));
				selectedWord = jQuery(this).children(":first").children(
						":first").text();
				jQuery(".inf-bw-metro-color-selected").removeClass(
						"inf-bw-metro-color-selected");
				jQuery(this).parent().addClass("inf-bw-metro-color-selected");
				var options = {
						currentPage : 1,
						totalPages :posBlogsCount,
						onPageClicked : function(e, originalEvent, type, page) {
							getComments(true, (page-1),posBlogsCount);
						}
					};
				jQuery('#pagination_blog').bootstrapPaginator(options);
				$(".inf-bw-prod_positiveComments").text(posBlogsCount+" Positive Blog(s)");
				$(".inf-bw-prod_negativeComments").text(negBlogsCount+" Negative Blog(s)");
				getComments(true, 0, posBlogsCount);
				jQuery('#pagination_blog').show();
			});

}

function getComments(isPositive, number,totCount) {
	if (totCount == 0) {
		$("#blog_text").text("No blogs found");
		$("#blog_data .mCSB_container").empty();
	} else {
		$("#blog_text").text("");
		jQuery.ajax({
			type : "POST",
			url : "/Inferlytics/EventsConnector",
			data :  "&type=comments&subProduct="
					+ subProduct + "&entity=" + entity + "&isPositive="
					+ isPositive + "&subDimension="
					+ encodeURIComponent(selectedSubdimension) + "&word="
					+ encodeURIComponent(selectedWord) + "&skip=" + number,
			dataType : "json",
			async : true,
			success : function(returnData) {				
				$("#blog_text").html("Title:"+returnData.Title);
				$("#blog_data .mCSB_container").html(FragBuilder(returnData.BlogContent));
				highlightText(returnData.SentenceNo);
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
		url : "/Inferlytics/EventsConnector",
		data : "type=dimension&subProduct="
				+ subProduct + "&entity=" + entity + "&subDimension="
				+ encodeURIComponent(subDimension),
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
			updateOnContentResize : true
		}
	});
	
	jQuery("#blog_data").mCustomScrollbar({
		advanced : {
			updateOnContentResize : true
		}
	});
	
	jQuery(".dimensions .tag").click(function() {
		jQuery(".selectedtag").removeClass("selectedtag");
		jQuery(this).addClass("selectedtag");
		selectedSubdimension = jQuery(this).text();
	});




}