package org.catamarancode.connect.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Min;

import org.catamarancode.entity.support.EntityFinder;
import org.catamarancode.entity.support.PersistableBase;

@Entity
public class Opportunity extends PersistableBase {
	
	public static EntityFinder objects;

	private String title;
	
	@Min(1000)
	private int size;  // in dollars
	
	private String description;
	
    private List<Person> contacts = new ArrayList<Person>();
    
	@ManyToMany
	public List<Person> getContacts() {
		return contacts;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setContacts(List<Person> contacts) {
		this.contacts = contacts;
	}

    
}
