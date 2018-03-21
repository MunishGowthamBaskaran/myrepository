package com.raremile.prd.inferlytics.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class WordScoreUpdateJob implements Job {

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		
		String text = "";
		try {
			StringBuffer sb = new StringBuffer();
			BufferedReader reader = new BufferedReader(new FileReader(
					"DBScripts/UpdateWPS_Score.sql"));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			text = sb.toString();
			reader.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Operator.getInstance().updateWordScoreJob(text);
		
	}

}
