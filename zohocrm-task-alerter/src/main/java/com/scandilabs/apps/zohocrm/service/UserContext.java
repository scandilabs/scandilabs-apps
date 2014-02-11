package com.scandilabs.apps.zohocrm.service;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.scandilabs.catamaran.type.Name;

/**
 * A bean that represents a wrapper around a User object stored in a session
 * @author mkvalsvik
 *
 */
@Component
@Scope("session")
public class UserContext implements Serializable {
	
	private static final String USER_MODEL_KEY = "user";
	public static final String LONG_TERM_USERID_COOKIE_REMOVED_VALUE = "cookie_value_removed";

	private static final long serialVersionUID = 1L;
	
	private Logger logger = LoggerFactory.getLogger(UserContext.class);	
	
	/**
	 * Workaround for unexpected session termination
	 * Must be set by login controller
	 * TODO: Make this more secure!!
	 */
	public static final String USERID_COOKIE_NAME = "CATAMARANUSERID";
	
	private String userKey = "userId";
	
	public void prepareModel(Map<String, Object> model) {
		User user = getUser();
		model.put(USER_MODEL_KEY, user);
	}
	
	public User getUser() {
		User user = new User();
		user.setAdministrator(true);
		user.setEmail("mkvalsvik@scandilabs.com");
		user.setName(Name.createFromFullNameString("Mads Kvalsvik"));
		return user;
	}
	
	public String getUserKey() {
		return this.userKey;
	}

	public void setUserKey(String userId) {
		this.userKey = userId;
	}
	
	/**
	 * TODO: Taking HttpServletRequest is a workaround for unexpected session termination issues
	 * @return
	 */
	public boolean isLoggedIn() {
		if (this.userKey != null) {
			return true;
		}
		return false;
	}
	
	public boolean isLoggedInAdministrator() {
		if (!this.isLoggedIn()) {
			return false;
		}
		
		return this.getUser().isAdministrator();
	}	
	
}
