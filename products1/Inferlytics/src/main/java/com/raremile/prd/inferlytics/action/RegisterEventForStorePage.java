package com.raremile.prd.inferlytics.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.raremile.prd.inferlytics.utils.ActivityLog;

/**
 * Servlet implementation class RegisterEventForStorePage
 */
public class RegisterEventForStorePage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterEventForStorePage() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub

		String type = request.getParameter("type");
		String productId = request.getParameter("productId");
		String entity = request.getParameter("entity");
		String subProduct = request.getParameter("subProduct");
		String category = request.getParameter("category");
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		if (productId != null && productId.isEmpty()) {
			productId = null;
		}
		Object userName = request.getSession().getAttribute("userName");
		String name = null;
		if (userName != null) {
			name = (String) userName;
		}
		if (type.equals("registerProduct")) {
			if (name != null && !name.contains("dev")) {
				ActivityLog.addClientDetailstoLog(entity, subProduct, "store",
						null, null, productId, null, null, ipAddress);
			}
		} else {
			String word = request.getParameter("word");
			String subDimension = request.getParameter("subDimension");
			String postId = request.getParameter("postId");
			String reviewType = request.getParameter("reviewType");
			if (name != null && !name.contains("dev")) {
				ActivityLog.addClientDetailstoLog(entity, subProduct, category,
						subDimension, word, productId, reviewType, postId,
						ipAddress);
			}
		}
	}

}
