<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<title>Insert title here</title>
<link href="pages/css/main.css" rel="stylesheet" type="text/css" />
<link href="pages/css/custom.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="pages/js/jquery.min.js"></script>
<link rel="stylesheet" type="text/css" href="pages/css/jqcloud.css" />
<script type="text/javascript" src="pages/js/jqcloud-1.0.3.min.js"></script>
<script src="pages/js/highcharts.js"></script>
<script type="text/javascript" src="pages/js/d3/d3.js"></script>
<script type="text/javascript" src="pages/js/easyMark.js"></script>
<script type="text/javascript" src="pages/js/d3/d3.layout.js"></script>

<style type="text/css">
	div.jqcloud span.positive {
        /* -webkit-writing-mode: vertical-rl;
        writing-mode: tb-rl; */
        color: green;
      }
      
      div.jqcloud span.negative {
        /* -webkit-writing-mode: vertical-rl;
        writing-mode: tb-rl; */
        color: red;
      }
      .chart {
display: block;
margin: auto;
margin-top: 60px;
font-size: 13px;
}

#chartbody {
	float: left;
}

.partitionwrapper {
	overflow: scroll;
}

rect {
	stroke: #eee;
	fill-opacity: .8;
}

rect.child {
	cursor: pointer;
}

rect.parent {
	cursor: pointer;
	fill: steelblue;
}

text {
	pointer-events: none;
}

.topComments {
	float: right;
	width: 350px;
	clear: none;
	display:none;
}

.highlight{
padding: 1px 2px;
background-color: #fff;
font-weight: bold;
}
</style>
<script type="text/javascript">
    var json = '${json}';
	$(document).ready(
			function() {
				
				Highcharts.setOptions({
					colors: ['#a1c21f', '#30c771', '#15f50a', '#FF9655', '#eb7563', '#f00e0e', '#FFE4C4']
				});
				var json2 = '${json2}';
				var json = '${json}';
				var entityProdMap = '${jsonEntityProdMap}';
				var dd = $('#entities');
				var ddproducts = $('#products');
				var categories = '${categories}';
				var series = '${series}';
				var posfeatures = '${posfeaturesList}';
				var negfeatures = '${negfeaturesList}';

				dd.append($('<option selected="selected"/>').text("--Select Brands--"));
				ddproducts.append($('<option selected="selected"/>').text("--Select Products--"));
				//var entities = document.getElementById("entities");
				
				opts=$.parseJSON(entityProdMap);
				
			    // $('>option', dd).remove(); // Clear old options first.
			     if (opts) {
			         $.each(opts, function(entityID, value) {
			             dd.append($('<option/>').val(entityID).text(entityID));
			         });
			     } 
			    drawContainer(json2);
				drawcontainer1(categories,series);
				drawfeatureCloud(posfeatures,negfeatures);
			
	});

	function drawContainer(json2) {
	
		json2 = $.parseJSON(json2);
		var pts = [];
		$.each(json2, function(i, v) {
			pts.push([ v.name, parseInt(v.data) ]);
		});

		$('#container')
				.highcharts(
						{
							chart : {
								plotBackgroundColor : null,
								plotBorderWidth : null,
								plotShadow : false
							},
							title : {
								text : 'Sentiment Analysis'
							},
							tooltip : {
								pointFormat : '{series.name}: <b>{point.percentage}%</b>',
								percentageDecimals : 1,
								enabled : true
							},
							credits : {
								enabled : false
							},
							plotOptions : {
								pie : {
									allowPointSelect : true,
									cursor : 'pointer',
									dataLabels : {
										enabled : true,
										color : '#000000',
										connectorColor : '#000000',
										formatter : function() {
											return '<b>' + this.point.name
													+ '</b>: '
													+ this.percentage + ' %';
										}
									}
								}
							},
							series : [ {
								type : 'pie',
								name : 'Total',
								data : pts
							} ]
						});
	}

	function drawcontainer1(categories,series) {
		var category = [];
		
		categories = $.parseJSON(categories);
		series = $.parseJSON(series);
		$.each(categories, function(i, v) {
			category.push(v);
		});

		$('#container1').highcharts({
			chart : {
				type : 'line',
				marginRight : 130,
				marginBottom : 25
			},
			title : {
				text : 'Average feeds analysis over few years',
				x : -20
			//center
			},
			subtitle : {
				text : 'Source: Rare Mile Sentimental Analysis',
				x : -20
			},
			xAxis : {
				categories : categories
			},
			credits : {
				enabled : false
			},
			yAxis : {
				title : {
					text : 'Count'
				},
				plotLines : [ {
					value : 0,
					width : 1,
					color : '#808080'
				} ]
			},
			tooltip : {
				valuePrefix : 'Total '
			},
			legend : {
				layout : 'vertical',
				align : 'right',
				verticalAlign : 'top',
				x : -10,
				y : 100,
				borderWidth : 0
			},
			series : series
		});
	}
	
	function drawfeatureCloud(posjson,negjson){
		
		posjson = $.parseJSON(posjson);
		negjson = $.parseJSON(negjson);
		
		 $("#posfeaturesTweet").jQCloud(posjson);
		$("#negfeaturesTweet").jQCloud(negjson);
	}

	  function populateProducts(entityID) {
	      var dd = $('#products');
	      
	    	var entityProdMap = '${jsonEntityProdMap}';
	       opts=$.parseJSON(entityProdMap);
	           $('>option', dd).remove(); // Clear old options first.
	           dd.append($('<option selected="selected"/>').text("--Select Products--"));
	            if (opts) {
	                $.each(opts, function(key, value) {
	                    if(entityID == key){
							var listToPopulate = opts[key];
							$.each(listToPopulate, function(index, prodValue) {
								dd.append($('<option/>').val(prodValue).text(prodValue));
								});
							}
	                 });
	            }     	
	    }
</script>
</head>
<body>
<div class="wrapper">
		<form  action="PlotChart" method="get" class="mainForm" name="chartForm" id="chartForm">
			<fieldset>
				<div class="widget first">
					<div class="head">
						<h5 class="iList">RM Sentiment Analysis</h5>
					</div>
					<div class="rowElem noborder" >
						<div style="float: left; width:10%" >Brand:</div>
						<div  style="float: left;  width:18%">
					     <select class="chzn-select" name="entities" id="entities"  onchange="populateProducts(this.value)">
        				</select>
						</div>
						<div style="float: left; width:10%">Product:</div>
						<div style="float: left;  width:18%" >
						<select class="chzn-select" name="products"   id="products">
						</select>
						</div>
						<div style="float: left;  width:10%"><input type="submit" name="submit" class="greyishBtn submitForm" onclick="" /></div>
					<div class="fix"></div>
					</div>
				</div>
			</fieldset>
		</form>
</div>
<div >
<div class="wrapper">
	<br>
    <div id="container"
		style="min-width: 400px; height: 400px; margin: 0 auto"></div>
	<br>
	<br>
	<br>
	<br>
	<div id="container1"
		style="min-width: 400px; height: auto; margin: 0 auto"></div>
	<br>
	<br>
	<br>
	<br>
<c:if test="${json2 !=null || json2=='' }">
	<div id="container2" style="min-width: 400px; height: 400px; margin: 0 auto"><div style="width:100%;float: left;">
<div  class="widget first">
					<div class="head">
						<h5>Dimension & SubDimension Of Products</h5>
						</div>
						</div>


<div  id="chartbody" style="width:60% ;margin-top: -20px; padding-bottom: 25px">
        <script type="text/javascript" src="pages/js/partition.js"></script>
        <div class="fix"></div>
</div>
<div class="widget topComments" style="width:25%;">
			<div class="head">
				<h5 class="iHelp">Top Comments</h5>
				<div class="num"><a  id="totalCount" class="blueNum"></a></div>
			</div>

			<div class="supTicket nobg">
				<ul>
					<li id="firstComment"></li>
				</ul>
			</div>

			<div class="supTicket">
				<ul>
					<li id="secondComment"></li>

				</ul>
			</div>
			<div class="supTicket">
				<ul>
					<li id="thirdComment"></li>

				</ul>
			</div>
			<div class="supTicket">
				<ul>
					<li id="fourthComment"></li>

				</ul>
			</div>
			<div class="supTicket">
				<ul>
					<li id="fifthComment"></li>

				</ul>
			</div>
			<div class="supTicket">
				<ul>
					<li id="sixthComment"></li>

				</ul>
			</div>
			
		</div>
</div>
</div>
</c:if>


<c:if test="${posfeaturesList !=null || posfeaturesList=='' }">
	<div  class="widget first">
					<div class="head">
						<h5>Positive & Negative Tag Cloud</h5>
						</div>
						</div>
	<div id="posfeaturesTweet" style="width: 46%; height: 550px; border: 1px solid #ccc;float:left; margin:20px 10px 20px 0px ;"></div>
	<div id="negfeaturesTweet" style="width:  46%; height: 550px; border: 1px solid #ccc;float:right;margin:20px 0px 20px 10px;"></div>
<div class="fix"></div>
</c:if>
	
	<br/>
	<br/>
	<%@include file="feeds.jsp"%>
</div>
</div>
</body>
</html>
	


