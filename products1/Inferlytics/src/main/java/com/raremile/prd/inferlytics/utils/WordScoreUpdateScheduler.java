package com.raremile.prd.inferlytics.utils;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class WordScoreUpdateScheduler {
	private static final Logger LOG = Logger.getLogger(WordScoreUpdateScheduler.class);
	
	public WordScoreUpdateScheduler(){/*
		
		try{
		final SchedulerFactory scheduleFac = new StdSchedulerFactory();
		final Scheduler scheduler = scheduleFac.getScheduler();
		scheduler.start();
		final JobDetail jDetail = new JobDetail(
				"WordScoreUpdateJobDetail",
				"WordScoreUpdateJob",
				WordScoreUpdateJob.class);
		final CronTrigger crTrigger = new CronTrigger("cronTrigger",
				"WordScoreUpdateJob", "* 21 * * * ");
		scheduler.scheduleJob(jDetail, crTrigger);
	} catch (Exception e) {
		LOG.info("Error in scheduling job.", e);
	}
	*/}
	
	
}
