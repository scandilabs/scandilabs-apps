package com.scandilabs.apps.zohocrm.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.scandilabs.catamaran.type.Name;
import com.scandilabs.catamaran.util.PasswordUtils;
import com.scandilabs.catamaran.util.Timestamped;

public class User implements Timestamped {
	
    private static final String PASSWORD_ENCODING_APPLICATION_SECRET = "e7be5c2bcb622788271feeffaffa";

    /**
     * Used so that we can distinguish between a key and a short id by a simple String.startsWith
     */
    public static final String SHORT_ID_PREFIX = "USERSHORTID";
    
    private static Logger logger = LoggerFactory.getLogger(User.class);

    private String zohoAuthToken;
    private String key;
    private String apiAuthToken;
    private Name name;
    private String email;
    private String encodedPassword;    
	private Date createdTime;
	private Date lastModifiedTime;
	private boolean administrator;
	private String contextId = "default"; // TODO: Set differently
    private String shortId; // Used for html links etc	
    
    private List<GmailAccount> gmailAccounts = new ArrayList<GmailAccount>();

	public User() {
		this.name = new Name();
        if (this.shortId == null) {
            this.shortId = SHORT_ID_PREFIX + RandomStringUtils.randomAlphanumeric(10);    
        }
	}
	

	/**
	 * Masked password
	 * @return
	 */
	public String getCleartextPassword() {
	    if (StringUtils.hasText(this.encodedPassword)) {
	        return "********";
	    }
	    return null;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public String getEmail() {
		return email;
	}
	
	/**
	 * Gets the encoded password. Normally not used for anything except keeping
	 * it around for when user data is re-saved.
	 * 
	 * @return encoded password as a String
	 */
	public String getEncodedPassword() {
		return this.encodedPassword;
	}
	
	public String getKey() {
        return key;
    }	
	
	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}
	
	public Name getName() {
		return name;
	}	
	
	public boolean passwordMatches(String cleartextPassword) {
		return PasswordUtils.passwordMatches(cleartextPassword, this.encodedPassword, PASSWORD_ENCODING_APPLICATION_SECRET);
	}

	/**
	 * Sets the encodedPassword by encoding the given cleartextPassword
	 * 
	 * @param cleartextPassword
	 */
	public void setCleartextPassword(String cleartextPassword) {
		this.encodedPassword = PasswordUtils.encode(cleartextPassword, PASSWORD_ENCODING_APPLICATION_SECRET);
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Sets the encoded password (used when hibernate populates this object from a
	 * data store)
	 * 
	 * @param encodedPassword
	 *            the encoded password
	 */
	public void setEncodedPassword(String encodedPassword) {
		this.encodedPassword = encodedPassword;
	}
	
	public void setKey(String key) {
        this.key = key;
    }

	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public boolean isAdministrator() {
		return administrator;
	}

	public void setAdministrator(boolean administratorFlag) {
		this.administrator = administratorFlag;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public String getShortId() {
		return shortId;
	}

	public void setShortId(String shortId) {
		this.shortId = shortId;
	}


	public String getZohoAuthToken() {
		return zohoAuthToken;
	}


	public void setZohoAuthToken(String zohoAuthToken) {
		this.zohoAuthToken = zohoAuthToken;
	}


	public String getApiAuthToken() {
		return apiAuthToken;
	}


	public void setApiAuthToken(String apiAuthToken) {
		this.apiAuthToken = apiAuthToken;
	}


	public List<GmailAccount> getGmailAccounts() {
		return gmailAccounts;
	}    
}
