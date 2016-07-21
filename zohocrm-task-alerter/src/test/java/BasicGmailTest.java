import java.util.List;

import javax.mail.MessagingException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.scandilabs.apps.zohocrm.entity.GmailAccount;
import com.scandilabs.apps.zohocrm.service.cron.DailyEmailDaemon;
import com.scandilabs.apps.zohocrm.service.gmail.GmailService;
import com.scandilabs.apps.zohocrm.util.EmailUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:zoho-api-spring.xml")
public class BasicGmailTest {

	private static Logger logger = LoggerFactory
			.getLogger(BasicGmailTest.class);

	@Autowired
	private GmailService gmailService;

	@Autowired
	private DailyEmailDaemon dailyEmailDaemon;

	@Before
	public void setUp() {

		// Prevent daemon from starting
		this.dailyEmailDaemon.setStopped(true);
	}

	@Test
	public void testGmailSearch() throws MessagingException {

		logger.info("Starting..");

		/*
		// mrazi@yahoo.com
		List<SimpleMailMessage> messages = this.gmailService.searchByFromAndTo(
			//	"kittakvalsvik@gmail.com", 10, false);
			//	"mrazi@yahoo.com", 10, false);
				gmailAccount, "ted@madakethealth.com", 10, false);

		for (SimpleMailMessage m : messages) {
			StringBuilder recipients = new StringBuilder();
			for (int i = 0; i < m.getTo().length; i++) {
				recipients.append(m.getTo()[i] + ", ");
			}

			System.out.println(m.getSentDate() + " from: " + m.getFrom()
					+ " to: " + recipients.toString() + "  " + m.getSubject());
			System.out.println("Body: " + EmailUtils.getEmailBodyTextExerpt(m.getText()));
		}
		*/

	}

}
