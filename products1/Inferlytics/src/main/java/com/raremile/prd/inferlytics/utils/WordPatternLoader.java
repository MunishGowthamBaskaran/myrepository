package com.raremile.prd.inferlytics.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.database.InsertThread;
import com.raremile.prd.inferlytics.entity.WordPatternScore;



public class WordPatternLoader {
	private final String pathToSWN = "C:\\Projects\\Sentiment Analysis\\DB\\SentiwordNet\\sentiwordnet_patterndata_18Jun2013.csv";
	private List<WordPatternScore> wpsList;
	
	
	public WordPatternLoader() {

		
		
		try {
			wpsList = new ArrayList<WordPatternScore>();
			
			BufferedReader csv = new BufferedReader(new FileReader(pathToSWN));
			String line = "";
			while ((line = csv.readLine()) != null) {
				String[] data = line.split("\t");
				
				Double score = Double.parseDouble(data[2])
						- Double.parseDouble(data[3]);
				String word = data[4];
				String pattern = data[6];
				
					WordPatternScore wps = new WordPatternScore();
					wps.setWord(word);
					wps.setPattern(pattern);
					wps.setScore(score);
					wpsList.add(wps);

				if (wpsList.size() == 200) {
					InsertThread wpsThread = new InsertThread();
					wpsThread.setWpsList(wpsList);
					wpsThread.setDbMethodToInvoke("storeWordPattern");
					wpsThread.start();
					wpsList = new ArrayList<WordPatternScore>();
				}
				
			}
			System.out.println(wpsList.size());
			long timestart = System.currentTimeMillis();
			DAOFactory
					.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
					.getLexiconDAO().storeWordPattern(null, wpsList, null);
			long timeEnd = System.currentTimeMillis();
			System.out.println("  time taken to insert "
					+ (timeEnd - timestart));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] s) {
		new WordPatternLoader();
	}
}