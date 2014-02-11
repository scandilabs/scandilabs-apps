package com.scandilabs.apps.zohocrm.util;

import java.text.SimpleDateFormat;

import net.sf.json.JSONArray;

public class ApplicationUtils {

	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String getField(JSONArray fields, String fieldName) {
		for (int j = 0; j < fields.size(); j++) {
    		String name = fields.getJSONObject(j).getString("val");
    		if (fieldName.equals(name)) {
    			return fields.getJSONObject(j).getString("content");
    		}
    	}
		return null;
	}
}
