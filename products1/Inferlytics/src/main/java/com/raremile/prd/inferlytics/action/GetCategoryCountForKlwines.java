package com.raremile.prd.inferlytics.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.raremile.prd.inferlytics.database.MongoConnector;
import com.raremile.prd.inferlytics.entity.KlwinesCategory;

/**
 * Servlet implementation class GetCategoryCountForKlwines
 */
public class GetCategoryCountForKlwines extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetCategoryCountForKlwines() {
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

		String subDimension=request.getParameter("subDimension");
		String synonymWord = request.getParameter("synonymword");
		String returnJson = "";
		String type = request.getParameter("type");

		if (type.equals("country")) {
			returnJson = MongoConnector.getCountriesForKlWines(subDimension,
					synonymWord);
		} else if (type.equals("subRegion")) {
			String country = request.getParameter("country");
			returnJson = MongoConnector.getsubRegionForKlWines(subDimension,
					synonymWord, country);
		} else {
			String klwinesCountry = request.getParameter("selectedCountry");
			String klwinesSubRegion = request.getParameter("selectedSubRegion");
			KlwinesCategory klwinesdata = MongoConnector
					.getklwinesCategoryCount(subDimension, synonymWord,
							klwinesCountry, klwinesSubRegion);
			returnJson = new Gson().toJson(klwinesdata);
		}
		PrintWriter writer = response.getWriter();
		writer.write(returnJson);
		writer.flush();
		writer.close();

	}

}
