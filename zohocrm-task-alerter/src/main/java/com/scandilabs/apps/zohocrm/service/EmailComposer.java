package com.scandilabs.apps.zohocrm.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections4.iterators.PeekingIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.util.StringUtils;

import com.scandilabs.apps.zohocrm.entity.GmailAccount;
import com.scandilabs.apps.zohocrm.entity.User;
import com.scandilabs.apps.zohocrm.service.gmail.GmailService;
import com.scandilabs.apps.zohocrm.service.gmail.MailUtils;
import com.scandilabs.apps.zohocrm.service.zoho.RecordType;
import com.scandilabs.apps.zohocrm.service.zoho.Row;
import com.scandilabs.apps.zohocrm.service.zoho.ZohoCrmApiService;
import com.scandilabs.apps.zohocrm.util.ApplicationUtils;
import com.scandilabs.apps.zohocrm.util.EmailUtils;
import com.scandilabs.catamaran.mail.send.SimpleHtmlMailSender;

/**
 * Queries Zoho data and sends email summary
 * 
 * @author mkvalsvik
 * 
 */
public class EmailComposer {

	private ZohoCrmApiService zohoCrmApiService;
	private SimpleHtmlMailSender mailSender;
	private GmailService gmailService;

	private String emailUrlPrefix;

	private static Logger logger = LoggerFactory.getLogger(EmailComposer.class);

	public String composeContactsHistoryBodyHtml(User user,
			List<JSONObject> contacts, boolean emailMode) {
		StringBuilder body = new StringBuilder();
		body.append("<table>");

		for (JSONObject contact : contacts) {

			// Extract field data
			JSONArray fields = contact.getJSONArray("FL");
			String firstName = ApplicationUtils.getField(fields, "First Name");
			String lastName = ApplicationUtils.getField(fields, "Last Name");
			String owner = ApplicationUtils.getField(fields, "Contact Owner");
			String account = ApplicationUtils.getField(fields, "Account Name");
			String email = ApplicationUtils.getField(fields, "Email");
			String phone = ApplicationUtils.getField(fields, "Phone");
			String description = ApplicationUtils.getField(fields,
					"Description");
			String lastActivityStr = ApplicationUtils.getField(fields,
					"Last Activity Time");
			String nextCallDateStr = ApplicationUtils.getField(fields,
					"Next Call Date");
			String contactId = ApplicationUtils.getField(fields, "CONTACTID");
			Date nextCallDate;
			try {
				nextCallDate = ApplicationUtils.DATE_FORMAT
						.parse(nextCallDateStr);
			} catch (ParseException e) {
				throw new RuntimeException("Error parsing date: "
						+ nextCallDateStr, e);
			}
			Date lastActivity;
			try {
				lastActivity = ApplicationUtils.DATE_FORMAT
						.parse(lastActivityStr);
			} catch (ParseException e) {
				throw new RuntimeException("Error parsing date: "
						+ lastActivityStr, e);
			}

			// Populate email body from contact
			logger.debug(String.format("It's time to call %s %s from %s",
					firstName, lastName, account));
			body.append("<tr>");
			body.append("<td>");
			body.append(String.format("%s %s", firstName, lastName));
			if (email != null) {
				body.append(" - ");
				body.append("<a href='mailto:" + email + "'>" + email + "</a>");
			}
			if (phone != null) {
				body.append(" - ");
				body.append("<a href='tel:" + phone + "'>" + phone + "</a>");
			}
			body.append("</td>");
			body.append("<td>&nbsp;</td>");

			body.append("<td>");
			body.append(account);
			body.append("</td>");
			body.append("<td>&nbsp;</td>");

			body.append("<td style='text-align:right'>");
			body.append(ApplicationUtils.TABLE_DISPLAY_DATE_FORMAT
					.format(lastActivity));
			body.append("</td>");

			body.append("</tr>");

			// Get emails and notes
			List<Row> rows = this.zohoCrmApiService.loadNotes(
					user.getZohoAuthToken(), contactId, RecordType.Contacts);
			List<SimpleMailMessage> messages = new ArrayList<SimpleMailMessage>();
			for (GmailAccount gmailAccount : user.getGmailAccounts()) {
				if (StringUtils.hasText(email)) {
					List<SimpleMailMessage> list = this.gmailService.searchByFromAndTo(
							gmailAccount, email, 5, false); 
					messages.addAll(list);
				}
			}
			
			// Combine emails into one sorted list
			MailUtils.sortBySendDateReverse(messages);

			// Sort emails and notes by date
			List<Object> activities = this.sortTwoByDate(rows, messages);

			// add one note or email per line/row
			for (Object activity : activities) {

				if (activity instanceof Row) {
					Row row = (Row) activity;
					Date noteDate = (Date) row.getFieldMap().get(
							"Modified Time");
					body.append("<tr>");
					body.append("<td colspan='5'>");
					body.append(ApplicationUtils.TABLE_DISPLAY_DATE_FORMAT
							.format(noteDate));
					body.append(" ");
					body.append(row.getFieldMap().get("Modified By"));
					body.append(" note: ");
					body.append(row.getFieldMap().get("Note Content"));
					body.append("</td>");
					body.append("</tr>");
				} else {
					SimpleMailMessage message = (SimpleMailMessage) activity;
					body.append("<tr>");
					body.append("<td colspan='5'>");
					body.append(ApplicationUtils.TABLE_DISPLAY_DATE_FORMAT
							.format(message.getSentDate()));
					body.append(" email '");
					body.append(message.getSubject());
					body.append("'<br/>");
					String truncatedText = EmailUtils
							.getEmailBodyTextExerpt(message.getText());
					body.append("<span style='font-style: italic'>");
					body.append(truncatedText);
					body.append("</span>");
					body.append("</td>");
					body.append("</tr>");

				}
			}

			// Description if found
			if (description != null) {
				body.append("<tr>");
				body.append("<td colspan='5'>");
				body.append("Description: " + description);
				body.append("</td>");
				body.append("</tr>");
			}

			// Action menu line
			String userTokenUrlSuffix = "";
			if (emailMode) {
				userTokenUrlSuffix = "?user=" + user.getApiAuthToken();
			}

			body.append("<tr>");

			body.append("<td>");
			body.append("<a href='" + this.emailUrlPrefix + "/contacts/view/"
					+ contactId + userTokenUrlSuffix + "'>VIEW</a>");
			body.append("</td>");
			body.append("<td>&nbsp;</td>");
			body.append("<td>");
			body.append("<a href='" + this.emailUrlPrefix
					+ "/contacts/postpone/" + contactId + userTokenUrlSuffix
					+ "'>POSTPONE</a>");
			body.append("</td>");
			body.append("<td>&nbsp;</td>");
			body.append("<td>");
			body.append("<a href='" + this.emailUrlPrefix + "/contacts/cancel/"
					+ contactId + userTokenUrlSuffix + "'>CANCEL</a>");
			body.append("</td>");
			body.append("</tr>");

			// Horizontal rule line
			body.append("<tr>");
			body.append("<td colspan='5'>");
			body.append("<hr/>");
			body.append("</td>");
			body.append("</tr>");
		}

		body.append("</table>");

		return body.toString();
	}

	public void sendContactNextCallEmail(User user) {
		SimpleDateFormat subjectDateFormat = new SimpleDateFormat("MMMM d");

		try {
			List<JSONObject> contacts = this.zohoCrmApiService
					.listContactsWithNextCallDateDue(user.getZohoAuthToken());
			if (contacts.isEmpty()) {
				logger.info("Exiting, no contacts to call today");
				return;
			}

			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(user.getEmail());
			message.setSubject(String.format("%s - %d contacts to call",
					subjectDateFormat.format(new Date()), contacts.size()));
			String bodyStr = this.composeContactsHistoryBodyHtml(user,
					contacts, true);
			message.setText(bodyStr);
			mailSender.send(message);
			logger.debug("Finished sending email");

		} catch (Exception e) {
			logger.error("Daily Email Daemon run error", e);
		}
	}

	public void setZohoCrmApiService(ZohoCrmApiService zohoCrmApiService) {
		this.zohoCrmApiService = zohoCrmApiService;
	}

	public void setMailSender(SimpleHtmlMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setEmailUrlPrefix(String emailUrlPrefix) {
		this.emailUrlPrefix = emailUrlPrefix;
	}

	public void setGmailService(GmailService gmailService) {
		this.gmailService = gmailService;
	}

	public static List<Object> sortTwoByDate(List<Row> rows,
			List<SimpleMailMessage> messages) {
		SimpleDateFormat lastDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String lastEmailDate = null;
		List<Object> activities = new ArrayList<Object>();
		PeekingIterator<Row> noteIterator = new PeekingIterator<Row>(
				rows.iterator());
		PeekingIterator<SimpleMailMessage> messageIterator = new PeekingIterator(
				messages.iterator());
		Date epochStartDate = new Date();
		epochStartDate.setTime(0);
		while (noteIterator.hasNext() || messageIterator.hasNext()) {
			Row row = noteIterator.peek();
			Date noteDate = epochStartDate;
			if (row != null) {
				noteDate = (Date) row.getFieldMap().get("Modified Time");
			}
			SimpleMailMessage message = messageIterator.peek();
			Date messageDate = epochStartDate;
			if (message != null) {
				messageDate = message.getSentDate();
			}

			if (noteDate.after(messageDate)) {

				// Advance iterator so that next comparison will use the next
				// object
				row = noteIterator.next();
				activities.add(row);
			} else {

				// Skip subsequent emails on the same date, only keep the most
				// recent
				if (lastEmailDate == null
						|| !lastEmailDate.equals(lastDateFormat.format(message
								.getSentDate()))) {
					activities.add(message);
				}

				// Advance iterator so that next comparison will use the next
				// object
				message = messageIterator.next();
				lastEmailDate = lastDateFormat.format(message.getSentDate());
			}
		}
		return activities;
	}

}
