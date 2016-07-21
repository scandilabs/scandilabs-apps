package com.scandilabs.apps.zohocrm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailUtils {
	private static Logger logger = LoggerFactory.getLogger(EmailUtils.class);

	// Static methods only
	private EmailUtils() {
	}

	public static String extractMimeMessageBodyText(Message message) {
		String result = null;
		if (!(message instanceof MimeMessage)) {
			throw new RuntimeException("Unknown message type: "
					+ message.getClass().toString());
		}

		try {
			MimeMessage m = (MimeMessage) message;
			Object contentObject = m.getContent();
			if (contentObject instanceof Multipart) {
				BodyPart clearTextPart = null;
				BodyPart htmlTextPart = null;
				Multipart content = (Multipart) contentObject;
				int count = content.getCount();
				for (int i = 0; i < count; i++) {
					BodyPart part = content.getBodyPart(i);
					if (part.isMimeType("text/plain")) {
						clearTextPart = part;
						break;
					} else if (part.isMimeType("text/html")) {
						htmlTextPart = part;
					}
				}

				if (clearTextPart != null) {
					result = (String) clearTextPart.getContent();
				} else if (htmlTextPart != null) {
					String html = (String) htmlTextPart.getContent();
					result = Jsoup.parse(html).text();
				}

			} else if (contentObject instanceof String) // a simple text message
			{
				result = (String) contentObject;
			} else // not a mime message
			{
				logger.warn("not a mime part or multipart ", message.toString());
				result = null;
			}
		} catch (MessagingException e) {
			logger.error("Messaging error", e);
		} catch (IOException e) {
			logger.error("IO error", e);
		}
		return result;
	}

	// Get the first lines of the email body
	public static String getEmailBodyTextExerpt(String body) {
		StringBuilder result = new StringBuilder();
		if (body == null) {
			return "No message body";
		}
		BufferedReader reader = new BufferedReader(new StringReader(body));
		int count = 0;
		while (count < 2) {
			String line = null;
			try {
				line = reader.readLine();
			} catch (IOException e) {
				logger.error("Can't read message body", e);
				count = Integer.MAX_VALUE;
			}
			if (line == null) {
				count = Integer.MAX_VALUE;
			} else {
				if (line.length() > 18) {
					if (count > 0) {
						result.append("<br/>");
					}
					result.append(line);
					count++;
				}	
			}
		}
		return result.toString();
	}

}
