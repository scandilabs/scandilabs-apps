package com.scandilabs.apps.zohocrm.service.cron;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.scandilabs.apps.zohocrm.entity.support.Repository;
import com.scandilabs.apps.zohocrm.entity.support.UserIdConstants;
import com.scandilabs.apps.zohocrm.service.EmailComposer;
import com.scandilabs.apps.zohocrm.service.zoho.ZohoCrmApiService;
import com.scandilabs.catamaran.mail.send.SimpleHtmlMailSender;

public class DailyEmailDaemon extends TimerTask {

    private Logger logger = LoggerFactory.getLogger(DailyEmailDaemon.class);

    @Autowired
    private ZohoCrmApiService zohoCrmApiService;
    
    @Autowired
    private SimpleHtmlMailSender mailSender;
    
    @Autowired
    private EmailComposer emailComposer;
    
    @Autowired
    Repository repository;
    
    private boolean stopped;
    
    private static Timer timer = new Timer("Daily-Email", true);
    private Date lastRunStartTime;
    
    /**
     * Starts the daemon service
     */
    public void init() {
    	if (stopped) {
    		logger.info("Daemon service was stopped before it even started.");
    		return;
    	}
        logger.info("Starting daily email daemon..");
        
        // Figure out noon today
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        // Run at 7am every day.
        timer.scheduleAtFixedRate(this, cal.getTime(), 86400000);
        
        //timer.schedule(this,  4000,  600000);
        logger.info(String.format(
                "Scheduled for run in %dms, and then every %dms", 4000,
                60000));

        // Initialize last run time -- note we're not trying to 'catch up' with
        // alerts that may have been sent while the service was down
        this.lastRunStartTime = new Date();
    }    

    public void run() {
    	
    	// Slight delay
    	try {
	        Thread.sleep(1000);
        } catch (InterruptedException e) {
	        logger.error("Timer error", e);
        }
    	
    	if (stopped) {
    		logger.info("Daemon service is stopped, run() cancelled.");
    		return;
    	}

		// Scandilabs
    	emailComposer.sendContactNextCallEmail(repository.loadUser(UserIdConstants.MADS_KEY));
    	
    	// Madaket
    	emailComposer.sendContactNextCallEmail(repository.loadUser(UserIdConstants.TED_KEY));    	
    }

	public void setZohoCrmApiService(ZohoCrmApiService zohoCrmApiService) {
		this.zohoCrmApiService = zohoCrmApiService;
	}

	public boolean isStopped() {
		return stopped;
	}

	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}
}
