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

	private Map<String, Object> fieldMap = new HashMap<String, Object>();
	private int rowNumber;
	
	public Map<String, Object> getFieldMap() {
		return fieldMap;
	}
	public void setFieldMap(Map<String, Object> fieldMap) {
		this.fieldMap = fieldMap;
	}
	
	public void addField(String key, Object value) {
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
	public Map<String, Object> getNoSpaceFieldMap() {
		Map<String, Object> noSpaceMap = new HashMap<String, Object>();
		for (String key : fieldMap.keySet()) {
			Object value = fieldMap.get(key);
			String nameNoSpaces = StringUtils.replace(key, " ", "_");
			noSpaceMap.put(nameNoSpaces, value);
		}
		return noSpaceMap;
	}
	
	public String toXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<row no=\"");
		sb.append(this.rowNumber);
		sb.append("\">");
		for (String key : fieldMap.keySet()) {
			Object value = fieldMap.get(key);
			sb.append("<FL val=\"");
			sb.append(key);
			sb.append("\">");
			if (value != null) {
				sb.append(value.toString());	
			} else {
				sb.append("null");
			}
			
			sb.append("</FL>");
		}
		sb.append("</row>");
		return sb.toString();
	}
}
