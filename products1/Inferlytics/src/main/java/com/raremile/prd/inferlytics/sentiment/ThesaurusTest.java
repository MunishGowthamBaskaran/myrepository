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

/**
 * @author pratyusha
 * @created 08-Jul-2013
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ThesaurusTest { 
	private static final Logger LOG = Logger.getLogger(ThesaurusTest.class);
  public static void main(String[] args) { 

// NOTE: replace test_only with your own key 
		try {
			new SendRequest("homosexual", "en_US", "4tufrLjtUZTOEmCJCuKS",
					"json");
		} catch (JSONException e) {

			LOG.error("JSONException while performing operation in main", e);
		}
  } 
} // end of Thesaurus 

class SendRequest { 
  final String endpoint = "http://thesaurus.altervista.org/thesaurus/v1"; 

	public SendRequest(String word, String language, String key, String output)
			throws JSONException {
    try { 
      URL serverAddress = new URL(endpoint + "?word="+URLEncoder.encode(word, "UTF-8")+"&language="+language+"&key="+key+"&output="+output); 
      HttpURLConnection connection = (HttpURLConnection)serverAddress.openConnection(); 
      connection.connect(); 
      int rc = connection.getResponseCode(); 
      if (rc == 200) { 
        String line = null; 
        BufferedReader br = new BufferedReader(new java.io.InputStreamReader(connection.getInputStream())); 
        StringBuilder sb = new StringBuilder(); 
        while ((line = br.readLine()) != null) {
			sb.append(line + '\n');
		} 

				if (sb.toString().equals("[]")) {
					System.out.println("No Result");
				} else {

				JSONObject req = new JSONObject(sb.toString());
				JSONArray array = req.getJSONArray("response");

				/*
				 * JSONObject obj = (JSONObject) JSONValue.parse(sb.toString());
				 * JSONArray array = (JSONArray)obj.get("response");
				 */
				for (int i = 0; i < array.length(); i++) {
          JSONObject list = (JSONObject) ((JSONObject)array.get(i)).get("list"); 
          System.out.println(list.get("category")+":"+list.get("synonyms")); 
					}
        } 
      } else {
		System.out.println("HTTP error:"+rc);
	} 
      connection.disconnect(); 
    } catch (java.net.MalformedURLException e) { 
      e.printStackTrace(); 
    } catch (java.net.ProtocolException e) { 
      e.printStackTrace(); 
    } catch (java.io.IOException e) { 
      e.printStackTrace(); 
    } 
  } 
} // end of SendRequest