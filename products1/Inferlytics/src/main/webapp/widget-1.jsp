<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html >
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<!-- Merged JS file -->
<script src="/Inferlytics/pages/js/ecomwidget/InferlyticsWidget.js"></script>

<!-- Merged CSS file -->
<link href="/Inferlytics/pages/css/ecomwidget/mergeStyle.css"	rel="stylesheet" type="text/css" />
<link href="/Inferlytics/pages/css/ecomwidget/testingstyle.css"	rel="stylesheet" type="text/css" />



</head>
<body style="margin: 0 auto;">
 
<div class="inf-bw-page" style ="background-position: top center;" ></div>
<div class="review-tool-Widget">
</div>

 <script type="text/javascript">
    var entity = "<c:out value='${param[\"entity\"]}' />";
	var subProduct = "<c:out value='${param[\"subProduct\"]}' />";
    var category= "<c:out value='${param[\"category\"]}' />";
	if(window.location.host.indexOf("localhost") != -1)
		address="http://" + window.location.host+ "/Inferlytics";
		else
		address="http://" + window.location.host;
    var basicWidget= 	new Inferlytics.InferlyticsEventsWidget({
         //mandatory parameters
         container: "#review-tool-Widget",
         widgetType: "category-related-widget",
         partnerBaseUrl: address,
		 brandName: entity,//'moltonbrown',
		 productName:subProduct,//'fragrance',
		 category:category
     });
</script> 

</body>
</html>
