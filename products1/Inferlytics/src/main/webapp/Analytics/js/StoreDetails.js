$.noConflict();
var sequence;
var sequence2;

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
					case "scNo":
						s.setAttribute("scNo", u);
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

function sendtoLoginPage() {
	jQuery(location).attr('href', "login.html");
}

function getChartDataForTimeLine() {

	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/GetChartDataForStore",
		data : "&subProduct=" + subProduct + "&entity=" + entity
				+ "&productId=" + productId + "&fromDate="
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

function addProductDetails(jsonData) {

	jQuery("#inf-storename").html(jsonData.productName);
	jQuery("#inf-address-text").html(jsonData.address);
	jQuery("#inf-prodImage").attr("src", jsonData.imageUrl);

}

function makeAjaxCall() {
	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/StoreConnector",
		async : true,
		data : "productId=" + productId + "&type=productDetails",
		dataType : "json",
		success : function(returnData) {
			addProductDetails(returnData);
		}
	});
}


function getDataForEmployee() {
	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/StoreConnector",
		data : "productId=" + productId + "&type=named employees&subProduct="
				+ subProduct + "&entity=" + entity
				+ "&subDimension=named employees",
		dataType : "json",
		success : function(returnData) {
			 appendHtmlForEmployee(returnData);
		}
	});

}

function appendHtmlForEmployee(jsonData) {
	var keyWordsHtml = FragBuilder(jsonData.KeywordsData);
	jQuery("#posEmployees").html(keyWordsHtml);
	jQuery("#posEmployees .category-list:first-child .posnegCount").click();
}


function submitClicked() {
	getChartDataForTimeLine();
}

/* This function plots the graph on to the Canvas */

function plotForTimeLine(jsond) {
	jQuery("#charts").empty();
	jQuery
			.jqplot(
					'charts',
					[ jsond.PositiveData, jsond.NegativeData ],
					{
						title : 'Incoming review Analysis',
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
								label : 'No of Clicks',
								tickInterval:1
							}
						},

						cursor : {
							show : true
						}
					});

}

/*
 * Keeps track of the particular time i.e selected at the moment day,week or
 * month
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
}

// Call on document ready

jQuery(function() {

	registerEvents();
	/* Set the date range in the input field */
	var date = new Date();
	date.setDate(date.getDate() - 7);

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
	getChartDataForTimeLine();
	getDataForCompetitors();
	getDataForDepartments();
	makeAjaxCall("top_Products", 1, true);
	getDataForShopping();
	getDataForEmployee();

});

function initiateTwitterSlider() {

	var optionsForTwitter = {
		autoPlay : false,
		nextButton : true,
		prevButton : true,
		preloader : true,
		navigationSkip : true,
		animateStartingFrameIn : false,
		autoPlayDelay : 3000,
		pauseOnHover : false,
		transitionThreshold : 200
	};
	try {
		sequence = $("#sequence-twitter").sequence(optionsForTwitter).data(
				"sequence");
		sequence.afterLoaded = function() {
			$(".sequence-prev, .sequence-next").fadeIn(100);
		}
		sequence3 = $("#sequence-twitter_3").sequence(optionsForTwitter).data(
				"sequence");
		sequence3.afterLoaded = function() {
			$(".sequence-prev, .sequence-next").fadeIn(100);
		}
		sequence4 = $("#sequence-twitter_4").sequence(optionsForTwitter).data(
				"sequence");
		sequence4.afterLoaded = function() {
			$(".sequence-prev, .sequence-next").fadeIn(100);
		}
		sequence5 = $("#sequence-twitter_5").sequence(optionsForTwitter).data(
				"sequence");
		sequence5.afterLoaded = function() {
			$(".sequence-prev, .sequence-next").fadeIn(100);
		}

	} catch (err) {
		alert(err);
	}

}

function getDataForShopping() {
	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/StoreConnector",
		data : "productId=" + productId
				+ "&type=shoppingExperience&subProduct=" + subProduct
				+ "&entity=" + entity + "&subDimension=purchase experience",
		dataType : "json",
		success : function(returnData) {
			appendHtmlForShoppingExperience(returnData);
		}
	});

}

function appendHtmlForShoppingExperience(jsonData) {
	var keyWordsHtml = FragBuilder(jsonData.KeywordsData);
	jQuery("#posPercentage").html(jsonData.PosPercentage);
	jQuery("#posDataPercentage")
			.attr("data-percentage", jsonData.PosPercentage);
	jQuery("#negPercentage").html(jsonData.NegPercentage);
	jQuery("#negDataPercentage")
			.attr("data-percentage", jsonData.NegPercentage);
	jQuery("#shoppingExperienceDimensions").html(keyWordsHtml);
	jQuery("#shoppingExperienceDimensions .category-list:first-child .posnegCount").click();
}

function getDataForCompetitors() {
	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/StoreConnector",
		data : "productId=" + productId + "&type=competitors&subProduct="
				+ subProduct + "&entity=" + entity
				+ "&subDimension=competitors",
		dataType : "json",
		success : function(returnData) {
			appendHtmlForCompetitors(returnData);
		}
	});

}

function appendHtmlForCompetitors(jsonData) {
	var keyWordsHtml = FragBuilder(jsonData.KeywordsData);
	jQuery("#competitorsDimensions").html(keyWordsHtml);
	jQuery("#competitorsDimensions .category-list:first-child .posnegCount").click();
}

function getDataForDepartments() {
	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/StoreConnector",
		data : "productId=" + productId + "&type=departments&subProduct="
				+ subProduct + "&entity=" + entity
				+ "&subDimension=departments",
		dataType : "json",
		success : function(returnData) {
			appendHtmlForDepartments(returnData);
		}
	});

}

function appendHtmlForDepartments(jsonData) {
	var keyWordsHtml = FragBuilder(jsonData.KeywordsData);
	jQuery("#departmentsDimensions").html(keyWordsHtml);
	jQuery("#departmentsDimensions .category-list:first-child .posnegCount").click();
	
}

function getComments(word, isPositive, subDimension, category) {
	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/StoreConnector",
		data : "productId=" + productId + "&type=comments&subProduct="
				+ subProduct + "&entity=" + entity + "&isPositive="
				+ isPositive + "&subDimension=" + subDimension + "&word="
				+ encodeURIComponent(word),
		dataType : "json",
		async : false,
		success : function(returnData) {
			appendComments(returnData, word, subDimension, category);
		}
	});
}

function appendComments(jsonData, word, subDimension, category) {
	switch (category) {
	case "named employees":
		appendHtmlForEmployeeComments(jsonData,  word);
		break;
	case "shoppingExperience":
		appendCommentsForShopping(jsonData, word);
		break;

	case "competitors":
		appendCommentsForCompetitors(jsonData, word);
		break;

	case "departments":
		appendCommentsForDepartment(jsonData, word);
		break;
	default:
		break;

	}
}

function appendHtmlForEmployeeComments(jsonData,  employeeName) {

	var toHtml = FragBuilder(jsonData);
	
		jQuery("#sequence-positive").html(toHtml);
		jQuery("#pos-header").html("Showing Reviews For " + employeeName);
	
	initiateTwitterSlider();
	highlightText("sequence-twitter");

}

function appendCommentsForShopping(jsonData, word) {
	var toHtml = FragBuilder(jsonData);
	jQuery("#sequence-shopping").html(toHtml);
	jQuery("#shopping-header").html("Showing Reviews For " + word);
	initiateTwitterSlider();
	highlightText("sequence-twitter_3");
}

function appendCommentsForCompetitors(jsonData, word) {
	var toHtml = FragBuilder(jsonData);
	jQuery("#sequence-competitors").html(toHtml);
	jQuery("#competitors-header").html("Showing Reviews For " + word);
	initiateTwitterSlider();
	highlightText("sequence-twitter_4");
}

function appendCommentsForDepartment(jsonData, word) {
	var toHtml = FragBuilder(jsonData);
	jQuery("#sequence-departments").html(toHtml);
	jQuery("#departments-header").html("Showing Reviews For " + word);
	initiateTwitterSlider();
	highlightText("sequence-twitter_5");

}

function highlightText(id) {
	var idName = id;
	setTimeout(function() {
		var classofText = "#" + idName + " .sequence-canvas .animate-in";
		var sentenceToHighlight = $(classofText).attr("scNo");
		jQuery(classofText).find('em:eq(' + sentenceToHighlight + ')').css(
				'backgroundColor', '#ff6');
	}, 300);
}
