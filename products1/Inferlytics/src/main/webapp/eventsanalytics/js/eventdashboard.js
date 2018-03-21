var selectedSubdimension = "";
var skip = 0;
var isPositiveClicked = true;
var totCount = 0;

jQuery(function() {
	getChartData();
	jQuery("#commentContainer").mCustomScrollbar({
		advanced : {
			updateOnContentResize : true
		}
	});
	$( "#idPosCommCount" ).click(function() {
		  showPosBlogs();
		});
	$( "#idnegCommCount" ).click(function() {
		  showNegBlogs();
		});
	$("#idShowMorebtn").click(function() {
		showMore();
		});
	plotChart();
	
});
function getUrlParams() {
	var vars = {};
	var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi,
			function(m, key, value) {
				vars[key] = value;
			});
	return vars;
}
function getChartData() {
	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/EventsConnector",
		data : "type=partitionData&subProduct=" + subProduct + "&entity="
				+ entity,
		dataType : "json",
		async : true,
		success : function(returnData) {

			if (returnData.KeywordsData == 0) {
				jQuery("#chartbody").html(
						"<h4><center>No data Found</center><h4>");

			} else {
				plotchart(returnData);
				$("#chartbody .node:nth-child(2)").click();
			}
		}
	});
}

function showNegBlogs(){
	skip=0;
	isPositiveClicked = false;
	getComments(false, totCount, selectedSubdimension,false);
}

function showPosBlogs(){
	skip=0;
	isPositiveClicked = true;
	getComments(true,  totCount, selectedSubdimension,false);
}
function showMore(){
	// need to get positive or negative
	getComments(isPositiveClicked,  totCount, selectedSubdimension,true);
}

function topicClick(d) {
	var subdimension = d.name;
	selectedSubdimension = d.name;
	skip=0;
	isPositiveClicked = true;
	totCount = d.size;
	$("#blogComments #idSubDimName").html(subdimension);
	$("#idPosCommCount").text(d.posCount);
	$("#idnegCommCount").text(d.negCount);
	getComments(true, totCount, selectedSubdimension,false);
	getWords(selectedSubdimension);
}
function getComments(isPositive, totCount, selectedSubdimension,isShowMore) {
	
	if(!isShowMore){
	$(".blogComment.nobg").remove();
	}
	if (totCount == 0) {
		$("#blogComments mCSB_container div:first-child").text("No blogs found");
	} else {
		jQuery
				.ajax({
					type : "POST",
					url : "/Inferlytics/EventsConnector",
					data : "&type=commentsForFeatures&subProduct=" + subProduct
							+ "&entity=" + entity + "&isPositive=" + isPositive
							+ "&subDimension="
							+ encodeURIComponent(selectedSubdimension)
							+ "&skip=" + skip,
					dataType : "json",
					async : true,
					success : function(returnData) {
						var html = FragBuilder($.parseJSON(JSON
								.stringify(returnData)));
						if(!isShowMore)
							{
						$("#commentContainer .mCSB_container .blogComment").last().before(html);
							}else{
						$("#commentContainer .mCSB_container  div:nth-last-child(2)").after(html);
							}
					}
				});
	}
	skip = skip+4;
}
function getWords(subDimension) {
	$("#idkeywords").empty();
	jQuery.ajax({
		type : "POST",
		url : "/Inferlytics/EventsConnector",
		data : "type=tagcloud&subProduct=" + subProduct + "&entity=" + entity
				+ "&subDimension=" + encodeURIComponent(subDimension),
		dataType : "json",
		async : true,
		success : function(returnData) {
			var posjson = $.parseJSON(JSON.stringify(returnData));
			$("#idkeywords").jQCloud(posjson, {
				width : '32%',
				height : 400,
				shape : 'vertical'
			});
		}
	});
}

function plotChart(){
	$.jqplot ('pointchart', [[6,7,9,8,7,6,8,7,5]], {
	        
		axesDefaults: {
	        labelRenderer: $.jqplot.CanvasAxisLabelRenderer
	      },
	         markerOptions: {
	            show: true,             // wether to show data point markers.
	            style: 'filledCircle',  // circle, diamond, square, filledCircle.
	                                    // filledDiamond or filledSquare.
	            lineWidth: 2,       // width of the stroke drawing the marker.
	            size: 9,            // size (diameter, edge length, etc.) of the marker.
	            color: '#fff'    // color of marker, set to color of line by default.
	        },
	      grid : {
				drawGridLines : false, 
				gridLineColor : '#FFF', 
				background : '#65b688',
				borderColor : '#d5d5d5', 
				gridLineWidth:0.25,
				borderWidth : 0,
				shadow : false
			},
	          axes: {
	        // options for each axis are specified in seperate option objects.
	        xaxis: {	
	        	show: true,
	           pad: 0,
	           showTicks: false,        // wether or not to show the tick labels,
	           showTickMarks: false, 
	          tickOptions: {
		       showGridline: false
		        }
	        },
	        yaxis: {
	        	showTickMarks: false,
	        	show: false,
	        	useSeriesColor:true,
	        	 pad: 1.2,
	        	tickOptions:{
	        		color:"#fff"
	        	}
	        }
	      },
	      seriesDefaults: {
	          show: true,     // wether to render the series.
	          xaxis: 'xaxis', // either 'xaxis' or 'x2axis'.
	          yaxis: 'yaxis', // either 'yaxis' or 'y2axis'.
	          color: '#FFF',
	          lineWidth:1
	      }
	    });

}