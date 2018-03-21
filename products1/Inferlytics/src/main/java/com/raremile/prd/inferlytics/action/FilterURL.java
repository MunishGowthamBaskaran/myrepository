package com.raremile.prd.inferlytics.action;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet Filter implementation class FilterURL
 */
public class FilterURL implements Filter {

	private static final Logger LOG = Logger.getLogger(FilterURL.class);

	/**
	 * Default constructor.
	 */
	public FilterURL() {
		// TODO Auto-generated constructor stub

	}

	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// place your code here
		HttpServletRequest request1 = (HttpServletRequest) request;
		String requestURI = request1.getRequestURI();
		String values[] = requestURI.split("/");
		LOG.info(requestURI + "------------");
		// pass the request along the filter chain

		if (requestURI.contains("products_fragrance")) {
			String productUrlValues[] = requestURI.split("products_fragrance/");
			HttpServletResponse resp = (HttpServletResponse) response;
			RequestDispatcher x = request1
					.getRequestDispatcher("/widget-3.jsp?productName="
							+ productUrlValues[1]);
			x.forward(request, resp);

			return;

		} else if (requestURI.contains("products_menshoes")) {
			String productUrlValues[] = requestURI.split("products_menshoes/");
			HttpServletResponse resp = (HttpServletResponse) response;
			RequestDispatcher x = request1
					.getRequestDispatcher("/widget-4.jsp?productName="
							+ productUrlValues[1]);
			x.forward(request, resp);

			return;

		} else if (requestURI.contains("products_womencoats")) {
			String productUrlValues[] = requestURI
					.split("products_womencoats/");
			HttpServletResponse resp = (HttpServletResponse) response;
			RequestDispatcher x = request1
					.getRequestDispatcher("/widget-5.jsp?productName="
							+ productUrlValues[1]);
			x.forward(request, resp);

			return;
		} else if (requestURI.contains("products_amplifiers")) {
			String productUrlValues[] = requestURI
					.split("products_amplifiers/");
			HttpServletResponse resp = (HttpServletResponse) response;
			RequestDispatcher x = request1
					.getRequestDispatcher("/widget-6.jsp?productName="
							+ productUrlValues[1]);
			x.forward(request, resp);

			return;
		} else if (requestURI.contains("products_effects")) {
			String productUrlValues[] = requestURI.split("products_effects/");
			HttpServletResponse resp = (HttpServletResponse) response;
			RequestDispatcher x = request1
					.getRequestDispatcher("/widget-7.jsp?productName="
							+ productUrlValues[1]);
			x.forward(request, resp);

			return;
		} else if (requestURI.contains("products_wines")) {
			String productUrlValues[] = requestURI.split("products_wines/");
			HttpServletResponse resp = (HttpServletResponse) response;
			RequestDispatcher x = request1
					.getRequestDispatcher("/widget-8.jsp?productName="
							+ productUrlValues[1]);
			x.forward(request, resp);

			return;
		} else if (requestURI.contains("products_tescowines")) {
			String productUrlValues[] = requestURI
					.split("products_tescowines/");
			HttpServletResponse resp = (HttpServletResponse) response;
			RequestDispatcher x = request1
					.getRequestDispatcher("/widget-9.jsp?productName="
							+ productUrlValues[1]);
			x.forward(request, resp);

			return;
		} else if (requestURI.contains("products_oldnavy")) {
			String productUrlValues[] = requestURI.split("products_oldnavy/");
			HttpServletResponse resp = (HttpServletResponse) response;
			RequestDispatcher x = request1
					.getRequestDispatcher("/widget-11.jsp?productName="
							+ productUrlValues[1]);
			x.forward(request, resp);

			return;
		}
		/*
		 * else if (values.length == 6 && requestURI.contains("nikeproducts")) {
		 * HttpServletResponse resp = (HttpServletResponse) response;
		 * RequestDispatcher x = request1
		 * .getRequestDispatcher("/widget-4.jsp?productName=" + values[5] +
		 * "&entity=" + values[2] + "&subProduct=" + values[3]);
		 * x.forward(request, resp);
		 * 
		 * return;
		 * 
		 * }
		 */
		else if (values.length == 6 && requestURI.contains("products")) {
			HttpServletResponse resp = (HttpServletResponse) response;
			RequestDispatcher x = request1
					.getRequestDispatcher("/widget-2.jsp?productName="
							+ values[5] + "&entity=" + values[2]
									+ "&subProduct=" + values[3]);
			x.forward(request, resp);

			return;
		} else if ((values.length == 4 || values.length == 5)
				&& !(requestURI.contains("GetKeyWords"))
				&& !(requestURI.contains("getPostsFromMongo"))
				&& !(requestURI.contains("ROOT"))
				&& !(requestURI.contains("pages"))
				&& !(requestURI.contains("images"))
				&& !(requestURI.contains("TEST"))
				&& !(requestURI.contains("Analytics"))
				&& !(requestURI.contains("yahoo"))
				&& !(requestURI.contains("eventsanalytics"))
				&& !(requestURI.contains("rest"))) {

			String entity = values[2];
			String subProduct = values[3];
			String category = new String();
			if (values.length == 4) {
				category = "null";
			} else {
				category = values[4];

			}

			HttpServletResponse resp = (HttpServletResponse) response;
			RequestDispatcher x = request1
					.getRequestDispatcher("/widget-1.jsp?entity=" + entity
							+ "&subProduct=" + subProduct + "&category="
							+ category);
			// run the save
			x.forward(request, resp);
			return;

		} else {
			chain.doFilter(request, response);
		}

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
