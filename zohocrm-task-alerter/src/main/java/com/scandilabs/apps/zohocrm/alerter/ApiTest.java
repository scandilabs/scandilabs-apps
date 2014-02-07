package com.scandilabs.apps.zohocrm.alerter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

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
            PostMethod post = new PostMethod(targetURL);
            post.setParameter("authtoken",authtoken);
            post.setParameter("scope","crmapi");
            HttpClient httpclient = new HttpClient();

            /*-------------------------------------- Execute the http request--------------------------------*/
            try 
            {
                long t1 = System.currentTimeMillis();
                int result = httpclient.executeMethod(post);
                System.out.println("HTTP Response status code: " + result);
                System.out.println(">> Time taken " + (System.currentTimeMillis() - t1));

                JSONObject json = JSONObject.fromObject(post.getResponseBodyAsString());
                System.out.println(
                		"json>" + post.getResponseBodyAsString());
                
                JSONArray rows = json.getJSONObject("response").getJSONObject("result").getJSONObject("Contacts").getJSONArray("row");
                
                for (int i = 0; i < rows.size(); i++) {
                	String rowNumber = (String) rows.getJSONObject(i).get("no");
                	JSONArray fields = rows.getJSONObject(i).getJSONArray("FL");
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

}
