package com.raremile.prd.inferlytics.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBObject;
import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.entity.ProdCommentAnalticInfo;
import com.raremile.prd.inferlytics.utils.ActivityLog;

/**
 * Servlet implementation class DoCaptureAnalytics
 */
public class DoCaptureAnalytics extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DoCaptureAnalytics() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String brand=request.getParameter("entity");
		String subProduct=request.getParameter("subProduct");
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		String eventType = request.getParameter("eventType");
		String callFrom = request.getParameter("callFrom");
		if (null != callFrom && !callFrom.equals("undefined")) {
			if (callFrom.equals("widget1")) {
				if (null != eventType && !eventType.equals("undefined")) {

					// register main widget click event in Analytics table
					if (eventType.equals("mainWidget")) {
						ActivityLog.addClientDetailstoLog(brand,subProduct,"mainWidget", null,
								null, null, null, null, ipAddress);
					} else if (eventType.equals("readReview")) {

						String word = request.getParameter("word");
						String postId = request.getParameter("postId");
						String[] productIds = postId.split(":");
						String reviewType=request.getParameter("reviewType");
						if(word.equals(""))
							word=null;
						ActivityLog.addClientDetailstoLog(brand,subProduct,"feature",
								request.getParameter("SubDim"), word,
								productIds[0], reviewType, postId, ipAddress);

					}

					else if (eventType.equals("productDetail")) {

						String word = request.getParameter("word");
						ActivityLog.addClientDetailstoLog(brand,subProduct,"feature",
								request.getParameter("subdimension"), word,
								request.getParameter("productId"), null, null,
								ipAddress);

					}

				}

			} else if (callFrom.equals("otherWidget")) {
				if (eventType.equals("readPost")) {
					String word = request.getParameter("word");
					String postId = request.getParameter("postId");
					String[] productIds = postId.split(":");
					ActivityLog.addClientDetailstoLog(brand,subProduct,"keyword", null, word,
							productIds[0], null, postId, ipAddress);
				} else if (eventType.equals("readPostForNike")) {
					String word = request.getParameter("word");
					ActivityLog.addClientDetailstoLog(brand,subProduct,"keyword", null, word,
							request.getParameter("productId"), null,
							request.getParameter("postId"), ipAddress);
				}

			}
		}

	}

}
