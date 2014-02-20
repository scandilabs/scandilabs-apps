
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
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

import com.scandilabs.apps.zohocrm.service.DailyEmailDaemon;
import com.scandilabs.apps.zohocrm.service.Row;
import com.scandilabs.apps.zohocrm.service.ZohoCrmApiService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:zoho-api-spring.xml")
public class BasicZohoApiCallTest {

	private static Logger logger = LoggerFactory
	        .getLogger(BasicZohoApiCallTest.class);

	@Autowired
	private ZohoCrmApiService zohoCrmApiService;
	
	@Autowired
	private DailyEmailDaemon dailyEmailDaemon;	

	@Before
	public void setUp() {
		
		// Prevent daemon from starting
		this.dailyEmailDaemon.setStopped(true);
	}

	@Test
	public void testListContacts() {

		logger.info("Starting..");
		List<JSONObject> list = this.zohoCrmApiService
		        .listContactsWithNextCallDateDue();
		logger.info("Received " + list.size() + " rows");
		Assert.assertTrue("size: " + list.size(), list.size() > 0);

	}
	
	@Test
	public void testLoadContact() {

		logger.info("Starting..");
		
		Row row = this.zohoCrmApiService.loadContactFields("755485000000058579");
		logger.info("Received " + row.getFieldMap().size() + " fields");
		for (String fieldName : row.getFieldMap().keySet()) {
			logger.info(fieldName + "=" + row.getFieldMap().get(fieldName));
		}		
		Assert.assertTrue("fields size: " + row.getFieldMap().size(), row.getFieldMap().size() > 0);
	}
	
	@Test
	public void testUpdateContact() {

		logger.info("Starting..");
		
		this.zohoCrmApiService.postponeContactNextCallDate("755485000000058579");
	}

}
