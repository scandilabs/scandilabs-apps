package com.scandilabs.apps.zohocrm.entity.support;

import java.util.ArrayList;
import java.util.List;

import com.scandilabs.apps.zohocrm.entity.User;
import com.scandilabs.catamaran.type.Name;

public class Repository {
	
	private static final List<User> users = new ArrayList<User>();
	
	static {
		User ted = new User();
		ted.setZohoAuthToken("a021c528e429d198cfe9269032491435");
		ted.setName(Name.createFromFullNameString("Ted Achtem"));
		ted.setEmail("ted@scandilabs.com");
		ted.setKey(UserIdConstants.TED_KEY);
		ted.setApiAuthToken(UserIdConstants.TED_API_TOKEN);
		users.add(ted);
		
		User mads = new User();
		mads.setZohoAuthToken("8cdb044dcad100b204412ce56de4d7b0");
		mads.setAdministrator(true);
		mads.setEmail("mkvalsvik@scandilabs.com");
		mads.setName(Name.createFromFullNameString("Mads Kvalsvik"));
		mads.setKey(UserIdConstants.MADS_KEY);
		mads.setApiAuthToken(UserIdConstants.MADS_API_TOKEN);
		users.add(mads);
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
