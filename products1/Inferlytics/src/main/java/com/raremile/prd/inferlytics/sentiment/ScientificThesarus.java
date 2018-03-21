/**
 *  * Copyright (c) 2013 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.sentiment;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author pratyusha
 * @created 16-Jul-2013
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class ScientificThesarus {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(ScientificThesarus.class);
	private static int counter = 0;
	public static Connection.Response openURL(String url) throws IOException {
		counter++;
		Connection.Response res = Jsoup
				.connect(url.trim())
				.userAgent(
						"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
				.timeout(100000).ignoreHttpErrors(true)
				.referrer("http://www.google.com").execute();
		LOG.info("Called Scientific thesarus these many times -->" + counter
				+ " WITH RESPONSE "
				+ res);
		return res;
	}

	public static List<String> findSynonyms(String word) throws IOException {
		String url = "http://dico.isc.cnrs.fr/dico/en/search?b=1&r=" + word
				+ "&send=Look+it+up";

		List<String> synonyms = new ArrayList<String>();

		Connection.Response response = openURL(url.trim());
		if (response.statusCode() == HttpURLConnection.HTTP_OK
				&& response.contentType().contains("text/html")) {
			if (!response.parse().toString().contains("you have exceeded")) {
			Document doc = Jsoup.parse(response.parse().toString());
			Elements links = doc.select("a[href]");
			for (Element link : links) {
				if (link.attr("href") != null
						&& link.attr("href").contains("b=1")) {
					synonyms.add(link.text());
				}
			}
			} else {
				return null;
			}
	}
		return synonyms;

	}

	public static void main(String[] s) {

		String word = "lowermost";

		try {
			List<String> synonyms = findSynonyms(word);
			System.out.println(synonyms);
		} catch (IOException e) {

			LOG.error("IOException while performing operation in main", e);
		}
	}

}
