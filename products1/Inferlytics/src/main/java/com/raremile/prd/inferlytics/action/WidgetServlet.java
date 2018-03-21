package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.raremile.prd.inferlytics.entity.WidgetFeatureEntity;
import com.raremile.prd.inferlytics.utils.BusinessUtil;



/**
 * Servlet implementation class WidgetServlet
 */
public class WidgetServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WidgetServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String brand = request.getParameter("entity");
		String subProduct = request.getParameter("subProduct");
	
		if (null != brand && null != subProduct) {
		

			List<WidgetFeatureEntity> wfeKeywordList = BusinessUtil
					.getKeywordsByEntitySubProduct(brand, subProduct);
			request.setAttribute("keywords", wfeKeywordList);
	
			request.getSession().removeAttribute("showProducts");
			request.getRequestDispatcher("EcomWidget.jsp").forward(request,
					response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String brand = request.getParameter("entity");
		String subProduct = request.getParameter("subProduct");
		String category=request.getParameter("category");
		if (null != brand & null != subProduct) {
			List<WidgetFeatureEntity> wfeFeatureList = BusinessUtil
					.getFeaturesByEntitySubProduct(brand, subProduct);
			request.setAttribute("features", wfeFeatureList);
			System.out.println("Set features in response");
			List<WidgetFeatureEntity> wfeKeywordList = BusinessUtil
					.getKeywordsByEntitySubProduct(brand, subProduct,category, false);
			request.setAttribute("keywords", wfeKeywordList);
	
			request.getSession().removeAttribute("showProducts");
			request.getRequestDispatcher("EcomWidget.jsp").forward(request,
					response);

		}
	}

}