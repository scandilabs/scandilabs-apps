package org.catamarancode.connect.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.catamarancode.entity.support.EntityFinder;
import org.catamarancode.entity.support.PersistableBase;
import org.catamarancode.util.PasswordUtils;
import org.catamarancode.util.Timestamped;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

@Entity
public class User extends PersistableBase implements Timestamped {
	
	private static final String PASSWORD_ENCODING_APPLICATION_SECRET = "a7cc5c2bcb622788271feeffaffa";

	/**
	 * TODO: Use reflection and the @Entity annotation to populate this during
	 * spring startup. That way, all the Person class needs to do is to declare
	 * this field.
	 */
	public static EntityFinder objects;

	private static Logger logger = LoggerFactory.getLogger(User.class);

	private String firstName;
	private String middleName;
	private String lastName;
	private String email;
	private boolean deleted;
	private String encodedPassword;
	private Date createdTime;
	private Date lastModifiedTime;
	
	/**
	 * Sets the encodedPassword by encoding the given cleartextPassword
	 * 
	 * @param cleartextPassword
	 */
	public void setCleartextPassword(String cleartextPassword) {
		this.encodedPassword = PasswordUtils.encode(cleartextPassword, PASSWORD_ENCODING_APPLICATION_SECRET);
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
	
	/**
	 * Gets the encoded password. Normally not used for anything except keeping
	 * it around for when user data is re-saved.
	 * 
	 * @return encoded password as a String
	 */
	@Column(length = 28)
	public String getEncodedPassword() {
		return this.encodedPassword;
	}
	
	/**
	 * Masked password
	 * @return
	 */
	@Transient
	public String getCleartextPassword() {
	    if (StringUtils.hasText(this.encodedPassword)) {
	        return "********";
	    }
	    return null;
	}	
	
	@Index(name="emailIndex")
	@Email
	public String getEmail() {
		return email;
	}

	@Index(name="firstNameIndex")
	@Size(min = 2)
	public String getFirstName() {
		return firstName;
	}
	
	public boolean passwordMatches(String cleartextPassword) {
		return PasswordUtils.passwordMatches(cleartextPassword, this.encodedPassword, PASSWORD_ENCODING_APPLICATION_SECRET);
	}

	@Index(name="lastNameIndex")
	@Size(min = 2)
	public String getLastName() {
		return lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	@Transient
	public String getName() {
		String middleStr = middleName == null ? "" : middleName + " ";

		if (StringUtils.hasText(firstName) && !StringUtils.hasText(lastName)) {
			return firstName;
		}

		if (!StringUtils.hasText(firstName) && StringUtils.hasText(lastName)) {
			return lastName;
		}

		return firstName + " " + middleStr + lastName;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	
}
