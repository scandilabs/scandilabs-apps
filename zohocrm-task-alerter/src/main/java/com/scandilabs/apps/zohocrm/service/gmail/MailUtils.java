package com.scandilabs.apps.zohocrm.service.gmail;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.mail.SimpleMailMessage;

public class MailUtils {

	
	public static void sortBySendDateReverse(List<SimpleMailMessage> messages) {
		Collections.sort(messages, new Comparator<SimpleMailMessage>() {
			public int compare(SimpleMailMessage s1, SimpleMailMessage s2) {

				return s2.getSentDate().compareTo(s1.getSentDate());
			}
		});
	}
}
