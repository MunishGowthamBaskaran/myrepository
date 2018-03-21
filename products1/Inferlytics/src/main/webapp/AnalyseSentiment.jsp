<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script type="text/javascript" src="pages/js/jquery.min.js"></script>
<link href="pages/css/main.css" rel="stylesheet" type="text/css" />
<link href="pages/css/custom.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="pages/js/analyse.js"></script>


<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Analyse</title>
<script type="text/javascript">
$(document).ready(function(){
	$('#showChart').click(function(){
		var frm = document.getElementById('sentiForm');
		if(frm) {
			frm.action = 'PlotChart';
			alert(frm.action);
			//document.getElementById("sentiForm").submit();
			document.sentiForm.submit();
		}
	});
});
</script>
</head>
<body>

	<div class="wrapper">
		<form action="DoSentimentAnalysis" method="post" class="mainForm" name="sentiForm" id="sentiForm">
			<fieldset>
				<div class="widget first">
					<div class="head">
						<h5 class="iList">RM Sentiment Analysis</h5>
					</div>
					<div class="rowElem noborder">
						<label>Enter Query:</label>
						<div class="formRight">
							<input id="brandname" type="text" name="query" />
							
						</div>
						
						<div class="fix"></div>
					</div>
					<div class="rowElem noborder">
						<label>Analyse single query :</label>
						<div class="formRight">
							<input id="analyse" type="text" name="analyse" />
							
						</div>
						
						<div class="fix"></div>
					</div>
					<input type="submit" name="submit" class="greyishBtn submitForm" />
					
				</div>
			</fieldset>
			<a href="javascript:void(0);" id="showChart">Show Charts</a>
		</form>
					<br/><br/>
					<a onclick="fetchdataFromTripAdvisor();" href="#">Analyse TripAdvisor Data</a>
					
					<c:if test="${requestScope.opinion != null}">
					
					<c:set var="opinion" value="${requestScope.opinion}" />
									<div class="head">
					<h5 class="iHelp">
						Opinion : 
						<c:out value="${opinion.opinionOrientation.sentiment}" /><br/>
						Polarity : <c:out value="${opinion.opinionOrientation.polarity}" /><br/>
						Detailed Sentiment: <c:out value="${opinion.opinionOrientation.detailedSentiment}" /><br/>
					</h5>
					
				</div>				
				
					</c:if>					
					
		<c:set var="feeds" value="${sessionScope.feeds}" />
		<c:if test="${sessionScope.feeds != null}">
			<div class="widget">
				<div class="head">
					<h5 class="iHelp">
						What people are talking about
						<c:out value="${requestScope.query}" />
					</h5>
					<div class="num">
						<a href="#" class="redNum"> 
						<c:out value="${fn:length(feeds)}" />
						</a>
					</div>
				</div>



				<c:forEach var="feed" varStatus="counter" items='${feeds}'>
					<c:set var="detailedSenti"
						value="${feed.opinion.opinionOrientation.detailedSentiment}"></c:set>
					<c:choose>
						<c:when test="${counter.index == 0}">
							<div class="supTicket nobg">
						</c:when>

						<c:otherwise>
							<div class="supTicket">
						</c:otherwise>
					</c:choose>

					<c:if test="${feed.opinion != null}">
						<c:set var="sentiment"
							value="${feed.opinion.opinionOrientation.sentiment}" />

						<c:choose>
							<c:when test="${sentiment == 'GOOD'}">
								<div class="issueSummarygood">
							</c:when>
							<c:when test="${sentiment == 'BAD'}">
								<div class="issueSummarybad">
							</c:when>
							<c:otherwise>
								<div class="issueSummaryneutral">
							</c:otherwise>
						</c:choose>

					<c:set var="features"
							value="${feed.opinion.features}" />

						<div class="ticketInfo">
							<ul>
								<li>${feed.content}</li>

								<li class="even"><strong>[
										${feed.opinion.opinionOrientation.sentiment} ]</strong>
								</li>
<li>
				<c:if test="${features != null}">
						<c:forEach var="feature" varStatus="counter" items='${features}'>
Feature: ${feature.noun} &nbsp SentimentWord : ${feature.adj}<br/>

</c:forEach>
</c:if>
</li>

								<li class="even" style="float: right;">${feed.date}</li>
							</ul>
							<div class="fix"></div>
						</div>
			</div>
			<div class="issueSummaryright openDiv ">
				<div id="dividFeedBack${feed.id}">

					<a href="javascript:void(0);"
						onclick="openDiv('idFeedback${feed.id}',true);"> Give Feedback
					</a>
				</div>
				<div>
					<a href="javascript:void(0);"
						onclick="openDiv('idPattern${feed.id}',false);"> Add Pattern </a>
				</div>
				<div>
					<a href="javascript:void(0);"
						onclick="openDiv('idNegation${feed.id}',false);"> Add Negation
					</a>
				</div>
				<div>
					<a href="javascript:void(0);"
						onclick="openDiv('idModifier${feed.id}',false);"> Add Modifier
					</a>
				</div>
			</div>
			<div class="fix"></div>
			<%-- 			<div id="idFeedback${feed.id}" class="tagPopUp"
								style="display: none;">
								<form class="mainForm">
									<input type="radio" id="radio1"
										name="feedback${feed.id}" value=0 checked="checked" />Correct
									<input type="radio" id="radio2" value=1
										name="feedback${feed.id}" />Incorrect <input
										type="submit" value="Give Feedback" class="blueBtn"
										onclick="giveFeedback($('input:radio[name=feedback${feed.id}]:checked').val(),'<%=feed.getId()%>'); return false;">
								</form>

							</div> --%>
			<!-- Support tickets -->
			<div id="idFeedback${feed.id}" class="tagPopUp"
				style="display: none;">
				
				<div class="widget">
					<div class="head">
						<h5 class="iHelp">Feedback</h5>
						<div class="num"><a href="#" onclick="$('.tagPopUp').hide();">close</a></div>
					</div>

					<div class="supTicket nobg">
						<div class="issueType">
							<span class="issueInfo">Overall Feedback</span>
							<div class="fix"></div>
						</div>

						<div class="issueSummary">
								
							<div class="ticketInfo ">
								<ul>
									<li><input type="radio" id="WeakPositive${feed.id}"
										name="<c:if test="${detailedSenti!=null}">${detailedSenti.value}:</c:if>feedback${feed.id}"
										value=0 /><label for="WeakPositive${feed.id}">Weak Positive</label> <input
										type="radio" id="Positive${feed.id}"
										name="<c:if test="${detailedSenti!=null}">${detailedSenti.value}:</c:if>feedback${feed.id}"
										value=1 /><label for="Positive${feed.id}">Positive</label> <input
										type="radio" id="StrongPositive${feed.id}"
										name="<c:if test="${detailedSenti!=null}">${detailedSenti.value}:</c:if>feedback${feed.id}"
										value=2 /><label for="StrongPositive${feed.id}">Strong Positive</label>
									</li>
									<li class="even">Positive</li>
									 <li >
									<input
										type="radio" id="Neutral${feed.id}"
										name="<c:if test="${detailedSenti!=null}">${detailedSenti.value}:</c:if>feedback${feed.id}"
										value=6 /><label for="Neutral${feed.id}">Neutral</label>
									</li> 
									<li class="even">Neutral</li>
										<li><input type="radio" id="WeakNegative${feed.id}"
										name="<c:if test="${detailedSenti!=null}">${detailedSenti.value}:</c:if>feedback${feed.id}"
										value=3 /><label for="WeakNegative${feed.id}">Weak
											Negative</label> <input type="radio" id="Negative${feed.id}"
										name="<c:if test="${detailedSenti!=null}">${detailedSenti.value}:</c:if>feedback${feed.id}"
										value=4 /><label for="Negative${feed.id}">Negative</label> <input
										type="radio" id="StrongNegative${feed.id}"
										name="<c:if test="${detailedSenti!=null}">${detailedSenti.value}:</c:if>feedback${feed.id}"
										value=5 /><label for="StrongNegative${feed.id}">Strong
											Negative</label>
											 
									</li>
									<li class="even"><input type="submit" value="Update Sentiment"
										class="blueBtn"
										onclick="giveFeedback('idFeedback${feed.id}','${feed.id}',
										<c:choose><c:when test="${detailedSenti!=null}">${detailedSenti.value}</c:when><c:otherwise>null</c:otherwise></c:choose>
										); return false;">


									</li>
									
								


								</ul>
							</div>
							<div class="fix"></div>
						</div>
					</div>
					<%-- <c:if test="${feed.sentimentFactors != null  }">
						<c:set var="sentimentFactors" value="${feed.sentimentFactors}" />
						<c:forEach var="entry" items="${sentimentFactors}">
							<c:if test="${entry.value != null}">
								<div class="supTicket">
									<div class="issueType">
										<span class="issueInfo"><c:out value="${entry.key}" />
										</span>
										<div class="fix"></div>
									</div>
									<c:forEach var="value" items="${entry.value}">
										<div class="issueSummary">
											<c:out value="${value}" />
											<div class="ticketInfo">
												<ul>
													<li><input type="radio" id="radio1"
														name="${value}sentifeedback${feed.id}" value=0 /><label
														for="radio1">Weak Positive</label> <input type="radio"
														id="radio1" name="${value}sentifeedback${feed.id}" value=1 /><label
														for="radio1">Positive</label> <input type="radio"
														id="radio1" name="${value}sentifeedback${feed.id}" value=2 /><label
														for="radio1">Strong Positive</label>
													</li>
													<li class="even"><input type="submit"
														value="Update Sentiment" class="blueBtn"
														onclick="updateSentiment($('input:radio[name={value}sentifeedback${feed.id}]:checked').val(),'${feed.id}'); return false;">


													</li>
													<li><input type="radio" id="radio1"
														name="${value}sentifeedback${feed.id}" value=3
														checked="checked" /><label for="radio1">Weak
															Negative</label> <input type="radio" id="radio1"
														name="${value}sentifeedback${feed.id}" value=4
														checked="checked" /><label for="radio1">Negative</label>
														<input type="radio" id="radio1"
														name="${value}sentifeedback${feed.id}" value=5
														checked="checked" /><label for="radio1">Strong
															Negative</label>
													</li>


												</ul>
												<div class="fix"></div>
											</div>
											<div class="fix"></div>
										</div>
									</c:forEach>

								</div>
							</c:if>
						</c:forEach>
		</c:if> --%>

				</div>
			</div>
			<div id="idPattern${feed.id}" class="tagPopUp" style="display: none;">
				<form class="mainForm">

					Word:<input type="text" id="idword${feed.id}" value="" /><br />
					Pattern:<input type="text" id="idPatternText${feed.id}" value="" /><br />
					Score:<input type="text" id="idScore${feed.id}" value="" /><br />
					<input type="submit" value="Add Pattern" class="blueBtn"
						onclick="AddPattern($('#idword${feed.id}').val(),$('#idPatternText${feed.id}').val(),$('#idScore${feed.id}').val()); return false;">

				</form>
			</div>
			<div id="idNegation${feed.id}" class="tagPopUp"
				style="display: none;">

				<form class="mainForm">
					Negation:<input type="text" id="idNegationWord${feed.id}" value="" /><br />
					<input type="submit" value="Add Negation" class="blueBtn"
						onclick="AddNegation($('#idNegationWord${feed.id}').val()); return false;">
				</form>

			</div>
			<div id="idModifier${feed.id}" class="tagPopUp"
				style="display: none;">

				<form class="mainForm">
					Modifier:<input type="text" id="idModifierWord${feed.id}" value="" /><br />
					Score:<input type="text" id="idModifierScore${feed.id}" value="" /><br />
					<input type="submit" value="Add Modifier" class="blueBtn"
						onclick="AddModifier($('#idModifierWord${feed.id}').val(),$('#idModifierScore${feed.id}').val()); return false;">
				</form>

			</div>


		</c:if>
	</div>
	</c:forEach>
	</div>
	</c:if>

	</div>
</body>
</html>