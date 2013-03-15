package org.catamarancode.connect.service;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Transient;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.catamarancode.connect.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * A bean that represents a wrapper around a User object stored in a session
 * @author mkvalsvik
 *
 */
@Component
@Scope("session")
public class UserContext implements Serializable {
	
	private static final String USER_MODEL_KEY = "user";

	private static final long serialVersionUID = 1L;
	
	private Logger logger = LoggerFactory.getLogger(UserContext.class);
	
	/**
	 * Workaround for unexpected session termination
	 * Must be set by login controller
	 * TODO: Make this more secure!!
	 */
	public static final String USERID_COOKIE_NAME = "CATAMARANUSERID";
	
	private Long userId;
	
	public void prepareModel(Map<String, Object> model) {
		User user = this.getUser();
		model.put(USER_MODEL_KEY, user);
	}
	
	@Transient
	public User getUser() {
		User user = User.objects.load(getUserId());
		return user;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	/**
	 * TODO: Taking HttpServletRequest is a workaround for unexpected session termination issues
	 * @return
	 */
	public boolean isLoggedIn(HttpServletRequest request) {
		
		// Workaround:  Check cookie if nothing in session
		if (this.userId == null) {	
			String cookieUserId = getCookieValue(USERID_COOKIE_NAME, request);
			if (cookieUserId != null) {
				userId = Long.valueOf(cookieUserId);					
					
				// Debug logging only
				String jSessionId = getCookieValue("JSESSIONID", request);
				if (jSessionId != null) {
					logger.debug(String.format("Workaround: Cookie JSESSIONID %s did not match HttpSession id %s but retained user logged in state via cookie %s with value %d", jSessionId, request.getSession().getId(), USERID_COOKIE_NAME, userId));
				} else {
					logger.debug(String.format("Workaround: Cookie JSESSIONID not found but retained user logged in state via cookie %s with value %d", USERID_COOKIE_NAME, userId));
				}					
			}			
		}
		
		if (this.userId != null) {
			return true;
		}
		return false;
	}
	
	private String getCookieValue(String name, HttpServletRequest request) {
		if (request.getCookies() == null) {
			return null;
		}
		for (int j = 0; j < request.getCookies().length; j++) {
			Cookie c = request.getCookies()[j];
			if (c.getName().equals(name)) {
				return c.getValue();
			}
		}
		return null;
	}

}
