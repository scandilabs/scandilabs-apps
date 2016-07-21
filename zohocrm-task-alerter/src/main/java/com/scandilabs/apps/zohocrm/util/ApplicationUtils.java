package com.scandilabs.apps.zohocrm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scandilabs.apps.zohocrm.service.zoho.Row;
import com.scandilabs.apps.zohocrm.service.zoho.ZohoCrmApiService;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class ApplicationUtils {
	
	private static Logger logger = LoggerFactory.getLogger(ApplicationUtils.class);

	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat TABLE_DISPLAY_DATE_FORMAT = new SimpleDateFormat("M/d/yyyy");
	
	public static String getField(JSONArray fields, String fieldName) {
		for (int j = 0; j < fields.size(); j++) {
    		String name = fields.getJSONObject(j).getString("val");
    		if (fieldName.equals(name)) {
    			return fields.getJSONObject(j).getString("content");
    		}
    	}
		return null;
	}
	
	public static List<Row> toRowList(List<JSONObject> jsonRows) {
		List<Row> list = new ArrayList<Row>();
		for (JSONObject jsonRow : jsonRows) {
			JSONArray fields = jsonRow.getJSONArray("FL");
			Row row = toRow(fields);
			list.add(row);
		}
		return list;
	}
	
	public static List<Map<String, Object>> toRowFieldMap(List<Row> rows) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Row row : rows) {
			Map<String, Object> map = row.getNoSpaceFieldMap();
			list.add(map);
		}
		return list;
	}
	
	public static Row toRow(JSONArray jsonFLArray) {
		Row row = new Row();
		for (int i = 0; i < jsonFLArray.size(); i++) {
			JSONObject field = jsonFLArray.getJSONObject(i);

			// Note some fields don't have content
			String fieldName = field.getString("val");
			String strValue = null;
			Object value = null;
			try {
				strValue = (String) field.getString("content");				 
			} catch (JSONException e) {
				logger.trace("Missing value for field: " + fieldName);
				continue;
			}
			
			// Convert date strings to Date objects
    		if (fieldName.equals("Last Activity Time") ||
    				fieldName.equals("Next Call Date") ||
    				fieldName.equals("Modified Time") || 
    				fieldName.equals("Created Time") ) {    			
    			try {
    	            value = ApplicationUtils.DATE_FORMAT.parse(strValue);
    	            
    	            // Also save a display string version of this date
    	            String displayFieldName = fieldName + " Display";
    	            String displayValue = TABLE_DISPLAY_DATE_FORMAT.format(value);
    	            row.addField(displayFieldName, displayValue);
                } catch (ParseException e) {
    	            logger.warn("Error parsing date string " + strValue + " of field " + fieldName, e);
                }            
    		} else {
    			value = strValue;	
    		}
    		row.addField(fieldName, value);
		}
		return row;
	}
}
