
import java.util.List;

import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.scandilabs.apps.zohocrm.entity.User;
import com.scandilabs.apps.zohocrm.entity.support.Repository;
import com.scandilabs.apps.zohocrm.entity.support.UserIdConstants;
import com.scandilabs.apps.zohocrm.service.DailyEmailDaemon;
import com.scandilabs.apps.zohocrm.service.EmailComposer;
import com.scandilabs.apps.zohocrm.service.ZohoCrmApiService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:zoho-api-spring.xml")
public class EmailComposerTest {

	private static Logger logger = LoggerFactory
	        .getLogger(EmailComposerTest.class);

	@Autowired
	private EmailComposer emailComposer;
	
	@Autowired
	Repository repository;
	
	@Autowired
	private DailyEmailDaemon dailyEmailDaemon;	
	
	@Autowired
	private ZohoCrmApiService zohoCrmApiService;	
	
	@Before
	public void setUp() {
		
		// Prevent daemon from starting
		this.dailyEmailDaemon.setStopped(true);
	}
	
	@Test
	public void testHistoryTableBodyHtml() {

		logger.info("Starting..");
		User user = repository.loadUser(UserIdConstants.TED_KEY);
		List<JSONObject> contacts = this.zohoCrmApiService.listContactsWithNextCallDateDue(user.getZohoAuthToken());
		String html = emailComposer.composeContactsHistoryBodyHtml(user, contacts, true);
		logger.info("Got html: " + html);
		logger.info("Done.");

	}

	@Test
	public void testScandiLabsEmail() {

		logger.info("Starting..");
		emailComposer.sendContactNextCallEmail(repository.loadUser(UserIdConstants.TED_KEY)); 
		logger.info("Done.");

	}
	


}
