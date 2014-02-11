package com.scandilabs.apps.zohocrm.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scandilabs.apps.zohocrm.util.ApplicationUtils;

public class ZohoCrmApiService {
	
	private static final int PAGE_SIZE = 100;
	
	private Logger logger = LoggerFactory.getLogger(ZohoCrmApiService.class);

	private String authtoken = "8cdb044dcad100b204412ce56de4d7b0";
	
	public ZohoCrmApiService() {
	}
	
	public List<JSONObject> listContactsWithNextCallDate() {
		String targetURL = "https://crm.zoho.com/crm/private/json/Contacts/getRecords";
		List<JSONObject> result = this.retrieveListAll(targetURL);
		List<JSONObject> filteredResult = new ArrayList<JSONObject>(); 
		
		// Sort by last call date
	    for (int i = 0; i < result.size(); i++) {
        	String rowNumber = (String) result.get(i).get("no");
        	JSONArray fields = result.get(i).getJSONArray("FL");
        	String nextCallDateStr = ApplicationUtils.getField(fields, "Next Call Date");
        	if (nextCallDateStr != null) {
            	filteredResult.add(result.get(i));
        	}
        }
	    return filteredResult;		
	}
	
	public List<JSONObject> listContactsWithNextCallDateDue() {
		List<JSONObject> result = this.listContactsWithNextCallDate();
		List<JSONObject> filteredResult = new ArrayList<JSONObject>();
		
		// Sort by last call date
	    for (int i = 0; i < result.size(); i++) {
        	String rowNumber = (String) result.get(i).get("no");
        	JSONArray fields = result.get(i).getJSONArray("FL");
        	String nextCallDateStr = ApplicationUtils.getField(fields, "Next Call Date");
        	Date nextCallDate;
            try {
	            nextCallDate = ApplicationUtils.DATE_FORMAT.parse(nextCallDateStr);
            } catch (ParseException e) {
	            throw new RuntimeException("Error parsing date: " + nextCallDateStr, e);
            }
        	if (nextCallDate.before(todaysDateAtMidnight()) ) {
            	filteredResult.add(result.get(i));
        	}
        }
	    return filteredResult;		
	}
	
	private Date todaysDateAtMidnight() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	/**
	 * Returns all results for a given api call, aggregated by multiple pages
	 * @param targetURL
	 * @return
	 */
	public List<JSONObject> retrieveListAll(String targetURL) {
		List<JSONObject> resultAll = new ArrayList<JSONObject>();
		int fromIndex = 1;
		int toIndex = PAGE_SIZE;
		int rows = PAGE_SIZE;
		while (rows >= PAGE_SIZE) {
			List<JSONObject> result = retrieveList(targetURL, fromIndex, toIndex);
			resultAll.addAll(result);
			rows = result.size();
			fromIndex = fromIndex + PAGE_SIZE;
			toIndex = toIndex + PAGE_SIZE;
		}
		return resultAll;
	}
	
	public List<JSONObject> retrieveList(String targetURL, int fromIndex, int toIndex) {
        logger.debug(
        		"Retrieving from " + fromIndex + " to " + toIndex);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost(targetURL);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("authtoken", authtoken));
        nvps.add(new BasicNameValuePair("scope", "crmapi"));
        nvps.add(new BasicNameValuePair("fromIndex", "" + fromIndex));
        nvps.add(new BasicNameValuePair("toIndex", "" + toIndex));        

        List<JSONObject> result = new ArrayList<JSONObject>();
        try 
        {
        	post.setEntity(new UrlEncodedFormEntity(nvps));
            long t1 = System.currentTimeMillis();
            HttpResponse httpResponse = httpClient.execute(post);
            logger.debug("HTTP Response status code: " + httpResponse.getStatusLine().getStatusCode());
            logger.debug(">> Time taken " + (System.currentTimeMillis() - t1));

            HttpEntity entity2 = httpResponse.getEntity();
            
            // Copy raw html into a string
            String htmlContent;
            try {
            	
            	ByteArrayOutputStream out = new ByteArrayOutputStream();
        		byte[] buffer = new byte[1024];
                int len = entity2.getContent().read(buffer);
                while (len != -1) {
                    out.write(buffer, 0, len);
                    len = entity2.getContent().read(buffer);
                }
                
    			htmlContent = out.toString();
    			EntityUtils.consume(entity2);
    		} catch (IllegalStateException e) {
    			throw new RuntimeException(e);
    			//logger.error("IllegalStateException while consuming entity");
    		} catch (IOException e) {
    			throw new RuntimeException(e);
    			//logger.error("IOException while consuming entity");
    		}
            
            JSONObject json = JSONObject.fromObject(htmlContent);
            logger.trace(
            		"json>" + htmlContent);
            
            JSONArray rows = json.getJSONObject("response").getJSONObject("result").getJSONObject("Contacts").getJSONArray("row");
            logger.debug(
            		"Received " + rows.size() + " rows.");

            
            for (int i = 0; i < rows.size(); i++) {
            	String rowNumber = (String) rows.getJSONObject(i).get("no");
            	result.add(rows.getJSONObject(i));
            }
            
        }
        catch(IOException e)
        {
            logger.error("Error during POST call to Zoho API", e);
        }    
        finally 
        {
            post.releaseConnection();
        }        
        
        return result;
	}	


}
