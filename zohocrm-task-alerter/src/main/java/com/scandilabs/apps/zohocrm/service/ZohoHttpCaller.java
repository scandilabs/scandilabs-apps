package com.scandilabs.apps.zohocrm.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class ZohoHttpCaller {
	
	private static Logger logger = LoggerFactory.getLogger(ZohoHttpCaller.class);
	
	private static Map<String, String> cache = new HashMap<String, String>(); 
	
	public static boolean testJsonMode = false;
	
	public static boolean cacheEnabled = true;
	
	private static final String SAMPLE_CONTACT_LIST_JSON = "{\"response\":{\"result\":{\"Contacts\":{\"row\":[{\"no\":\"1\",\"FL\":[{\"content\":\"755485000000058579\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Andra\",\"val\":\"First Name\"},{\"content\":\"Marx\",\"val\":\"Last Name\"},{\"content\":\"andramarx@yahoo.com\",\"val\":\"Email\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Next Call Date\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]},{\"no\":\"2\",\"FL\":[{\"content\":\"755485000000058577\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Alicia\",\"val\":\"First Name\"},{\"content\":\"Pichard\",\"val\":\"Last Name\"},{\"content\":\"apichard@mit.edu\",\"val\":\"Email\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]},{\"no\":\"3\",\"FL\":[{\"content\":\"755485000000058575\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Alexis\",\"val\":\"First Name\"},{\"content\":\"Richardson\",\"val\":\"Last Name\"},{\"content\":\"alexis.richardson@gmail.com\",\"val\":\"Email\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]},{\"no\":\"4\",\"FL\":[{\"content\":\"755485000000058573\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Ajay\",\"val\":\"First Name\"},{\"content\":\"Bhardwaj\",\"val\":\"Last Name\"},{\"content\":\"ajay.bhardwaj@myersholum.com\",\"val\":\"Email\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]},{\"no\":\"5\",\"FL\":[{\"content\":\"755485000000058571\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Aish\",\"val\":\"First Name\"},{\"content\":\"Agrawal\",\"val\":\"Last Name\"},{\"content\":\"aish.agrawal@gmail.com\",\"val\":\"Email\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]},{\"no\":\"6\",\"FL\":[{\"content\":\"755485000000058569\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Adam\",\"val\":\"First Name\"},{\"content\":\"Giuffre\",\"val\":\"Last Name\"},{\"content\":\"adam@custommade.com\",\"val\":\"Email\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]},{\"no\":\"7\",\"FL\":[{\"content\":\"755485000000058567\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Adam\",\"val\":\"First Name\"},{\"content\":\"Pearlman\",\"val\":\"Last Name\"},{\"content\":\"apearlman@intralinks.com\",\"val\":\"Email\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]},{\"no\":\"8\",\"FL\":[{\"content\":\"755485000000058565\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Adam\",\"val\":\"First Name\"},{\"content\":\"Brod\",\"val\":\"Last Name\"},{\"content\":\"adam.brod@gmail.com\",\"val\":\"Email\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]},{\"no\":\"9\",\"FL\":[{\"content\":\"755485000000058563\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Aaron\",\"val\":\"First Name\"},{\"content\":\"Sawitsky\",\"val\":\"Last Name\"},{\"content\":\"asawitsky@boathouseinc.com\",\"val\":\"Email\"},{\"content\":\"617-680-3454\",\"val\":\"Phone\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Next Call Date\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]}]}},\"uri\":\"/crm/private/json/Contacts/getRecords\"}}";
	private static final String SAMPLE_SINGLE_CONTACT_JSON = "{\"response\":{\"result\":{\"Contacts\":{\"row\":{\"no\":\"1\",\"FL\":[{\"content\":\"755485000000058579\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Andra\",\"val\":\"First Name\"},{\"content\":\"Marx\",\"val\":\"Last Name\"},{\"content\":\"andramarx@yahoo.com\",\"val\":\"Email\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Next Call Date\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]}}},\"uri\":\"/crm/private/json/Contacts/getRecordById\"}}";
	private static final String SAMPLE_ERROR_JSON = "{\"response\":{\"nodata\":{\"message\":\"There is no data to show\",\"code\":\"4422\"},\"uri\":\"/crm/private/json/Contacts/getRecordById\"}}";
	private static final String SAMPLE_NOTES_JSON = "{\"response\":{\"result\":{\"Notes\":{\"row\":[{\"no\":\"1\",\"FL\":[{\"content\":\"755485000000141006\",\"val\":\"id\"},{\"val\":\"Title\"},{\"content\":\"Zoho CRM is an On-demand Customer Relationship Management (CRM) software for managing your customer relations in a better way. Zoho CRM software helps streamline your organization-wide sales, marketing, customer support, and inventory management functions in a single system.\n\nUnderstanding Zoho CRM\nGet an insight into the basics and know your Zoho CRM better.\n\nGetting Started\nGet started the easy way with some initial setup and customizations.\",\"val\":\"Note Content\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\" Mads\",\"val\":\"Owner Name\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\" Mads\",\"val\":\"Created By\"},{\"content\":\"2014-02-20 16:19:56\",\"val\":\"Created Time\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\" Mads\",\"val\":\"Modified By\"},{\"content\":\"2014-02-20 16:19:56\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"ISVOICENOTES\"}]},{\"no\":\"2\",\"FL\":[{\"content\":\"755485000000141004\",\"val\":\"id\"},{\"val\":\"Title\"},{\"content\":\"Test note #2\",\"val\":\"Note Content\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\" Mads\",\"val\":\"Owner Name\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\" Mads\",\"val\":\"Created By\"},{\"content\":\"2014-02-20 16:19:15\",\"val\":\"Created Time\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\" Mads\",\"val\":\"Modified By\"},{\"content\":\"2014-02-20 16:19:15\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"ISVOICENOTES\"}]},{\"no\":\"3\",\"FL\":[{\"content\":\"755485000000141002\",\"val\":\"id\"},{\"val\":\"Title\"},{\"content\":\"Test note #1\",\"val\":\"Note Content\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\" Mads\",\"val\":\"Owner Name\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\" Mads\",\"val\":\"Created By\"},{\"content\":\"2014-02-20 16:19:01\",\"val\":\"Created Time\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\" Mads\",\"val\":\"Modified By\"},{\"content\":\"2014-02-20 16:19:01\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"ISVOICENOTES\"}]}]}},\"uri\":\"/crm/private/json/Notes/getRelatedRecords\"}}";

	private static String toCacheKey(String authtoken, String url, Map<String, String> params) {
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("?");
		for (String key : params.keySet()) {
			sb.append(key);
			sb.append("=");
			sb.append(params.get(key));
			sb.append("&");
		}
		sb.append("authtoken=");
		sb.append(authtoken);
		return sb.toString();
	}
	
	public static void removeFromCache(String authtoken, String url, Map<String, String> params) {
		String cacheKey = toCacheKey(authtoken, url, params);
		cache.remove(cacheKey);
		logger.debug("Cleared from cache: " + cacheKey);
	}
	
	/**
	 * Removes all entries that start with this url
	 * @param url
	 */
	public static void removeStartingUrlsFromCache(String url) {
		int count = 0;
		Map<String, String> modifiedCache = new HashMap<String, String>();
		for (String key : cache.keySet()) {
			if (key.startsWith(url)) {
				count++;
			} else {
				modifiedCache.put(key, cache.get(key));				
			}
		}
		cache = modifiedCache;
		logger.debug("Cleared " + count + " keys from cache starting with: " + url);
	}
	
	public static void removeAllFromCache() {		
		cache.clear();
	}
	
	public static String postAndReturnHtml(String authtoken, String url,
	        Map<String, String> params) {

		if (testJsonMode) {
			logger.warn("Test mode: Returning hard-coded JSON content");
			if (url.indexOf("ById") > 0) {
				return SAMPLE_SINGLE_CONTACT_JSON;
			}
			return SAMPLE_CONTACT_LIST_JSON;
		}
		
		// Is this an update/change/insert request?
		boolean isUpdate = false;
		for (String key : params.keySet()) {
			if (key.equals("xmlData")) {
				isUpdate = true;
			}
		}
		
		// First lookup in cache
		String cacheKey = null;
		if (!isUpdate) {
			cacheKey = toCacheKey(authtoken, url, params);
			if (cacheEnabled) {						
				String cachedJson = cache.get(cacheKey);
				if (cachedJson != null) {
					logger.debug("Cache hit for key: " + cacheKey);
					return cachedJson;
				}
			}
		}

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("authtoken", authtoken));
		nvps.add(new BasicNameValuePair("scope", "crmapi"));
		for (String key : params.keySet()) {
			String value = params.get(key);
			nvps.add(new BasicNameValuePair(key, value));
		}

		String htmlContent = null;
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps));
			long t1 = System.currentTimeMillis();
			HttpResponse httpResponse = httpClient.execute(post);
			logger.debug(String.format(
			        "POST returned status %d in %d millis from %s",
			        httpResponse.getStatusLine().getStatusCode(),
			        (System.currentTimeMillis() - t1), url));
			HttpEntity entity2 = httpResponse.getEntity();

			// Copy raw html into a string
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
				// logger.error("IllegalStateException while consuming entity");
			} catch (IOException e) {
				throw new RuntimeException(e);
				// logger.error("IOException while consuming entity");
			}

		} catch (IOException e) {
			logger.error("Error during POST call to Zoho API: " + url, e);
		} finally {
			post.releaseConnection();
		}
		
		// Store in cache and return
		if (cacheKey != null) {
			cache.put(cacheKey, htmlContent);
			logger.debug("Saved to cache: " + cacheKey);
		}		
		return htmlContent;
	}
}
