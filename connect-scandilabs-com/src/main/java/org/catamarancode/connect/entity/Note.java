package org.catamarancode.connect.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.catamarancode.entity.support.EntityFinder;
import org.catamarancode.entity.support.PersistableBase;
import org.catamarancode.util.Timestamped;

@Entity
public class Note extends PersistableBase implements Timestamped {
	
	public static EntityFinder objects;

	private String subject;
	private String body;
	private Person contact;
	private Date createdTime;
    private Date lastModifiedTime;
    
    private static final int BODY_ABSTRACT_LENGTH = 20;

	@ManyToOne(cascade = { CascadeType.ALL }, optional=false)
	@JoinColumn(name="contact_id", nullable=false)
    public Person getContact() {
        return contact;
    }

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Lob
	public String getBody() {
		return body;
	}
	
	@Transient
	public String getBodyAbstract() {
		if (this.body == null) {
			 return null;
		}
		if (this.body.length() < BODY_ABSTRACT_LENGTH) {
			return this.body;
		}
		
		return body.substring(0, BODY_ABSTRACT_LENGTH);
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setContact(Person contact) {
		this.contact = contact;
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
