package com.scandilabs.apps.zohocrm.service;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

/**
 * A representation of a row of Zoho data, with a row number and multiple fields
 * @author mkvalsvik
 *
 */
public class Row {

	private Map<String, String> fieldMap = new HashMap<String, String>();
	private int rowNumber;
	
	public Map<String, String> getFieldMap() {
		return fieldMap;
	}
	public void setFieldMap(Map<String, String> fieldMap) {
		this.fieldMap = fieldMap;
	}
	
	public void addField(String key, String value) {
		this.fieldMap.put(key, value);
	}
	
	public int getRowNumber() {
		return rowNumber;
	}
	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}
	
	/**
	 * Converts field names like "First Name" to "First_Name" so that they work with Freemarker etc
	 * @return
	 */
	public Map<String, String> getNoSpaceFieldMap() {
		Map<String, String> noSpaceMap = new HashMap<String, String>();
		for (String key : fieldMap.keySet()) {
			String value = fieldMap.get(key);
			String nameNoSpaces = StringUtils.replace(key, " ", "_");
			noSpaceMap.put(nameNoSpaces, value);
		}
		return noSpaceMap;
	}
	
	public String toXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<row no\"");
		sb.append(this.rowNumber);
		sb.append("\">");
		for (String key : fieldMap.keySet()) {
			String value = fieldMap.get(key);
			sb.append("<FL val=\"");
			sb.append(key);
			sb.append("\">");
			sb.append(value);
			sb.append("</FL>");
		}
		sb.append("</row>");
		return sb.toString();
	}
}
