
import java.util.List;

import net.sf.json.JSONObject;

import org.junit.Assert;
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
import com.scandilabs.apps.zohocrm.service.cron.DailyEmailDaemon;
import com.scandilabs.apps.zohocrm.service.zoho.RecordType;
import com.scandilabs.apps.zohocrm.service.zoho.Row;
import com.scandilabs.apps.zohocrm.service.zoho.ZohoCrmApiService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:zoho-api-spring.xml")
public class BasicZohoApiCallTest {

	private static Logger logger = LoggerFactory
	        .getLogger(BasicZohoApiCallTest.class);

	@Autowired
	private ZohoCrmApiService zohoCrmApiService;
	
	@Autowired
	private DailyEmailDaemon dailyEmailDaemon;	
	
	@Autowired
	Repository repository;
	
	private User user;

	@Before
	public void setUp() {
		
		// Prevent daemon from starting
		this.dailyEmailDaemon.setStopped(true);
		
		// Set the user to use
		this.user = repository.loadUser(UserIdConstants.MADS_KEY);
	}

	@Test
	public void testListContacts() {

		logger.info("Starting..");
		List<JSONObject> list = this.zohoCrmApiService
		        .listContactsWithNextCallDateDue(this.user.getZohoAuthToken());
		logger.info("Received " + list.size() + " rows");
		Assert.assertTrue("size: " + list.size(), list.size() > 0);

	}
	
	@Test
	public void testLoadNotes() {

		logger.info("Starting..");
		List<Row> list = this.zohoCrmApiService
		        .loadNotes(this.user.getZohoAuthToken(), "755485000000058579", RecordType.Contacts);
		// this has no notes -- List<Row> list = this.zohoCrmApiService.loadNotes("755485000000065011", RecordType.Contacts);
		
		logger.info("Received " + list.size() + " rows");
		Assert.assertTrue("size: " + list.size(), list.size() > 0);

	}	
	
	@Test
	public void testLoadContact() {

		logger.info("Starting..");
		
		Row row = this.zohoCrmApiService.loadContactFields(this.user.getZohoAuthToken(), "755485000000058579");
		logger.info("Received " + row.getFieldMap().size() + " fields");
		for (String fieldName : row.getFieldMap().keySet()) {
			logger.info(fieldName + "=" + row.getFieldMap().get(fieldName));
		}		
		Assert.assertTrue("fields size: " + row.getFieldMap().size(), row.getFieldMap().size() > 0);
	}
	
	@Test
	public void testUpdateContact() {

		logger.info("Starting..");
		
		this.zohoCrmApiService.postponeContactNextCallDate(this.user.getZohoAuthToken(), "755485000000058579");
	}

}
