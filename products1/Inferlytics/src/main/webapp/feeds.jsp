<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

	<c:set var="feeds" value="${requestScope.feeds}" />
		<c:if test="${requestScope.feeds != null}">
			<div class="widget">
				<div class="head">
					<h5 class="iHelp">
						What people are talking about
						<c:out value="${requestScope.query}" />
					</h5>
					<div class="num">
						<a href="#" class="redNum"> <c:out value="${fn:length(feeds)}" />
						</a>
					</div>
				</div>



				<c:forEach var="feed" varStatus="counter" items='${requestScope.feeds}'>
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

						<div class="ticketInfo">
							<ul>
								<li>${feed.feedData}</li>

								<li class="even"><strong>[
										${feed.opinion.opinionOrientation.sentiment} ]</strong>
								</li>

								<li class="even" style="float: right;">${feed.feedDate}</li>
							</ul>
							<div class="fix"></div>
						</div>
			</div>

			<div class="fix"></div>

		</c:if>
	</div>
	</c:forEach>
	</div>
	</c:if>