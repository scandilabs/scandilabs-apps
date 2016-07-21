package com.scandilabs.apps.zohocrm.entity.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.scandilabs.apps.zohocrm.entity.GmailAccount;
import com.scandilabs.apps.zohocrm.entity.User;
import com.scandilabs.catamaran.type.Name;

public class Repository {
	
	@Autowired()
	@Qualifier("applicationProperties")
	private Properties applicationProperties;

	private Logger logger = LoggerFactory.getLogger(Repository.class);	
	
	private static final List<User> users = new ArrayList<User>();
	
	public Repository(Properties applicationProperties) {
		this.applicationProperties = applicationProperties;
	
		logger.debug("Initializing Zoho CRM users in Repository..");
//		User ted = new User();
//		ted.setZohoAuthToken("a021c528e429d198cfe9269032491435");
//		ted.setName(Name.createFromFullNameString("Ted Achtem"));
//		ted.setEmail("ted@madakethealth.com");
//		ted.setKey(UserIdConstants.TED_KEY);
//		ted.setApiAuthToken(UserIdConstants.TED_API_TOKEN);
//		users.add(ted);
		
		User mads = new User();
		mads.setZohoAuthToken("8cdb044dcad100b204412ce56de4d7b0");
		mads.setAdministrator(true);
		mads.setEmail("mkvalsvik@scandilabs.com");
		mads.setName(Name.createFromFullNameString("Mads Kvalsvik"));
		mads.setKey(UserIdConstants.MADS_KEY);
		mads.setApiAuthToken(UserIdConstants.MADS_API_TOKEN);
		
		GmailAccount scandilabsAccount = new GmailAccount();
		scandilabsAccount.setEmail(applicationProperties.getProperty("email.mads.scandilabs.user"));		
		scandilabsAccount.setPassword(applicationProperties.getProperty("email.mads.scandilabs.password"));
		mads.getGmailAccounts().add(scandilabsAccount);
		
		GmailAccount personalAccount = new GmailAccount();
		personalAccount.setEmail(applicationProperties.getProperty("email.mads.personal.user"));
		personalAccount.setPassword(applicationProperties.getProperty("email.mads.personal.password"));
		mads.getGmailAccounts().add(personalAccount);
		
		users.add(mads);		
		logger.debug("DONE Initializing Zoho CRM users in Repository.");
	}
	
	public void setApplicationProperties(Properties applicationProperties) {
	    this.applicationProperties = applicationProperties;
	}
	
	public User loadUser(String userKey) {
		for (User user : users) {
			if (user.getKey().equals(userKey)) {
				return user;
			}
		}
		return null;
	}
	
	public User loadUserByEmail(String email) {		
		for (User user : users) {
			if (user.getEmail().equalsIgnoreCase(email)) {
				logger.debug("Found user " + user.getName() + " with key " + user.getKey() + " by email " + email);
				return user;
			}
		}
		return null;
	}
	
	public User loadUserByApiToken(String token) {		
		for (User user : users) {
			if (user.getApiAuthToken().equals(token)) {
				return user;
			}
		}
		return null;
	}	

}
