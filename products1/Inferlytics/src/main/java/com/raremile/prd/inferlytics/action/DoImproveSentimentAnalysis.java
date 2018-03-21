package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.entity.DetailedSentiment;
import com.raremile.prd.inferlytics.entity.Feed;
import com.raremile.prd.inferlytics.entity.WordPatternScore;
import com.raremile.prd.inferlytics.exception.CriticalException;
import com.raremile.prd.inferlytics.exception.DAOException;



/**
 * Servlet implementation class DoImproveSentimentAnalysis
 */
public class DoImproveSentimentAnalysis extends HttpServlet {
	private static final Logger LOG = Logger
			.getLogger(DoImproveSentimentAnalysis.class);
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DoImproveSentimentAnalysis() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		int submitType = Integer.parseInt(request.getParameter("submitType"));

		try{
		switch (submitType) {
		case 0:
			
			List<Feed> feeds = (List<Feed>) request.getSession().getAttribute(
					"feeds");
		
			long feedId = Long.parseLong(request.getParameter("feedId"));
			int feedback = Integer.parseInt(request.getParameter("feedback"));
			LOG.info("Feedback "+feedback);
			DAOFactory.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE).getLexiconDAO().giveFeedback(DetailedSentiment.getSentimentFromInteger(feedback), feedId);
			/*Operator.getInstance()
					.updatePOSPattern(
							DetailedSentiment.getSentimentFromInteger(feedback),
							feedId);*/

			break;
		case 1:
			LOG.info("Pattern");
			WordPatternScore wps = new WordPatternScore();
			wps.setWord(request.getParameter("word"));
			wps.setPattern(request.getParameter("pattern"));
			wps.setScore(Double.parseDouble(request.getParameter("score")));
			ArrayList<WordPatternScore> wpsList = new ArrayList<WordPatternScore>();
			wpsList.trimToSize();
			//Operator.getInstance().storeWordPattern(wpsList);
				DAOFactory
						.getInstance(
								ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
						.getLexiconDAO()
.storeWordPattern(null, wpsList, null);
			break;
		case 2:
			LOG.info("Negation");
				/*
				 * Operator.getInstance().storeNegation(
				 * request.getParameter("negation"));
				 */
			break;
		case 3:
			LOG.info("Modifier");
				/*
				 * Operator.getInstance().storeModifier(
				 * request.getParameter("modifier"),
				 * Double.parseDouble(request.getParameter("score")));
				 */
			break;

		default:
			break;
		}
		}
		catch(DAOException de){
			response.setStatus(400);
		}
		catch(CriticalException ce){
			response.setStatus(400);
			/*request.getRequestDispatcher("error.jsp")
			.forward(request, response);*/
		}
	}

}