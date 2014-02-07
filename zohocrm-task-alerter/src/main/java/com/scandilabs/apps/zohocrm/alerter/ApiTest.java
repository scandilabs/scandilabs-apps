package com.scandilabs.apps.zohocrm.alerter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

public class ApiTest {

	public ApiTest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
        try    
        {      
           String authtoken = "8cdb044dcad100b204412ce56de4d7b0";
           String targetURL = "https://crm.zoho.com/crm/private/json/Contacts/getRecords"; 
            String paramname = "content";
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(targetURL);
            //PostMethod post = new PostMethod(targetURL);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("authtoken",authtoken));
            nvps.add(new BasicNameValuePair("scope","crmapi"));
            nvps.add(new BasicNameValuePair("fromIndex","1"));
            nvps.add(new BasicNameValuePair("toIndex","200"));
            post.setEntity(new UrlEncodedFormEntity(nvps));

            /*-------------------------------------- Execute the http request--------------------------------*/
            try 
            {
                long t1 = System.currentTimeMillis();
                HttpResponse httpResponse = httpClient.execute(post);
                System.out.println("HTTP Response status code: " + httpResponse.getStatusLine().getStatusCode());
                System.out.println(">> Time taken " + (System.currentTimeMillis() - t1));

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
                System.out.println(
                		"json>" + htmlContent);
                
                JSONArray rows = json.getJSONObject("response").getJSONObject("result").getJSONObject("Contacts").getJSONArray("row");
                
                for (int i = 0; i < rows.size(); i++) {
                	String rowNumber = (String) rows.getJSONObject(i).get("no");
                	JSONArray fields = rows.getJSONObject(i).getJSONArray("FL");
                	String nextCallDateStr = getField(fields, "Next Call Date");
                	if (nextCallDateStr == null) {
                		System.out.println("No call date in row " + rowNumber);
                	} else {
                		System.out.println("Call date: " + nextCallDateStr);
                	}
                	
                	
                	for (int j = 0; j < fields.size(); j++) {
                		String fieldName = fields.getJSONObject(j).getString("val");
                		String fieldValue = fields.getJSONObject(j).getString("content");
                		
                		System.out.println("rowNumber: " + rowNumber + ", field: " + fieldName + "-->" + fieldValue);
                		
                		if (fieldName.equals("CONTACTID") && fieldValue.equals("755485000000083007")) {
                			System.out.println("UPDATE NOW");
                		}
                	}
                	
                }
                
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }    
            finally 
            {
                post.releaseConnection();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }    

	}
	
	private static String getField(JSONArray fields, String fieldName) {
		for (int j = 0; j < fields.size(); j++) {
    		String name = fields.getJSONObject(j).getString("val");
    		if (fieldName.equals(name)) {
    			return fields.getJSONObject(j).getString("content");
    		}
    	}
		return null;
	}

}
