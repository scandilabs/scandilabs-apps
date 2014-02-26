package com.scandilabs.apps.zohocrm.service;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.scandilabs.apps.zohocrm.entity.User;
import com.scandilabs.apps.zohocrm.entity.support.Repository;
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
	
	private String userKey;
	
	public void prepareModel(Repository repository, Map<String, Object> model) {
		User user = getUser(repository);
		model.put(USER_MODEL_KEY, user);
	}
	
	public User getUser(Repository repository) {
		return repository.loadUser(this.getUserKey());
	}
	
	public String getUserKey() {
		return this.userKey;
	}

	public void setUserKey(String userId) {
		this.userKey = userId;
	}

	public boolean isLoggedIn(Repository repository, String userAuthToken) {
		User user = null;
		if (StringUtils.hasText(userAuthToken)) {
			user = repository.loadUserByApiToken(userAuthToken);
			this.userKey = user.getKey();
			logger.debug("Received user auth token " + userAuthToken + ", setting session user key: " + user.getKey());
		} else {
			logger.debug("User token was blank");
		}
		
		return isLoggedIn();
	}
	
	private boolean isLoggedIn() {
		if (this.userKey != null) {
			logger.debug("Found user key in session: " + this.userKey);
			return true;
		}
		logger.debug("No user key in session");
		return false;
	}
	
	public boolean isLoggedInAdministrator(Repository repository) {
		if (!this.isLoggedIn()) {
			return false;
		}
		
		return this.getUser(repository).isAdministrator();
	}	
	
}
