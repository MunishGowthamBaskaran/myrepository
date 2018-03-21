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
<link href="/Inferlytics/pages/css/ecomwidget/mergeStyle.css"	rel="stylesheet" type="text/css" />
<link href="/Inferlytics/pages/css/ecomwidget/testingstyle.css"	rel="stylesheet" type="text/css" />
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
	<script src="/Inferlytics/Analytics/js/FragBuilder.js"></script>
</head>
<body>
  <div class="inf-oldnavy-prd-bckground "></div>
	<img class="inf-oldnavy-prd-prodimg" id="inf-mb-prd-productImg" src="" />
	<div>
		<div class="inf-prd-prodname" id="inf-klwines-prd-prodname"></div>
		<div class="inf-oldnavy-price" id="inf-mb-prd-price"></div>
		<div class="review-tool-Widget"></div>
		
	

	</div>
  
 <script type="text/javascript">
 		var prodId = "<c:out value='${param[\"productName\"]}' />";
		if(window.location.host.indexOf("localhost") != -1)
			address="http://" + window.location.host+ "/Inferlytics";
			else
			address="http://" + window.location.host;
		var widget = new Inferlytics.InferlyticsEventsWidget({
			//mandatory parameters
			container : "#review-tool-Widget",
			widgetType : "oldnavy-product-widget",
			partnerBaseUrl : address,
			brandName : 'oldnavy',
			productName : 'womendress',
			productId : prodId
		});
		
	</script>
</body>
</html>