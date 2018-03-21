package com.raremile.prd.inferlytics.utils;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.commons.FilePropertyManager;

public class ActivityScheduler {
	private static final Logger LOG = Logger.getLogger(ActivityScheduler.class);

	/**
	 * @param args
	 */

	public static void scheduleActivity(){
		// TODO Auto-generated method stub
		try {
			SchedulerFactory sf = new StdSchedulerFactory();
			Scheduler scheduler = sf.getScheduler();
			scheduler.start();
			JobDetail job = JobBuilder.newJob(ActivityManager.class)
					.withIdentity("ActivityManager").build();
			CronTrigger cronTrigger = TriggerBuilder
					.newTrigger()
					.withIdentity("crontrigger", "crontriggergroup1")
					.withSchedule(
							CronScheduleBuilder.cronSchedule(FilePropertyManager.getProperty(
									ApplicationConstants.APPLICATION_PROPERTIES_FILE,
									ApplicationConstants.CRON_REGEX)))
					.build();

			scheduler.scheduleJob(job, cronTrigger);
		} catch (Exception e) {
			
			LOG.info("Error in scheduling job.", e);
		}
	}

}
