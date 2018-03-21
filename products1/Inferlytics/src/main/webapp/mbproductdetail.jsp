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
<script src="/Inferlytics/pages/js/ecomwidget/ui/jquery.ui.mouse.js"></script>
<script src="/Inferlytics/pages/js/ecomwidget/jquery.mousewheel.min.js"></script>
<script src="/Inferlytics/pages/js/productdetail/productdetail.js"></script>
<link
	href='http://fonts.googleapis.com/css?family=Open+Sans:400,700,600'
	rel='stylesheet' type='text/css' />
<link href="/Inferlytics/pages/css/productdetail/productdetail.css"
	rel="stylesheet" type="text/css" />
<link href="/Inferlytics/pages/css/productdetail/mbproductdetail.css"
	rel="stylesheet" type="text/css" />
	<script src="/Inferlytics/pages/js/ecomwidget/FragBuilder.js"></script>
	<script src="/Inferlytics/pages/js/ecomwidget/highlight.js"></script>
<link
	href="/Inferlytics/pages/css/ecomwidget/jquery.mCustomScrollbar.css"
	rel="stylesheet" type="text/css" />
<script type="text/javascript">
	var prodId = "<c:out value='${param[\"productName\"]}' />";
	var entity = "<c:out value='${param[\"entity\"]}' />";
	var subProduct = "<c:out value='${param[\"subProduct\"]}' />";
	$(function() {
		getKeywords(prodId);

	});
</script>



</head>

<body>

	<div class="topImage"></div>
	<div class="productImg">
		<img id="productImg">
	</div>
	<div class="prodnameimg"></div>






	<div class="inferlytics-keywords">
		<!-- 
						<div class="inferlytics-Overlay" id="inferlytics-posoverlay"> 
							<div class="inferlytics-closebutton"></div>
							<div class="inferlytics-goback"></div>
							<div class="inferlytics-comments-header">Showing user reviews associated with keyword</div>
						<div class="review_container">
						<div class="review_panel">
							<div class="review positivereview" id="LB206:11" subdim="body parts" word="hands" style="height: auto; opacity: 1;">
								<div class="review_comment expand" style="height: 54px;">
									<p><em style="background-color: rgb(255, 255, 102);">Lovely, soft, smooth hands.. </em><em style="background-color: rgb(255, 255, 102);">This hand cream goes on very well - just a little is needed - and is easily absorbed into the skin, leaving hands soft and supple. </em><em style="background-color: rgb(255, 255, 102);">It doesn't leave hands feeling greasy which is a huge bonus and has a gentle scent, so my husband uses it too as it's not too girly. </em><em style="background-color: rgb(255, 255, 102);">This will keep your hands soft and chap-free, but if you need to address really dried out or chapped skin bear in mind it is (as the label states) a lotion and not a cream. </em><em style="background-color: rgb(255, 255, 102);">I keep mine beside the bed and after a night time application I wake with lovely soft hands. </em><em>Fully recommend :)</em></p></div></div></div>
						
							<div class="review_panel">
							<div class="review positivereview" id="LB206:11" subdim="body parts" word="hands" style="height: auto; opacity: 1;">
								<div class="review_comment expand" style="height: 54px;">
									<p><em style="background-color: rgb(255, 255, 102);">Lovely, soft, smooth hands.. </em><em style="background-color: rgb(255, 255, 102);">This hand cream goes on very well - just a little is needed - and is easily absorbed into the skin, leaving hands soft and supple. </em><em style="background-color: rgb(255, 255, 102);">It doesn't leave hands feeling greasy which is a huge bonus and has a gentle scent, so my husband uses it too as it's not too girly. </em><em style="background-color: rgb(255, 255, 102);">This will keep your hands soft and chap-free, but if you need to address really dried out or chapped skin bear in mind it is (as the label states) a lotion and not a cream. </em><em style="background-color: rgb(255, 255, 102);">I keep mine beside the bed and after a night time application I wake with lovely soft hands. </em><em>Fully recommend :)</em></p></div></div></div>
										<div class="review_panel">
							<div class="review positivereview" id="LB206:11" subdim="body parts" word="hands" style="height: auto; opacity: 1;">
								<div class="review_comment expand" style="height: 54px;">
									<p><em style="background-color: rgb(255, 255, 102);">Lovely, soft, smooth hands.. </em><em style="background-color: rgb(255, 255, 102);">This hand cream goes on very well - just a little is needed - and is easily absorbed into the skin, leaving hands soft and supple. </em><em style="background-color: rgb(255, 255, 102);">It doesn't leave hands feeling greasy which is a huge bonus and has a gentle scent, so my husband uses it too as it's not too girly. </em><em style="background-color: rgb(255, 255, 102);">This will keep your hands soft and chap-free, but if you need to address really dried out or chapped skin bear in mind it is (as the label states) a lotion and not a cream. </em><em style="background-color: rgb(255, 255, 102);">I keep mine beside the bed and after a night time application I wake with lovely soft hands. </em><em>Fully recommend :)</em></p></div></div></div>
						</div>
						
					</div> -->
		<div class="inferlytics-posthumb"></div>
		<div id="poskeyWords" class="inferlytics-poskeywords"></div>
		<div class="inferlytics-Overlay posoverlay"
			id="inferlytics-posoverlay">
				<div class="inferlytics-closebutton"></div>
							<div class="inferlytics-goback"></div>
							<div class="inferlytics-comments-header">Showing user reviews associated with keyword</div>
			<div class="review_container" id="posreview_container"></div>
		</div>

		<div class="inferlytics-showmoreKeywords posShowmore"
			id="inferlytics-posShowmore"></div>
	</div>


	<div class="inferlytics-keywords">
		<div class="inferlytics-Overlay negoverlay"
			id="inferlytics-negoverlay">
				<div class="inferlytics-closebutton"></div>
							<div class="inferlytics-goback"></div>
							<div class="inferlytics-comments-header">Showing user reviews associated with keyword</div>
			<div class="review_container" id="negreview_container"></div>
		</div>
		<div class="inferlytics-negthumb"></div>
		<div id="negkeyWords" class="inferlytics-negkeywords"></div>
		<div class="inferlytics-showmoreKeywords negShowmore"
			id="inferlytics-negShowmore"></div>
	</div>



	<div class="proddetails"></div>
	<div class="prodsuggestion"></div>
	<div class="bottomimg"></div>
</body>
</html>