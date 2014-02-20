package com.scandilabs.apps.zohocrm.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

import com.scandilabs.apps.zohocrm.util.ApplicationUtils;
import com.scandilabs.apps.zohocrm.web.UserController;
import com.scandilabs.catamaran.mail.send.SimpleHtmlMailSender;

public class DailyEmailDaemon extends TimerTask {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private ZohoCrmApiService zohoCrmApiService;
    
    @Autowired
    private SimpleHtmlMailSender mailSender;
    
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
        timer.schedule(this, 4000, 60000);
        logger.info(String.format(
                "Scheduled for run in %dms, and then every %dms", 4000,
                60000));

        // Initialize last run time -- note we're not trying to 'catch up' with
        // alerts that may have been sent while the service was down
        this.lastRunStartTime = new Date();
    }    

    public void run() {
    	
    	if (stopped) {
    		logger.info("Daemon service is stopped, run() cancelled.");
    		return;
    	}

		SimpleDateFormat subjectDateFormat = new SimpleDateFormat("MMMM d hh:mm:ss");
		SimpleDateFormat tableDateFormat = new SimpleDateFormat("d/M/yy");

    	try {
    		List<JSONObject> contacts = this.zohoCrmApiService.listContactsWithNextCallDateDue();
    		    		
    		SimpleMailMessage message = new SimpleMailMessage();
    		message.setTo("mkvalsvik@scandilabs.com");
    		message.setSubject(String.format("%s - %d contacts to call", subjectDateFormat.format(new Date()), contacts.size()));
    		StringBuilder body = new StringBuilder();
    		body.append("<table>");
    		
    		for (JSONObject contact : contacts) {
    			
    			// Extract field data
    			JSONArray fields = contact.getJSONArray("FL");
            	String firstName = ApplicationUtils.getField(fields, "First Name");
            	String lastName = ApplicationUtils.getField(fields, "Last Name");
            	String owner = ApplicationUtils.getField(fields, "Contact Owner");
            	String account = ApplicationUtils.getField(fields, "Account Name");
            	String email = ApplicationUtils.getField(fields, "Email");
            	String lastActivityStr = ApplicationUtils.getField(fields, "Last Activity Time");
            	String nextCallDateStr = ApplicationUtils.getField(fields, "Next Call Date");
            	String contactId = ApplicationUtils.getField(fields, "CONTACTID");
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
            	
                // Populate email body from contact
            	logger.debug(String.format("It's time to call %s %s from %s", firstName, lastName, account));
        		body.append("<tr>");
        		body.append("<td>");
        		body.append(String.format("%s %s", firstName, lastName));
        		body.append("</td>");
        		body.append("<td>&nbsp;</td>");
        		
        		body.append("<td>");
        		body.append(account);
        		body.append("</td>");
        		body.append("<td>&nbsp;</td>");
        		
        		body.append("<td style='text-align:right'>");
        		body.append(tableDateFormat.format(lastActivity));
        		body.append("</td>");
        		
        		body.append("</tr>");
        		body.append("<tr>");
        		
        		body.append("<td>");
        		body.append("<a href='http://localhost:8081/contacts/view/" + contactId + "'>view</a>");
        		body.append("</td>");
        		body.append("<td>&nbsp;</td>");
        		body.append("<td>");
        		body.append("<a href='http://localhost:8081/contacts/postpone/" + contactId + "'>postpone</a>");        		
        		body.append("</td>");
        		
        		body.append("</tr>");
        		body.append("<tr>");
        		body.append("<td colspan='5'>");
        		body.append("<hr/>");
        		body.append("</td>");
        		body.append("</tr>");
    		}
    		
    		// Send
    		body.append("</table>");
    		message.setText(body.toString());            
    		mailSender.send(message);        

    		
    	} catch (Exception e) {
    		logger.error("Daily Email Daemon run error", e);
    	}
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
