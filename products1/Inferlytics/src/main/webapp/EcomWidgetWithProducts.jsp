<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Ratings</title>
<link href="/Inferlytics/pages/css/ecomwidget/style.css"
	rel="stylesheet" type="text/css" />
<link
	href="/Inferlytics/pages/css/ecomwidget/jquery.mCustomScrollbar.css"
	rel="stylesheet" type="text/css" />
<link href="/Inferlytics/pages/css/ecomwidget/animate.min.css"
	rel="stylesheet" type="text/css" />

<link
	href='http://fonts.googleapis.com/css?family=Open+Sans:400,700,600'
	rel='stylesheet' type='text/css' />
<script src="/Inferlytics/pages/js/ecomwidget/FragBuilder.js"></script>
<!--  <script src="pages/js/ecomwidget/jquery-2.0.3.js"></script>  -->

<script src="http://code.jquery.com/jquery-2.0.3.min.js"></script>
<script src="http://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>

<script src="/Inferlytics/pages/js/ecomwidget/ui/jquery.ui.core.js"></script>
<script src="/Inferlytics/pages/js/ecomwidget/ui/jquery.ui.widget.js"></script>
<script src="/Inferlytics/pages/js/ecomwidget/ui/jquery.ui.tabs.js"></script>
<script src="/Inferlytics/pages/js/ecomwidget/ui/jquery.ui.mouse.js"></script>
<script src="/Inferlytics/pages/js/ecomwidget/ui/jquery.ui.resizable.js"></script>
<script src="/Inferlytics/pages/js/ecomwidget/jquery.mousewheel.min.js"></script>
<script
	src="/Inferlytics/pages/js/ecomwidget/jquery.mCustomScrollbar.min.js"></script>

<!-- <script src="/pages/js/ecomwidget/masonry.pkgd.min.js"></script> -->
<script src="/Inferlytics/pages/js/ecomwidget/transition/jquery.js"></script>
<script src="/Inferlytics/pages/js/ecomwidget/highlight.js"></script>
<script src="/Inferlytics/pages/js/ecomwidget/transition/cssanimate.js"></script>



<script>
var val=1;
var featureJSON="<c:out value='${featureList}' />";
featureJSON=featureJSON.replace(/&#034;/g,'\"');
featureJSON=JSON.parse(featureJSON);
var CHROME = (navigator.userAgent.toString().toLowerCase().indexOf("chrome") != -1);
var widgetTop;
	$(function() {
		
		totalwidth = $(document).width() - 405;
		
		$("#tabs").tabs({
			activate : function(event, ui) {
				addFeatures();
				loadfeatureeffect();
				setkeywordwidth();
			}
		  
		});
		
		

		$("#resizable").resizable({
			handles : 'nw',
			minWidth : 500,
			minHeight : 400,
			maxWidth : totalwidth,
			containment : "body",
			aspectRatio: false,
			//alsoResize: "#info_panel"
			resize : function(event, ui) {
				
				if(CHROME){
				var top= ui.position.top-window.pageYOffset;
					  $(".outer_wrapper").css({"top" :top});
				}
				
				w = $('#resizable').width();
				globalHeight=$('.review_container').height();
				$(".tabs ul.tabs_panel").width(w);
			
				if ($('.info_panel').hasClass('panel_open')) {
					$('.info_panel').css({
						'right' : w + 'px',
					});
				} else {
					dw = w - 498 + 30;
					$('.info_panel').css({
						'right' : dw + 'px',
					});
					
				}
				w = $('.tab_container').width() - 20;

				$('.data_container').css({
					'width' : w + 'px'
				});
				$('#feature_sorting').css({
					'width' : w + 'px'
				});
				$('#feature_sorting.tab_content.tab_content2').css({
					'width' : w + 'px'
				});
				
				$('.review_container').css({
					'height' : $('.info_panel').height() - 120-$('#product_name').height() + 'px'
				});
				$('.data_container1').css({
					'height' : $('.info_panel').height() - 140 + 'px'
				});
				$('.data_container').css({
					'height' : $('.info_panel').height() - 140 + 'px'
				});

				$('.data_container1').css({
					'width' : w + 'px'
				});
				loadfeatureeffect();
				$(".data_container").mCustomScrollbar("update");
				$(".data_container1").mCustomScrollbar("update");
				$(".review_container").mCustomScrollbar("update");

			}

		});

	});
	
function setkeywordwidth(){
	if(val==1){
	$( "#tabs" ).tabs( "refresh" );
	$('.keys').each(function(index, element) {
		keyw = $(this).children().outerWidth();

		$(this).css({
			'width' : keyw + 'px'
		});
		$(this).children().css({
			'width' : keyw + 'px'
		});
		
		setTimeout(function(){$(".data_container").mCustomScrollbar("update");},30);
	});
	
	}
	
	val=0;
}
	
	
	
	
</script>
<style>
</style>
</head>

<body style="padding: 0px;">
	<div class="popup_overlay"></div>
	<div class="page"></div>

	<div id="resizable" class="ui-widget-content outer_wrapper">
		<div class="showcase_btn">
			<div class="showcase_btn_icon">
				<p>Find Products Based On User Reviews</p>
			</div>
		</div>
		<div id="info_panel" class="info_panel panel_close">
			<div class="slidepanel"></div>
			<div class="info_container">

				<div class="header_section">
					<h3></h3>
					<div class="userinfo">Please click on the <img src="/Inferlytics/pages/images/posCount.png" class="commentsIcon"/> or  <img src="/Inferlytics/pages/images/negCount.png" class="commentsIcon"/> to see the positive or negative comments for the product.</div>
					<div id="product_name" title=""></div>
						<div id="headerIcon">
						<span id="span_back" class="span_back_hidden"></span>
						<div class="review_type">
							<div class="positive_review">
								<div id="positiveCountno"></div>
								<div id="posarrow" class="inferlytics-arrow"></div>
							</div>
							<div class="negative_review">
								<div id="negativeCountno"></div>
								<div id="negarrow" class="inferlytics-arrow"></div>
							</div>

						</div>
					</div>
				</div>
				
				
				<div class="review_container" id="review_container"></div>
			</div>
		</div>
		<div class="content_panel">
			<div class="header_panel">
				<div class="dragger"></div>
				<div class="icon"></div>
				<h1>Find Products Based On User Reviews</h1>
				<div class="minimize_panel"></div>
			</div>
			<div class="content_container">
				<div id="tabs" class="tabs">

					<ul class="tabs_panel">
						<li><a class="selected feature" title="By Features"
							href="#tabs-1"><span></span><label class="keyword"><div>Features</div></label></a></li>
					</ul>

					<div id="tabs-1" class="tab_container ">

						<div class="tab_header">
							<div class="infer-headerinfo">Click on a product trait to
								find related products.</div>

						</div>
						<div id="back" Onclick="addFeatures()"></div>
						<div id="feature_sorting" class="tab_content tab_content2 ">
							<div class="data_container1">
								<div id="subcontainer"
									class="tp-portfolio theBigThemePunchGallery"></div>
							</div>

						</div>
					</div>
				</div>
			</div>
		</div>

	</div>
	<script type="text/javascript"
		src="/Inferlytics/pages/js/ecomwidget/custom_script.js">
		
	</script>

</body>
</html>