<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sessionId" value="${sessionScope.userId}" />
<c:set var="userId" value="${param[\"userId\"]}" />
<c:choose>
	<c:when test="${!sessionId.equals(userId)}">
		<c:redirect url="login.html" />
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
<link href="_/css/bootstrap.css" rel="stylesheet" media="screen">
<link href="_/css/speechBubbles.css" rel="stylesheet" media="screen">
<link href="_/css/fontello.css" rel="stylesheet" media="screen">
<link href="_/css/mystyle.css" rel="stylesheet" media="screen">
<link href="_/css/shop_slider.css" rel="stylesheet" media="screen">
<link href="_/css/slider.css" rel="stylesheet" media="screen">
<link href="_/css/slider_2.css" rel="stylesheet" media="screen">
<link href="_/css/sub_slider.css" rel="stylesheet" media="screen">
<link href="_/css/twitter_slider.css" rel="stylesheet" media="screen">
<link href="_/css/slide_background.css" rel="stylesheet" media="screen">
<link href="_/css/shop_slider_background.css" rel="stylesheet"
	media="screen">
<link href="_/css/subpage_banners.css" rel="stylesheet" media="screen">
<link href="_/css/jrating.jquery.css" rel="stylesheet" media="screen">
<!--[if IE 9]><link rel="stylesheet" type="text/css" href="_/css/ie9.css"><![endif]-->
<link
	href="http://fonts.googleapis.com/css?family=Open+Sans:400italic,400,300,600,700,800"
	rel="stylesheet" type="text/css">
<link href="http://fonts.googleapis.com/css?family=Lora:400,400italic"
	rel="stylesheet" type="text/css">
<script type="text/javascript" src="_/js/modernizr.js" style=""></script>
<script type="text/javascript" src="_/js/bootstrap.js"></script>
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
<script type="text/javascript" src="_/js/google_map.js"></script>
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
<link rel="stylesheet" type="text/css" href="css/jquery.jqplot.css" />
<link href="css/jquery.mCustomScrollbar.css" rel="stylesheet"
	type="text/css" />
<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script type="text/javascript"
	src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>


<script language="javascript" type="text/javascript"
	src="js/jquery.jqplot.min.js"></script>
<script src="js/bootstrap-paginator.min.js"></script>
<script type="text/javascript"
	src="js/jqplugin/jqplot.dateAxisRenderer.min.js"></script>
<script type="text/javascript"
	src="js/jqplugin/jqplot.highlighter.min.js"></script>
<script type="text/javascript"
	src="js/jqplugin/jqplot.canvasTextRenderer.min.js"></script>
<script type="text/javascript"
	src="js/jqplugin/jqplot.canvasAxisLabelRenderer.min.js"></script>

<script type="text/javascript" src="js/jqplugin/jqplot.cursor.min.js"></script>
<script src="js/jquery.mousewheel.min.js"></script>
<script src="js/jquery.mCustomScrollbar.min.js"></script>
<script src="js/analyticschart.js"></script>
<script>
	var entity = getUrlParams()["brand"];
	var subProduct = getUrlParams()["subProduct"];
	var userId = "<c:out value='${param[\"userId\"]}' />";
</script>

<body class="home home-2" style="">

	<nav class="navbar navbar-default navbar-fixed-top" role="navigation"
		style="display: block;">
		<div class="container">
			<div class="row">
				<div class="col col-sm-12">
					<div class="navbar-header">
						<button type="button" class="navbar-toggle" data-toggle="collapse"
							data-target="#mynavbar">
							<span class="sr-only">Toggle navigation</span> <span
								class="icon-bars"></span> <span class="icon-bars"></span> <span
								class="icon-bars"></span>
						</button>
						<a class="navbar-brand" href="#"><img
							src="http://images.inferlytics.com/logo.png" alt="Logo"></a>
					</div>
					<div class="collapse navbar-collapse" id="mynavbar">
						<div class="left-cell">
							<ul class="nav navbar-nav">
								<li><a href="#">Analytics Chart</a></li>
								<li><a href="#analytics-data">Analytics Data</a></li>
								<li><a href="#top-products">TOP PRODUCTS AND REVIEWS</a></li>

								<li><a href="#recent-reviews">Recent Reviews</a></li>

							</ul>
						</div>
						<div class="right-cell">
							<form class="navbar-form navbar-right">
								<ul>
									<li class="hidden-mobile">
										<form class="form-inline" role="form" id="logoutForm"
											action="/Inferlytics/Logout">
											<div class="row">
												<div class="form-group col-xs-12 col-sm-12">
													<a href="#" type="submit" class="md-trigger"
														data-modal="login-modal" onclick="logout()">Logout</a>
												</div>
											</div>
										</form>
									</li>

								</ul>
							</form>
						</div>

					</div>
				</div>
			</div>
		</div>
	</nav>
	<section class="client-logos ">
		<div class="container">
			<div class="row">
				<div class="section-head">
					<header class="col col-sm-12 centered">
						<section>

							<h2>Analytics Chart</h2>
						</section>
						<hr>
					</header>

				</div>
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
							<div class="form-group col-xs-12 col-sm-4">
								<label>From Date:</label> <input type="text"
									class="form-control" id="fromDatepicker" placeholder="fromdate"
									required="">
							</div>
							<div class="form-group col-xs-12 col-sm-4">
								<label>To Date:</label> <input type="text" class="form-control"
									id="toDatepicker" placeholder="todate" required="">
							</div>
							<div class="form-group col-xs-12 col-sm-1">
								<a class="btn btn-owl next " Onclick="submitClicked()">
									Submit </a>
							<a class="button-loading" id="loader1"></a>
									
							</div>
							<div class="form-group col-xs-12 col-sm-3">
								<ul class="selectTime">
									<li class="_GAbd _GAl">Day</li>
									<li class="_GAbd">Week</li>
									<li class="_GAbd">Month</li>
								</ul>
							</div>
						</div>
					</form>
				</div>
			</div>

			<div class="row">
				<div class="col-lg-10 col-md-10 col-sm-10">
					<div class="lineChart_section">
						<h4>Evaluation Result</h4>
						<div id="charts" style="height: 400px; width: 100%;"></div>
					</div>
				</div>
				<div class="col-lg-2 col-md-2 col-sm-2">
					<div class="fun-facts-section">
						<div class="container grid-icon">
							<div class="row">
								<div
									class="col col-xs-12 col-sm-12 uk-scrollspy-init-inview uk-scrollspy-inview uk-animation-fade"
									data-uk-scrollspy="{cls:'uk-animation-fade', delay:600}">
									<span><i class="icon-group"></i></span>
									<h4 class="dt-counter" style="opacity: 1;"></h4>
									<p>No of Unique Users</p>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</section>

	<section class="client-logos ">
		<div class="container">
			<div class="row">
				<div class="section-head">
					<header class="col col-sm-12 centered">
						<section>
							<h2>Incoming Review and Analysis</h2>
						</section>
						<hr>
					</header>
				</div>
				<div class="col-xs-6"></div>
				<div class="col-xs-3"></div>
				<div class="col-xs-3" style="margin-bottom: -48px;"></div>
			</div>
			<div class="row"></div>
			<div class="row">
				<div class="col-lg-12 col-md-12 col-sm-12">
					<div class="lineChart_section">
						<h4>Evaluation Result</h4>
						<div id="timeLineCharts" style="height: 400px; width: 100%;"></div>
					</div>
				</div>
			</div>
		</div>
	</section>



	<section class="graphic_section container" id="analytics-data">
		<div class="row">
			<div class="section-head">
				<header class="col col-sm-12 centered">
					<section>
						<h2>Analytics Data</h2>
					</section>
					<hr>
				</header>
			</div>

			<div class="container">


				<div class="row">
					<div class="col-md-2 col-sm-5 col-xs-12  ">

						<div class="pie_chart_holder normal">
							<div class="percentage" data-percent="65" data-linewidth="16"
								data-active="#1abc9c" data-noactive="#ecf0f1"
								style="height: 312px; overflow: hidden;" id="pos_review_Count">


								<canvas class="doughnutChart" data-percent="0" width="260"
									height="260" style="width: 260px; height: 260px;"></canvas>

								<div class="pie_chart_text"
									style="position: relative; top: -280px; opacity: 1;">

									<span class="tocounter" style="line-height: 260px;">64</span>

									<p></p>
									<h5 class="percentage_head" id="posPercentage">Positive
										Review Clicks</h5>
								</div>
							</div>
						</div>
					</div>

					<div class="col-md-2 col-sm-5 col-xs-12 ">

						<div class="pie_chart_holder normal">
							<div class="percentage" data-percent="50" data-linewidth="16"
								data-active="#1abc9c" data-noactive="#ecf0f1"
								style="height: 312px; overflow: hidden;" id="neg_review_Count">
								<canvas class="doughnutChart" data-percent="0" width="260"
									height="260" style="width: 260px; height: 260px;"></canvas>
								<div class="pie_chart_text"
									style="position: relative; top: -280px; opacity: 1;">
									<span class="tocounter" style="line-height: 260px;">50</span>

									<p></p>
									<h5 class="percentage_head" id="negPercentage">Negative
										Review Clicks</h5>
								</div>
							</div>
						</div>
					</div>
					<div class="col-lg-4 col-md-4 col-sm-4">
						<div class="panel-group custom-accordion" id="accordion2">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4 class="panel-title">Top Navigations</h4>

								</div>
								<div id="collapseOne2" class="panel-collapse"
									style="height: auto;">
									<table class="table table-striped">
										<thead>
											<tr>
												<th>#</th>
												<th>Navigation</th>
												<th>No of clicks</th>

											</tr>
										</thead>
										<tbody id="top_subdimensions">

										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
					<div class="col-lg-4 col-md-4 col-sm-4">
						<div class="panel-group custom-accordion" id="accordion2">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4 class="panel-title">Top Traits</h4>

								</div>
								<div id="collapseTwo2" class="panel-collapse"
									style="height: auto;">
									<table class="table table-striped">
										<thead>
											<tr>
												<th>#</th>
												<th>Traits</th>
												<th>No of Clicks</th>
										</thead>
										<tbody id="top_words">
										</tbody>
									</table>
								</div>
							</div>

						</div>
					</div>


				</div>


			</div>
		</div>
	</section>

	<section class="graphic_section container" id="analytics-data">
		<div class="row">
			<div class="section-head">
				<header class="col col-sm-12 centered">
					<section>
						<h2>Timeline Data</h2>
					</section>
					<hr>
				</header>
			</div>

			<div class="container">


				<div class="row">
					<div class="col-lg-6 col-md-6 col-sm-6">
						<div class="panel-group custom-accordion" id="posTraits">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4 class="panel-title">Top Positive Traits</h4>

								</div>
								<div id="collapseOne2" class="panel-collapse"
									style="height: auto;">
									<table class="table table-striped">
										<thead>
											<tr>
												<th>#</th>
												<th>Navigation</th>
												<th>No of Occurences</th>

											</tr>
										</thead>
										<tbody id="top_positive_traits">

										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-6">
						<div class="panel-group custom-accordion" id="negTraits">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4 class="panel-title">Top Negative Traits</h4>

								</div>
								<div id="collapseTwo2" class="panel-collapse"
									style="height: auto;">
									<table class="table table-striped">
										<thead>
											<tr>
												<th>#</th>
												<th>Traits</th>
												<th>No of Occurence	s</th>
										</thead>
										<tbody id="top_negative_traits">
										</tbody>
									</table>
								</div>
							</div>

						</div>
					</div>


				</div>


			</div>
		</div>
	</section>
	<section class="graphic_section container" id="analytics-data">
		<div class="row">
			<div class="section-head">
				<header class="col col-sm-12 centered">
					<section>
						<h2>Timeline Data</h2>
					</section>
					<hr>
				</header>
			</div>
			<div class="row">
				<div class="col-xs-12">
					<h4>Filter Date</h4>
					<form class="form-inline" role="form" id="dt-contact-form1"
						novalidate="novalidate">
						<div class="row">
							<div class="form-group col-xs-12 col-sm-4">
								<label>From Date:</label> <input type="text"
									class="form-control" id="fromDatepicker1"
									placeholder="fromdate" required="">
							</div>
							<div class="form-group col-xs-12 col-sm-4">
								<label>To Date:</label> <input type="text" class="form-control"
									id="toDatepicker1" placeholder="todate" required="">
							</div>


						</div>
						<div class="row" style="margin-top: 30px">
							<div class="form-group col-xs-12 col-sm-4">
								<label>From Date:</label> <input type="text"
									class="form-control" id="fromDatepicker2"
									placeholder="fromdate" required="">
							</div>
							<div class="form-group col-xs-12 col-sm-4">
								<label>To Date:</label> <input type="text" class="form-control"
									id="toDatepicker2" placeholder="todate" required="">
							</div>
							<div class="form-group col-xs-12 col-sm-1">
								<a class="btn btn-owl next" Onclick="compareTraits()">
									Submit </a>
									<a class="button-loading" id="loader2"></a>
							</div>

						</div>
					</form>
				</div>
			</div>

			<div class="container">


				<div class="row">
					<div class="col-lg-6 col-md-6 col-sm-6">
						<div class="panel-group custom-accordion" id="posTraitsChange">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4 class="panel-title">Top Positive Traits</h4>

								</div>
								<div id="collapseOne2" class="panel-collapse"
									style="height: auto;">
									<table class="table table-striped">
										<thead>
											<tr>
												<th>#</th>
												<th>Navigation</th>
												<th>Period 1</th>
												<th>Period 2</th>
												<th>Change</th>
											</tr>
										</thead>
										<tbody id="positive_trait_change">
								
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-6">
						<div class="panel-group custom-accordion" id="negTraitsChange">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4 class="panel-title">Top Negative Traits</h4>

								</div>
								<div id="collapseTwo2" class="panel-collapse"
									style="height: auto;">
									<table class="table table-striped">
										<thead>
											<tr>
												<th>#</th>
												<th>Navigation</th>
												<th>Period 1</th>
												<th>Period 2</th>
												<th>Change</th>
											</tr>
										</thead>
										<tbody id="negative_trait_change">
										</tbody>
									</table>
								</div>
							</div>

						</div>
					</div>


				</div>


			</div>
		</div>
	</section>
	<section class="featured-work-2 " id="top-products">
		<div class="container">
			<div class="row">
				<div class="section-head">
					<header class="col col-xs-12 centered">
						<section>
							<h2>TOP PRODUCTS AND REVIEWS</h2>
						</section>
						<hr>
					</header>
				</div>
			</div>

			<div class="row">
				<div class="col-sm-6 sidebar">



					<div class="widget widget-tab">
						<!-- Nav tabs -->
						<ul class="nav nav-tabs nav-justified">
							<li class="active"><a href="#home" data-toggle="tab">Top
									Products</a></li>


						</ul>

						<!-- Tab panes -->
						<div class="tab-content">
							<div class="tab-pane fade active in" id="top_Products"></div>
							<div id="top_Products_Pagination"></div>


						</div>
					</div>
				</div>
				<div class="col-sm-6 sidebar">



					<div class="widget widget-tab">
						<!-- Nav tabs -->
						<ul class="nav nav-tabs nav-justified">
							<li class="active"><a href="#home" data-toggle="tab">Top
									Reviews</a></li>


						</ul>

						<!-- Tab panes -->
						<div class="tab-content">
							<div class="tab-pane fade active in">
								<div id="top_Comments" class="row topComments"></div>
								<div id="top_Comments_Pagination"></div>

							</div>


						</div>
					</div>
				</div>
			</div>
		</div>
	</section>
	<section class="featured-work-2 " id="recent-reviews">
		<div class="container">
			<div class="row">
				<div class="section-head">
					<header class="col col-xs-12 centered">
						<section>
							<h2>RECENT REVIEWS</h2>
						</section>
						<hr>
					</header>
				</div>
			</div>

			<div class="row">
				<div class="col-sm-6 sidebar">



					<div class="widget widget-tab">
						<!-- Nav tabs -->
						<ul class="nav nav-tabs nav-justified">
							<li class="active"><a href="#home" data-toggle="tab">RECENT
									POSITIVE REVIEWS</a></li>


						</ul>

						<!-- Tab panes -->
						<div class="tab-content">
							<div id="pos_recent_message">No recent reviews found in
								this given date range , showing all latest positive reviews.</div>
							<div class="tab-pane fade active in">
								<div class="row recent_positive_reviews_class"
									id="recent_positive_reviews"></div>
								<div class="row">

									<form class="form-inline" role="form">
										<div class="form-group">
											<label class="label-row">No of rows :</label> <select
												id="pos-recentComments-count" class="form-control">
												<option>5</option>
												<option>10</option>
												<option>25</option>
												<option>50</option>
											</select>
										</div>
										<div id="recent_positive_reviews_Pagination"
											class="pagination" style=""></div>



									</form>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-sm-6 sidebar">



					<div class="widget widget-tab">
						<!-- Nav tabs -->
						<ul class="nav nav-tabs nav-justified">
							<li class="active"><a href="#home" data-toggle="tab">RECENT
									NEGATIVE REVIEWS</a></li>


						</ul>

						<!-- Tab panes -->
						<div class="tab-content">
							<div id="neg_recent_message">No recent reviews found in
								this given date range , showing all latest negative reviews,</div>
							<div class="tab-pane fade active in">
								<div class="row recent_negative_reviews_class"
									id="recent_negative_reviews"></div>
								<div class="row">
									<form class="form-inline" role="form">
										<div class="form-group">
											<label class="label-row">No of rows :</label> <select
												id="neg-recentComments-count" class="form-control">
												<option>5</option>
												<option>10</option>
												<option>25</option>
												<option>50</option>
											</select>
										</div>
										<div id="recent_negative_reviews_Pagination"
											class="pagination" style=""></div>



									</form>
								</div>

							</div>


						</div>
					</div>
				</div>
			</div>
		</div>
	</section>



	<footer>
		<section class="container footer-section">
			<div class="col-lg-5 col-xs-12">
				<p>© 2014 Inferlytics. All right reserved</p>
			</div>
			<div class="col-lg-7 footer-menu">
				<ul class="nav nav-pills">

				</ul>
			</div>
		</section>
		<section class="ss-style-doublediagonal" data-type="background"
			data-speed="10" style="background-position: 50% -501.1px;"></section>
	</footer>



	<div class="md-overlay"></div>
	<div class="jquery-media-detect"></div>
	<script type="text/javascript" src="_/js/jrating.jquery.js"></script>
	<script type="text/javascript" src="_/js/myscript.js"></script>
	<script src="_/js/core.js"></script>
	<script src="_/js/scrollspy.js"></script>
	<script type="text/javascript" src="_/js/modal_effects.js"></script>

</body>
</html>