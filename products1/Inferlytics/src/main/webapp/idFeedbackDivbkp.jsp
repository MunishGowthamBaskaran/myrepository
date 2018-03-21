			<!-- Support tickets -->
			<div id="idFeedback${feed.id}" class="tagPopUp"
				style="display: none;">
				<div class="widget">
					<div class="head">
						<h5 class="iHelp">Feedback</h5>
					</div>

					<div class="supTicket nobg">
						<div class="issueType">
							<span class="issueInfo">Overall Feedback</span>
							<div class="fix"></div>
						</div>

						<div class="issueSummary">

							<div class="ticketInfo ">
								<ul>
									<li><input type="radio" id="radio1"
										name="feedback${feed.id}" value=0 checked="checked" /><label
										for="radio1">Correct</label>&nbsp;<input type="radio"
										id="radio2" value=1 name="feedback${feed.id}" /><label
										for="radio2">InCorrect</label>
									</li>
									<li class="even"><input type="submit"
										value="Give Feedback" class="blueBtn"
										onclick="giveFeedback($('input:radio[name=feedback${feed.id}]:checked').val(),'${feed.id}'); return false;">
									</li>
								</ul>

							</div>
							<div class="fix"></div>
						</div>
					</div>
					<c:if test="${feed.sentimentFactors != null  }">
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
					</c:if>

				</div>
			</div>