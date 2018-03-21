<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Ratings</title>
<link href="pages/css/ecomwidget/style.css" rel="stylesheet"
	type="text/css" />
<link href="pages/css/ecomwidget/jquery.mCustomScrollbar.css"
	rel="stylesheet" type="text/css" />
<link href="pages/css/ecomwidget/animate.min.css" rel="stylesheet"
	type="text/css" />
	
<link
	href='http://fonts.googleapis.com/css?family=Open+Sans:400,700,600'
	rel='stylesheet' type='text/css' />
<script src="pages/js/ecomwidget/FragBuilder.js"></script>
<!--  <script src="pages/js/ecomwidget/jquery-2.0.3.js"></script>  -->

<script src="http://code.jquery.com/jquery-2.0.3.min.js"></script>
<script src="http://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>

<script src="pages/js/ecomwidget/ui/jquery.ui.core.js"></script>
<script src="pages/js/ecomwidget/ui/jquery.ui.widget.js"></script>
<script src="pages/js/ecomwidget/ui/jquery.ui.tabs.js"></script>
<script src="pages/js/ecomwidget/ui/jquery.ui.mouse.js"></script>
<script src="pages/js/ecomwidget/ui/jquery.ui.resizable.js"></script>
<script src="pages/js/ecomwidget/jquery.mousewheel.min.js"></script>
<script src="pages/js/ecomwidget/jquery.mCustomScrollbar.min.js"></script>

<!-- <script src="pages/js/ecomwidget/masonry.pkgd.min.js"></script> -->
<script src="pages/js/ecomwidget/transition/jquery.js"></script>
<script src="pages/js/ecomwidget/highlight.js"></script>
<script src="pages/js/ecomwidget/transition/cssanimate.js"></script>



<script>
var val=1;
	$(function() {
		
		totalwidth = $(document).width() - 405;
		$("#tabs").tabs().addClass("ui-tabs-vertical ui-helper-clearfix");
		$("#tabs").tabs({
			activate : function(event, ui) {
				addFeatures();
				loadfeatureeffect();
				setkeywordwidth();
			}
		  
		});
		
		
		$("#tabs li").removeClass("ui-corner-top").addClass("ui-corner-left");
		$("#resizable").resizable({
			handles : 'nw',
			minWidth : 550,
			minHeight : 400,
			maxWidth : totalwidth,
			containment : "body",
			//alsoResize: "#info_panel"
			resize : function(event, ui) {
				w = $('#resizable').width();
				if ($('.info_panel').hasClass('panel_open')) {
					$('.info_panel').css({
						'right' : w + 'px',
					});
				} else {
					dw = w - 400 + 30;
					$('.info_panel').css({
						'right' : dw + 'px',
					});
				}
				w = $('.tab_container').width() - 50;

				$('.data_container').css({
					'width' : w + 'px'
				});
				$('.review_container').css({
					'height' : $('.info_panel').height() - 70 + 'px'
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
		$(".data_container").mCustomScrollbar("update");
	});
	
	}
	
	val=0;
}
	
	
	
	
</script>
</head>

<body onload="refresh()" style="padding: 0px;">
	<div class="popup_overlay"></div>
	<div class="page"></div>

	<div id="resizable" class="ui-widget-content outer_wrapper">
		<div class="showcase_btn">
			<div class="showcase_btn_icon">
				<p>Reviews</p>
			</div>
		</div>
		<div id="info_panel" class="info_panel panel_close">
			<div class="slidepanel"></div>
			<div class="info_container">
				<div class="header_section">
				
					<h3>Showing all products</h3>
					<span id="span_back" class="span_back_hidden"><a OnClick="setNullForProductId()"> Back</a></span>
					<div class="review_type">
						<div class="positive_review">
							<span class="select_type checkbox"></span>
						</div>
						<div class="negative_review">
							<span class="select_type checkbox"></span>
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
				<h1>User Reviews</h1>
				<div class="minimize_panel"></div>
			</div>
			<div class="content_container">
				<div id="tabs" class="tabs">
					<ul class="tabs_panel">
				<li><a class="selected feature" title="By Features" 
							href="#tabs-1"><span></span><label class="keyword"></label></a></li>
						<li><a class="selected keyword" title="By Keywords"
							href="#tabs-2"><span></span><label class="keyword"></label></a></li>
							
					</ul>
			
					<div id="tabs-1" class="tab_container ">

						<div class="tab_header">
							<h2>By Features</h2>

						</div>
						<div id="back" Onclick="addFeatures()">
						<a href="#">&nbsp;&nbsp;Go Back</a>
						</div>
						<div id="feature_sorting" class="tab_content tab_content2 ">
							<ul class="sorting_tab ">
								<li class="selected" data-category="catall"><a
									href="#all_block">All</a></li>
								<li data-category="cata"><a href="#positive_block">Positive</a></li>
								<li data-category="catb"><a href="#negative_block">Negative</a></li>
							</ul>
							
							<div class="data_container1">
								<div id="subcontainer"
									class="tp-portfolio theBigThemePunchGallery">
									
							</div>
							</div>

						</div>
					</div>
						<div id="tabs-2" class="tab_container">
						<div class="tab_header">
							<h2>By Keywords</h2>
															<div class="search_form">
								<form>
									<input placeholder="Search" type="text" name="search"
										id="search_key" /> <label class="clear_search"></label> <a
										class="search_btn" id="submit_key"></a>
								</form>
							</div>
						</div>
						<div id="keyword_sorting_div" class="tab_content">
							<ul id="keyword_sorting" class="sorting_tab">
								<li data-category="keycatall" class="selected"><a href="#all">All</a></li>
								<li data-category="keycata"><a href="#positive">Positive</a></li>
								<li data-category="keycatb"><a href="#negative">Negative</a></li>
							</ul>
							<c:set var="keywords" value="${requestScope.keywords}" />
							<div class="data_container">
							<div id="keywordsubcontainer">
								<c:forEach var="keyword" varStatus="counter" items='${keywords}'>

									<div
										class='keys all keycatall <c:if test="${keyword.positiveCount > 0}">positive keycata </c:if><c:if test="${keyword.negativeCount > 0}"> negative keycatb</c:if>'>
										<ul>
											<li class="keyword_header"
												onclick="allKeysClick('${keyword.name}',1,0,${keyword.count},this);">
												<div class="front">
													<span>${keyword.name}</span> <label class="lbl all ">${keyword.count}</label>
													<c:if test="${keyword.positiveCount > 0}">
														<label class="lbl positive hide_label">${keyword.positiveCount}</label>
													</c:if>
													<c:if test="${keyword.negativeCount > 0}">
														<label class="lbl negative hide_label">${keyword.negativeCount}</label>
													</c:if>
												</div>
												<div class="back">
													<span>${keyword.name}</span><label>${keyword.count}</label>
												</div></li>
											<li class="keyword_count"><span class="keyword_positive"
												onclick="allKeysClick('${keyword.name}',2,0,${keyword.positiveCount},this);"><label>${keyword.positiveCount}</label></span>
												<span class="keyword_negative"
												onclick="allKeysClick('${keyword.name}',3,0,${keyword.negativeCount},this);"><label>${keyword.negativeCount}</label></span>
											</li>
										</ul>
									</div>
								</c:forEach>
</div>
							</div>
						</div>
					</div	>
				</div>
			</div>
		</div>

	</div>
	<script type="text/javascript"
		src="pages/js/ecomwidget/custom_scriptforWidget.js">
		
	</script>

</body>
</html>