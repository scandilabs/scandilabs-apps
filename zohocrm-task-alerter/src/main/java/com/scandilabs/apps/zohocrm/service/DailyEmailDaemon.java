package com.scandilabs.apps.zohocrm.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.scandilabs.apps.zohocrm.util.ApplicationUtils;
import com.scandilabs.apps.zohocrm.web.UserController;

public class DailyEmailDaemon extends TimerTask {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private ZohoCrmApiService zohoCrmApiService;
    
    private static Timer timer = new Timer("Daily-Email", true);
    private Date lastRunStartTime;
    
    /**
     * Starts the daemon service
     */
    public void init() {
        logger.info("Starting daily email daemon..");
        timer.schedule(this, 4000, 60000);
        logger.info(String.format(
                "Scheduled for run in %dms, and then every %dms", 4000,
                60000));

        // Initialize last run time -- note we're not trying to 'catch up' with
        // alerts that may have been sent while the service was down
        this.lastRunStartTime = new Date();
    }    

    public void run() {
    	try {
    		List<JSONObject> contacts = this.zohoCrmApiService.listContactsWithNextCallDateDue();
    		
    		for (JSONObject contact : contacts) {
    			JSONArray fields = contact.getJSONArray("FL");
            	String firstName = ApplicationUtils.getField(fields, "First Name");
            	String lastName = ApplicationUtils.getField(fields, "Last Name");
            	String owner = ApplicationUtils.getField(fields, "Contact Owner");
            	String account = ApplicationUtils.getField(fields, "Account Name");
            	String email = ApplicationUtils.getField(fields, "Email");
            	String lastActivityStr = ApplicationUtils.getField(fields, "Last Activity Time");
            	String nextCallDateStr = ApplicationUtils.getField(fields, "Next Call Date");
            	Date nextCallDate;
                try {
    	            nextCallDate = ApplicationUtils.DATE_FORMAT.parse(nextCallDateStr);
                } catch (ParseException e) {
    	            throw new RuntimeException("Error parsing date: " + nextCallDateStr, e);
                }            	
            	Date lastActivity;
                try {
    	            lastActivity = ApplicationUtils.DATE_FORMAT.parse(lastActivityStr);
                } catch (ParseException e) {
    	            throw new RuntimeException("Error parsing date: " + lastActivityStr, e);
                }            	
            	
            	logger.debug(String.format("It's time to call %s %s from %s", firstName, lastName, account));
    		}
    		
    	} catch (Exception e) {
    		logger.error("Daily Email Daemon run error", e);
    	}
    }

	public void setZohoCrmApiService(ZohoCrmApiService zohoCrmApiService) {
		this.zohoCrmApiService = zohoCrmApiService;
	}
}
