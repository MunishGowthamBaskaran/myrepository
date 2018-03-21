package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.utils.ActivityLog;
import com.raremile.prd.inferlytics.utils.UserCredentials;

/**
 * Servlet implementation class Login
 */
public class Login extends HttpServlet {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(Login.class);
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
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

		String username = request.getParameter("userName");
		String password = request.getParameter("password");
		HttpSession session = request.getSession(true);
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		UserCredentials userLogin = null;
		try{
			userLogin = DAOFactory
					.getInstance(ApplicationConstants.USERDETAILS_PROPERTIES_FILE)
					.getValidator().verifyCredentials(username, password);
			if (userLogin.isSuccess()) {
				if (userLogin.getUserType().equals(1)
						&& userLogin.getUserName() != null
						&& !userLogin.getUserName().contains("dev")) {
					ActivityLog.addClientDetailstoLog(
							userLogin.getEntityName(),
							userLogin.getSubProduct(), "login", null, null,
							null, null, null, ipAddress);
				}
				session.setAttribute("userId", userLogin.getUserId());
				session.setAttribute("userName", userLogin.getUserName());
				session.setAttribute("user", userLogin);
				// If success redirect directly from here, otherwise
			}
		}catch(Exception Ex){
			LOG.error("Exception occured while checking user credentials.");
		}
		PrintWriter writer = response.getWriter();
		writer.write(new Gson().toJson(userLogin));
		writer.flush();
		writer.close();

	}

}
