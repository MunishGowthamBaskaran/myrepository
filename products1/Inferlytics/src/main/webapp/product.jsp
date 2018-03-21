<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>Ratings</title>
<script src="http://code.jquery.com/jquery-2.0.3.min.js"></script>
<script src="http://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
<script
	src="/Inferlytics/pages/js/ecomwidget/jquery.mCustomScrollbar.min.js"></script>
<link
	href="/Inferlytics/pages/css/ecomwidget/jquery.mCustomScrollbar.css"
	rel="stylesheet" type="text/css" />
<link
	href='http://fonts.googleapis.com/css?family=Open+Sans:400,700,600'
	rel='stylesheet' type='text/css' />
<script src="/Inferlytics/pages/js/ecomwidget/ui/jquery.ui.mouse.js"></script>
<script src="/Inferlytics/pages/js/ecomwidget/jquery.mousewheel.min.js"></script>
<script src="/Inferlytics/pages/js/ecomwidget/highlight.js"></script>

<link href="/Inferlytics/pages/css/styleProduct.css" rel="stylesheet"
	type="text/css" />
<script src="/Inferlytics/pages/js/ecomwidget/FragBuilder.js"></script>
<script type="text/javascript"
	src="/Inferlytics/pages/js/productspage.js"></script>

<script type="text/javascript">
	var prodId = "<c:out value='${param[\"productName\"]}' />";
	var entity = "<c:out value='${param[\"entity\"]}' />";
	var subProduct = "<c:out value='${param[\"subProduct\"]}' />";
	$(function() {
		$(".prodImage").hide();

		getDetails(prodId);

	});
</script>
<style>
</style>
</head>

<body>
<div id="negoverlay">
			<div class="closeOverlay">
				<a href="#"></a>
			</div>
			<div class="header">Comments</div>
			<div class="review_container" id="negreview_container"></div>
		</div>
	<div id="posoverlay">
			<div class="closeOverlay">
				<a href="#"></a>
			</div>
			<div class="header">Comments</div>
			<div class="review_container" id="posreview_container"></div>
	</div>
	
	
	<div id="productName"></div>
	<div id="poskeyWords" class="keywords">
	<div id="poskeywordsOverlay">
	
	</div>

	</div>
	<div id="negkeyWords" class="keywords">
		<div id="negkeywordsOverlay">
	
	</div>
	</div>
	<img id="loader" align="middle"
		src="/Inferlytics/pages/images/loading.gif" />
	<div class="prodImage">
		<img id="productImg" src="" />
	</div>



</body>

</html>
