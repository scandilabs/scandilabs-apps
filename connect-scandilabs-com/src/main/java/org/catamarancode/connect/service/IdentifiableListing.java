package org.catamarancode.connect.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.catamarancode.connect.entity.Person;
import org.catamarancode.entity.support.Identifiable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * A bean that represents the order of a list of objects. This allows previous/next navigation.
 * @author mkvalsvik
 *
 */
@Component
@Scope("session")
public class IdentifiableListing implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Long> ids = new ArrayList<Long>();
	
	private Long current;
	private String path;
	private String pathName;
	
	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getNext() {
		if (this.current == null) {
			return null;
		}
		return findNext(this.current);
	}

	public Long findNext(Long current) {
		int currentPos = ids.indexOf(current);
		if (currentPos == -1) {
			return null;
		}
		if (ids.isEmpty()) {
			return null;
		}
		
		// Last element?
		if ((currentPos + 1) == ids.size()) {
			return null;
		}
		return ids.get(currentPos + 1);
	}
	
	public Long findPrevious(Long current) {
		int currentPos = ids.indexOf(current);
		if (currentPos == -1) {
			return null;
		}
		if (ids.isEmpty()) {
			return null;
		}
		
		// First element?
		if (currentPos < 1) {
			return null;
		}		
		return ids.get(currentPos - 1);
	}
	
	public void remove(Long id) {
		int currentPos = ids.indexOf(current);
		ids.remove(currentPos);
	}
	
	public Long getPrevious() {
		if (this.current == null) {
			return null;
		}
		return findPrevious(this.current);
	}
	
	public Long getFirst() {
		if (this.ids == null || this.ids.isEmpty()) {
			return null;
		}
		return this.ids.get(0);
	}
	
	public void setCurrent(Long current) {
		this.current = current;
	}

	public void reset(List<? extends Identifiable> list, String pathName, String path) {
		ids.clear();
		for (Identifiable entity : list) {
			ids.add(entity.getId());
		}		
		this.path = path;
		this.pathName = pathName;
	}
}
