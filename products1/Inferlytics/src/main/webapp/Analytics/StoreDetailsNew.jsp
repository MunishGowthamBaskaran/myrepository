<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sessionId" value="${sessionScope.userId}" />
<c:set var="userId" value="${param[\"userId\"]}" />


<c:choose>
	<c:when test="${!sessionId.equals(userId)}">
		<c:redirect url="Analytics/login.html" />
	</c:when>
</c:choose>
<html style=""
	class=" js no-touch csstransforms csstransforms3d csstransitions js no-touch csstransforms csstransforms3d csstransitions js no-touch csstransforms csstransforms3d csstransitions js no-touch csstransforms csstransforms3d csstransitions js no-touch csstransforms csstransforms3d csstransitions js no-touch csstransforms csstransforms3d csstransitions js no-touch csstransforms csstransforms3d csstransitions js no-touch csstransforms csstransforms3d csstransitions">
<head>
<style type="text/css">
.gm-style .gm-style-mtc label,.gm-style .gm-style-mtc div {
	font-weight: 400
}
</style>
<style type="text/css">
.gm-style .gm-style-cc span,.gm-style .gm-style-cc a,.gm-style .gm-style-mtc div
	{
	font-size: 10px
}
</style>
<link type="text/css" rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700">
<style type="text/css">
@media print {
	.gm-style .gmnoprint,.gmnoprint {
		display: none
	}
}

@media screen {
	.gm-style .gmnoscreen,.gmnoscreen {
		display: none
	}
}
</style>
<style type="text/css">
.gm-style {
	font-family: Roboto, Arial, sans-serif;
	font-size: 11px;
	font-weight: 400;
	text-decoration: none
}
</style>

<title>Inferlytics Analytics</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="Analytics/_/css/bootstrap.css" rel="stylesheet"
	media="screen">
<link href="Analytics/_/css/fontello.css" rel="stylesheet"
	media="screen">
<link href="Analytics/_/css/mystyleStore.css" rel="stylesheet"
	media="screen">
<link href="Analytics/_/css/shop_slider.css" rel="stylesheet"
	media="screen">
<link href="Analytics/_/css/slider.css" rel="stylesheet" media="screen">
<link href="Analytics/_/css/slider_2.css" rel="stylesheet"
	media="screen">
<link href="Analytics/_/css/sub_slider.css" rel="stylesheet"
	media="screen">
<link href="Analytics/_/css/twitter_slider.css" rel="stylesheet"
	media="screen">
<link href="Analytics/_/css/slide_background.css" rel="stylesheet"
	media="screen">
<link href="Analytics/_/css/shop_slider_background.css" rel="stylesheet"
	media="screen">
<link href="Analytics/_/css/subpage_banners.css" rel="stylesheet"
	media="screen">
<link href="Analytics/_/css/jrating.jquery.css" rel="stylesheet"
	media="screen">
<!--[if IE 9]><link rel="stylesheet" type="text/css" href="_/css/ie9.css"><![endif]-->
<link
	href="http://fonts.googleapis.com/css?family=Open+Sans:400italic,400,300,600,700,800"
	rel="stylesheet" type="text/css">
<link href="http://fonts.googleapis.com/css?family=Lora:400,400italic"
	rel="stylesheet" type="text/css">
<script type="text/javascript" src="Analytics/_/js/modernizr.js"
	style=""></script>
<script type="text/javascript" src="Analytics/_/js/bootstrap.js"></script>
<script type="text/javascript"
	src="https://maps.googleapis.com/maps/api/js?key=AIzaSyASm3CwaK9qtcZEWYa-iQwHaGi3gcosAJc&amp;sensor=false"></script>
<script
	src="https://maps.gstatic.com/intl/en_us/mapfiles/api-3/15/7/main.js"
	type="text/javascript"></script>
<script
	src="https://maps.gstatic.com/intl/en_us/mapfiles/api-3/15/7/main.js"
	type="text/javascript"></script>
<script
	src="https://maps.gstatic.com/intl/en_us/mapfiles/api-3/15/7/main.js"
	type="text/javascript"></script>
<script
	src="https://maps.gstatic.com/intl/en_us/mapfiles/api-3/15/7/main.js"
	type="text/javascript"></script>
<script
	src="https://maps.gstatic.com/intl/en_us/mapfiles/api-3/15/7/main.js"
	type="text/javascript"></script>
<script
	src="https://maps.gstatic.com/intl/en_us/mapfiles/api-3/15/7/main.js"
	type="text/javascript"></script>
<script
	src="https://maps.gstatic.com/intl/en_us/mapfiles/api-3/15/7/main.js"
	type="text/javascript"></script>
<script
	src="https://maps.gstatic.com/intl/en_us/mapfiles/api-3/15/7/main.js"
	type="text/javascript"></script>
<script type="text/javascript" src="Analytics/_/js/google_map.js"></script>
<style>
.sequence-preloader {
	height: 100%;
	position: absolute;
	width: 100%;
	z-index: 999999;
}
</style>
<script type="text/javascript" charset="UTF-8"
	src="https://maps.gstatic.com/cat_js/intl/en_us/mapfiles/api-3/15/7/%7Bcommon,map,util,marker%7D.js"></script>
<script type="text/javascript" charset="UTF-8"
	src="https://maps.gstatic.com/cat_js/intl/en_us/mapfiles/api-3/15/7/%7Bonion%7D.js"></script>
<script type="text/javascript" charset="UTF-8"
	src="https://maps.gstatic.com/cat_js/intl/en_us/mapfiles/api-3/15/7/%7Bcontrols,stats%7D.js"></script>
<script type="text/javascript" charset="UTF-8"
	src="https://maps.gstatic.com/cat_js/intl/en_us/mapfiles/api-3/15/7/%7Bcommon,map,util,marker%7D.js"></script>
<script type="text/javascript" charset="UTF-8"
	src="https://maps.gstatic.com/cat_js/intl/en_us/mapfiles/api-3/15/7/%7Bstats%7D.js"></script>
<script type="text/javascript" charset="UTF-8"
	src="https://maps.gstatic.com/cat_js/intl/en_us/mapfiles/api-3/15/7/%7Bcommon,map,util,marker%7D.js"></script>
<script type="text/javascript" charset="UTF-8"
	src="https://maps.gstatic.com/cat_js/intl/en_us/mapfiles/api-3/15/7/%7Bstats%7D.js"></script>
<script type="text/javascript" charset="UTF-8"
	src="https://maps.gstatic.com/cat_js/intl/en_us/mapfiles/api-3/15/7/%7Bcommon,map,util,marker%7D.js"></script>
<script type="text/javascript" charset="UTF-8"
	src="https://maps.gstatic.com/cat_js/intl/en_us/mapfiles/api-3/15/7/%7Bstats%7D.js"></script>
<script type="text/javascript" charset="UTF-8"
	src="https://maps.gstatic.com/cat_js/intl/en_us/mapfiles/api-3/15/7/%7Bcommon,map,util,marker%7D.js"></script>
<script type="text/javascript" charset="UTF-8"
	src="https://maps.gstatic.com/cat_js/intl/en_us/mapfiles/api-3/15/7/%7Bstats%7D.js"></script>
<script type="text/javascript" charset="UTF-8"
	src="https://maps.gstatic.com/cat_js/intl/en_us/mapfiles/api-3/15/7/%7Bcommon,map,util,marker%7D.js"></script>
<script type="text/javascript" charset="UTF-8"
	src="https://maps.gstatic.com/cat_js/intl/en_us/mapfiles/api-3/15/7/%7Bstats%7D.js"></script>
<script type="text/javascript" charset="UTF-8"
	src="https://maps.gstatic.com/cat_js/intl/en_us/mapfiles/api-3/15/7/%7Bcommon,map,util,marker%7D.js"></script>
<script type="text/javascript" charset="UTF-8"
	src="https://maps.gstatic.com/cat_js/intl/en_us/mapfiles/api-3/15/7/%7Bstats%7D.js"></script>
<script type="text/javascript" charset="UTF-8"
	src="https://maps.gstatic.com/cat_js/intl/en_us/mapfiles/api-3/15/7/%7Bcommon,map,util,marker%7D.js"></script>
<script type="text/javascript" charset="UTF-8"
	src="https://maps.gstatic.com/cat_js/intl/en_us/mapfiles/api-3/15/7/%7Bstats%7D.js"></script>
</head>

<link
	href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css"
	rel="stylesheet" />
<link rel="stylesheet" type="text/css"
	href="Analytics/css/jquery.jqplot.css" />
<link rel="stylesheet" type="text/css" href="Analytics/css/jqcloud.css" />
<link href="Analytics/css/jquery.mCustomScrollbar.css" rel="stylesheet"
	type="text/css" />
<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script type="text/javascript"
	src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>
<link rel="shortcut icon" href="/favicon.ico" type="image/x-icon">
<link rel="icon" href="/favicon.ico" type="image/x-icon">

<script language="javascript" type="text/javascript"
	src="Analytics/js/jquery.jqplot.min.js"></script>
<script src="Analytics/js/bootstrap-paginator.min.js"></script>
<script type="text/javascript"
	src="Analytics/js/jqplugin/jqplot.dateAxisRenderer.min.js"></script>
<script type="text/javascript"
	src="Analytics/js/jqplugin/jqplot.highlighter.min.js"></script>
<script type="text/javascript"
	src="Analytics/js/jqplugin/jqplot.canvasTextRenderer.min.js"></script>
<script type="text/javascript"
	src="Analytics/js/jqplugin/jqplot.canvasAxisLabelRenderer.min.js"></script>
<script type="text/javascript"
	src="Analytics/js/jqplugin/jqplot.cursor.min.js"></script>
<script type="text/javascript"
	src="Analytics/js/jqplugin/jqplot.cursor.min.js"></script>
<script src="Analytics/js/jquery.mousewheel.min.js"></script>
<script src="Analytics/js/jquery.mCustomScrollbar.min.js"></script>
<script src="Analytics/js/StoreDetailsNew.js"></script>
<script type="text/javascript" src="Analytics/js/featureAnimator.js"></script>
<script type="text/javascript" src="Analytics/js/FragBuilder.js"></script>
<script src="Analytics/js/jqcloud-1.0.4.js"></script>
<script>
	var entity = '${requestScope.entity}';
	var subProduct = '${requestScope.subProduct}';
</script>

<body class="home home-2" style="">

<nav class="navbar navbar-inverse navbar-fixed-top navbar-infer" role="navigation">
  <div class="container">

    <div class="row">
      <div class="col col-sm-12">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle infer-resp" data-toggle="collapse" data-target="#mynavbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bars"></span>
            <span class="icon-bars"></span>
            <span class="icon-bars"></span>
          </button>
          <a class="navbar-brand" href="http://www.inferlytics.com"><img src="http://www.images.inferlytics.com/logo.png" alt="Logo"></a>
        </div>
      </div>
       <nav class="right-cell">
        <ul class="nav navbar-nav pull-right contact-us">
         <li>
            <a>Welcome ${sessionScope.userName}!</a>
          </li>
          
          <li>
            <a href="#" onclick="logout()">Logout</a>
          </li>

        </ul>
      </nav>
    </div>
  </div>
</nav>

<!-- 	<div class="row" style="padding:6px;" id="mainHeader">
		<img src="http://www.images.inferlytics.com/logo.png" alt="Logo">
		<div style="float:right;padding-right:50px;">
		<form class="form-inline" role="form" id="logoutForm"
											action="/Inferlytics/Logout">
											<div class="row">
												<div class="form-group col-xs-12 col-sm-12">
												<a href="#" type="submit" style="color: #ffffff;text-decoration: none;"
														data-modal="login-modal" onclick="logout()">Logout</a>											
												</div>
											</div>
										</form>
										</div>
		
	</div> -->
	<hr>
	<div class="row maincontentRow">
		<div class="col-sm-2 facilities">

			<div class="facilitiesHeading">FACILITIES</div>
<div id="storeHolder">
			<c:forEach var="store" items="${requestScope.allStores}">
				<div class="col-sm-12">

					<div class="product-thumbnail"
						onClick="setProductId('<c:out value="${store.id}" />',this)">
						<img class="img-responsive"
							src="<c:out value="${store.imageSrc}" />" alt="">
					</div>
					<p class="product-name">
						<c:out value="${store.storeName}" />
					</p>
					<div class="score">
						OverallScore :
						<c:out value="${store.avgScore}" />
					</div>
					<%-- <ul class="list-unstyled" id="topKeyWords">
									<c:forEach var="keyWords" items="${store.keyWords}">
										<li class="category-list"><div class="topics">
										<c:out value="${keyWords.keyWord}" />
										</div>
											<span class="posnegCount"><c:out value="${keyWords.percentage}" />%</span></li>

									</c:forEach>
								</ul> --%>
				</div>
			</c:forEach>
		</div>
		</div>
		<div class="col-sm-10 mainContent">

			<div class="mainHeader">INCOMING REVIEW ANALYSIS</div>
			<section class="client-logos ">

				<div class="container">
					<div class="row">
						<div class="col-xs-6"></div>
						<div class="col-xs-3"></div>
						<div class="col-xs-3" style="margin-bottom: -48px;"></div>
					</div>
					<div class="row">
						<div class="col-xs-12">
							<h4>Filter Date</h4>
							<form class="form-inline" role="form" id="dt-contact-form"
								novalidate="novalidate">
								<div class="row">
									<div class="form-group col-xs-12 col-sm-5">
										<label class="dateText">From Date:</label> <input type="text"
											class="form-control" id="fromDatepicker"
											placeholder="fromdate" required="">
									</div>
									<div class="form-group col-xs-12 col-sm-5">
										<label class="dateText">To Date:</label> <input type="text"
											class="form-control" id="toDatepicker" placeholder="todate"
											required="">
									</div>
									<div class="form-group col-xs-12 col-sm-1">
										<a class="btn btn-owl next submitButton"
											Onclick="submitClicked()"> Submit </a>
									</div>

								</div>
							</form>
						</div>
					</div>

					<div class="row">
						<div class="col-lg-11 col-md-11 col-sm-11">
							<a class="btn btn-owl next resetButton" id="resetzoomTimeline">
								Reset Zoom </a>
							<div class="lineChart_section">
								<h4>Evaluation Result</h4>
								<div id="charts" style="height: 400px; width: 100%;"></div>
							</div>
						</div>
						<div class="col-lg-2 col-md-2 c	ol-sm-2"></div>
					</div>
				</div>
			</section>
			<div class="dt-featured-product">
				<div class="container">
					<h2 id="selectedHotel"></h2>
					<hr>
					<div class="mainHeader">ANALYSIS SUMMARY</div>
					<div class="row dataContent">
						<div class="widget widget-tab">
							<!-- Nav tabs -->
							<ul class="nav nav-tabs nav-justified"
								style="background: rgb(219, 219, 219);">
								<li class="active"><a href="#home" data-toggle="tab">Strengths</a></li>
								<li><a href="#recent" data-toggle="tab">Weakness</a></li>
							</ul>

							<!-- Tab panes -->
							<div class="tab-content">
								<div class="tab-pane fade in active" id="home">
									<div class="col-sm-3" id="subTopicsContainer">
										<h3 class="heading">Top Categories</h3>
										<div id="feature_sor">
											<div class="list-unstyled" id="topPosDimensions"></div>
										</div>
									</div>
									<div class="col-sm-9 commentsContainer">
										<h3 class="heading">Comments</h3>
										<div>
											<div id="commentsHeaderForPos"></div>
										</div>
										<div id="pos_Comments"></div>
									</div>

								</div>
								<div class="tab-pane fade" id="recent">
									<div class="col-sm-3" id="subTopicsNegContainer">
										<h3 class="heading">Top Categories</h3>
										<div id="feature_sor">
											<ul class="list-unstyled" id="topNegDimensions">

											</ul>
										</div>
									</div>
									<div class="col-sm-9 commentsContainer	">
										<h3 class="heading">Comments</h3>
										<div>
											<div id="commentsHeaderForNeg"></div>
										</div>
										<div id="neg_Comments"></div>

									</div>


								</div>

							</div>
						</div>
					</div>
					<div class="mainHeader"
						style="margin-top: 18px; margin-left: -5px;">SENTIMENT
						ANALYSIS SUMMARY ACROSS CATEGORIES</div>
					<div class="row summaryDiv">

						<div class="row" id="piechartRow">

							<div id="pieChartSlider" class="owl-carousel owl-theme">
								<div class='pie_chart_holder normal'>
									<div class='percentage' data-percent='64' data-linewidth='16'
										data-active='#000' data-noactive='#ecf0f1'>
										<canvas class="doughnutChart" data-percent='64'></canvas>
										<div class='pie_chart_text'>
											<span class='tocounter'>64</span>
											<h5 class="dimNames">Statistics (Visitors)</h5>
											<p></p>
										</div>
									</div>
								</div>
							</div>

							<div class="customNavigation">
								<a class="btn prevs">Previous</a> <a class="btn nexts">Next</a>
							</div>
						</div>
						<hr>
						<div class="row dataContent">
							<%-- 					
					<div class="col-sm-2">
						<div class="widget widget-tags">
							<h3 class="heading">Topics</h3>
							<div class="allTopics">
								<ul class="list-unstyled dimensions">
									<c:forEach var="store" items="${requestScope.subDimension}">
										<li class="tag" onClick="getDimensions('${store}')"><a><c:out
													value="${store}" /></a></li>
									</c:forEach>
								</ul>
							</div>
						</div>


					</div> --%>
							<div class="col-sm-5" id="subTopicsContainer">
								<h3 class="heading">Sub Categories</h3>
								<div id="feature_sorting">
									<div class="inf-bw-data_container1">
										<div id="subContainer"></div>

									</div>
								</div>
							</div>
							<div class="col-sm-7">
								<h3 class="heading">Comments</h3>
								<div>
									<div id="commentsHeader">Showing Comments for Other
										Employees</div>
									<div class="inf-bw-review_type">
										<div class="inf-bw-positive_review">
											<div id="inf-bw-positiveCountno">41</div>
											<div id="posarrow" class="inf-bw-inferlytics-arrow"></div>
										</div>
										<div class="inf-bw-negative_review">
											<div id="inf-bw-negativeCountno">1</div>
											<div id="negarrow" class="inf-bw-inferlytics-arrow"
												style="display: none;"></div>
										</div>

									</div>
								</div>
								<div id="top_Comments"></div>

							</div>

						</div>
						<hr>
						<div class="row dataContent">
							<div class="col-xs-12">
								<h4>Filter Date</h4>
								<form class="form-inline" role="form" id="dt-contact-forms"
									novalidate="novalidate">
									<div class="row">
										<div class="form-group col-xs-12 col-sm-5">
											<label class="dateText">From Date:</label> <input type="text"
												class="form-control" id="fromDateSubTopic"
												placeholder="fromdate" required="">
										</div>
										<div class="form-group col-xs-12 col-sm-5">
											<label class="dateText">To Date:</label> <input type="text"
												class="form-control" id="toDateSubTopic"
												placeholder="todate" required="">
										</div>
										<div class="form-group col-xs-12 col-sm-1">
											<a class="btn btn-owl next" id="submitSubTopicChart"
												onclick="getChartDataForSubTopics()"> Submit </a>
										</div>

									</div>
								</form>
							</div>
						</div>
						<div class="row dataContent subTopicChartcontainer">
							<div class="col-lg-10 col-md-10 col-sm-10">
								<a class="btn btn-owl next resetButton" id="resetzoomSubtopic"
									onclick="getChartDataForSubTopics()"> Reset Zoom </a>
								<div class="lineChart_section">
									<h4>Evaluation Result</h4>
									<div id="subTopicHeader"></div>
									<div id="subTopics_chart" style="height: 400px; width: 100%;"></div>
								</div>
							</div>
							<div class="col-lg-2 col-md-2 col-sm-2"></div>
						</div>
					</div>
				</div>

			</div>


		</div>
	</div>

	<div class="md-overlay"></div>
	<div class="jquery-media-detect"></div>
	<script type="text/javascript" src="Analytics/_/js/jrating.jquery.js"></script>
	<script type="text/javascript" src="Analytics/_/js/myscript.js"></script>
	<script type="text/javascript" src="Analytics/_/js/core.js"></script>
	<script type="text/javascript" src="Analytics/_/js/scrollspy.js"></script>
	<script type="text/javascript" src="Analytics/_/js/modal_effects.js"></script>
	
	<c:choose>
	<c:when test="${userId == 3}">
		<script>
	(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	})(window,document,'script','http://www.google-analytics.com/analytics.js','ga');
	
	ga('create', 'UA-47104128-1', 'inferlytics.com');
	ga('send', 'pageview');
	
	</script>
	</c:when>
</c:choose>
	
</body>
</html>