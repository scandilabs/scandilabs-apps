package com.scandilabs.apps.zohocrm.service.gmail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;

import com.scandilabs.apps.zohocrm.entity.GmailAccount;
import com.scandilabs.apps.zohocrm.util.EmailUtils;
import com.sun.mail.gimap.GmailFolder;
import com.sun.mail.gimap.GmailRawSearchTerm;
import com.sun.mail.gimap.GmailStore;

public class GmailService {

	private static Logger logger = LoggerFactory.getLogger(GmailService.class);

	private int MAX_LOOKBACK_DAYS = 3600;

	Properties props = System.getProperties();

	public GmailService() {
		props.setProperty("mail.store.protocol", "gimap");
	}

	public List<SimpleMailMessage> searchByFromAndTo(GmailAccount gmailAccount, String emailToSearchFor,
			int rows, boolean includeBodyInSearch) {

		GmailStore store = null;
		GmailFolder inbox = null;
		Message[] foundMessages = null;
		List<SimpleMailMessage> returnList = null;
		try {
			Session session = Session.getDefaultInstance(props, null);
			store = (GmailStore) session.getStore("gimap");
			store.connect("imap.gmail.com", gmailAccount.getEmail(), gmailAccount.getPassword());
			inbox = (GmailFolder) store.getFolder("[Gmail]/All Mail");
			inbox.open(Folder.READ_ONLY);

			int count = 0;
			List<Message> messageList = new ArrayList<Message>();
			String term = null;
			if (includeBodyInSearch) {
				term = emailToSearchFor;
			} else {
				term = "to:" + emailToSearchFor + " OR from:"
						+ emailToSearchFor;
			}
			foundMessages = inbox.search(new GmailRawSearchTerm(term));
			logger.debug("got " + foundMessages.length + " for term " + term);
			count = count + foundMessages.length;
			for (int i = (foundMessages.length - 1); i >= 0; i--) {
				// logger.debug("adding one " + i);
				messageList.add(foundMessages[i]);
			}

			logger.debug("Found " + count + " messages on server to or from "
					+ emailToSearchFor);

			// Now limit to accurate page size and also trigger lazy load
			returnList = new ArrayList<SimpleMailMessage>();
			int foundRows = rows;
			if (messageList.size() < rows) {
				foundRows = messageList.size();
			}
			for (int i = 0; i < foundRows; i++) {
				returnList.add(this.toSimpleMailMessage(messageList.get(i)));
			}

		} catch (AuthenticationFailedException e) {
			logger.error("No such provider", e);
			return new ArrayList<SimpleMailMessage>();
			
		} catch (NoSuchProviderException e) {
			logger.error("No such provider", e);
		} catch (MessagingException e) {
			logger.error("Message error", e);			
		} catch (IOException e) {
			logger.error("IO error", e);
		} finally {
			if (inbox != null) {
				try {
					inbox.close(false);
				} catch (Exception e) {
					// eat this
				}
			}
			if (store != null) {
				try {
					store.close();
				} catch (Exception e) {
					// eat this
				}
			}
		}

		return returnList;
	}

	/**
	 * @deprecated TODO delete this method
	 * @param emailToSearchFor
	 * @param rows
	 * @param includeBodyInSearch
	 * @return
	 */
	/*
	public List<SimpleMailMessage> searchByFromAndToXXX(
			String emailToSearchFor, int rows, boolean includeBodyInSearch) {

		GmailStore store = null;
		GmailFolder inbox = null;
		Message[] foundMessages = null;
		List<SimpleMailMessage> returnList = null;
		try {
			Session session = Session.getDefaultInstance(props, null);
			store = (GmailStore) session.getStore("gimap");
			store.connect("imap.gmail.com", user, password);
			inbox = (GmailFolder) store.getFolder("[Gmail]/All Mail");
			inbox.open(Folder.READ_ONLY);

			int count = 0;
			int daysBackBase = 30;
			List<Message> messageList = new ArrayList<Message>();
			int loopCount = 2;
			int oldDaysBack = 0;
			int daysBack = 0;
			while (count < rows && daysBack < MAX_LOOKBACK_DAYS) {
				loopCount++;
				oldDaysBack = daysBack;
				daysBack = daysBackBase * fibonacci(loopCount);
				String term = null;
				if (oldDaysBack > 0) {
					if (includeBodyInSearch) {
						term = emailToSearchFor + " newer_than:" + daysBack
								+ "d older_than:" + oldDaysBack + "d";
					} else {
						term = "to:" + emailToSearchFor + " OR from:"
								+ emailToSearchFor + " newer_than:" + daysBack
								+ "d older_than:" + oldDaysBack + "d";
					}
				} else {
					if (includeBodyInSearch) {
						term = emailToSearchFor + " newer_than:" + daysBack
								+ "d";
					} else {
						term = "to:" + emailToSearchFor + " OR from:"
								+ emailToSearchFor + " newer_than:" + daysBack
								+ "d";
					}
				}

				foundMessages = inbox.search(new GmailRawSearchTerm(term));
				logger.debug("got " + foundMessages.length + " for term "
						+ term);
				count = count + foundMessages.length;
				for (int i = (foundMessages.length - 1); i >= 0; i--) {
					// logger.debug("adding one " + i);
					messageList.add(foundMessages[i]);
				}
			}

			// foundMessages = inbox.search(new GmailRawSearchTerm("to:" +
			// emailToSearchFor + " OR from:" + emailToSearchFor +
			// " newer_than:50d"));
			logger.debug("Found " + count + " messages on server to or from "
					+ emailToSearchFor);

			// Now limit to accurate page size and also trigger lazy load
			returnList = new ArrayList<SimpleMailMessage>();
			int foundRows = rows;
			if (messageList.size() < rows) {
				foundRows = messageList.size();
			}
			for (int i = 0; i < foundRows; i++) {
				returnList.add(this.toSimpleMailMessage(messageList.get(i)));
			}

		} catch (NoSuchProviderException e) {
			logger.error("No such provider", e);
			System.exit(1);
		} catch (MessagingException e) {
			logger.error("Message error", e);
			System.exit(2);
		} catch (IOException e) {
			logger.error("IO error", e);
			System.exit(2);
		} finally {
			if (inbox != null) {
				try {
					inbox.close(false);
				} catch (MessagingException e) {
					// eat this
				}
			}
			if (store != null) {
				try {
					store.close();
				} catch (MessagingException e) {
					// eat this
				}
			}
		}

		return returnList;
	}
	*/

	public int fibonacci(int n) {
		if (n == 0)
			return 0;
		else if (n == 1)
			return 1;
		else
			return fibonacci(n - 1) + fibonacci(n - 2);
	}

	public SimpleMailMessage toSimpleMailMessage(Message message)
			throws IOException, MessagingException {
		SimpleMailMessage sm = new SimpleMailMessage();

		String from = InternetAddress.toString(message.getFrom());
		sm.setFrom(from);
		String replyTo = InternetAddress.toString(message.getReplyTo());
		sm.setReplyTo(replyTo);
		int toCount = message.getAllRecipients().length;
		String[] toArr = new String[toCount];
		for (int i = 0; i < toCount; i++) {
			Address address = message.getAllRecipients()[i];
			if (address != null) {
				toArr[i] = address.toString();
			}
		}
		sm.setTo(toArr);

		String subject = message.getSubject();
		sm.setSubject(subject);
		Date sent = message.getSentDate();
		sm.setSentDate(sent);

		// Get body text
		String result = EmailUtils.extractMimeMessageBodyText(message); 
		sm.setText(result);

		return sm;
	}

}
