package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.raremile.prd.inferlytics.connectors.TripAdvisorConnector;



/**
 * Servlet implementation class DoSentimentAnalysisFromTripAdvisor
 */
public class DoSentimentAnalysisFromTripAdvisor extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger
			.getLogger(DoSentimentAnalysisFromTripAdvisor.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DoSentimentAnalysisFromTripAdvisor() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("AnalyseSentiment.jsp").forward(request,
				response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOG.info("Entered servlet here in DoSentimentAnalysisFromTripAdvisor");
		String url = // "http://www.tripadvisor.com/Hotel_Review-g154998-d254839-Reviews-Niagara_Plaza_Hotel_Conference_Centre-Niagara_Falls_Ontario.html";
		"http://www.tripadvisor.com/Hotel_Review-g52970-d1098764-Reviews-BEST_WESTERN_PREMIER_Eden_Resort_Suites-Lancaster_Lancaster_County_Pennsylvania.html";
		// searchTripAdvisor(url, "Niagara Plaza");
		try {
			TripAdvisorConnector.searchTripAdvisor(url,
					"WESTERN PREMIER Eden Resort");
		} catch (ParseException e) {

			LOG.error("ParseException while performing operation in doPost", e);
		}
	}

}