<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<!-- Merged JS file -->
<script src="/Inferlytics/pages/js/ecomwidget/InferlyticsWidget.js"></script>

<!-- Merged CSS file -->
<link href="/Inferlytics/pages/css/ecomwidget/mergeStyle.css"	rel="stylesheet" type="text/css" />
<link href="/Inferlytics/pages/css/ecomwidget/testingstyle.css"	rel="stylesheet" type="text/css" />
</head>
<body>

	<div class="inf-nike-prd-topImage"></div>
	<div class="inf-nike-prd-leftImage"></div>
	<img class="inf-nike-prd-prodimg" id="inf-mb-prd-productImg" src="" />
	<div>
		<div class="inf-prd-prodname" id="inf-nike-prd-prodname"></div>
		<div class="inf-nike-prd-rating"></div>
		<div class="inf-nike-price" id="inf-mb-prd-price"></div>
		<div class="review-tool-Widget"></div>
		<div class="inf-nike-prd-widgetRightImage"></div>

	</div>

	<div class="inf-nike-prd-rightImage"></div>
	<div class="inf-nike-prd-bottomimg"></div> 
   
 <script type="text/javascript">
		var prodId = "<c:out value='${param[\"productName\"]}' />";
		
		if(window.location.host.indexOf("localhost") != -1)
			address="http://" + window.location.host+ "/Inferlytics";
			else
			address="http://" + window.location.host;
		var widget = new Inferlytics.InferlyticsEventsWidget({
			//mandatory parameters
			container : "#review-tool-Widget",
			widgetType : "nike-product-widget",
			partnerBaseUrl : address,
			brandName : 'nike',
			productName : 'menshoes',
			productId : prodId
		});
	</script>


</body>
</html>