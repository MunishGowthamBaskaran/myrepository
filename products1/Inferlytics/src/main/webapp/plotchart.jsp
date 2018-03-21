<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Rare Mile Sentiment Analysis</title>
<link href="pages/css/main.css" rel="stylesheet" type="text/css" />
<link href="pages/css/custom.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="pages/css/jqcloud.css" />
<script type="text/javascript" src="pages/js/jquery.min.js"></script>

<script type="text/javascript" src="pages/js/jqcloud-1.0.3.min.js"></script>
<script src="pages/js/highcharts.js"></script>
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
</style>
<script type="text/javascript">
	$(document).ready(
			function() {
				
				Highcharts.setOptions({
					colors: ['#a1c21f', '#30c771', '#15f50a', '#FF9655', '#eb7563', '#f00e0e', '#000000']
				});
				var json = '${json2}';
				//alert(json);
				var categories = '${categories}';
				var series = '${series}';
				var features = '${featuresList}';
				//alert(jj);
				drawContainer(json);
				drawcontainer1(categories,series);
				drawfeatureCloud(features);
				drawDonut();

			});

	function drawContainer(json) {
	
		json = $.parseJSON(json);
		var pts = [];
		$.each(json, function(i, v) {
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
								text : 'Trip Advisor Sentiment Analysis'
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
				text : 'Source: Raremile Sentimental Analysis',
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
	
	function drawfeatureCloud(json){
		json = $.parseJSON(json);
		$("#featuresTweet").jQCloud(json);
	}
	
	function drawDonut() {
	    
            var colors = Highcharts.getOptions().colors,
            categories = ['MSIE', 'Firefox', 'Chrome', 'Safari', 'Opera'],
            name = 'Sentiment On Dimensions',
            data = [{
                    y: 55.11,
                    color: colors[0],
                    drilldown: {
                        name: 'MSIE versions',
                        categories: ['MSIE 6.0', 'MSIE 7.0', 'MSIE 8.0', 'MSIE 9.0'],
                        data: [10.85, 7.35, 33.06, 2.81],
                        color: colors[0]
                    }
                }, {
                    y: 21.63,
                    color: colors[1],
                    drilldown: {
                        name: 'Firefox versions',
                        categories: ['Firefox 2.0', 'Firefox 3.0', 'Firefox 3.5', 'Firefox 3.6', 'Firefox 4.0'],
                        data: [0.20, 0.83, 1.58, 13.12, 5.43],
                        color: colors[1]
                    }
                }, {
                    y: 11.94,
                    color: colors[2],
                    drilldown: {
                        name: 'Chrome versions',
                        categories: ['Chrome 5.0', 'Chrome 6.0', 'Chrome 7.0', 'Chrome 8.0', 'Chrome 9.0',
                            'Chrome 10.0', 'Chrome 11.0', 'Chrome 12.0'],
                        data: [0.12, 0.19, 0.12, 0.36, 0.32, 9.91, 0.50, 0.22],
                        color: colors[2]
                    }
                }, {
                    y: 7.15,
                    color: colors[3],
                    drilldown: {
                        name: 'Safari versions',
                        categories: ['Safari 5.0', 'Safari 4.0', 'Safari Win 5.0', 'Safari 4.1', 'Safari/Maxthon',
                            'Safari 3.1', 'Safari 4.1'],
                        data: [4.55, 1.42, 0.23, 0.21, 0.20, 0.19, 0.14],
                        color: colors[3]
                    }
                }, {
                    y: 2.14,
                    color: colors[4],
                    drilldown: {
                        name: 'Opera versions',
                        categories: ['Opera 9.x', 'Opera 10.x', 'Opera 11.x'],
                        data: [ 0.12, 0.37, 1.65],
                        color: colors[4]
                    }
                }];
    
    
        // Build the data arrays
        var browserData = [];
        var versionsData = [];
        for (var i = 0; i < data.length; i++) {
    
            // add browser data
            browserData.push({
                name: categories[i],
                y: data[i].y,
                color: data[i].color
            });
    
            // add version data
            for (var j = 0; j < data[i].drilldown.data.length; j++) {
                var brightness = 0.2 - (j / data[i].drilldown.data.length) / 5 ;
                versionsData.push({
                    name: data[i].drilldown.categories[j],
                    y: data[i].drilldown.data[j],
                    color: Highcharts.Color(data[i].color).brighten(brightness).get()
                });
            }
        }
    
        // Create the chart
        $('#donutcontainer').highcharts({
            chart: {
                type: 'pie'
            },
            title: {
                text: 'Browser market share, April, 2011'
            },
            yAxis: {
                title: {
                    text: 'Total percent market share'
                }
            },
            plotOptions: {
                pie: {
                    shadow: false,
                    center: ['50%', '50%']
                }
            },
            tooltip: {
        	    valueSuffix: '%'
            },
            credits : {
				enabled : false
			},
            series: [{
                name: 'Browsers',
                data: browserData,
                size: '60%',
                dataLabels: {
                    formatter: function() {
                        return this.y > 5 ? this.point.name : null;
                    },
                    color: 'white',
                    distance: -30
                }
            }, {
                name: 'Versions',
                data: versionsData,
                size: '80%',
                innerSize: '60%',
                dataLabels: {
                    formatter: function() {
                        // display only if larger than 1
                        return this.y > 1 ? '<b>'+ this.point.name +':</b> '+ this.y +'%'  : null;
                    }
                }
            }]
        });
    }
</script>
</head>
<body>
<div class="wrapper">
	<br>


	<div id="container"
		style="min-width: 400px; height: 400px; margin: 0 auto"></div>
	<br>
	<br>
	<br>
	<br/>
	<br/>
	<div id="donutcontainer" style="min-width: 400px; height: 400px; margin: 0 auto"></div>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	
	<br>
	
	<br>
	<br>
	<br>
	<br>
	<!-- <div id="container2" style="min-width: 400px; height: 400px; margin: 0 auto"></div> -->
	<center>
	<div id="featuresTweet" style="width: 600px; height: 550px; border: 1px solid #ccc;"></div>
	</center>
	
	<%@include file="feeds.jsp"%>
	
</div>
</body>
</html>