<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Events Dashboard</title>
<link rel="stylesheet" type="text/css" href="css/eventdashboard.css" />
<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>

<script src="http://d3js.org/d3.v3.min.js"></script>
<link rel="stylesheet" type="text/css"
	href="../Analytics/_/css/bootstrap.css" />
	<link href="../Analytics/css/jquery.mCustomScrollbar.css" rel="stylesheet"
	type="text/css" />
<script type="text/javascript" src="js/eventdashboard.js"></script>
<script type="text/javascript" src="../pages/js/ecomwidget/FragBuilder.js"></script>
<link rel="stylesheet" type="text/css" href="../pages/css/jqcloud.css" />
<script type="text/javascript" src="../pages/js/jqcloud-1.0.3.min.js"></script>
<script type="text/javascript" src="../Analytics/js/jquery.mCustomScrollbar.min.js"></script>
<script  type="text/javascript"
	src="../Analytics/js/jquery.jqplot.min.js"></script>
<script type="text/javascript"
	src="../Analytics/js/jqplugin/jqplot.canvasTextRenderer.min.js"></script>
<script type="text/javascript"
	src="../Analytics/js/jqplugin/jqplot.canvasAxisLabelRenderer.min.js"></script>

<script>
	var entity = getUrlParams()["brand"];
	var subProduct = getUrlParams()["subProduct"];
</script>

</head>
<body>
	<!-- Header navigation -->
	<nav class="navbar navbar-inverse .navbar-static-top navbar-infer">
		<div class="container">

			<div class="row">
				<div class="col">
					<div class="navbar-header">
						<a class="navbar-brand" href="http://www.inferlytics.com"><img
							src="http://www.images.inferlytics.com/logo.png" alt="Logo"></a>
					</div>
				</div>
				<nav class="right-cell">
					<ul class="nav navbar-nav pull-right contact-us">
						<li><a>Welcome ${sessionScope.userName}!</a></li>

						<li><a href="#" onclick="logout()">Logout</a></li>

					</ul>
				</nav>
			</div>
		</div>
	</nav>

	<div class="container">
		<div class="row">
			<div class="col-md-4">
				Trending topics and conversations
				<div class="widget">
<ul>
				<li>Events and Parties.<small class="text-success"> Sentiment is Positive</small></li>
				<li>Sessions. <small class="text-success">Sentiment is positive</small></li>
				<li>Speakers. <small class="text-muted">Sentiment is neutral</small></li>
				<li>Hackathon <small class="text-danger">Sentiment is positive</small></li>
				
				</ul>
				</div>
			</div>
			<div class="col-md-4  " >
				Analysis Summary
				<div class="col-md-12 widgetContainer">			
				<div class="row" >
				<img src="http://chart.apis.google.com/chart?chs=358x140&amp;cht=gom&amp;chco=FFFFFF,000000&amp;chd=t:80&amp;"  alt="Sentence Severity" />
				</div>
				<div class="row widgetSmall">
				  <div class="col-md-4 " style="border-right:solid 1px;"><span class="text-center strong" style="font-weight:bold;">+0.85</span><p style="font-size:smaller;line-height:1.5;">Overall SentimentScore</p></div>
				  <div class="col-md-4 " style="border-right:solid 1px;"><span class="text-center" style="font-weight:bold;">5500</span><p style="font-size:smaller; line-height:1.5;">Postings analysed</p></div>
				  <div class="col-md-4 "><span class="text-center" style="font-weight:bold;">45</span><p class="small" style="font-size:smaller;line-height:1.5;">Days Since Nov 21 2014</p></div>
				</div>
				</div>
			</div>
			<div class="col-md-4 ">
				Sentiment Trends
				<div class="col-md-12 widgetContainer">	
				<div class="row" >
				<div id="pointchart"	style="background-color:#65b688;  width:358px; height:225px;" >				
				</div>
				</div>
						<div class="row widgetSmall">
				  <div class="col-md-4 " style="border-right:solid 1px;"><span class="text-center strong" style="font-weight:bold;">3.5M</span><p style="font-size:smaller;line-height:1.5;">total posts</p></div>
				  <div class="col-md-4 " style="border-right:solid 1px;"><span class="text-center" style="font-weight:bold;">2.5M</span><p style="font-size:smaller; line-height:1.5;">Positive</p></div>
				  <div class="col-md-4 "><span class="text-center" style="font-weight:bold;">1.25M</span><p class="small" style="font-size:smaller;line-height:1.5;">Negative</p></div>
				</div>
				
				</div>
			</div>
		</div>
		<h5>Topic Wise Trends</h5>
		<hr>
		<div class="row inf-row-border">
			<div class="col-md-3">
				<div id="chartbody"></div>
				<script type="text/javascript" src="js/partition.js"></script>
			</div>
			<div class="col-md-5">
				<div id="blogComments" class="widget blogComments">
					<div class="blogComment head ">
						<span class="headtitle">Blogs for <span id="idSubDimName">Hackathon</span></span>
						<button type="button" id="idPosCommCount"
							class="num btn btn-success btn-xs">225</button>
						<button type="button" id="idnegCommCount"
							class="num btn btn-danger btn-xs">356</button>
					</div>
						<div id="commentContainer" class="commentContainer">
					<div class="blogComment">
						<button type="button" id="idShowMorebtn" class="btn btn-primary btn-xs center-block">show
							more</button>
						<div class="fix"></div>
					</div>
					</div>
				</div>

			</div>
			<div id="idkeywords" class="col-md-3 infcloud" >
			
			</div>
			
		</div>

	</div>
<div id="tooltip" class="hidden">
  <p><strong id="heading"></strong></p>
  <p><span id="mentions"></span></p>
 </div>
</body>
</html>