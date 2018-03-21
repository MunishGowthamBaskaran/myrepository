package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.raremile.prd.inferlytics.connectors.TwitterConnector;
import com.raremile.prd.inferlytics.entity.Feed;
import com.raremile.prd.inferlytics.entity.Opinion;
import com.raremile.prd.inferlytics.exception.CriticalException;
import com.raremile.prd.inferlytics.sentiment.SentimentAnalysis;
import com.raremile.prd.inferlytics.utils.Util;



/**
 * Servlet implementation class DoSentimentAnalysis
 */
public class DoSentimentAnalysis extends HttpServlet {
	private static final Logger LOG = Logger
			.getLogger(DoSentimentAnalysis.class);
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		LOG.info("about to display");
		request.getRequestDispatcher("AnalyseSentiment.jsp").forward(request,
				response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		String analyse = request.getParameter("analyse");
		String query = request.getParameter("query");

		if (analyse != null && !analyse.isEmpty()) {
			Feed feed = new Feed();
			feed.setFeedData(query);
			feed.setFeedId("1111");
			Opinion opinion = new Opinion();
			opinion.setObject("nike");
			feed.setOpinion(opinion);
			SentimentAnalysis.setOpinion(feed);

			request.setAttribute("opinion", opinion);

			request.getRequestDispatcher("AnalyseSentiment.jsp").forward(
					request, response);
		} else {
		LOG.info("Query is " + query);
		try {
			List<Feed> feeds = /*DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getLexiconDAO().getFeedsForEntity(query);*/
			 TwitterConnector.searchTweets(query);

			if (feeds != null) {
				Collections.sort(feeds, Util.FEED_ORDER);
				LOG.info("Feeds size -->" + feeds.size());
				request.getSession().setAttribute("feeds", feeds);
				request.setAttribute("query", query);
			}
			request.getRequestDispatcher("AnalyseSentiment.jsp").forward(
					request, response);
		} catch (CriticalException ex) {
			request.getRequestDispatcher("error.jsp")
					.forward(request, response);
		}
		}
	}

}