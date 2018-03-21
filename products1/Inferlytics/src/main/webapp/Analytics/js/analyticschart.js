$.noConflict();

var FragBuilder = (function() {
	var e = function(e, t) {
		for ( var n in t) {
			e.style[n] = t[n]
		}
	};
	var t = function(e, t) {
		for ( var n in t) {
			e.onclick = n
		}
	};
	var n = function(t) {
		var r = document.createDocumentFragment();
		for ( var i in t) {
			if (!("tagName" in t[i]) && "textContent" in t[i]) {
				r.appendChild(document.createTextNode(t[i]["textContent"]))
			} else if ("tagName" in t[i]) {
				var s = document.createElement(t[i].tagName);
				delete t[i].tagName;
				for ( var o in t[i]) {
					var u = t[i][o];
					switch (o) {
					case "textContent":
						s.appendChild(document.createTextNode(u));
						break;
					case "style":
						e(s, u);
						break;
					case "click":
						s.setAttribute("onclick", u);
						break;
					case "childNodes":
						s.appendChild(n(u));
						break;
					case "src":
						s.src = u;
						break;
					case "href":
						s.setAttribute("href", u);
						s.setAttribute("target", "_blank");
						break;
					case "id":
						s.setAttribute("id", u);
						break;
					case "subDim":
						s.setAttribute("subDim", u);
						break;
					case "word":
						s.setAttribute("word", u);
						break;
					default:
						if (o in s) {
							s[o] = u
						}
						break
					}
				}
				r.appendChild(s)
			} else {
				throw "Error: Malformed JSON Fragment"
			}
		}
		return r
	};
	var r = function(e) {
		var t = document.createElement("div"), n = document
				.createDocumentFragment();
		t.innerHTML = e;
		while (t.hasChildNodes()) {
			n.appendChild(t.firstChild)
		}
		return n
	};
	return function(e) {
		if (typeof e === "string") {
			return r(e)
		} else {
			return n(e)
		}
	}
}());

function fragBuilder(json, idname, pagination) {

	if (pagination) {
		var pageCount;
		jQuery.each(json, function(index, items) {

			if (items.tagName == "Count") {
				pageCount = items.count;

			}

		});

		var options = "";

		if (idname == "top_Products") {
			options = {
				currentPage : 1,
				totalPages : pageCount,
				onPageClicked : function(e, originalEvent, type, page) {
					makeAjaxCall("top_Products", page, false);
				}
			}

		} else {
			options = {
				currentPage : 1,
				totalPages : pageCount,
				onPageClicked : function(e, originalEvent, type, page) {
					makeAjaxCall("top_Comments", page, false);
				}
			}
		}
		jQuery('#' + idname + '_Pagination').bootstrapPaginator(options);
	}
	var toHtml = FragBuilder(json);
	if (idname == "top_Products") {
		jQuery("#" + idname).html(toHtml);
	} else {
		jQuery(".topComments .mCSB_container").html(toHtml);
	}
}

function fragBuilderForRecentComments(json, idname, pagination) {

	if (pagination) {

		var pageCount;
		jQuery.each(json, function(index, items) {

			if (items.tagName == "Count") {
				pageCount = items.count;

			}
			if (items.tagName == "Message") {
				if (items.isShowMessage) {
					if (idname == "recent_positive_reviews") {
						jQuery("#pos_recent_message").show();
					} else {
						jQuery("#neg_recent_message").show();
					}
				} else {

					if (idname == "recent_positive_reviews") {
						jQuery("#pos_recent_message").hide();
					} else {
						jQuery("#neg_recent_message").hide();

					}

				}
			}
		});

		var options = "";

		if (idname == "recent_positive_reviews") {
			options = {
				currentPage : 1,
				totalPages : pageCount,
				onPageClicked : function(e, originalEvent, type, page) {
					getRecentComments((page - 1), "true",
							jQuery('#pos-recentComments-count option:selected')
									.text(), "recent_positive_reviews", false);

				}
			}

		} else {
			options = {
				currentPage : 1,
				totalPages : pageCount,
				onPageClicked : function(e, originalEvent, type, page) {
					getRecentComments((page - 1), "false",
							jQuery('#neg-recentComments-count option:selected')
									.text(), "recent_negative_reviews", false);

				}
			}

		}

		jQuery('#' + idname + '_Pagination').bootstrapPaginator(options);
	}
	var toHtml = FragBuilder(json);

	if (idname == "recent_positive_reviews") {
		jQuery(".recent_positive_reviews_class .mCSB_container").html(toHtml);
	} else {
		jQuery(".recent_negative_reviews_class .mCSB_container").html(toHtml);
	}

}

function sendtoLoginPage() {
	jQuery(location).attr('href', "login.html");
}

function getChartData() {
	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/GetChartData",
		data : "&time=" + selectedTime + "&subProduct=" + subProduct
				+ "&entity=" + entity + "&userId=" + userId + "&fromDate="
				+ jQuery('#fromDatepicker').val() + "&toDate="
				+ jQuery('#toDatepicker').val(),

		dataType : "json",
		success : function(returnData) {
			if (returnData.Fail) {
				sendtoLoginPage();
			} else {
				plot(returnData);
			}
		}
	});
}

function getChartDataForTimeLine() {

	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/GetChartData",
		data : "&time=" + selectedTime + "&subProduct=" + subProduct
				+ "&entity=" + entity + "&userId=" + userId + "&fromDate="
				+ jQuery('#fromDatepicker').val() + "&toDate="
				+ jQuery('#toDatepicker').val() + "&chartType=timeLine",

		dataType : "json",
		success : function(returnData) {
			if (returnData.Fail) {
				sendtoLoginPage();
			} else {
				plotForTimeLine(returnData);
			}
		}
	});

}

function makeAjaxCall(optionName, pageno, pagination) {
	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/AnalyserConnector",
		async : true,
		data : "&subProduct=" + subProduct + "&entity=" + entity + "&userId="
				+ userId + "&fromDate=" + jQuery('#fromDatepicker').val()
				+ "&toDate=" + jQuery('#toDatepicker').val() + "&optionName="
				+ optionName + "&pageno=" + pageno,
		dataType : "json",
		success : function(returnData) {
			if (returnData.Fail) {
				sendtoLoginPage();
			} else {
				fragBuilder(returnData, optionName, pagination);
			}
		}
	});

}

function getDataforTraitsComparison() {

	var returnValue = "";
	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/AnalyserConnector",
		async : false,
		beforeSend: function() {
			jQuery("#loader2").show();
		  },
		data : "&subProduct=" + subProduct + "&entity=" + entity + "&userId="
				+ userId + "&fromDate1=" + jQuery('#fromDatepicker1').val()
				+ "&toDate1=" + jQuery('#toDatepicker1').val() + "&fromDate2="
				+ jQuery('#fromDatepicker2').val() + "&toDate2="
				+ jQuery('#toDatepicker2').val() + "&optionName=compareTraits",
		dataType : "json",
		success : function(returnData) {
			jQuery("#loader2").hide();
			if (returnData.Fail) {
				sendtoLoginPage();
			} else {
				jQuery("#positive_trait_change").html(FragBuilder(returnData.PosTraitsJson));
				jQuery("#negative_trait_change").html(FragBuilder(returnData.NegTraitsJson));
			}
		}
	});
	return returnValue;

}

function makeAjaxCallForAnalyticsData(optionName, pageno) {
	var returnValue = "";
	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/AnalyserConnector",
		async : false,
		data : "&subProduct=" + subProduct + "&entity=" + entity + "&userId="
				+ userId + "&fromDate=" + jQuery('#fromDatepicker').val()
				+ "&toDate=" + jQuery('#toDatepicker').val() + "&optionName="
				+ optionName + "&pageno=" + pageno,
		dataType : "json",
		success : function(returnData) {
			if (returnData.Fail) {
				sendtoLoginPage();
			} else {
				returnValue = returnData;
			}
		}
	});
	return returnValue;
}

function getRecentComments(pageno, isPositive, limit, idName, pagination) {
	var returnValue = "";
	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/AnalyserConnector",
		async : true,
		data : "&subProduct=" + subProduct + "&entity=" + entity + "&userId="
				+ userId + "&fromDate=" + jQuery('#fromDatepicker').val()
				+ "&toDate=" + jQuery('#toDatepicker').val()
				+ "&optionName=recentComments" + "&pageno=" + pageno
				+ "&limit=" + limit + "&isPositive=" + isPositive,
		dataType : "json",
		success : function(returnData) {
			if (returnData.Fail) {
				sendtoLoginPage();
			} else {
				fragBuilderForRecentComments(returnData, idName, pagination);
			}
		}
	});
	return returnValue;
}

function getTopTraitsForTimeLine() {
	var returnValue = "";
	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/AnalyserConnector",
		data : "&subProduct=" + subProduct + "&entity=" + entity + "&userId="
				+ userId + "&fromDate=" + jQuery('#fromDatepicker').val()
				+ "&toDate=" + jQuery('#toDatepicker').val()
				+ "&optionName=topTraits",
		dataType : "json",
		success : function(returnData) {
			jQuery("#loader1").hide();
			if (returnData.Fail) {
				sendtoLoginPage();
			} else {
				jsontoHtmlForTopTraits(returnData);
			}
		}
	});
	return returnValue;
}

function jsontoHtmlForTopTraits(jsonData) {
	var htmlData = "";

	jQuery.each(jsonData.PosTraits, function(index, data) {
		htmlData += "<tr><td>" + (index + 1) + "</td><td>" + data.keyWord
				+ "</td><td>" + data.count + "</td></tr>";
	});

	jQuery("#top_positive_traits").html(htmlData);

	htmlData = "";
	jQuery.each(jsonData.NegTraits, function(index, data) {
		htmlData += "<tr><td>" + (index + 1) + "</td><td>" + data.keyWord
				+ "</td><td>" + data.count + "</td></tr>";

	});
	jQuery("#top_negative_traits").html(htmlData);

}
function submitClicked() {
	jQuery("#loader1").show();
	getChartData();
	getChartDataForTimeLine();
	jsontoHtml(makeAjaxCallForAnalyticsData("topWords", 0));
	makeAjaxCall("top_Products", 1, true);
	makeAjaxCall("top_Comments", 1, true);
	getRecentComments(0, "true", jQuery(
			'#pos-recentComments-count option:selected').text(),
			"recent_positive_reviews", true);
	getRecentComments(0, "false", jQuery(
			'#neg-recentComments-count option:selected').text(),
			"recent_negative_reviews", true);
	getTopTraitsForTimeLine();
}

function logout() {
	jQuery("form:first").submit();
}
function jsontoHtml(jsonData) {
	var htmlData = "";
	jQuery
			.each(
					jsonData,
					function(index, item) {
						if (item.type === "subDimension") {
							jQuery.each(item.items, function(index, data) {
								htmlData += "<tr><td>" + (index + 1)
										+ "</td><td>" + data.subDimension
										+ "</td><td>" + data.Count
										+ "</td></tr>";

							});

							jQuery("#top_subdimensions").html(htmlData);

						} else if (item.type === "words") {
							htmlData = "";
							jQuery.each(item.items, function(index, data) {
								htmlData += "<tr><td>" + (index + 1)
										+ "</td><td>" + data.word + "</td><td>"
										+ data.Count + "</td></tr>";
							});
							jQuery("#top_words").html(htmlData);
						} else if (item.type === "users") {
							jQuery(".dt-counter").html(item.Count);
							dtCounter();

						} else {
							if (item.TotalCount > 0) {
								var posPercentage = ((item.PosReviewCount)
										/ (item.TotalCount) * 100).toFixed(0);
								var negPercentage = ((item.NegReviewCount)
										/ (item.TotalCount) * 100).toFixed(0);

								jQuery(".doughnutChart").remove();
								var htmlposData = '<canvas class="doughnutChart" data-percent='
										+ posPercentage
										+ ' width="260" height="260" style="width: 260px; height: 260px;"  ></canvas>';
								var htmlnegData = '<canvas class="doughnutChart" data-percent='
										+ negPercentage
										+ ' width="260" height="260" style="width: 260px; height: 260px;"  ></canvas>';
								jQuery("#pos_review_Count")
										.prepend(htmlposData);
								jQuery("#posPercentage").html("POSITIVE REVIEW CLICKS ("+item.PosReviewCount+")");
								jQuery("#neg_review_Count")
										.prepend(htmlnegData);
								jQuery("#negPercentage").html("NEGATIVE REVIEW CLICKS ("+item.NegReviewCount+")");
								initDoughnutProgressBar2();
							}
						}
					});
}

/* This function plots the graph on to the Canvas */
function plot(jsond) {
	jQuery("#charts").empty();
	jQuery
			.jqplot(
					'charts',
					[ jsond ],
					{
						title : 'Inferlytics Analytics Chart',
						seriesColors : [ "#1abc9c" ],
						seriesDefaults : {
							lineWidth : 4

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
						axes : {
							xaxis : {
								renderer : jQuery.jqplot.DateAxisRenderer,
								tickOptions : {
									formatString : '%d&nbsp%b&nbsp,%y'
								},
								label : 'Date',
								labelRenderer : jQuery.jqplot.CanvasAxisLabelRenderer

							},
							yaxis : {
								min : 0,
								tickOptions : {
									formatString : '%d'
								},
								label : 'No of Clicks'
							}

						},
						highlighter : {
							show : true,
							sizeAdjust : 7.5,
							tooltipAxes : 'both',
							formatString : '<table class="jqplot-highlighter"><tr><td>Date:</td><td>%s</td></tr>'
									+ '<tr><td>Clicks:</td><td> %d</td></tr></table>',
						},
						cursor : {
							show : true
						}
					});
}

function plotForTimeLine(jsond) {
	jQuery("#timeLineCharts").empty();
	jQuery
			.jqplot(
					'timeLineCharts',
					[ jsond.PositiveData, jsond.NegativeData ],
					{
						title : 'Incoming Review Analysis By Time',
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
								label : 'No of Reviews'
							}

						},
						highlighter : {
							show : true,
						},
						cursor : {
							show : true
						}
					});

}

var selectedTime = "day";
/*
 * Keeps track of the particular time i.e selected at the moment (day,week or
 * month)
 */

function getUrlParams() {
	var vars = {};
	var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi,
			function(m, key, value) {
				vars[key] = value;
			});
	return vars;
}

/* When the Document is ready register events and load data */

function registerEvents() {
	jQuery(".topComments").mCustomScrollbar({
		advanced : {
			updateOnContentResize : true
		}
	});

	jQuery(".recent_positive_reviews_class").mCustomScrollbar({
		advanced : {
			updateOnContentResize : true
		}
	});

	jQuery(".recent_negative_reviews_class").mCustomScrollbar({
		advanced : {
			updateOnContentResize : true
		}
	});

	jQuery('#pos-recentComments-count').on(
			'change',
			function() {
				getRecentComments(0, "true", jQuery(
						'#pos-recentComments-count option:selected').text(),
						"recent_positive_reviews", true);
			});

	jQuery('#neg-recentComments-count').on(
			'change',
			function() {
				getRecentComments(0, "false", jQuery(
						'#neg-recentComments-count option:selected').text(),
						"recent_negative_reviews", true);

			});

	jQuery('#top_Comments').on(
			"click",
			".inf-bw-review",
			function() {
				if (jQuery(this).find('.inf-bw-review_comment').hasClass(
						'expand')) {
					var h = jQuery(this).find('.inf-bw-review_comment p')
							.height();
					if ((h + 30) > 108) {
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

	jQuery('#recent_positive_reviews').on(
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

	jQuery('#recent_negative_reviews').on(
			"click",
			".inf-bw-review",
			function() {
				if (jQuery(this).find('.inf-bw-review_comment').hasClass(
						'expand')) {
					var h = jQuery(this).find('.inf-bw-review_comment p')
							.height();
					if ((h + 30) > 108) {
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

	/* Register an event to changet the time i.e date , day or week */
	jQuery("._GAbd").click(function() {
		jQuery("._GAbd").removeClass("_GAl");
		jQuery(this).addClass("_GAl");
		selectedTime = jQuery(this).text().toLowerCase()
		getChartData();
	});

}

// Call on document ready

jQuery(function() {

	registerEvents();
	/* Set the date range in the input field */

	intializeDatePickers("#fromDatepicker1", "#toDatepicker1");
	intializeDatePickers("#fromDatepicker", "#toDatepicker");
	intializeDatePickers("#fromDatepicker2", "#toDatepicker2");
	getChartData(); /* Gets the chart Data and makes a plot of it */
	getChartDataForTimeLine();
	jsontoHtml(makeAjaxCallForAnalyticsData("topWords", 0));
	makeAjaxCall("top_Products", 1, true);
	makeAjaxCall("top_Comments", 1, true);
	getRecentComments(0, "true", 5, "recent_positive_reviews", true);
	getRecentComments(0, "false", 5, "recent_negative_reviews", true);
	getTopTraitsForTimeLine();
	

});

function intializeDatePickers(id1, id2) {
	var date = new Date();
	date.setDate(date.getDate() - 7);

	var newFormattedDate = ("0" + (date.getMonth() + 1).toString()).substr(-2)
			+ "/" + ("0" + date.getDate().toString()).substr(-2) + "/"
			+ (date.getFullYear().toString()).substr(2);

	jQuery(id1).datepicker({
		onClose : function(selectedDate) {
			jQuery(id2).datepicker("option", "minDate", selectedDate);
		}
	});
	jQuery(id2).datepicker({
		onClose : function(selectedDate) {
			jQuery(id1).datepicker("option", "maxDate", selectedDate);
		}
	});
	jQuery(id1).datepicker("setDate", newFormattedDate);
	jQuery(id2).datepicker("setDate", new Date());
	jQuery(id1).datepicker("option", "maxDate", new Date());
	jQuery(id2).datepicker("option", "maxDate", new Date());
}

function compareTraits() {
	
	
	getDataforTraitsComparison();
}