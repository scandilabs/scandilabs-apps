package com.scandilabs.apps.zohocrm.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private static final String SAMPLE_CONTACT_LIST_JSON = "{\"response\":{\"result\":{\"Contacts\":{\"row\":[{\"no\":\"1\",\"FL\":[{\"content\":\"755485000000058579\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Andra\",\"val\":\"First Name\"},{\"content\":\"Marx\",\"val\":\"Last Name\"},{\"content\":\"andramarx@yahoo.com\",\"val\":\"Email\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Next Call Date\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]},{\"no\":\"2\",\"FL\":[{\"content\":\"755485000000058577\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Alicia\",\"val\":\"First Name\"},{\"content\":\"Pichard\",\"val\":\"Last Name\"},{\"content\":\"apichard@mit.edu\",\"val\":\"Email\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]},{\"no\":\"3\",\"FL\":[{\"content\":\"755485000000058575\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Alexis\",\"val\":\"First Name\"},{\"content\":\"Richardson\",\"val\":\"Last Name\"},{\"content\":\"alexis.richardson@gmail.com\",\"val\":\"Email\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]},{\"no\":\"4\",\"FL\":[{\"content\":\"755485000000058573\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Ajay\",\"val\":\"First Name\"},{\"content\":\"Bhardwaj\",\"val\":\"Last Name\"},{\"content\":\"ajay.bhardwaj@myersholum.com\",\"val\":\"Email\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]},{\"no\":\"5\",\"FL\":[{\"content\":\"755485000000058571\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Aish\",\"val\":\"First Name\"},{\"content\":\"Agrawal\",\"val\":\"Last Name\"},{\"content\":\"aish.agrawal@gmail.com\",\"val\":\"Email\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]},{\"no\":\"6\",\"FL\":[{\"content\":\"755485000000058569\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Adam\",\"val\":\"First Name\"},{\"content\":\"Giuffre\",\"val\":\"Last Name\"},{\"content\":\"adam@custommade.com\",\"val\":\"Email\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]},{\"no\":\"7\",\"FL\":[{\"content\":\"755485000000058567\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Adam\",\"val\":\"First Name\"},{\"content\":\"Pearlman\",\"val\":\"Last Name\"},{\"content\":\"apearlman@intralinks.com\",\"val\":\"Email\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]},{\"no\":\"8\",\"FL\":[{\"content\":\"755485000000058565\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Adam\",\"val\":\"First Name\"},{\"content\":\"Brod\",\"val\":\"Last Name\"},{\"content\":\"adam.brod@gmail.com\",\"val\":\"Email\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]},{\"no\":\"9\",\"FL\":[{\"content\":\"755485000000058563\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Aaron\",\"val\":\"First Name\"},{\"content\":\"Sawitsky\",\"val\":\"Last Name\"},{\"content\":\"asawitsky@boathouseinc.com\",\"val\":\"Email\"},{\"content\":\"617-680-3454\",\"val\":\"Phone\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Next Call Date\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]}]}},\"uri\":\"/crm/private/json/Contacts/getRecords\"}}";
	private static final String SAMPLE_SINGLE_CONTACT_JSON = "{\"response\":{\"result\":{\"Contacts\":{\"row\":{\"no\":\"1\",\"FL\":[{\"content\":\"755485000000058579\",\"val\":\"CONTACTID\"},{\"content\":\"755485000000057001\",\"val\":\"SMOWNERID\"},{\"content\":\"Mads\",\"val\":\"Contact Owner\"},{\"content\":\"Andra\",\"val\":\"First Name\"},{\"content\":\"Marx\",\"val\":\"Last Name\"},{\"content\":\"andramarx@yahoo.com\",\"val\":\"Email\"},{\"content\":\"755485000000057001\",\"val\":\"SMCREATORID\"},{\"content\":\"Mads\",\"val\":\"Created By\"},{\"content\":\"755485000000057001\",\"val\":\"MODIFIEDBY\"},{\"content\":\"Mads\",\"val\":\"Modified By\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Created Time\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Modified Time\"},{\"content\":\"false\",\"val\":\"Email Opt Out\"},{\"content\":\"false\",\"val\":\"Add to QuickBooks\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Next Call Date\"},{\"content\":\"2013-01-08 15:05:47\",\"val\":\"Last Activity Time\"}]}}},\"uri\":\"/crm/private/json/Contacts/getRecordById\"}}";
	private static final String SAMPLE_ERROR_JSON = "{\"response\":{\"nodata\":{\"message\":\"There is no data to show\",\"code\":\"4422\"},\"uri\":\"/crm/private/json/Contacts/getRecordById\"}}";

	private boolean testJsonMode;

	private static final int PAGE_SIZE = 100;

	private Logger logger = LoggerFactory.getLogger(ZohoCrmApiService.class);

	private String authtoken = "8cdb044dcad100b204412ce56de4d7b0";

	private static DateFormat apiDateFormat = new SimpleDateFormat(
	        "yyyy-MM-dd hh:mm:ss");

	public ZohoCrmApiService() {
		this.testJsonMode = true;
	}

	public List<JSONObject> listContactsWithNextCallDate() {
		String targetURL = "https://crm.zoho.com/crm/private/json/Contacts/getRecords";
		List<JSONObject> result = this.retrieveListAll(targetURL);
		List<JSONObject> filteredResult = new ArrayList<JSONObject>();

		// Sort by last call date
		for (int i = 0; i < result.size(); i++) {
			String rowNumber = (String) result.get(i).get("no");
			JSONArray fields = result.get(i).getJSONArray("FL");
			String nextCallDateStr = ApplicationUtils.getField(fields,
			        "Next Call Date");
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
			String nextCallDateStr = ApplicationUtils.getField(fields,
			        "Next Call Date");
			Date nextCallDate;
			try {
				nextCallDate = ApplicationUtils.DATE_FORMAT
				        .parse(nextCallDateStr);
			} catch (ParseException e) {
				throw new RuntimeException("Error parsing date: "
				        + nextCallDateStr, e);
			}
			if (nextCallDate.before(todaysDateAtMidnight())) {
				filteredResult.add(result.get(i));
			}
		}
		return filteredResult;
	}

	public void postponeContactNextCallDate(String recordId) {
		logger.debug(
        		String.format("Postponing contact with id %s", recordId));
		
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.WEEK_OF_YEAR, 2);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		Row row = this.loadContactFields(recordId);
		row.addField("Next Call Date", apiDateFormat.format(cal.getTime()));
		
		this.postUpdate(recordId, RecordType.Contacts, row);
	}

	public Row loadContactFields(String recordId) {
		return this.loadRecordFields(recordId, RecordType.Contacts);
	}

	public Row loadRecordFields(String recordId, RecordType recordType) {
		logger.debug(String.format("Retrieving record type %s with id %s",
		        recordType, recordId));
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", recordId);
		String htmlContent = this.postAndReturnHtml(
		        "https://crm.zoho.com/crm/private/json/" + recordType
		                + "/getRecordById", params);
		logger.trace("json>" + htmlContent);

		JSONObject rowJson = this.extractSingleRow(htmlContent, recordType);

		String rowNumber = (String) rowJson.get("no");
		JSONArray fields = rowJson.getJSONArray("FL");

		Row row = toRow(fields);

		return row;
	}

	private Row toRow(JSONArray jsonFLArray) {
		Row row = new Row();
		for (int i = 0; i < jsonFLArray.size(); i++) {
			JSONObject field = jsonFLArray.getJSONObject(i);
			row.addField(field.getString("val"), field.getString("content"));
		}
		return row;
	}

	private String postAndReturnHtml(String url, Map<String, String> params) {

		if (this.testJsonMode) {
			logger.warn("Test mode: Returning hard-coded JSON content");
			if (url.indexOf("ById") > 0) {
				return SAMPLE_SINGLE_CONTACT_JSON;
			}
			return SAMPLE_CONTACT_LIST_JSON;
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
		return htmlContent;
	}

	private void postUpdate(String recordId, RecordType recordType, Row row) {

		String xmlData = "<Contacts>" + row.toXML() + "</Contacts>";
		
		if (this.testJsonMode) {
			logger.warn("Test mode: Skipping this update for id " + recordId + ", here's the xml: " + xmlData);
			return;
		}

		DefaultHttpClient httpClient = new DefaultHttpClient();
		String url = "https://crm.zoho.com/crm/private/xml/"
		        + recordType.name() + "/updateRecords";
		HttpPost post = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("authtoken", authtoken));
		nvps.add(new BasicNameValuePair("scope", "crmapi"));
		nvps.add(new BasicNameValuePair("newFormat", "1"));
		nvps.add(new BasicNameValuePair("id", recordId));		
		nvps.add(new BasicNameValuePair("xmlData", xmlData));

		try {
			post.setEntity(new UrlEncodedFormEntity(nvps));
			long t1 = System.currentTimeMillis();
			HttpResponse httpResponse = httpClient.execute(post);
			logger.debug(String.format(
			        "POST returned status %d in %d millis from %s",
			        httpResponse.getStatusLine().getStatusCode(),
			        (System.currentTimeMillis() - t1), url));
			HttpEntity entity2 = httpResponse.getEntity();
		} catch (IOException e) {
			logger.error("Error during POST call to Zoho API: " + url, e);
		} finally {
			post.releaseConnection();
		}
	}

	public List<JSONObject> retrieveList(String targetURL, int fromIndex,
	        int toIndex) {
		logger.debug("Retrieving from " + fromIndex + " to " + toIndex);
		Map<String, String> params = new HashMap<String, String>();
		params.put("fromIndex", "" + fromIndex);
		params.put("toIndex", "" + toIndex);
		String htmlContent = this.postAndReturnHtml(targetURL, params);

		List<JSONObject> result = new ArrayList<JSONObject>();
		JSONObject json = JSONObject.fromObject(htmlContent);
		logger.trace("json>" + htmlContent);

		JSONArray rows = this.extractRows(htmlContent, RecordType.Contacts);
		logger.debug("Received " + rows.size() + " rows.");

		for (int i = 0; i < rows.size(); i++) {
			String rowNumber = (String) rows.getJSONObject(i).get("no");
			result.add(rows.getJSONObject(i));
		}

		return result;
	}

	private JSONArray extractRows(String htmlContent, RecordType recordType) {
		JSONObject json = JSONObject.fromObject(htmlContent);
		JSONArray rows = json.getJSONObject("response").getJSONObject("result")
		        .getJSONObject(recordType.name()).getJSONArray("row");
		return rows;
	}

	private JSONObject extractSingleRow(String htmlContent,
	        RecordType recordType) {
		JSONObject json = JSONObject.fromObject(htmlContent);
		JSONObject row = json.getJSONObject("response").getJSONObject("result")
		        .getJSONObject(recordType.name()).getJSONObject("row");
		return row;
	}

	/**
	 * Returns all results for a given api call, aggregated by multiple pages
	 * 
	 * @param targetURL
	 * @return
	 */
	public List<JSONObject> retrieveListAll(String targetURL) {
		List<JSONObject> resultAll = new ArrayList<JSONObject>();
		int fromIndex = 1;
		int toIndex = PAGE_SIZE;
		int rows = PAGE_SIZE;
		while (rows >= PAGE_SIZE) {
			List<JSONObject> result = retrieveList(targetURL, fromIndex,
			        toIndex);
			resultAll.addAll(result);
			rows = result.size();
			fromIndex = fromIndex + PAGE_SIZE;
			toIndex = toIndex + PAGE_SIZE;
		}
		return resultAll;
	}

	private Date todaysDateAtMidnight() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

}
