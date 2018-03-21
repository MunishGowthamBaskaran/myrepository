<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<!-- Merged JS file -->
<script src="/Inferlytics/pages/js/ecomwidget/InferlyticsWidget.js"></script>

<!-- Merged CSS file -->
<link href="/Inferlytics/pages/css/ecomwidget/mergeStyle.css"
	rel="stylesheet" type="text/css" />
<link href="/Inferlytics/pages/css/ecomwidget/testingstyle.css"
	rel="stylesheet" type="text/css" />

<link rel="stylesheet"
	href="/Inferlytics/pages/css/ecomwidget/owlcarousel/owl.carousel.css">
<link href="/Inferlytics/Analytics/_/css/fontello.css" rel="stylesheet" media="screen">

<link rel="stylesheet"
	href="/Inferlytics/pages/css/ecomwidget/owlcarousel/owl.theme.css">

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<script
	src="/Inferlytics/pages/js/ecomwidget/owlcarousel/owl.carousel.js"></script>
<script src="/Inferlytics/Analytics/js/FragBuilder.js"></script>
</head>
<body>
	<div class="inf-klwines-prd-bckground "></div>
	<img class="inf-klwines-prd-prodimg" id="inf-mb-prd-productImg" src="" />
	<div>
		<div class="inf-prd-prodname" id="inf-klwines-prd-prodname"></div>
		<div class="inf-klwines-price" id="inf-mb-prd-price"></div>
		<div class="review-tool-Widget" style="display: none;"></div>
		<div class="inf-prd-categoryKeywords"></div>

		<div id="inf-bw-klwinessimilar-block">
			Similar Wines :
				<div id="inf-bw-klwinessimilar-innerBlock" class="owl-carousel">
				<div>
					<img src="/Inferlytics/pages/images/ecomwidget/loading.gif"
						class="loaderClass">
				</div>
			</div>
		</div>

	</div>

	<script type="text/javascript">
		var prodId = "<c:out value='${param[\"productName\"]}' />";
		var keywordsHtml = "<div id='traitsHeader'></div>";
		var similarWinesSize = 0;
		var currentvalue = 0;
		var jsonDataForSimilarWines;
		if (window.location.host.indexOf("localhost") != -1)
			address = "http://" + window.location.host + "/Inferlytics";
		else
			address = "http://" + window.location.host;
		var widget = new Inferlytics.InferlyticsEventsWidget({
			//mandatory parameters
			container : "#review-tool-Widget",
			widgetType : "klwines-product-widget",
			partnerBaseUrl : address,
			brandName : 'klwines',
			productName : 'winesnew',
			productId : prodId
		});
		$
				.ajax({
					type : "POST",
					url : address + "/KlwinesConnector",
					data : "productId="
							+ prodId
							+ "&entity=klwines&subProduct=winesnew&type=similarWines",
					dataType : "json",
					success : function(data) {

						similarWinesSize = data.length;
						if (similarWinesSize == 0) {
							$("#inf-bw-klwinessimilar-innerBlock")
									.html(
											"<div class='msg'>No matches were found for this wine.</div>");
						} else {
							jsonDataForSimilarWines = JSON.parse(JSON
									.stringify(data));
							currentvalue = 10;
							var slicedData = data.slice(0, 10);
							$("#inf-bw-klwinessimilar-innerBlock").html(
									FragBuilder(slicedData));
							if(currentvalue<similarWinesSize){
								$("#inf-bw-klwinessimilar-innerBlock").append(
										"<div class='showMoreSimilarProduct' onclick='onClickShowMore()'><i class='icon-up-4'></i><div>Show More</div></div>");
								}
							$("#inf-bw-klwinessimilar-innerBlock").owlCarousel(
									{
										items : 2,
										pagination : false,
										navigation : true
									});
						}
					}
				});

		$.ajax({
			type : "POST",
			url : address + "/KlwinesConnector",
			data : "productId=" + prodId
					+ "&entity=klwines&subProduct=winesnew&type=keyWords",
			dataType : "json",
			success : function(data) {
				$.each(data, function(i, value) {
					keywordsHtml += '<div class="inf-prd-categoryKeyword">';
					keywordsHtml += '<div class="inf-prd-category">'
							+ value.feature + '</div>';
					$.each(value.words, function(k, word) {
						keywordsHtml += '<div class="inf-prd-word">'
								+ word.name + '(' + word.count + ')' + '</div>'
					});
					keywordsHtml += "</div>";
				});

				$(".inf-prd-categoryKeywords").html(keywordsHtml);

			}

		});

		function onClickShowMore() {
			
			
				var jsonToAttach = JSON.parse(JSON
						.stringify(jsonDataForSimilarWines.slice(0,
								currentvalue+10)));
				$("#inf-bw-klwinessimilar-innerBlock").html(
						FragBuilder(jsonToAttach));
				currentvalue = currentvalue + 10;
				if(currentvalue<similarWinesSize){
					$("#inf-bw-klwinessimilar-innerBlock").append(
					"<div class='showMoreSimilarProduct' onclick='onClickShowMore()'><i class='icon-up-4'></i><div>Show More</div></div>");
				}
				$("#inf-bw-klwinessimilar-innerBlock").data('owlCarousel')
						.reinit({
							items : 2,
							pagination : false,
							navigation : true
						});
				$("#inf-bw-klwinessimilar-innerBlock").data('owlCarousel').goTo(currentvalue-10);
				
				
			
		}
		$("#inf-mb-prd-negkeyWords").hide();
	</script>
</body>
</html>