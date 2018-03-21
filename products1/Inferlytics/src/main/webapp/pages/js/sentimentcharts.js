$(function () {
	Highcharts.setOptions({
	     colors: ['#eb7563', '#000000', '#30c771', '#f00e0e', '#15f50a', '#FF9655', '#a1c21f']
	    });
		var pts = [];
		var url = document.URL;
        url = url + '?handle=PlotChart';
        url = url + '&brand='+${brand}+"&method=individual";
		 $.ajax({
			  url: url,
			  dataType: 'json',
			  async: false,
			  success: function(json) {
				  $.each(json, function(i,v){
					    pts.push([v.name, parseInt(v.data)]);
					});
				  
				  $('#container').highcharts({
			            chart: {
			                plotBackgroundColor: null,
			                plotBorderWidth: null,
			                plotShadow: false
			            },
			            title: {
			                text: 'Twitter Feed Sentiment Analysis'
			            },
			            tooltip: {
			        	    pointFormat: '{series.name}: <b>{point.percentage}%</b>',
			            	percentageDecimals: 1,
							enabled: true
			            },
						credits: {
							enabled: false
			            },
			            plotOptions: {
			                pie: {
			                    allowPointSelect: true,
			                    cursor: 'pointer',
			                    dataLabels: {
			                        enabled: true,
			                        color: '#000000',
			                        connectorColor: '#000000',
			                        formatter: function() {
			                            return '<b>'+ this.point.name +'</b>: '+ this.percentage +' %';
			                        }
			                    }
			                }
			            },
			            series: [{
			                type: 'pie',
			                name: 'Total',
			                data: pts
			            }]
			        });
			  }
			});
		 
		 var category = [];
		 var url1 = document.URL;
	        url1 = url1 + '?handle=PlotChart';
	        url1 = url1 + '&brand='+${brand}+"&method=collective";
	        $.ajax({
				  url: url1,
				  dataType: 'json',
				  async: false,
				  success: function(json) {
					  $.each(json.categories, function(i,v){
						  category.push(v);
						});
					  
					  $('#container2').highcharts({
						  chart: {
				                type: 'bar'
				            },
				            title: {
				                text: 'Average tweets analysis over few days'
				            },
				            subtitle: {
				                text: 'Source: Raremile Sentimental Analysis'
				            },
				            xAxis: {
				                categories: json.categories,
				                title: {
				                    text: null
				                }
				            },
				            yAxis: {
				                min: 0,
				                title: {
				                    text: 'Total count',
				                    align: 'high'
				                },
				                labels: {
				                    overflow: 'justify'
				                }
				            },
				            tooltip: {
				                valuePrefix: 'Total '
				            },
				            plotOptions: {
				                bar: {
				                    dataLabels: {
				                        enabled: true
				                    }
				                }
				            },
				            legend: {
				                layout: 'vertical',
				                align: 'right',
				                verticalAlign: 'top',
				                x: -100,
				                y: 100,
				                floating: true,
				                borderWidth: 1,
				                backgroundColor: '#FFFFFF',
				                shadow: true
				            },
				            credits: {
				                enabled: false
				            },
				            series: json.series
				        });
					  
					  $('#container1').highcharts({
				            chart: {
				                type: 'line',
				                marginRight: 130,
				                marginBottom: 25
				            },
				            title: {
				                text: 'Average tweets analysis over few days',
				                x: -20 //center
				            },
				            subtitle: {
				                text: 'Source: Raremile Sentimental Analysis',
				                x: -20
				            },
				            xAxis: {
				                categories: json.categories
				            },
				            credits: {
				                enabled: false
				            },
				            yAxis: {
				                title: {
				                    text: 'Count'
				                },
				                plotLines: [{
				                    value: 0,
				                    width: 1,
				                    color: '#808080'
				                }]
				            },
				            tooltip: {
				                valuePrefix: 'Total '
				            },
				            legend: {
				                layout: 'vertical',
				                align: 'right',
				                verticalAlign: 'top',
				                x: -10,
				                y: 100,
				                borderWidth: 0
				            },
				            series: json.series
				        });
				  }
			  });
    });
    
