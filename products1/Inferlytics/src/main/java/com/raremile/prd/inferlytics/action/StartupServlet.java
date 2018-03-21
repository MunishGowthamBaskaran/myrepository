package com.raremile.prd.inferlytics.action;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.raremile.prd.inferlytics.preprocessing.CacheManager;
import com.raremile.prd.inferlytics.utils.ActivityManager;
import com.raremile.prd.inferlytics.utils.ActivityScheduler;




public class StartupServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(StartupServlet.class);

	@Override
	public void init() throws ServletException {

		try {
			
			LOG.info("Initializing Scheduler Servlet");
			CacheManager.instantiateIds();
			ActivityScheduler.scheduleActivity();
			LOG.info("Leaving Startup servlet");
			//new WordScoreUpdateScheduler();
		} catch (Exception ex) {
			LOG.error("Exception in startup", ex);
		}
	}
	
	public void destroy() {
	   ActivityManager.insertActivityToDB(false);
	   LOG.info("called Activity Manager in servlet destroy method");
	  }


	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}
}
