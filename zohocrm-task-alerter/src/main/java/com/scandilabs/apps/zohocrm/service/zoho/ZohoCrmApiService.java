package com.scandilabs.apps.zohocrm.service.zoho;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scandilabs.apps.zohocrm.util.ApplicationUtils;

public class ZohoCrmApiService {

	private static final int PAGE_SIZE = 200;

	private static Logger logger = LoggerFactory
			.getLogger(ZohoCrmApiService.class);

	private static DateFormat apiDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm:ss");

	public List<JSONObject> listContacts(String authtoken) {
		String targetURL = "https://crm.zoho.com/crm/private/json/Contacts/getRecords";
		List<JSONObject> result = this.retrieveListAll(authtoken, targetURL);
		return result;
	}

	public static void sortByLastFirstName(List<JSONObject> list) {
		List<JSONObject> filteredResult = new ArrayList<JSONObject>();

		Collections.sort(list, new Comparator<JSONObject>() {
			public int compare(JSONObject s1, JSONObject s2) {

				JSONArray fields = s1.getJSONArray("FL");
				String firstName = ApplicationUtils.getField(fields,
						"First Name");
				String lastName = ApplicationUtils
						.getField(fields, "Last Name");
				String name = lastName + ", " + firstName;

				JSONArray fields2 = s2.getJSONArray("FL");
				String firstName2 = ApplicationUtils.getField(fields2,
						"First Name");
				String lastName2 = ApplicationUtils.getField(fields2,
						"Last Name");
				String name2 = lastName2 + ", " + firstName2;
				return name.compareTo(name2);
			}
		});

	}

	public List<JSONObject> listContactsWithNextCallDate(String authtoken) {
		List<JSONObject> result = this.listContacts(authtoken);
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

	public List<JSONObject> listContactsWithNextCallDateDue(String authtoken) {
		List<JSONObject> result = this.listContactsWithNextCallDate(authtoken);
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

	public void postponeContactNextCallDate(String authtoken, String recordId) {
		logger.debug(String.format("Postponing contact with id %s", recordId));

		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.WEEK_OF_YEAR, 2);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		this.clearContactViewCache(authtoken, recordId);
		Row row = this.loadContactFields(authtoken, recordId);
		row.addField("Next Call Date", apiDateFormat.format(cal.getTime()));

		this.postUpdate(authtoken, recordId, RecordType.Contacts, row);
		this.clearContactViewCache(authtoken, recordId);
	}

	public void addContactNote(String authtoken, String recordId, String note) {
		logger.debug(String.format("Adding note to contact with id %s",
				recordId));

		Row row = new Row();
		row.addField("entityId", recordId);
		row.addField("Note Title", " ");
		row.addField("Note Content", note);

		this.addNote(authtoken, recordId, row);

		// Clear list of notes for this contact from cache
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", recordId);
		params.put("parentModule", RecordType.Contacts.name());
		String url = "https://crm.zoho.com/crm/private/json/Notes/getRelatedRecords";
		ZohoHttpCaller.removeFromCache(authtoken, url, params);
	}

	public void cancelContactNextCallDate(String authtoken, String recordId) {
		logger.debug(String
				.format("Setting next call date to a very high date for contact with id %s",
						recordId));

		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, 2099);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		this.clearContactViewCache(authtoken, recordId);
		Row row = this.loadContactFields(authtoken, recordId);
		row.addField("Next Call Date", apiDateFormat.format(cal.getTime()));

		this.postUpdate(authtoken, recordId, RecordType.Contacts, row);
		this.clearContactViewCache(authtoken, recordId);
	}

	public Row loadContactFields(String authtoken, String recordId) {
		return this.loadRecordFields(authtoken, recordId, RecordType.Contacts);
	}

	public Row loadRecordFields(String authtoken, String recordId,
			RecordType recordType) {
		logger.debug(String.format("Retrieving record type %s with id %s",
				recordType, recordId));
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", recordId);
		String htmlContent = ZohoHttpCaller.postAndReturnHtml(authtoken,
				"https://crm.zoho.com/crm/private/json/" + recordType
						+ "/getRecordById", params);
		logger.trace("json>" + htmlContent);

		JSONObject rowJson = this.extractSingleRow(htmlContent, recordType);

		String rowNumber = (String) rowJson.get("no");
		JSONArray fields = rowJson.getJSONArray("FL");

		Row row = ApplicationUtils.toRow(fields);

		return row;
	}

	public List<Row> loadNotes(String authtoken, String recordId,
			RecordType recordType) {
		logger.debug(String.format(
				"Retrieving notes related to type %s with id %s", recordType,
				recordId));
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", recordId);
		params.put("parentModule", RecordType.Contacts.name());
		String htmlContent = ZohoHttpCaller
				.postAndReturnHtml(
						authtoken,
						"https://crm.zoho.com/crm/private/json/Notes/getRelatedRecords",
						params);
		logger.trace("json>" + htmlContent);

		List<Row> result = new ArrayList<Row>();
		JSONArray rows = this.extractRows(htmlContent, RecordType.Notes);
		if (rows == null) {
			logger.debug("Received zero rows.");
			return result;
		}
		logger.debug("Received " + rows.size() + " rows.");

		for (int i = 0; i < rows.size(); i++) {
			String rowNumber = (String) rows.getJSONObject(i).get("no");
			result.add(ApplicationUtils.toRow(rows.getJSONObject(i)
					.getJSONArray("FL")));
		}
		return result;
	}

	private void addNote(String authtoken, String contactId, Row row) {

		String xmlData = "<Notes>" + row.toXML() + "</Notes>";

		if (ZohoHttpCaller.testJsonMode) {
			logger.warn("Test mode: Skipping this add note for contact id "
					+ contactId + ", here's the xml: " + xmlData);
			return;
		}

		String url = "https://crm.zoho.com/crm/private/xml/Notes/insertRecords";

		Map<String, String> params = new HashMap<String, String>();
		params.put("newFormat", "" + "1");
		params.put("xmlData", xmlData);
		logger.debug(String.format("POSTing update request %s to %s", xmlData,
				url));
		String htmlContent = ZohoHttpCaller.postAndReturnHtml(authtoken, url,
				params);
		logger.trace("json>" + htmlContent);
	}

	private void postUpdate(String authtoken, String recordId,
			RecordType recordType, Row row) {

		String xmlData = "<Contacts>" + row.toXML() + "</Contacts>";

		if (ZohoHttpCaller.testJsonMode) {
			logger.warn("Test mode: Skipping this update for id " + recordId
					+ ", here's the xml: " + xmlData);
			return;
		}

		String url = "https://crm.zoho.com/crm/private/xml/"
				+ recordType.name() + "/updateRecords";

		Map<String, String> params = new HashMap<String, String>();
		params.put("newFormat", "" + "1");
		params.put("id", recordId);
		params.put("xmlData", xmlData);
		logger.debug(String.format("POSTing update request %s to %s", xmlData,
				url));
		String htmlContent = ZohoHttpCaller.postAndReturnHtml(authtoken, url,
				params);
		logger.trace("json>" + htmlContent);
	}

	private void clearContactViewCache(String authtoken, String recordId) {

		// Individual record
		Map<String, String> cacheClearParams = new HashMap<String, String>();
		cacheClearParams.put("id", recordId);
		String cacheClearUrl = "https://crm.zoho.com/crm/private/json/"
				+ RecordType.Contacts.name() + "/getRecordById";
		ZohoHttpCaller.removeFromCache(authtoken, cacheClearUrl,
				cacheClearParams);

		// All contact listings
		ZohoHttpCaller
				.removeStartingUrlsFromCache("https://crm.zoho.com/crm/private/json/Contacts/getRecords");
	}

	public List<JSONObject> retrieveList(String authtoken, String targetURL,
			int fromIndex, int toIndex) {
		logger.debug("Retrieving from " + fromIndex + " to " + toIndex);
		Map<String, String> params = new HashMap<String, String>();
		params.put("fromIndex", "" + fromIndex);
		params.put("toIndex", "" + toIndex);
		String htmlContent = ZohoHttpCaller.postAndReturnHtml(authtoken,
				targetURL, params);

		List<JSONObject> result = new ArrayList<JSONObject>();
		JSONObject json = JSONObject.fromObject(htmlContent);
		logger.trace("json>" + htmlContent);

		JSONArray rows = this.extractRows(htmlContent, RecordType.Contacts);
		if (rows == null) {
			logger.debug("Received zero rows.");
			return result;
		}
		logger.debug("Received " + rows.size() + " rows.");

		for (int i = 0; i < rows.size(); i++) {
			String rowNumber = (String) rows.getJSONObject(i).get("no");
			result.add(rows.getJSONObject(i));
		}

		return result;
	}

	private JSONArray extractRows(String htmlContent, RecordType recordType) {
		JSONObject json = JSONObject.fromObject(htmlContent);
		JSONObject response = json.getJSONObject("response");
		JSONObject result = response.optJSONObject("result");
		if (result != null) {
			JSONObject module = result.getJSONObject(recordType.name());

			// Note there element inside may be either a single object row or an
			// array of rows
			try {
				JSONArray rows = module.getJSONArray("row");
				return rows;
			} catch (JSONException e) {

				// Most likely there's only a single row so we transform it into
				// an array.
				JSONArray rows = new JSONArray();
				JSONObject row = module.getJSONObject("row");
				rows.add(row);
				return rows;
			}
		}
		return null;
	}

	private JSONObject extractSingleRow(String htmlContent,
			RecordType recordType) {
		JSONObject json = JSONObject.fromObject(htmlContent);
		JSONObject response = json.getJSONObject("response");
		JSONObject result = response.optJSONObject("result");
		if (result != null) {
			return result.getJSONObject(recordType.name()).getJSONObject("row");
		}
		return null;
	}

	/**
	 * Returns all results for a given api call, aggregated by multiple pages
	 * 
	 * @param targetURL
	 * @return
	 */
	public List<JSONObject> retrieveListAll(String authtoken, String targetURL) {
		List<JSONObject> resultAll = new ArrayList<JSONObject>();
		int fromIndex = 1;
		int toIndex = PAGE_SIZE;
		int rows = PAGE_SIZE;
		while (rows >= PAGE_SIZE) {
			List<JSONObject> result = retrieveList(authtoken, targetURL,
					fromIndex, toIndex);
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
