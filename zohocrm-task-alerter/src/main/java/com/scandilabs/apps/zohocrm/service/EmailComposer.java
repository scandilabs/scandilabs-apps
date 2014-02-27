package com.scandilabs.apps.zohocrm.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;

import com.scandilabs.apps.zohocrm.entity.User;
import com.scandilabs.apps.zohocrm.util.ApplicationUtils;
import com.scandilabs.catamaran.mail.send.SimpleHtmlMailSender;

/**
 * Queries Zoho data and sends email summary
 * @author mkvalsvik
 *
 */
public class EmailComposer {

	private ZohoCrmApiService zohoCrmApiService;
	private SimpleHtmlMailSender mailSender;
	
	private String emailUrlPrefix;	
	
	private Logger logger = LoggerFactory.getLogger(EmailComposer.class);
	
	public String composeContactsHistoryBodyHtml(User user, List<JSONObject> contacts, boolean emailMode) {
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
        	String phone = ApplicationUtils.getField(fields, "Phone");
        	String description = ApplicationUtils.getField(fields, "Description");
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
    		if (email != null) {
        		body.append(" - ");
        		body.append("<a href='mailto:" + email + "'>" + email + "</a>");
    		}
    		if (phone != null) {
        		body.append(" - ");
	    		body.append("<a href='tel:" + phone + "'>" + phone + "</a>");
    		}		
    		body.append("</td>");
    		body.append("<td>&nbsp;</td>");
    		
    		body.append("<td>");
    		body.append(account);
    		body.append("</td>");
    		body.append("<td>&nbsp;</td>");
    		
    		body.append("<td style='text-align:right'>");
    		body.append(ApplicationUtils.TABLE_DISPLAY_DATE_FORMAT.format(lastActivity));
    		body.append("</td>");
    		
    		body.append("</tr>");
    		
            // Get notes and add one note per line/row
            List<Row> rows = this.zohoCrmApiService.loadNotes(user.getZohoAuthToken(), contactId, RecordType.Contacts);
			for (Row row : rows) {
            	Date noteDate = (Date) row.getFieldMap().get("Modified Time");
				body.append("<tr>");
				body.append("<td colspan='5'>");				
				body.append(ApplicationUtils.TABLE_DISPLAY_DATE_FORMAT.format(noteDate));
				body.append(" note from ");
				body.append(row.getFieldMap().get("Modified By"));
				body.append(": ");
				body.append(row.getFieldMap().get("Note Content"));
				body.append("</td>");
				body.append("</tr>");
			}
			
    		// Description if found
			if (description != null) {
        		body.append("<tr>");
				body.append("<td colspan='5'>");
				body.append("Description: " + description);
				body.append("</td>");
				body.append("</tr>");
			}
    		
			// Action menu line
			String userTokenUrlSuffix = "";
			if (emailMode) {
				userTokenUrlSuffix = "?user=" + user.getApiAuthToken();
			}
			
    		body.append("<tr>");
    		
    		body.append("<td>");
    		body.append("<a href='" + this.emailUrlPrefix + "/contacts/view/" + contactId + userTokenUrlSuffix + "'>VIEW</a>");
    		body.append("</td>");
    		body.append("<td>&nbsp;</td>");
    		body.append("<td>");
    		body.append("<a href='" + this.emailUrlPrefix + "/contacts/postpone/" + contactId + userTokenUrlSuffix + "'>POSTPONE</a>");        		
    		body.append("</td>");
    		body.append("<td>&nbsp;</td>");
    		body.append("<td>");
    		body.append("<a href='" + this.emailUrlPrefix + "/contacts/cancel/" + contactId + userTokenUrlSuffix + "'>CANCEL</a>");        		
    		body.append("</td>");
    		body.append("</tr>");
    		
    		// Horizontal rule line
    		body.append("<tr>");
    		body.append("<td colspan='5'>");
    		body.append("<hr/>");
    		body.append("</td>");
    		body.append("</tr>");
		}
		
		body.append("</table>");
		
		return body.toString();
	}
	
	public void sendContactNextCallEmail(User user) {
		SimpleDateFormat subjectDateFormat = new SimpleDateFormat("MMMM d");		

    	try {
    		List<JSONObject> contacts = this.zohoCrmApiService.listContactsWithNextCallDateDue(user.getZohoAuthToken());
    		if (contacts.isEmpty()) {
    			logger.info("Exiting, no contacts to call today");
    			return;
    		}
    		    		
    		SimpleMailMessage message = new SimpleMailMessage();
    		message.setTo(user.getEmail());
    		message.setSubject(String.format("%s - %d contacts to call", subjectDateFormat.format(new Date()), contacts.size()));
			String bodyStr = this.composeContactsHistoryBodyHtml(user, contacts, true);
    		message.setText(bodyStr);            
    		mailSender.send(message);        
			logger.debug("Finished sending email");
    		
    	} catch (Exception e) {
    		logger.error("Daily Email Daemon run error", e);
    	}
	}

	public void setZohoCrmApiService(ZohoCrmApiService zohoCrmApiService) {
		this.zohoCrmApiService = zohoCrmApiService;
	}

	public void setMailSender(SimpleHtmlMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setEmailUrlPrefix(String emailUrlPrefix) {
		this.emailUrlPrefix = emailUrlPrefix;
	}

}
